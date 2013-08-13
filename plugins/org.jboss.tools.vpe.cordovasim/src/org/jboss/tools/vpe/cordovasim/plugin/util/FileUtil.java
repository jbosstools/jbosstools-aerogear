package org.jboss.tools.vpe.cordovasim.plugin.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.tools.vpe.cordovasim.plugin.model.Plugin;


public class FileUtil {
	private static final String FILE = "\"file\": ";
	private static final String ID = "\"id\": ";
	private static final String CLOBBERS = "\"clobbers\": [\n";
	private static final String MERGES = "\"merges\": [\n";
	private static final String PLUGIN_XML = "plugin.xml";
	private static final String BEGINING = "cordova.define('cordova/plugin_list', function(require, exports, module) { \n module.exports = [\n";
	private static final String END = "]\n});";

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

	public static String generateContent(List<Plugin> plugins) {
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
		return BEGINING + pluginContent + END;
	}

}