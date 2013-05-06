package org.jboss.tools.vpe.cordovasim.eclipse.launch.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.jboss.tools.vpe.cordovasim.eclipse.launch.CordovaSimLaunchConstants;

public class CordovaSimLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		String projectString = configuration.getAttribute(CordovaSimLaunchConstants.PROJECT, (String) null);
		String rootFolderString = configuration.getAttribute(CordovaSimLaunchConstants.ROOT_FOLDER, (String) null);
		String startPageString = configuration.getAttribute(CordovaSimLaunchConstants.START_PAGE, (String) null);
		Integer port = null;
		if (configuration.hasAttribute(CordovaSimLaunchConstants.PORT)) {
			port = configuration.getAttribute(CordovaSimLaunchConstants.PORT, 0);
		}
		
		CordovaSimLauncher.launchCordovaSim(projectString, rootFolderString, startPageString, port);//TODO: NPE etc.
	}
}
