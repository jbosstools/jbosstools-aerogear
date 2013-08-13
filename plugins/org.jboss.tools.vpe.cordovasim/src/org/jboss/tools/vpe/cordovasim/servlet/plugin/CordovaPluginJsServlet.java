package org.jboss.tools.vpe.cordovasim.servlet.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.tools.vpe.cordovasim.plugin.model.Plugin;
import org.jboss.tools.vpe.cordovasim.plugin.util.DomUtil;
import org.jboss.tools.vpe.cordovasim.plugin.util.FileUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class CordovaPluginJsServlet extends HttpServlet {
	private String pluginDir;
	
	public CordovaPluginJsServlet(String pluginDir) {
		super();
		this.pluginDir = pluginDir;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<File> pluginXmlFiles = FileUtil.getPluginXmlFiles(new File(pluginDir));
		List<Plugin> allPlugins = new ArrayList<Plugin>();
		for (File file : pluginXmlFiles) {
			try {
				DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = dBuilder.parse(file);
				doc.getDocumentElement().normalize();
				List<Plugin> pluginsFromDocument = DomUtil.getPluginsFromDocument(doc);
				allPlugins.addAll(pluginsFromDocument);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
		}
		String content = FileUtil.generateContent(allPlugins);
		resp.setContentType("application/javascript");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().write(content);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
}
