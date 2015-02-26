/*******************************************************************************
 * Copyright (c) 2013-2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cordovasim.eclipse.server.internal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
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
import org.jboss.tools.cordovasim.eclipse.Activator;
import org.jboss.tools.cordovasim.eclipse.internal.util.CordovaFileUtil;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.ConfigServlet;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.CordovaJsServlet;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.CordovaPluginJsServlet;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.CrossOriginProxyServlet;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.FormatDataServlet;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.HostFileServlet;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.NotCachingResourceHandler;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.PluginServlet;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.StaticResponseServlet;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.UploadFileServlet;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.WorkspaceFileServlet;
import org.osgi.framework.Bundle;

/**
 * @author Yahor Radtsevich (yradtsevich)
 * @author Ilya Buziuk (ibuziuk)
 */
public class ServerCreator {	

	@SuppressWarnings("nls")
	public static Server createServer(final IProject project, final IContainer resourceBase, String cordovaEngineLocation, Integer port) {
		Server server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setReuseAddress(false);
		connector.setSoLingerTime(0);  // Linux keeps the port blocked without this line
		
		port = (port != null) ? port : 0; // If port is undefined use any free port
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
				
		String ripplePath = getRippleResoursePath("ripple"); 
		rippleResourceHandler.setResourceBase(ripplePath);
		ContextHandler rippleContextHandler = new ContextHandler("/ripple/assets");
		rippleContextHandler.setHandler(rippleResourceHandler);
		
		ResourceHandler cordovaResourceHandler = new NotCachingResourceHandler();
		String cordovaPath = getRippleResoursePath("ripple/cordova");
		cordovaResourceHandler.setResourceBase(cordovaPath);
		ContextHandler cordovaContextHandler = new ContextHandler("/ripple/cordova");
		cordovaContextHandler.setHandler(cordovaResourceHandler);
		
		String resourseLocation = resourceBase.getRawLocation().makeAbsolute().toOSString();
		ResourceHandler wwwResourceHandler = new NotCachingResourceHandler();
		wwwResourceHandler.setDirectoriesListed(true);
		wwwResourceHandler.setResourceBase(resourseLocation);
		ContextHandler wwwContextHandler = new ContextHandler("/"); 
		wwwContextHandler.setHandler(wwwResourceHandler);
		
		String workspaceResoureLocation = "/" + project.getName() + "/" + resourceBase.getProjectRelativePath().toOSString();
		ServletHolder workspaceServletHolder = new ServletHolder(new WorkspaceFileServlet(workspaceResoureLocation));
		ServletHandler workspaceServletHandler = new ServletHandler();
		workspaceServletHandler.addServletWithMapping(workspaceServletHolder, "/");
						
		ServletHolder cordovaJsServletHolder = new ServletHolder(new CordovaJsServlet(cordovaEngineLocation));
		ServletHandler cordovaJsServetHandler = new ServletHandler();
		cordovaJsServetHandler.addServletWithMapping(cordovaJsServletHolder, "/cordova.js"); 
		
		ServletHolder configHolder = new ServletHolder(new ConfigServlet(project));
		ServletHandler configHandler = new ServletHandler();
		configHandler.addServletWithMapping(configHolder, "/config.xml");
		
		File pluginDir = CordovaFileUtil.getPluginDir(resourseLocation); 
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
				wwwResourceHandler,
				cordovaJsServetHandler,
				configHandler,
				cordovaPluginJsServetHandler,
				cordovaContextHandler,
				pluginServletHandler,
				proxyServletHandler,
				fileUploadContextHandler,
				hostFileServletHandler,
				formatDataServletHandler,
				workspaceServletHandler,
				new DefaultHandler()
			});
		server.setHandler(handlers);
		return server;
	}
	
	private static String getRippleResoursePath(String ripplePath) {
		Bundle bundle = Platform.getBundle("org.jboss.tools.cordovasim.ripple"); //$NON-NLS-1$
		URL fileURL = bundle.getEntry(ripplePath);
		try {
			URL resolvedFileURL = FileLocator.toFileURL(fileURL);
			// We need to use the 3-arg constructor of URI in order to properly escape file system chars
			URI resolvedURI = new URI(resolvedFileURL.getProtocol(), resolvedFileURL.getPath(), null);
			File file = new File(resolvedURI);
			if (file != null && file.exists()) {
				ripplePath = file.getAbsolutePath();
			}
		} catch (URISyntaxException e) {
			Activator.logError(e.getMessage(), e);
		} catch (IOException e) {
			Activator.logError(e.getMessage(), e);
		}
		return ripplePath;
	}
	
}

