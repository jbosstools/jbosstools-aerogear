package org.jboss.tools.vpe.cordovasim.eclipse.launch.internal;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.ResourcesPlugin;
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
		//TODO: validate parameters (move methods from tabs here)
		//TODO: create a class for LC constants
		String rootFolderString = configuration.getAttribute(CordovaSimLaunchConstants.ROOT_FOLDER, "");
		String startPageString = configuration.getAttribute(CordovaSimLaunchConstants.START_PAGE, "");
		int port = configuration.getAttribute(CordovaSimLaunchConstants.PORT, 0);
		
		IContainer root = ResourcesPlugin.getWorkspace().getRoot();
		IContainer rootFolder = (IContainer) root.findMember(rootFolderString);
		
		CordovaSimLauncher.launchCordovaSim(
				rootFolder.getFullPath().toString(),
				startPageString,
				port);//TODO: NPE etc.
	}
}
