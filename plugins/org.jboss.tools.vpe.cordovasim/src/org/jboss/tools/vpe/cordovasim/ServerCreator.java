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
import org.jboss.tools.vpe.cordovasim.servlets.camera.FormatDataServlet;
import org.jboss.tools.vpe.cordovasim.servlets.camera.HostFileServlet;
import org.jboss.tools.vpe.cordovasim.servlets.camera.UploadFileServlet;

/**
 * @author Yahor Radtsevich (yradtsevich)
 */
public class ServerCreator {
	public static Server createServer(String resourceBase, int port) {
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
		
		RewriteHandler cordovaRewriteHandler = new RewriteHandler();
		cordovaRewriteHandler.setRewriteRequestURI(true);
		cordovaRewriteHandler.setRewritePathInfo(true);
		cordovaRewriteHandler.setHandler(cordovaContextHandler);
		cordovaRewriteHandler.addRule(new Rule() {
			@Override
			public String matchAndApply(String target, HttpServletRequest request,
					HttpServletResponse response) throws IOException {
				if (request.getPathInfo().equals("/cordova.js")){ // JBIDE-14319
					return "/ripple/cordova/cordova-2.7.0.js";
				} else if (request.getPathInfo().equals("/cordova_plugins.json")){ // JBIDE-14453
					return "/ripple/cordova/cordova_plugins.json";
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
				cordovaRewriteHandler,
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
