package org.jboss.tools.aerogear.android.ui;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidDevice;
import org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidLaunchConstants;
import org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidSDK;
import org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidSDKManager;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.ui.launch.HybridProjectLaunchShortcut;

public class AndroidDeviceLaunchShortcut extends HybridProjectLaunchShortcut {

	private AndroidDevice deviceToRun;

	@Override
	protected boolean validateBuildToolsReady() throws CoreException {
		if(!SDKLocationHelper.defineSDKLocationIfNecessary())
			return false;
		AndroidSDKManager sdkManager = new AndroidSDKManager();
		List<AndroidSDK> targets = sdkManager.listTargets();
		if(targets == null || targets.isEmpty() ){
			throw new CoreException(new Status(IStatus.ERROR, AndroidUI.PLUGIN_ID, "No targets to build against"));
		}
		return true;
		
		
	}

	@Override
	protected boolean shouldProceedWithLaunch(HybridProject project) {
		if (getDeviceToRun() == null) {
			MessageDialog
					.openError(PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getShell(),
							"No Device attached",
							"No developer enabled android device is attached to this computer please attach your device and try again.");
			return false;
		}
		return super.shouldProceedWithLaunch(project);
	}
	
	@Override
	protected void updateLaunchConfiguration(ILaunchConfigurationWorkingCopy wc) {
		wc.setAttribute(AndroidLaunchConstants.ATTR_IS_DEVICE_LAUNCH, true);
		AndroidDevice device = getDeviceToRun();
		if(device != null ){
			wc.setAttribute(AndroidLaunchConstants.ATTR_DEVICE_SERIAL, device.getSerialNumber());
		}
	}
	
	@Override
	protected boolean isCorrectLaunchConfiguration(IProject project,
			ILaunchConfiguration config) throws CoreException {
		if(config.getAttribute(AndroidLaunchConstants.ATTR_IS_DEVICE_LAUNCH, false)){
			return super.isCorrectLaunchConfiguration(project, config);
		}
		return false;
	}
	
	@Override
	protected String getLaunchConfigurationTypeID() {
		return AndroidLaunchConstants.ID_LAUNCH_CONFIG_TYPE;
	}

	private AndroidDevice getDeviceToRun() {
		if (deviceToRun == null) {
			try {
				AndroidSDKManager sdkManager = new AndroidSDKManager();
				List<AndroidDevice> devices = sdkManager.listDevices();
				for (AndroidDevice androidDevice : devices) {
					if (!androidDevice.isEmulator()) {
						deviceToRun = androidDevice;
						break;
					}
				}
			} catch (CoreException e) {
				return null;
			}
		}
		return deviceToRun;
	}

	@Override
	protected String getLaunchConfigurationNamePrefix(IProject project) {
		return project.getName() + " (Android Device)";
	}
	
	
	


	
}
