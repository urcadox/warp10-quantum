import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory
import org.eclipse.jetty.server.HttpConfiguration
import org.eclipse.jetty.server.HttpConnectionFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.Resource;

class Main {
    static void main(String... args) {
        try {
            new Main().run();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    void run() {

        Server server = new Server();


        HttpConfiguration config = new HttpConfiguration();
        HttpConnectionFactory http1 = new HttpConnectionFactory(config);
        HTTP2CServerConnectionFactory http2c = new HTTP2CServerConnectionFactory(config);

        ServerConnector connector = new ServerConnector(server, http1, http2c);
        connector.setPort(8090);
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

        server.start();
        server.dumpStdErr();
        server.join();
    }

}
