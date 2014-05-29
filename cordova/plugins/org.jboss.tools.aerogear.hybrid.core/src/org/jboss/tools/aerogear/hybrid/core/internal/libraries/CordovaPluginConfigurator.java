package org.jboss.tools.aerogear.hybrid.core.internal.libraries;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class CordovaPluginConfigurator {

	public static void enableCordovaJSPlugin(IProject project) throws CoreException, IOException {
		org.jboss.tools.jst.jsdt.utils.ConfigUtils.enableCordovaJSPlugin(project);
	}
}
