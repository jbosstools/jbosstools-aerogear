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
package org.jboss.tools.aerogear.hybrid.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.jboss.tools.aerogear.hybrid.core.plugin.model.Plugin;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class CordovaFileUtil {
	private static final String CLOBBERS = "\"clobbers\": [\n";
	private static final String CORDOVA_PLUGINS_JS_BEGINNING = "cordova.define('cordova/plugin_list', function(require, exports, module) { \n module.exports = [\n";
	private static final String CORDOVA_PLUGINS_JS_END = "]\n});";
	private static final String CORDOVA_DEFINE = "cordova.define(";
	private static final String ID = "\"id\": ";
	private static final String MERGES = "\"merges\": [\n";
	private static final String PLUGIN_XML = "plugin.xml";
	private static final String FILE = "\"file\": ";
	private static final String FUNCTION_BEGINNING = "function(require, exports, module) {\n";
	private static final String FUNCTION_END = "});";

	
	/**
	 * Returns {@link List} of plugin.xml files from the "plugins" directory of the hybrid project
	 */
	public static List<File> getPluginXmlFiles(File pluginsDir) {
		List<File> pluginXmlFiles = new ArrayList<File>();
		File[] pluginDirs = pluginsDir.listFiles();
		for (File pluginDir : pluginDirs) {
			if (pluginDir.isDirectory()) {
				File pluginXmlFile = new File(pluginDir, PLUGIN_XML);
				if (pluginDir.exists()) {
					pluginXmlFiles.add(pluginXmlFile);
				}
			}
		}

		return pluginXmlFiles;
	}

	/**
	 * Returns {@link String} representation of the cordova_plugins.js 
	 * 
	 * @param plugins {@link List} of cordova {@link Plugin}
	 */
	public static String generateCordovaPluginsJsContent(List<Plugin> plugins) {
		String pluginContent = "";
		Iterator<Plugin> pluginIterator = plugins.iterator();
		while (pluginIterator.hasNext()) {
			Plugin plugin = pluginIterator.next();
			pluginContent += "\n\t{\n";
			pluginContent += "\t\t" + FILE + "\"" + plugin.getFile() + "\",\n";
			pluginContent += "\t\t" + ID + "\"" + plugin.getId() + "\",\n";
			Iterator<String> mapperIterator = null;
			if (plugin.getClobbers().size() > 0) {
				pluginContent += "\t\t" + CLOBBERS;
				mapperIterator = plugin.getClobbers().iterator();
			} else if (plugin.getMerges().size() > 0) {
				pluginContent += "\t\t" + MERGES;
				mapperIterator = plugin.getMerges().iterator();
			}
			while (mapperIterator.hasNext()) {
				String clobber = mapperIterator.next();
				pluginContent += "\t\t\t\"" + clobber + "\"";
				if (mapperIterator.hasNext()) {
					pluginContent += ",\n";
				} else {
					pluginContent += "\n\t\t]";
				}
			}
			if (pluginIterator.hasNext()) {
				pluginContent += "\n\t},";
			} else {
				pluginContent += "\n\t}\n";
			}
		}
		
		return CORDOVA_PLUGINS_JS_BEGINNING + pluginContent + CORDOVA_PLUGINS_JS_END;
	}
	
	
	/** 
	 * Wraps the plugin's  .js file content in a function 
	 * (adds "cordova.define( pluginId, function(require, exports, module) {...} ) 
	 */
	public static String generatePluginContent(File file, String pluginId) throws FileNotFoundException {
		String content = null;
		if (file.exists()) {
			String fileContent = new Scanner(file).useDelimiter("\\A").next();
			content = CORDOVA_DEFINE + '"' + pluginId + '"' + ", " + FUNCTION_BEGINNING + fileContent + FUNCTION_END;
		}
		
		return content;
	}

}