package org.jboss.tools.vpe.cordovasim.eclipse.server.internal;

import java.io.File;
import java.io.IOException;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.Rule;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.tools.vpe.cordovasim.eclipse.cordova.plugin.internal.CordovaFileUtil;
import org.jboss.tools.vpe.cordovasim.eclipse.servlet.internal.CordovaJsServlet;
import org.jboss.tools.vpe.cordovasim.eclipse.servlet.internal.CordovaPluginJsServlet;
import org.jboss.tools.vpe.cordovasim.eclipse.servlet.internal.CrossOriginProxyServlet;
import org.jboss.tools.vpe.cordovasim.eclipse.servlet.internal.FormatDataServlet;
import org.jboss.tools.vpe.cordovasim.eclipse.servlet.internal.HostFileServlet;
import org.jboss.tools.vpe.cordovasim.eclipse.servlet.internal.NotCachingResourceHandler;
import org.jboss.tools.vpe.cordovasim.eclipse.servlet.internal.PluginServlet;
import org.jboss.tools.vpe.cordovasim.eclipse.servlet.internal.StaticResponseServlet;
import org.jboss.tools.vpe.cordovasim.eclipse.servlet.internal.UploadFileServlet;

/**
 * @author Yahor Radtsevich (yradtsevich)
 * @author Ilya Buziuk (ibuziuk)
 */
public class ServerCreator {	

	@SuppressWarnings("nls")
	public static Server createServer(final String resourceBase, String cordovaEngineLocation, int port) {
		Server server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setReuseAddress(false);
		connector.setSoLingerTime(0);  // Linux keeps the port blocked without this line
		connector.setPort(port);
		connector.setHost("localhost"); 
		server.addConnector(connector);
				
		ServletHolder userAgentServletHolder = new ServletHolder(new StaticResponseServlet("OK")); 
		ServletHandler userAgentServletHandler = new ServletHandler();
		userAgentServletHandler.addServletWithMapping(userAgentServletHolder, "/ripple/user-agent"); 
		
		ServletHolder proxyServletHolder = new ServletHolder(new CrossOriginProxyServlet("tinyhippos_rurl")); 
		proxyServletHolder.setAsyncSupported(true);
		ServletHandler proxyServletHandler = new ServletHandler();
		proxyServletHandler.addServletWithMapping(proxyServletHolder, "/ripple/xhr_proxy"); 
		
		ServletContextHandler fileUploadContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		ServletHolder uploadFileServletHolder = new ServletHolder(new UploadFileServlet());
		uploadFileServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(null, 1048576, 1048576, 262144));
		fileUploadContextHandler.addServlet(uploadFileServletHolder, "/ripple/fileUpload"); 

		ServletHolder hostFileServletHolder = new ServletHolder(new HostFileServlet());
		ServletHandler hostFileServletHandler = new ServletHandler();
		hostFileServletHandler.addServletWithMapping(hostFileServletHolder, "/temp-photo/*");
		
		ServletHolder formatDataServletHolder = new ServletHolder(new FormatDataServlet());
		ServletHandler formatDataServletHandler = new ServletHandler();
		formatDataServletHandler.addServletWithMapping(formatDataServletHolder, "/ripple/formatData"); 
		
		ResourceHandler rippleResourceHandler = new ResourceHandler();
		rippleResourceHandler.setDirectoriesListed(true);
		rippleResourceHandler.setWelcomeFiles(new String[] { "index.html" }); 
		String ripplePath = ServerCreator.class.getClassLoader().getResource("ripple").toExternalForm(); 
		rippleResourceHandler.setResourceBase(ripplePath);
		ContextHandler rippleContextHandler = new ContextHandler("/ripple/assets");
		rippleContextHandler.setHandler(rippleResourceHandler);
		
		ResourceHandler cordovaResourceHandler = new NotCachingResourceHandler();
		String cordovaPath = ServerCreator.class.getClassLoader().getResource("ripple/cordova").toExternalForm(); 
		cordovaResourceHandler.setResourceBase(cordovaPath);
		ContextHandler cordovaContextHandler = new ContextHandler("/ripple/cordova");
		cordovaContextHandler.setHandler(cordovaResourceHandler);
		
		ResourceHandler wwwResourceHandler = new NotCachingResourceHandler();
		wwwResourceHandler.setDirectoriesListed(true);
		wwwResourceHandler.setResourceBase(resourceBase);
		ContextHandler wwwContextHandler = new ContextHandler("/"); 
		wwwContextHandler.setHandler(wwwResourceHandler);
		
		ServletHolder cordovaJsServletHolder = new ServletHolder(new CordovaJsServlet(cordovaEngineLocation));
		ServletHandler cordovaJsServetHandler = new ServletHandler();
		cordovaJsServetHandler.addServletWithMapping(cordovaJsServletHolder, "/cordova.js"); 
		
		File pluginDir = CordovaFileUtil.getPluginDir(resourceBase); 
		ServletHolder cordovaPluginJsServletHolder = new ServletHolder(new CordovaPluginJsServlet(pluginDir));
		ServletHandler cordovaPluginJsServetHandler = new ServletHandler();
		cordovaPluginJsServetHandler.addServletWithMapping(cordovaPluginJsServletHolder, "/cordova_plugins.js"); 
		
		ServletHolder pluginServletHolder = new ServletHolder(new PluginServlet(pluginDir));
		ServletHandler pluginServletHandler = new ServletHandler();
		pluginServletHandler.addServletWithMapping(pluginServletHolder, "/plugins/*"); 
		
		RewriteHandler rippleRewriteHandler = new RewriteHandler();
		rippleRewriteHandler.setRewriteRequestURI(true);
		rippleRewriteHandler.setRewritePathInfo(true);
		rippleRewriteHandler.setHandler(rippleContextHandler);
		rippleRewriteHandler.addRule(new Rule() {
			@Override
			public String matchAndApply(String target, HttpServletRequest request,
					HttpServletResponse response) throws IOException {
				if (request.getParameter("enableripple") != null) { 
					return "/ripple/assets/index.html"; 
				} else {
					return null;
				}
			}
		});
				
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] {
				userAgentServletHandler,
				rippleRewriteHandler,
				wwwContextHandler,
				cordovaJsServetHandler,
				cordovaPluginJsServetHandler,
				cordovaContextHandler,
				pluginServletHandler,
				proxyServletHandler,
				fileUploadContextHandler,
				hostFileServletHandler,
				formatDataServletHandler,
				new DefaultHandler()
			});
		server.setHandler(handlers);
		return server;
	}
	
}

