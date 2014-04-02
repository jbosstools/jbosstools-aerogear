package org.jboss.tools.vpe.cordovasim.util;

import java.io.File;

import org.jboss.tools.vpe.browsersim.ui.BrowserSim;

/**
 * @author Konstantin Marmalyukov (kmarmaliykov)
 */
public class ResourcesUtil {
	private static final String RESOURCES_ROOT_FOLDER = "/org/jboss/tools/vpe/cordovasim/resources/"; //$NON-NLS-1$

	public static File getResourceAsFile(String name) {
		if (name.startsWith("/")) { //$NON-NLS-1$
			return new File(BrowserSim.class.getResource(name).getPath());
		} else {
			return new File(BrowserSim.class.getResource(RESOURCES_ROOT_FOLDER + name).getPath());
		}
	}
}
