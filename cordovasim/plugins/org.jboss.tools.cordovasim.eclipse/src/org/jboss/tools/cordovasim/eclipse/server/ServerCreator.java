/*******************************************************************************
 * Copyright (c) 2013-2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cordovasim.eclipse.server;

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
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.jboss.tools.cordovasim.eclipse.Activator;
import org.jboss.tools.cordovasim.eclipse.internal.util.CordovaFileUtil;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.ConfigXMLServlet;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.CordovaJsServlet;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.CordovaPluginJsServlet;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.CrossOriginProxyServlet;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.FormatDataServlet;
import org.jboss.tools.cordovasim.eclipse.servlet.internal.HostFileServlet;
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
	private static final String CORDOVASIM_RIPPLE_BUNDLE = "org.jboss.tools.cordovasim.ripple";  //$NON-NLS-1$
	private static final String RIPPLE_FOLDER = "ripple"; //$NON-NLS-1$
	private static final String RIPPLE_CORDOVA_FOLDER = "ripple/cordova"; //$NON-NLS-1$
	private static final String LOCALHOST = "localhost"; //$NON-NLS-1$

	public static Server createServer(final IProject project, final IContainer resourceBase, final String cordovaEngineLocation, final Integer port) {
		QueuedThreadPool threadPool = new QueuedThreadPool(100, 10);
		Server server = new Server(threadPool);
		server.manage(threadPool);
		
		ServerConnector connector = new ServerConnector(server);
		connector.setReuseAddress(false);
		connector.setSoLingerTime(0);  // Linux keeps the port blocked without this line
		
		connector.setHost(LOCALHOST); 
		connector.setPort(setupPort(port));
	
		server.setConnectors(new Connector[] {connector});

		// Basic application context (Handler Tree)
		ServletContextHandler rippleContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		rippleContext.setContextPath("/"); //$NON-NLS-1$
		
		// Servlet for cordova.js hosting
		ServletHolder cordovaJsServletHolder = new ServletHolder(new CordovaJsServlet(cordovaEngineLocation));
		rippleContext.addServlet(cordovaJsServletHolder, "/cordova.js");  //$NON-NLS-1$
		
		// Hosting ripple/assets folder
		String ripplePath = getResoursePathFromBundle(RIPPLE_FOLDER, CORDOVASIM_RIPPLE_BUNDLE); 
		ServletHolder rippleHome = new ServletHolder("ripple-home", DefaultServlet.class); //$NON-NLS-1$
		rippleHome.setInitParameter("resourceBase", ripplePath); //$NON-NLS-1$
		rippleHome.setInitParameter("pathInfoOnly","true"); //$NON-NLS-1$ //$NON-NLS-2$
		rippleHome.setInitParameter("dirAllowed", "true");  //$NON-NLS-1$//$NON-NLS-2$
		rippleContext.addServlet(rippleHome, "/ripple/assets/*"); //$NON-NLS-1$
		
		// User-Agent request (always return 200)
		ServletHolder userAgentServletHolder = new ServletHolder(new StaticResponseServlet("OK"));  //$NON-NLS-1$
		rippleContext.addServlet(userAgentServletHolder, "/ripple/user-agent"); //$NON-NLS-1$
					
		// Hybrid App config.xml
		ServletHolder configXMLHolder = new ServletHolder(new ConfigXMLServlet(project));
		rippleContext.addServlet(configXMLHolder, "/config.xml"); //$NON-NLS-1$
		
		// Servlet for generating cordova_plugins.js file 
		String wwwLocation = resourceBase.getRawLocation().makeAbsolute().toOSString();
		File pluginDir = CordovaFileUtil.getPluginDir(wwwLocation); 
		ServletHolder cordovaPluginJsServletHolder = new ServletHolder(new CordovaPluginJsServlet(pluginDir));
		rippleContext.addServlet(cordovaPluginJsServletHolder, "/cordova_plugins.js");  //$NON-NLS-1$
		
		String cordovaLocation = getResoursePathFromBundle(RIPPLE_CORDOVA_FOLDER, CORDOVASIM_RIPPLE_BUNDLE);
		ServletHolder cordovaHome = new ServletHolder("cordova-home", DefaultServlet.class); //$NON-NLS-1$
		cordovaHome.setInitParameter("resourceBase", cordovaLocation); //$NON-NLS-1$
		cordovaHome.setInitParameter("dirAllowed", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		cordovaHome.setInitParameter("pathInfoOnly","true"); //$NON-NLS-1$ //$NON-NLS-2$
		rippleContext.addServlet(cordovaHome, "/ripple/cordova/*"); //$NON-NLS-1$
		
		// PluginServlet for handling cordova plugins 
		ServletHolder pluginServletHolder = new ServletHolder(new PluginServlet(pluginDir));
		rippleContext.addServlet(pluginServletHolder, "/plugins/*");  //$NON-NLS-1$
				
		// Local Proxy Servlet
		ServletHolder proxyServletHolder = new ServletHolder(new CrossOriginProxyServlet("tinyhippos_rurl"));  //$NON-NLS-1$
		proxyServletHolder.setAsyncSupported(true);
		proxyServletHolder.setInitParameter("maxThreads", "10"); //$NON-NLS-1$ //$NON-NLS-2$
		rippleContext.addServlet(proxyServletHolder, "/ripple/xhr_proxy"); //$NON-NLS-1$
		
		// File Upload Servlet (Camera API)
		ServletHolder uploadFileServletHolder = new ServletHolder(new UploadFileServlet());
		uploadFileServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(null, 1048576, 1048576, 262144));
		rippleContext.addServlet(uploadFileServletHolder, "/ripple/fileUpload");  //$NON-NLS-1$
		
		// Temp Photo Servlet (Capture Image API)
		ServletHolder hostFileServletHolder = new ServletHolder(new HostFileServlet());
		rippleContext.addServlet(hostFileServletHolder, "/temp-photo/*"); //$NON-NLS-1$
		
		// Data Format Servlet (Capture API)
		ServletHolder formatDataServletHolder = new ServletHolder(new FormatDataServlet());
		rippleContext.addServlet(formatDataServletHolder, "/ripple/formatData");  //$NON-NLS-1$
		
		// Servlet for handling workspace resources (projects "www" folder and linked resources)
		String workspaceResoureLocation = "/" + project.getName() + "/" + resourceBase.getProjectRelativePath().toOSString(); //$NON-NLS-1$ //$NON-NLS-2$
		ServletHolder workspaceServletHolder = new ServletHolder(new WorkspaceFileServlet(workspaceResoureLocation));
		rippleContext.addServlet(workspaceServletHolder, "/"); //$NON-NLS-1$
													
		RewriteHandler rippleRewriteHandler = new RewriteHandler();
		rippleRewriteHandler.setRewriteRequestURI(true);
		rippleRewriteHandler.setRewritePathInfo(true);
		rippleRewriteHandler.setHandler(rippleContext);
		rippleRewriteHandler.addRule(new Rule() {
			@Override
			public String matchAndApply(String target, HttpServletRequest request,
					HttpServletResponse response) throws IOException {
				if (request.getParameter("enableripple") != null) {  //$NON-NLS-1$
					return "/ripple/assets/index.html";  //$NON-NLS-1$
				} else {
					return null;
				}
			}
		});
		
		server.setHandler(rippleRewriteHandler);
		return server;
	}
	
	private static int setupPort(final Integer port) {
		return (port != null) ? port : 0; // If port is undefined use any free port
	}
	
	private static String getResoursePathFromBundle(final String path, final String bundleName) {
		String resourcePath = null;
		Bundle bundle = Platform.getBundle(bundleName); 
		URL fileURL = bundle.getEntry(path);
		try {
			URL resolvedFileURL = FileLocator.toFileURL(fileURL);
			// We need to use the 3-arg constructor of URI in order to properly escape file system chars
			URI resolvedURI = new URI(resolvedFileURL.getProtocol(), resolvedFileURL.getPath(), null);
			File file = new File(resolvedURI);
			if (file != null && file.exists()) {
				resourcePath = file.getAbsolutePath();
			}
		} catch (URISyntaxException e) {
			Activator.logError(e.getMessage(), e);
		} catch (IOException e) {
			Activator.logError(e.getMessage(), e);
		}
		return resourcePath;
	}
	
}

