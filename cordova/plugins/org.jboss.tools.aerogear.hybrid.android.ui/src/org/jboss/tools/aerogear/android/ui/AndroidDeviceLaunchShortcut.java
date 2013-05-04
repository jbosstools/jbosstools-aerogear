package org.jboss.tools.aerogear.android.ui;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
		AndroidDevice deviceToRun = getDeviceToRun();

		if (deviceToRun == null) {
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
		AndroidDevice device = getDeviceToRun();
		if(device != null ){
			wc.setAttribute(AndroidLaunchConstants.ATTR_DEVICE_SERIAL, device.getSerialNumber());
		}
	}
	
	@Override
	protected String getLaunchConfigurationTypeID() {
		return AndroidLaunchConstants.ID_LAUNCH_CONFIG_TYPE;
	}

	private AndroidDevice getDeviceToRun() {
		try {
			AndroidSDKManager sdkManager = new AndroidSDKManager();
			List<AndroidDevice> devices = sdkManager.listDevices();
			AndroidDevice deviceToRun = null;
			if (devices != null) {
				for (AndroidDevice androidDevice : devices) {
					if (!androidDevice.isEmulator()) {
						deviceToRun = androidDevice;
						break;
					}
				}
			}
			return deviceToRun;
		} catch (CoreException e) {
			return null;
		}

	}
	
	
	


	
}
