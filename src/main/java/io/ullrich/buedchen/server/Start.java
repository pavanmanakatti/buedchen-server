package io.ullrich.buedchen.server;

import com.google.common.eventbus.EventBus;
import io.ullrich.buedchen.server.events.ServerStarted;
import io.ullrich.buedchen.server.events.content.ChannelContentAdded;
import io.ullrich.buedchen.server.listeners.ChannelContentListeners;
import io.ullrich.buedchen.server.listeners.ChannelListeners;
import io.ullrich.buedchen.server.listeners.ClientListeners;
import io.ullrich.buedchen.server.listeners.DeadEventListener;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Start {

    private static final Logger logger = LoggerFactory.getLogger(Start.class);

    public Start() {
        EventBusWrapper eventBus = new EventBusWrapper(new EventBus());

        //setup domain
        Channels channels = new Channels();
        Clients clients = new Clients();

        //wire listeners
        eventBus.register(new ClientListeners(eventBus, channels, clients));
        eventBus.register(new ChannelListeners(eventBus, channels, clients));
        eventBus.register(new ChannelContentListeners(eventBus, channels, clients));
        eventBus.register(new DeadEventListener());

        channels.addChannel(new Channel("UNASSIGNED", "CLIENT NOT ASSIGNED"));
        Content content = new Content("http://localhost:8080/api/v1/unassigned", 120, "Unassigned Channel", "Unassigned");
        channels.addContentToChannel("UNASSIGNED", content);
        eventBus.post(new ChannelContentAdded("UNASSIGNED", content));

        //setup jetty etc.
        Server server = new Server(8080);

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
            ServletHolder wsHolder = new ServletHolder(new ServletContainer(new ResourceConfig().register(new Websocket(eventBus))));
            restContext.addServlet(wsHolder, "/ws");

            ServletHolder restServlet = restContext.addServlet(ServletContainer.class, "/*");
            restServlet.setInitOrder(0);
            restServlet.setInitParameter("jersey.config.server.provider.classnames", REST.class.getCanonicalName());

            ServletHolder restHolder = new ServletHolder(new ServletContainer(new ResourceConfig().register(new REST(eventBus, clients, channels))));
            restContext.addServlet(restHolder, "/api");

            /*FilterHolder filterHolder = new FilterHolder();
            filterHolder.setFilter(new CrossOriginFilter());
            context.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));*/
            server.start();
            eventBus.post(new ServerStarted());
            server.join();
        } catch (Exception ex) {
            logger.error("Could not start server", ex);
        } finally {
            server.destroy();
        }
    }

    public static void main(String[] args) {
        new Start();

    }
}
