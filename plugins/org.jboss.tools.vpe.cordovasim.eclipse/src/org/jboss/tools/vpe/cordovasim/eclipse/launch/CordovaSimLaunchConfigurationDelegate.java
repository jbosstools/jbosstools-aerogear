package org.jboss.tools.vpe.cordovasim.eclipse.launch;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.jboss.tools.vpe.cordovasim.eclipse.Activator;

public class CordovaSimLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		//TODO: validate parameters (move methods from tabs here)
		//TODO: create a class for LC constants
		String rootFolderString = configuration.getAttribute(Activator.PLUGIN_ID + ".ROOT_FOLDER", "");
		String startPageString = configuration.getAttribute(Activator.PLUGIN_ID + ".START_PAGE", "");
		int port = configuration.getAttribute(Activator.PLUGIN_ID + ".PORT", 4400);
		
		IContainer root = ResourcesPlugin.getWorkspace().getRoot();
		IContainer rootFolder = (IContainer) root.findMember(rootFolderString);
		IResource startPage = rootFolder.findMember(startPageString);
		CordovaSimLauncher.launchCordovaSim("file:///" + startPage.getLocation().toString());//NPE etc.
	}

}
