import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory
import org.eclipse.jetty.server.HttpConfiguration
import org.eclipse.jetty.server.HttpConnectionFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.Resource

import java.util.regex.Matcher
import java.util.regex.Pattern;

class Main {

    Properties props;
    int port;
    String host;
    final static int DEFAULT_PORT = 8090;
    final static String DEFAULT_HOST = "127.0.0.1";

    static void main(String... args) {

        Main main = new Main();


        if (args.length > 0) {
          main.props = new Properties();
          File propertiesFile = new File(args[0])
          propertiesFile.withInputStream {
            main.props.load(it)
          }
          if (null == (main.port = main.props.getProperty("quantum.port") as int)) {
            main.port = DEFAULT_PORT;
          }

          if (null != (main.host = main.props.getProperty("quantum.host") as String)) {

            println("Property quantum.host found, value: ${main.host}");
            Pattern VAR = Pattern.compile(".*\\\$\\{([^}]+)\\}.*");
            Matcher m = VAR.matcher(main.host);
            if (m.matches()) {
              String var = m.group(1);

              println("Substitution pattern found for quantum.host, value: ${var}");

              if (main.props.containsKey(var)) {
                println("Substitution pattern found in properties file, value: ${ main.props.getProperty(var)}");
                main.host =  main.props.getProperty(var);
              } else {
                System.err.println("Property '" + var + "' referenced in property quantum.host is unset, unsetting");
                main.host = DEFAULT_HOST;
              }
              println("main.host value: ${main.host}");
            }

          } else {
            main.host = DEFAULT_HOST;
          }
        } else{
          main.port = DEFAULT_PORT;
          main.host = DEFAULT_HOST;
        }



        try {
          main.run();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    void run() {


        // The Jetty Server.
        Server server = new Server();

        // Common HTTP configuration.
        HttpConfiguration config = new HttpConfiguration();

        // HTTP/1.1 support.
        HttpConnectionFactory http1 = new HttpConnectionFactory(config);

        // HTTP/2 cleartext support.
        HTTP2CServerConnectionFactory http2c = new HTTP2CServerConnectionFactory(config);

        ServerConnector connector = new ServerConnector(server, http1, http2c);

        connector.setHost(this.host);
        connector.setPort(this.port);
        server.addConnector(connector);


        URL webRootLocation = this.getClass().getResource("/public_html/.touch");
        if (webRootLocation == null)
        {
            throw new IllegalStateException("Unable to determine webroot URL location");
        }

        URI webRootUri = URI.create(webRootLocation.toURI().toASCIIString().replaceFirst("/.touch\$","/"));
        System.err.printf("Web Root URI: %s%n",webRootUri);


        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.setBaseResource(Resource.newResource(webRootUri));
        context.setWelcomeFiles([ "index.html" ] as String[]);
        context.getMimeTypes().addMimeMapping("txt","text/plain;charset=utf-8");

        ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);
        holderPwd.setInitParameter("dirAllowed","true");
        context.addServlet(holderPwd,"/");

        server.setHandler(context);

        System.out.println("#### quantum.endpoint " + InetAddress.getByName(this.host) + ":" + this.port);

        server.start();
        server.dumpStdErr();
        server.join();
    }

}
