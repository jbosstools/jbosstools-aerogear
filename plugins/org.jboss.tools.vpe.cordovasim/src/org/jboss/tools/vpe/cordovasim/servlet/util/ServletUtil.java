package org.jboss.tools.vpe.cordovasim.servlet.util;

import java.io.File;

public class ServletUtil {

	public static String generateEtagForFile(File file) {
		if (file.exists()) {
			return String.valueOf(file.lastModified());
		}
		return null;
	}
}
