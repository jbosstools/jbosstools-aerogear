package org.jboss.tools.vpe.cordovasim.plugin.model;

import java.util.HashMap;
import java.util.Map;

public class FileIdMapper {
	private static final Map<String, String> INSTANCE = new HashMap<String, String>(); // Map with plugin's "file" and "id" 
	
	private FileIdMapper() {
	}
	
	public static Map<String, String> getMapper() {
		return INSTANCE;
	}
	
}
