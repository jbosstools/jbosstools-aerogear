/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.aerogear.hybrid.android.core.adt;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.jboss.tools.aerogear.hybrid.android.core.AndroidCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.HybridProjectLaunchConfigConstants;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.config.WidgetModel;

public class AndroidLaunchDelegate implements ILaunchConfigurationDelegate2 {

	private File artifactsDir;
	private AndroidDevice device;
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		AndroidSDKManager sdk = new AndroidSDKManager();
	
		HybridProject project = HybridProject.getHybridProject(getProject(configuration));
		WidgetModel model = WidgetModel.getModel(project);
		Widget widget = model.getWidgetForRead();
		String packageName = widget.getId();
		String name = project.getBuildArtifactAppName();

		sdk.installApk(new File(artifactsDir,name+"-debug.apk" ), device.getSerialNumber());
		
		sdk.startApp(packageName+"/."+name, device.getSerialNumber());
		String logcatFilter = configuration.getAttribute(AndroidLaunchConstants.ATTR_LOGCAT_FILTER, AndroidLaunchConstants.VAL_DEFAULT_LOGCAT_FILTER);
		sdk.logcat(logcatFilter,null,null, device.getSerialNumber());
		
	}

	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		return null;
	}

	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		if(monitor.isCanceled() ){
			return false;
		}
		BuildDelegate buildDelegate = new BuildDelegate();
		buildDelegate.init(getProject(configuration), null);
		buildDelegate.buildNow(monitor);
		artifactsDir = buildDelegate.getBinaryDirectory();
		return true;
	}

	@Override
	public boolean finalLaunchCheck(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		monitor.done();
		return true;
	}

	@Override
	public boolean preLaunchCheck(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		// Start ADB Server
		AndroidSDKManager sdk = new AndroidSDKManager();
		sdk.killADBServer();
		sdk.startADBServer();
		
		if(configuration.getAttribute(AndroidLaunchConstants.ATTR_IS_DEVICE_LAUNCH, false)){
			String  serial = configuration.getAttribute(AndroidLaunchConstants.ATTR_DEVICE_SERIAL, (String)null);
			Assert.isNotNull(serial);
			List<AndroidDevice> devices = sdk.listDevices();
			for (AndroidDevice androidDevice : devices) {
				if(serial.equals(androidDevice.getSerialNumber()))
				{
					this.device = androidDevice;
					break;
				}
			}
			if(this.device != null )
			{
				monitor.done();
				return true;
			}else{
				throw new CoreException(new Status(IStatus.ERROR, AndroidCore.PLUGIN_ID, "Device with serial number "+ 
			serial +" is no longer available"));
			}
		}
		
		//Run emulator
		AndroidDevice emulator = getEmulator();
		// Do we have any emulators to run on?
		if ( emulator == null ){
			// No emulators lets start an emulator.
			// Check if we have an AVD
			List<String> avds = sdk.listAVDs();
			if (avds == null || avds.isEmpty()){
				throw new CoreException(new Status(IStatus.ERROR, AndroidCore.PLUGIN_ID, "No Android AVDs are available"));
			}
			String avdName = configuration.getAttribute(AndroidLaunchConstants.ATTR_AVD_NAME, (String)null);
			if(avdName == null || !avds.contains(avdName)){
				avdName = avds.get(0);
			}
			//start the emulator.
			sdk.startEmulator(avdName);
			// wait for it to come online 
			sdk.waitForEmulator();
		}
		this.device = getEmulator();
		if(this.device == null ){// This is non-sense so is adb
			sdk.killADBServer();
			sdk.startADBServer();
			this.device = getEmulator();
		}
		monitor.done();
		return true;
	}
	
	private AndroidDevice getEmulator() throws CoreException{
		AndroidSDKManager sdk = new AndroidSDKManager();
		List<AndroidDevice> devices = sdk.listDevices();
		for (AndroidDevice androidDevice : devices) {
			if ( androidDevice.isEmulator() && androidDevice.getState() == AndroidDevice.STATE_DEVICE )
				return androidDevice;
		}
		return null;
	}
	
	//TODO: duplicated form IOSLaunchDelegate... move both to a common utility.
	private IProject getProject(ILaunchConfiguration configuration){
		try{
			String projectName = configuration.getAttribute(HybridProjectLaunchConfigConstants.ATTR_BUILD_SCOPE, (String)null);
			if(projectName != null ){
				 return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			}
		}catch(CoreException e){
			return null;
		}
		return null;
	}
}
