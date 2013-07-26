/*******************************************************************************
 * Copyright (c) 2007-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim;

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
import org.jboss.tools.vpe.cordovasim.plugin.util.CordovaFileUtil;
import org.jboss.tools.vpe.cordovasim.servlet.plugin.CordovaPluginJsServlet;
import org.jboss.tools.vpe.cordovasim.servlet.plugin.PluginServlet;
import org.jboss.tools.vpe.cordovasim.servlets.camera.FormatDataServlet;
import org.jboss.tools.vpe.cordovasim.servlets.camera.HostFileServlet;
import org.jboss.tools.vpe.cordovasim.servlets.camera.UploadFileServlet;

/**
 * @author Yahor Radtsevich (yradtsevich)
 * @author Ilya Buziuk (ibuziuk)
 */
public class ServerCreator {	

	public static Server createServer(final String resourceBase, int port) {
		Server server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setReuseAddress(false);
		connector.setSoLingerTime(0);  // Linux keeps the port blocked without this line
		connector.setPort(port);
		connector.setHost("localhost"); //$NON-NLS-1$
		server.addConnector(connector);
				
		ServletHolder userAgentServletHolder = new ServletHolder(new StaticResponseServlet("OK")); //$NON-NLS-1$
		ServletHandler userAgentServletHandler = new ServletHandler();
		userAgentServletHandler.addServletWithMapping(userAgentServletHolder, "/ripple/user-agent"); //$NON-NLS-1$
		
		ServletHolder proxyServletHolder = new ServletHolder(new CrossOriginProxyServlet("tinyhippos_rurl")); //$NON-NLS-1$
		proxyServletHolder.setAsyncSupported(true);
		ServletHandler proxyServletHandler = new ServletHandler();
		proxyServletHandler.addServletWithMapping(proxyServletHolder, "/ripple/xhr_proxy"); //$NON-NLS-1$
		
		ServletContextHandler fileUploadContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		ServletHolder uploadFileServletHolder = new ServletHolder(new UploadFileServlet());
		uploadFileServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(null, 1048576, 1048576, 262144));
		fileUploadContextHandler.addServlet(uploadFileServletHolder, "/ripple/fileUpload"); //$NON-NLS-1$

		ServletHolder hostFileServletHolder = new ServletHolder(new HostFileServlet());
		ServletHandler hostFileServletHandler = new ServletHandler();
		hostFileServletHandler.addServletWithMapping(hostFileServletHolder, "/temp-photo/*"); //$NON-NLS-1$
		
		ServletHolder formatDataServletHolder = new ServletHolder(new FormatDataServlet());
		ServletHandler formatDataServletHandler = new ServletHandler();
		formatDataServletHandler.addServletWithMapping(formatDataServletHolder, "/ripple/formatData"); //$NON-NLS-1$
		
		ResourceHandler rippleResourceHandler = new ResourceHandler();
		rippleResourceHandler.setDirectoriesListed(true);
		rippleResourceHandler.setWelcomeFiles(new String[] { "index.html" }); //$NON-NLS-1$
		String ripplePath = ServerCreator.class.getClassLoader().getResource("ripple").toExternalForm(); //$NON-NLS-1$
		rippleResourceHandler.setResourceBase(ripplePath);
		ContextHandler rippleContextHandler = new ContextHandler("/ripple/assets"); //$NON-NLS-1$
		rippleContextHandler.setHandler(rippleResourceHandler);
		
		ResourceHandler cordovaResourceHandler = new NotCachingResourceHandler();
		String cordovaPath = ServerCreator.class.getClassLoader().getResource("ripple/cordova").toExternalForm(); //$NON-NLS-1$
		cordovaResourceHandler.setResourceBase(cordovaPath);
		ContextHandler cordovaContextHandler = new ContextHandler("/ripple/cordova"); //$NON-NLS-1$
		cordovaContextHandler.setHandler(cordovaResourceHandler);
		
		ResourceHandler wwwResourceHandler = new NotCachingResourceHandler();
		wwwResourceHandler.setDirectoriesListed(true);
		wwwResourceHandler.setResourceBase(resourceBase);
		ContextHandler wwwContextHandler = new ContextHandler("/"); //$NON-NLS-1$
		wwwContextHandler.setHandler(wwwResourceHandler);
		
		File pluginDir = CordovaFileUtil.getPluginDir(resourceBase); 
		ServletHolder cordovaPluginJsServletHolder = new ServletHolder(new CordovaPluginJsServlet(pluginDir));
		ServletHandler cordovaPluginJsServetHandler = new ServletHandler();
		cordovaPluginJsServetHandler.addServletWithMapping(cordovaPluginJsServletHolder, "/cordova_plugins.js"); //$NON-NLS-1$
		
		ServletHolder pluginServletHolder = new ServletHolder(new PluginServlet(pluginDir));
		ServletHandler pluginServletHandler = new ServletHandler();
		pluginServletHandler.addServletWithMapping(pluginServletHolder, "/plugins/*"); //$NON-NLS-1$
		
		RewriteHandler rippleRewriteHandler = new RewriteHandler();
		rippleRewriteHandler.setRewriteRequestURI(true);
		rippleRewriteHandler.setRewritePathInfo(true);
		rippleRewriteHandler.setHandler(rippleContextHandler);
		rippleRewriteHandler.addRule(new Rule() {
			@Override
			public String matchAndApply(String target, HttpServletRequest request,
					HttpServletResponse response) throws IOException {
				if (request.getParameter("enableripple") != null) { //$NON-NLS-1$
					return "/ripple/assets/index.html"; //$NON-NLS-1$
				} else {
					return null;
				}
			}
		});
		
		RewriteHandler cordovaRewriteHandler = new RewriteHandler();
		cordovaRewriteHandler.setRewriteRequestURI(true);
		cordovaRewriteHandler.setRewritePathInfo(true);
		cordovaRewriteHandler.setHandler(cordovaContextHandler);
		cordovaRewriteHandler.addRule(new Rule() {
			@Override
			public String matchAndApply(String target, HttpServletRequest request,
					HttpServletResponse response) throws IOException {
				String pathInfo = request.getPathInfo(); 
				String cordovaVersion = CordovaFileUtil.getCordovaVersion(resourceBase);
				
				if (cordovaVersion.equals("3.1.0")) { //$NON-NLS-1$
					if (pathInfo.equals("/cordova.js")) { //$NON-NLS-1$
						return "/ripple/cordova/cordova-3.1.0.js"; //$NON-NLS-1$
					}
					return null;
				} else { // Will be implemented in the context of multiple version support issue
					if (pathInfo.equals("/cordova.js")) { // JBIDE-14319 //$NON-NLS-1$
						return "/ripple/cordova/cordova-2.7.0.js"; //$NON-NLS-1$
					} else if (pathInfo.equals("/cordova_plugins.json")) { // JBIDE-14453 //$NON-NLS-1$
						return "/ripple/cordova/cordova_plugins.json"; //$NON-NLS-1$
					}
					return null;
				}
			}
		});
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] {
				userAgentServletHandler,
				rippleRewriteHandler,
				wwwContextHandler,
				cordovaRewriteHandler,
				cordovaPluginJsServetHandler,
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
