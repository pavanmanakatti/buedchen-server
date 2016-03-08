package io.ullrich.buedchen.server;

import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

public class Start2 {
    
    public static void main(String[] args) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        //server.setHandler(context);
        try {
            ServletContextHandler wsContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
            wsContext.setContextPath("/ws");
            
            ServletContextHandler restContext = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
            restContext.setContextPath("/api");
            
            HandlerCollection handlers = new HandlerCollection();
            handlers.addHandler(wsContext);
            handlers.addHandler(restContext);
            server.setHandler(handlers);
            
            ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(wsContext);
            wscontainer.addEndpoint(Websocket.class);
            
            ServletHolder restServlet = restContext.addServlet(ServletContainer.class, "/*");
            restServlet.setInitOrder(0);
            restServlet.setInitParameter("jersey.config.server.provider.classnames", REST.class.getCanonicalName());
            
            REST rest = new REST();
            ServletHolder restHolder = new ServletHolder(new ServletContainer(new ResourceConfig().register(new REST())));
            restContext.addServlet(restHolder, "/api");

            /*FilterHolder filterHolder = new FilterHolder();
            filterHolder.setFilter(new CrossOriginFilter());
            context.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));*/
// Add WebSocket endpoint to javax.websocket layer
            server.start();
            server.dump(System.err);
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }
}
