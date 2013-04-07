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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidLaunchConstants;
import org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidProjectGenerator;
import org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidSDK;
import org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidSDKManager;
import org.jboss.tools.aerogear.hybrid.ui.launch.HybridProjectLaunchShortcut;

public class AndroidLaunchShortcut extends HybridProjectLaunchShortcut {

	@Override
	protected void validateBuildReady() throws CoreException {
		AndroidSDKManager sdkManager = new AndroidSDKManager();
		List<AndroidSDK> targets = sdkManager.listTargets();
		if(targets == null || targets.isEmpty() ){
			throw new CoreException(new Status(IStatus.ERROR, AndroidUI.PLUGIN_ID, "No targets to build against"));
		}
	}

	@Override
	protected String getLaunchConfigurationTypeID() {
		return AndroidLaunchConstants.ID_LAUNCH_CONFIG_TYPE;
	}

}
