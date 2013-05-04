/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *       Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.aerogear.android.ui;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidLaunchConstants;
import org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidSDK;
import org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidSDKManager;
import org.jboss.tools.aerogear.hybrid.ui.launch.HybridProjectLaunchShortcut;

public class AndroidEmulatorLaunchShortcut extends HybridProjectLaunchShortcut {

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
	protected String getLaunchConfigurationTypeID() {
		return AndroidLaunchConstants.ID_LAUNCH_CONFIG_TYPE;
	}

}
