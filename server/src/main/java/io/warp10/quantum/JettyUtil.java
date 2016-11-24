package io.warp10.quantum;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;

public class JettyUtil {
  public static void setSendServerVersion(Server server, boolean send) {
    //
    // Remove display of Server header
    // @see http://stackoverflow.com/questions/15652902/remove-the-http-server-header-in-jetty-9
    //

    for(Connector y : server.getConnectors()) {
      for(ConnectionFactory x  : y.getConnectionFactories()) {
        if(x instanceof HttpConnectionFactory) {
          ((HttpConnectionFactory)x).getHttpConfiguration().setSendServerVersion(send);
        }
      }
    }
  }
}
