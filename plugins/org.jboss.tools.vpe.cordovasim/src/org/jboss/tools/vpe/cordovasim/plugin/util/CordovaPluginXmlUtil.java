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
package org.jboss.tools.vpe.cordovasim.plugin.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.tools.vpe.cordovasim.plugin.exception.PluginJsException;
import org.jboss.tools.vpe.cordovasim.plugin.model.Plugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class CordovaPluginXmlUtil {
	public static final String PLATFORM_ANDROID = "android"; //$NON-NLS-1$
	public static final String PLATFORM_IOS = "ios"; //$NON-NLS-1$

	private static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
	private static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
	private static final String ATTRIBUTE_SRC = "src"; //$NON-NLS-1$
	private static final String ATTRIBUTE_TARGET = "target"; //$NON-NLS-1$
	private static final String PLUGINS_DIR = "plugins"; //$NON-NLS-1$
	private static final String TAG_CLOBBERS = "clobbers"; //$NON-NLS-1$
	private static final String TAG_JS_MODULE = "js-module"; //$NON-NLS-1$
	private static final String TAG_MERGES = "merges"; //$NON-NLS-1$
	private static final String TAG_PLATFORM = "platform"; //$NON-NLS-1$
	
	/**
	 * Returns a {@link List} of all plugins for the specific platform from the {@link Document} of the plugin.xml 
	 */
	public static List<Plugin> getPluginsFromDocument(Document document, String platformName) {
		List<Plugin> plugins = new ArrayList<Plugin>();
		String pluginXmlId = getPluginXmlId(document); // plugin.xml id, not plugin id

		List<Element> suitableJsModules = getJsModulesForSpecificPlatform(document, platformName);
		Iterator<Element> iterator = suitableJsModules.iterator();

		while (iterator.hasNext()) {
			Element jsModuleElement = (Element) iterator.next();
			Plugin plugin = createPlugin(jsModuleElement, pluginXmlId);
			if (plugin != null) {
				plugins.add(plugin);
			}
		}

		return plugins;
	}

	/** 
	 *  Returns a {@link List} of all plugins for the specific platfom from the {@link List} of plugin.xml files
	 */
	public static List<Plugin> getPluginsfromFiles(List<File> pluginXmlFiles, String platformName)
			throws PluginJsException {
		List<Plugin> allPlugins = new ArrayList<Plugin>();
		for (File file : pluginXmlFiles) {
			try {
				DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = dBuilder.parse(file);
				doc.getDocumentElement().normalize();
				List<Plugin> pluginsFromDocument = CordovaPluginXmlUtil.getPluginsFromDocument(doc, platformName);
				allPlugins.addAll(pluginsFromDocument);
			} catch (ParserConfigurationException e) {
				throw new PluginJsException(e);
			} catch (IOException e) {
				throw new PluginJsException(e);
			} catch (SAXException e) {
				throw new PluginJsException(e);
			}
		}
		return allPlugins;
	}

	private static Plugin createPlugin(Element jsModuleElement, String pluginXmlId) {
		Plugin plugin = null;
		String moduleName = jsModuleElement.getAttribute(ATTRIBUTE_NAME);
		String src = jsModuleElement.getAttribute(ATTRIBUTE_SRC);

		if ((moduleName != null) && (src != null)) {
			List<Element> mergesList = getChildElementsByName(jsModuleElement, TAG_MERGES);
			List<Element> clobbersList = getChildElementsByName(jsModuleElement, TAG_CLOBBERS);

			List<String> clobbers = getMappers(clobbersList);
			List<String> merges = getMappers(mergesList);

			String pluginId = pluginXmlId + "." + moduleName; // plugin id = pligin.xml.id + moduleName //$NON-NLS-1$
			String file = PLUGINS_DIR + "/" + pluginXmlId + "/" + src; //$NON-NLS-1$ //$NON-NLS-2$

			if (clobbers.size() > 0 || merges.size() > 0) {
				plugin = new Plugin(file, pluginId, clobbers, merges);
			}
		}

		return plugin;
	}

	private static List<Element> getJsModulesForSpecificPlatform(Document doc, String platformName) {
		List<Element> suitableJsModules = new ArrayList<Element>();
		Element documentElement = doc.getDocumentElement();
		NodeList childNodes = documentElement.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;

				if (isJsModuleElement(element)) { // Common js-module for all types of projects (ios, android, wp8 etc.)
					suitableJsModules.add(element);
				} else if (isPlatformElement(element) && (element.getAttribute(ATTRIBUTE_NAME) != null)
						&& element.getAttribute(ATTRIBUTE_NAME).equals(platformName)) { // platform-specific js-module
					List<Element> androidJsModules = getChildElementsByName(element, TAG_JS_MODULE);
					suitableJsModules.addAll(androidJsModules);
				}
			}
		}

		return suitableJsModules;
	}

	private static List<String> getMappers(List<Element> mappersList) {
		List<String> mappers = new ArrayList<String>();
		for (int i = 0; i < mappersList.size(); i++) {
			Element mapperElement = mappersList.get(i);
			String mapper = mapperElement.getAttribute(ATTRIBUTE_TARGET);
			mappers.add(mapper);
		}

		return mappers;
	}

	private static List<Element> getChildElementsByName(Element parentElement, String childElementName) {
		List<Element> elementList = new ArrayList<Element>();
		NodeList childNodes = parentElement.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) node;
				if (childElement.getNodeName().equals(childElementName)) {
					elementList.add(childElement);
				}
			}
		}

		return elementList;
	}

	private static boolean isJsModuleElement(Element element) {
		String elementName = element.getNodeName();
		return elementName.equals(TAG_JS_MODULE);
	}

	private static boolean isPlatformElement(Element element) {
		String elementName = element.getNodeName();
		return elementName.equals(TAG_PLATFORM);
	}

	private static String getPluginXmlId(Document doc) {
		Element element = doc.getDocumentElement();
		return element.getAttribute(ATTRIBUTE_ID);
	}
	
}