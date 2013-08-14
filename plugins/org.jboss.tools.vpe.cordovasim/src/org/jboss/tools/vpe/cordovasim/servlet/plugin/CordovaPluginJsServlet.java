package org.jboss.tools.vpe.cordovasim.servlet.plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.tools.vpe.cordovasim.plugin.model.Plugin;
import org.jboss.tools.vpe.cordovasim.plugin.util.DomUtil;
import org.jboss.tools.vpe.cordovasim.plugin.util.FileUtil;
import org.jboss.tools.vpe.cordovasim.servlet.util.ServletUtil;


public class CordovaPluginJsServlet extends HttpServlet {
	private static final String JS_MIME_TYPE = "application/javascript";
	private static final String IF_NONE_MATCH = "If-None-Match";
	private static final String ETAG = "Etag";
	private String pluginDir;

	public CordovaPluginJsServlet(String pluginDir) {
		super();
		this.pluginDir = pluginDir;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String ifNoneMatchValue = req.getHeader(IF_NONE_MATCH);
		String eTag = ServletUtil.generateEtagForFile(new File(pluginDir));

		if ((ifNoneMatchValue != null) && (eTag.equals(ifNoneMatchValue))) {
			resp.setHeader(ETAG, eTag);
			resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		} else {
			List<File> pluginXmlFiles = FileUtil.getPluginXmlFiles(new File(pluginDir)); // get all plugin.xml files from the "plugins" folder of the hybrid project 
			List<Plugin> allPlugins = DomUtil.getPluginsfromFiles(pluginXmlFiles); // generate Plugins from the plugin.xml files
			
			String content = FileUtil.generateContent(allPlugins);
			
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType(JS_MIME_TYPE);
			resp.setHeader(ETAG, eTag);
			resp.getWriter().write(content);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
