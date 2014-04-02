package org.jboss.tools.vpe.cordovasim.eclipse.launch.internal;

import org.eclipse.osgi.util.NLS;

/**
 * @author kmarmaliykov
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.vpe.cordovasim.eclipse.launch.internal.messages"; //$NON-NLS-1$
	public static String CordovaSimLaunchConfigurationTab_BROWSE;
	public static String CordovaSimLaunchConfigurationTab_CHOOSE_ROOT_FOLDER;
	public static String CordovaSimLaunchConfigurationTab_CHOOSE_START_PAGE_FILE;
	public static String CordovaSimLaunchConfigurationTab_MAIN;
	public static String CordovaSimLaunchConfigurationTab_NO_ROOT_FOLDER;
	public static String CordovaSimLaunchConfigurationTab_NO_START_PAGE;
	public static String CordovaSimLaunchConfigurationTab_PORT;
	public static String CordovaSimLaunchConfigurationTab_PROJECT;
	public static String CordovaSimLaunchConfigurationTab_PROJECT_SELECTION;
	public static String CordovaSimLaunchConfigurationTab_ROOT_FOLDER;
	public static String CordovaSimLaunchConfigurationTab_ROOT_FOLDER_SELECTION;
	public static String CordovaSimLaunchConfigurationTab_SELECT_PROJECT;
	public static String CordovaSimLaunchConfigurationTab_SERVER_PORT;
	public static String CordovaSimLaunchConfigurationTab_START_PAGE;
	public static String CordovaSimLaunchConfigurationTab_START_PAGE_SELECTION;
	public static String CordovaSimLaunchConfigurationTab_USE_DEFAULT;
	public static String CordovaSimLauncher_CANNOT_FIND_ROOT_FOLDER;
	public static String CordovaSimLauncher_CANNOT_RUN_CORDOVASIM;
	public static String CordovaSimLauncher_CORDOVASIM;
	public static String CordovaSimLaunchParametersUtil_INVALID_PORT;
	public static String CordovaSimLaunchParametersUtil_INVALID_ROOT_FOLDER_PATH;
	public static String CordovaSimLaunchParametersUtil_INVALID_START_PAGE_PATH;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
