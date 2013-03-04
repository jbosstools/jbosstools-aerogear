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
package org.jboss.tools.aerogear.hybrid.ios.core.simulator;

import org.jboss.tools.aerogear.hybrid.ios.core.IOSCore;
/**
 * Constants used by the iOS Simulator launch configuration type
 * @author Gorkem Ercan
 *
 */
public interface IOSSimulatorLaunchConstants {

	/**
	 * Type id for the IOSSimulator Launch 
	 */
	public static final String LAUNCH_CONFIG_TYPE = "org.jboss.tools.aerogear.hybrid.ios.core.IOSSimulatorLaunchConfigType";

	/**
	 * Build scope, usually a project name in the workspace
	 */
	
	public static final String ATTR_BUILD_SCOPE = IOSCore.PLUGIN_ID + ".ATTR_BUILD_SCOPE";
	
	/**
	 * Device family attribute passed to ios-sim to select iphone, ipad etc..
	 */
	public static final String ATTR_DEVICE_FAMILY = IOSCore.PLUGIN_ID + ".ATTR_DEVICE_FAMILY";
	
	/**
	 * Attribute to enable retina display or not for the simulator
	 */
	public static final String ATTR_USE_RETINA = IOSCore.PLUGIN_ID + ".ATTR_USE_RETINA";
	
	/**
	 * Attribute to use taller device skin
	 */
	public static final String ATTR_USE_TALL = IOSCore.PLUGIN_ID + ".ATTR_USE_TALL";

	/**
	 * Attribute to pass the desired simulator version to be launched
	 */
	public static final String ATTR_SIMULATOR_SDK_DESCRIPTION = IOSCore.PLUGIN_ID + ".ATTR_SIMULATOR_SDK_VERSION";
	
	/**
	 * Value for device family attribute
	 */
	public static final String VAL_DEVICE_FAMILY_IPHONE = "iphone";
	
	/**
	 * Value for device family attribute
	 */	
	public static final String VAL_DEVICE_FAMILY_IPAD = "ipad";
	
}
