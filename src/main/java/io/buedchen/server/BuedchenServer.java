package io.buedchen.server;

import io.buedchen.server.api.Providers;
import io.buedchen.server.api.REST;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.websocket.DeploymentException;
import java.util.EnumSet;

public class BuedchenServer {

    public static class Builder {
        private static final Logger logger = LoggerFactory.getLogger(BuedchenServer.class);
        private ServletContextHandler restContext;
        private ServletContextHandler wsContext;
        private ContextHandler swaggerUIContext;
        private HandlerCollection handlers = new HandlerCollection();

        public Builder() {

        }

        public Builder restContext() {

            ResourceConfig resourceConfig = new ResourceConfig();
            resourceConfig.packages(REST.class.getPackage().getName(), ApiListingResource.class.getPackage().getName());
            resourceConfig.packages(Providers.class.getPackage().getName(),
                    ApiListingResource.class.getPackage().getName());
            ServletContainer servletContainer = new ServletContainer(resourceConfig);
            ServletHolder entityBrowser = new ServletHolder(servletContainer);
            restContext = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
            restContext.setContextPath("/api");
            restContext.addServlet(entityBrowser, "/*");

            handlers.addHandler(restContext);

            FilterHolder filterHolder =
                    restContext.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
            filterHolder.setFilter(new CrossOriginFilter());
            filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
            filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
                    "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
            filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");

            return this;
        }

        public Builder wsContext() {
            wsContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
            wsContext.setContextPath("/ws");

            handlers.addHandler(wsContext);
            return this;
        }

        public Builder swaggerUIContext() {
            // Build the Swagger Bean
            // This configures Swagger
            BeanConfig beanConfig = new BeanConfig();
            beanConfig.setResourcePackage(REST.class.getPackage().getName());
            beanConfig.setBasePath("/api");

            beanConfig.setDescription("REST API");
            beanConfig.setTitle("Buedchen TV");
            beanConfig.setScan(true);

            // Handler for Swagger UI
            try {
                handlers.addHandler(buildSwaggerUI());
            } catch (Exception e) {
                logger.error("Error : ", e);
            }

            return this;
        }

        public Server build(Integer port) {

            Server server = new Server(port);
            server.setHandler(this.handlers);

            if (wsContext != null) {
                try {
                    ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(wsContext);
                    wscontainer.addEndpoint(Websocket.class);
                } catch (ServletException e) {
                    logger.error("Error :", e);
                } catch (DeploymentException e) {
                    logger.error("Error :", e);
                }
            }
            return server;
        }

        private ContextHandler buildSwaggerUI() throws Exception {
            final ResourceHandler swaggerUIResourceHandler = new ResourceHandler();
            swaggerUIResourceHandler.setResourceBase(
                    BuedchenServer.class.getClassLoader().getResource("swaggerui").toURI().toString());
            swaggerUIContext = new ContextHandler();
            swaggerUIContext.setContextPath("/docs/");
            swaggerUIContext.setHandler(swaggerUIResourceHandler);

            return swaggerUIContext;
        }
    }
}
