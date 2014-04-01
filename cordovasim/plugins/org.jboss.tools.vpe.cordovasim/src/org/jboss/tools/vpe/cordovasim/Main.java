package org.jboss.tools.vpe.cordovasim;

import java.net.MalformedURLException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.ProxyServlet;

public class Main {

	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable {
		Server _server = new Server();

        SelectChannelConnector _connector = new SelectChannelConnector();
        _connector.setPort(6789);
        _server.addConnector(_connector);

        HandlerCollection handlers = new HandlerCollection();
        _server.setHandler(handlers);

        ServletContextHandler proxyCtx = new ServletContextHandler(handlers, "/proxy", ServletContextHandler.NO_SESSIONS); //$NON-NLS-1$
        ServletHolder proxyServletHolder = new ServletHolder(new ProxyServlet()
        {
            @Override
            protected HttpURI proxyHttpURI(String scheme, String serverName, int serverPort, String uri) throws MalformedURLException
            {
                // Proxies any call to "/proxy" to "/"
            	HttpURI httpUri = new HttpURI(uri.substring("/proxy/".length())); //$NON-NLS-1$
            	setHostHeader(httpUri.getHost()); // XXX
                return httpUri;
            }
        });
        proxyServletHolder.setAsyncSupported(true);
        proxyServletHolder.setInitParameter("timeout", String.valueOf(5 * 60 * 1000L)); //$NON-NLS-1$
        proxyCtx.addServlet(proxyServletHolder, "/*"); //$NON-NLS-1$

        ServletContextHandler appCtx = new ServletContextHandler(handlers, "/", ServletContextHandler.SESSIONS); //$NON-NLS-1$

        handlers.addHandler(proxyCtx);
        handlers.addHandler(appCtx);

        _server.start();

        HttpClient _client = new HttpClient();
        _client.start();

	}

}
