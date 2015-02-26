/*******************************************************************************
 * Copyright (c) 2014-2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cordovasim;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.browsersim.ui.model.preferences.CommonPreferences;
import org.jboss.tools.browsersim.ui.model.preferences.SpecificPreferences;
import org.jboss.tools.browsersim.ui.model.preferences.SpecificPreferencesStorage;
import org.jboss.tools.browsersim.ui.ManageDevicesDialog;
import org.jboss.tools.cordovasim.model.preferences.CordavaSimSpecificPreferencesStorage;
import org.jboss.tools.cordovasim.model.preferences.CordovaSimSpecificPreferences;

/**
 * @author Konstantin Marmalyukov (kmarmaliykov)
 * @author Ilya Buziuk (ibuziuk)
 */
public class CordovaSimManageDeviceDialog extends ManageDevicesDialog {
	/** @see org.jboss.tools.browsersim.eclipse.callbacks.RestartCallback */
	private static final String CORDOVASIM_RESTART_COMMAND = "org.jboss.tools.cordavasim.command.restart:"; //$NON-NLS-1$
	private static final String PARAMETER_DELIMITER = "_PARAMETER_DELIMITER_"; //$NON-NLS-1$
	
	private Button DontShowUnsupportedPluginsCheckBox; // JBIDE-17588 CordovaSim: Need to replace "I Haz CheeseBurger" pop-up
	private boolean showUnsupportedPlugins;
	
	public CordovaSimManageDeviceDialog(Shell parent, int style, CommonPreferences oldCommonPreferences,
			SpecificPreferences oldSpecificPreferences, String currentUrl) {
		super(parent, style, oldCommonPreferences, oldSpecificPreferences, currentUrl);
		this.showUnsupportedPlugins = ((CordovaSimSpecificPreferences) oldSpecificPreferences).showUnsupportedPluginsPopUp();
	}

	@Override
	protected SpecificPreferencesStorage getSpecificPreferencesStorage() {
		return CordavaSimSpecificPreferencesStorage.INSTANCE;
	}
	
	@Override
	protected void loadDefaultPreferences() {
		CordovaSimSpecificPreferences defaultPreferences = (CordovaSimSpecificPreferences) getSpecificPreferencesStorage().loadDefault();
		showUnsupportedPlugins = defaultPreferences.showUnsupportedPluginsPopUp();
		super.loadDefaultPreferences();
	}
	
	@Override
	protected void createContents() {
		super.createContents();
		Group unsupportedPluginsGroup = new Group(settingsComposite, SWT.NONE);
		unsupportedPluginsGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		unsupportedPluginsGroup.setText(Messages.CordovaSimManageDevicesDialog_UNSUPPORTED_PLUGINS);
		unsupportedPluginsGroup.setLayout(new GridLayout(2, false));
		DontShowUnsupportedPluginsCheckBox = new Button(unsupportedPluginsGroup, SWT.CHECK);
		DontShowUnsupportedPluginsCheckBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		DontShowUnsupportedPluginsCheckBox.setText(Messages.CordovaSimManageDevicesDialog_UNSUPPORTED_PLUGINS_CHECKBOX);
	}
	
	@Override
	public void updateDevices() {
		super.updateDevices();
		DontShowUnsupportedPluginsCheckBox.setSelection(!showUnsupportedPlugins);
	}
		
	@Override
	protected SpecificPreferences create(String selectedDeviceId, boolean useSkins, boolean enableLiveReload, int liveReloadPort, boolean enableTouchEvents, boolean isJavaFx) {
		return new CordovaSimSpecificPreferences(selectedDeviceId, useSkins, enableLiveReload, liveReloadPort, enableTouchEvents,
				oldSpecificPreferences.getOrientationAngle(), getParent().getLocation(),
				((CordovaSimSpecificPreferences) oldSpecificPreferences).getCordovaBrowserLocation(),
				((CordovaSimSpecificPreferences) oldSpecificPreferences).getCordovaBrowserSize(), isJavaFx, 
				!DontShowUnsupportedPluginsCheckBox.getSelection(), ((CordovaSimSpecificPreferences) oldSpecificPreferences).getRipplePreferences());
	}
	
	@Override
	protected void sendRestartCommand() {
		CordovaSimArgs.setRestartRequired(true);
		System.out.println(generateRestartCommand()); // Fire restart command @see CordovaSimRestartCallback.java	
	}
	
	private String generateRestartCommand() {
		String command = CORDOVASIM_RESTART_COMMAND + PARAMETER_DELIMITER + CordovaSimArgs.getRootFolder()
				+ PARAMETER_DELIMITER + CordovaSimArgs.getHomeUrl() + PARAMETER_DELIMITER
				+ "-version" + PARAMETER_DELIMITER + CordovaSimArgs.getCordovaVersion();  //$NON-NLS-1$
		
		String proxy = CordovaSimArgs.getProxy();
		if (proxy != null) {
			command += PARAMETER_DELIMITER + "-proxy" + PARAMETER_DELIMITER + proxy; //$NON-NLS-1$
		}
		return command;
	}
	
}