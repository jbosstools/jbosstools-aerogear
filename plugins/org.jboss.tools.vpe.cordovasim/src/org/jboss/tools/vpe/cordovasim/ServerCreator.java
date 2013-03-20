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
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.camera.HostFileServlet;
import org.eclipse.jetty.servlets.camera.UploadFileServlet;

/**
 * @author Yahor Radtsevich (yradtsevich)
 */
public class ServerCreator {
	public static Server createServer(String resourceBase, int port) {
		Server server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(port);
		server.addConnector(connector);

		ServletHolder userAgentServletHolder = new ServletHolder(new StaticResponseServlet("OK"));
		ServletHandler userAgentServletHandler = new ServletHandler();
		userAgentServletHandler.addServletWithMapping(userAgentServletHolder, "/ripple/user-agent");
		
		ServletHolder proxyServletHolder = new ServletHolder(new CrossOriginProxyServlet("tinyhippos_rurl"));
		proxyServletHolder.setAsyncSupported(true);
		ServletHandler proxyServletHandler = new ServletHandler();
		proxyServletHandler.addServletWithMapping(proxyServletHolder, "/ripple/xhr_proxy");
		
		ServletHolder uploadFileServletHolder = new ServletHolder(new UploadFileServlet());
		ServletHandler uploadFileServletHandler = new ServletHandler();
		uploadFileServletHandler.addServletWithMapping(uploadFileServletHolder, "/fileupload");
		
		ServletHolder hostFileServletHolder = new ServletHolder(new HostFileServlet());
		ServletHandler hostFileServletHandler = new ServletHandler();
		hostFileServletHandler.addServletWithMapping(hostFileServletHolder, "/temp-photo/*");
		
		ResourceHandler rippleResourceHandler = new ResourceHandler();
		rippleResourceHandler.setDirectoriesListed(true);
		rippleResourceHandler.setWelcomeFiles(new String[] { "index.html" });
		rippleResourceHandler.setResourceBase("./ripple-ui");
		ContextHandler rippleContextHandler = new ContextHandler("/ripple/assets");
		rippleContextHandler.setHandler(rippleResourceHandler);
		
		ResourceHandler wwwResourceHandler = new ResourceHandler();
		wwwResourceHandler.setDirectoriesListed(true);
		wwwResourceHandler.setResourceBase(resourceBase);
		ContextHandler wwwContextHandler = new ContextHandler("/");
		wwwContextHandler.setHandler(wwwResourceHandler);
		

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] {
				userAgentServletHandler,
				wwwContextHandler,
				proxyServletHandler,
				rippleContextHandler,
				uploadFileServletHandler,
				hostFileServletHandler,
				new DefaultHandler(),
			});
		
		RewriteHandler rewriteHandler = new RewriteHandler();
		rewriteHandler.setRewriteRequestURI(true);
		rewriteHandler.setRewritePathInfo(true);
		rewriteHandler.setHandler(handlers);
		rewriteHandler.addRule(new Rule() {
			@Override
			public String matchAndApply(String target, HttpServletRequest request,
					HttpServletResponse response) throws IOException {
				if ("true".equals(request.getParameter("enableripple"))) {
					return "/ripple/assets/index.html";
				} else {
					return null;
				}
			}
		});
		server.setHandler(rewriteHandler);
		
		return server;
	}
}
