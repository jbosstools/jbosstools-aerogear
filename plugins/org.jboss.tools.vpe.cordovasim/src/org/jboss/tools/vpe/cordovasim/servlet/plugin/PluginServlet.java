package org.jboss.tools.vpe.cordovasim.servlet.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.tools.vpe.cordovasim.plugin.model.FileIdMapper;
import org.jboss.tools.vpe.cordovasim.servlet.util.ServletUtil;

public class PluginServlet extends HttpServlet {
	private static final String ETAG = "Etag";
	private static final String IF_NONE_MATCH = "If-None-Match";
	private static final String CORDOVA_DEFINE = "cordova.define(";
	private static final String FUNCTION_BEGGINING = "function(require, exports, module) {\n";
	private static final String FUNCTION_END = "});";
	private String pluginDir;
	
	public PluginServlet(String pluginDir) {
		super();
		this.pluginDir = pluginDir;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		File file = new File(pluginDir + pathInfo);

		if (file.exists()) {
			String ifNoneMatchValue = req.getHeader(IF_NONE_MATCH);
			String eTag = ServletUtil.generateEtagForFile(file);

			if ((ifNoneMatchValue != null) && (eTag.equals(ifNoneMatchValue))) {
				resp.setHeader(ETAG, eTag);
				resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			} else {
				String uri = req.getRequestURI();
				String pluginId = generatePluginId(uri);
				if (pluginId == null) {
					resp.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				} else {
					String fileContent = new Scanner(file).useDelimiter("\\A").next();
					String content = CORDOVA_DEFINE + "\"" + pluginId + "\"" + ", " + FUNCTION_BEGGINING + fileContent + FUNCTION_END;
					
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.setContentType("application/javascript");
					resp.setHeader(ETAG, eTag);
					resp.getWriter().write(content);
				}
			}
		} else {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	};

	private String generatePluginId(String uri) {
		return FileIdMapper.getMapper().get(uri.substring(1)); // removing first "/"
	}
	
}
