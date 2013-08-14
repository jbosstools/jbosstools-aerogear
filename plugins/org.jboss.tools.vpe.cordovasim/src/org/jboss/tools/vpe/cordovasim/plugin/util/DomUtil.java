package org.jboss.tools.vpe.cordovasim.plugin.util;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.tools.vpe.cordovasim.plugin.model.Plugin;
import org.jboss.tools.vpe.cordovasim.plugin.model.FileIdMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class DomUtil {
	private static final String ID = "id";
	private static final String JS_MODULE = "js-module";
	private static final String SRC_ATTRIBUTE = "src";
	private static final String NAME_ATTRIBUTE = "name";	
	private static final String MERGES = "merges";
	private static final String CLOBBERS = "clobbers";
	private static final String TARGET_ATTRIBUTE = "target";
	private static final String ANDROID = "android";
	private static final String PLUGINS_DIR = "plugins";

	public static List<Plugin> getPluginsFromDocument(Document doc) {
		List<Plugin> plugins = new ArrayList<Plugin>();
		String pluginXmlId = getId(doc); // plugin.xml id, not plugin id
		NodeList nodeList = doc.getElementsByTagName(JS_MODULE);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (isAndroidJsModule(node)) {
					Element jsModuleElement = (Element) node;
					Plugin plugin = createPlugin(jsModuleElement, pluginXmlId);
					if (plugin != null) {
						plugins.add(plugin);
					}
				}
			}
		}
		return plugins;
	}
	
	public static List<Plugin> getPluginsfromFiles(List<File> pluginXmlFiles) {
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
		return allPlugins;
	}
	
	private static Plugin createPlugin(Element jsModuleElement, String pluginXmlId) {
		Plugin plugin = null;
		String moduleName = jsModuleElement.getAttribute(NAME_ATTRIBUTE);
		String src = jsModuleElement.getAttribute(SRC_ATTRIBUTE);

		if ((moduleName != null) && (src != null)) {
			NodeList clobberList = jsModuleElement.getElementsByTagName(CLOBBERS);
			NodeList mergeList = jsModuleElement.getElementsByTagName(MERGES);
			
			List<String> clobbers = getMappers(clobberList);
			List<String> merges = getMappers(mergeList);
			
			String pluginId = pluginXmlId + "." + moduleName; // plugin id = pligin.xml.id + moduleName
			String file = PLUGINS_DIR + "/" + pluginXmlId + "/" + src;
			
			if (clobbers.size() > 0 || merges.size() > 0) {
				plugin = new Plugin(file, pluginId, clobbers, merges);
				FileIdMapper.getMapper().put(file, pluginId); // Map with plugin's file and id is needed in PluginServlet
			}
		}
		return plugin;
	}
	
	
	private static List<String> getMappers(NodeList clobberList) {
		List<String> mappers = new ArrayList<String>();
		for (int j = 0; j < clobberList.getLength(); j++) {
			Node mapperNode = clobberList.item(j);
			if (mapperNode.getNodeType() == Node.ELEMENT_NODE) {
				Element cloberElement = (Element) mapperNode;
				String clober = cloberElement.getAttribute(TARGET_ATTRIBUTE);
				mappers.add(clober);
			}
		}
		return mappers;
	}

	private static boolean isAndroidJsModule(Node node) {
		Node parentNode = node.getParentNode();
		if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
			Element parentElement = (Element) parentNode;
			String platformName = parentElement.getAttribute(NAME_ATTRIBUTE);
			if (platformName == null || platformName.trim().equals("") || platformName.equals(ANDROID)) { // HACK - need to do this better
				return true;
			}
		}
		return false;
	}

	private static String getId(Document doc) {
		Element element = doc.getDocumentElement();
		return element.getAttribute(ID);
	}
	
}