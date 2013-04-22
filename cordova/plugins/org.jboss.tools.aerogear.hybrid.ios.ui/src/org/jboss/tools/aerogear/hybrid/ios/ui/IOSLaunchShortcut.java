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
package org.jboss.tools.aerogear.hybrid.ios.ui;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.aerogear.hybrid.ios.core.simulator.IOSSimulatorLaunchConstants;
import org.jboss.tools.aerogear.hybrid.ios.core.xcode.XCodeBuild;
import org.jboss.tools.aerogear.hybrid.ui.launch.HybridProjectLaunchShortcut;
/**
 * Launch shortcut for launching iOS Simulator. 
 * @see HybridProjectLaunchShortcut
 * 
 * @author Gorkem Ercan
 *
 */
public class IOSLaunchShortcut extends HybridProjectLaunchShortcut{

	@Override
	protected void validateBuildToolsReady() throws CoreException {
		XCodeBuild xcode = new XCodeBuild();
		xcode.version();
	}

	@Override
	protected String getLaunchConfigurationTypeID() {
		return IOSSimulatorLaunchConstants.ID_LAUNCH_CONFIG_TYPE;
	}

}
