package com.revoluttask;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;


public class App {

    public static void main( String[] args ) throws Exception {
        Server server = new Server(8080);

        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");

        ServletHolder servlet = handler.addServlet(ServletContainer.class, "/*");
        servlet.setInitOrder(0);
        servlet.setInitParameter("jersey.config.server.provider.packages", "com/revoluttask/api");

        server.setHandler(handler);

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }
}
