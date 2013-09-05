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
package org.jboss.tools.aerogear.hybrid.ui.plugins.internal;

import static org.jboss.tools.aerogear.hybrid.ui.plugins.internal.CordovaPluginSelectionPage.PLUGIN_SOURCE_DIRECTORY;
import static org.jboss.tools.aerogear.hybrid.ui.plugins.internal.CordovaPluginSelectionPage.PLUGIN_SOURCE_GIT;
import static org.jboss.tools.aerogear.hybrid.ui.plugins.internal.CordovaPluginSelectionPage.PLUGIN_SOURCE_REGISTRY;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginManager;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaPluginRegistryManager;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaRegistryPluginVersion;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;

public class CordovaPluginWizard extends Wizard implements IWorkbenchWizard{
	static final String IMAGE_WIZBAN = "/icons/wizban/cordova_plugin_wiz.png";
	private static final String DIALOG_SETTINGS_KEY = "CordovaPluginWizard";
	
	private CordovaPluginSelectionPage pageOne;
	private RegistryConfirmPage pageTwo;
	private IStructuredSelection initialSelection;
	
	public CordovaPluginWizard() {
		setWindowTitle("Cordova Plugin Discovery");
		setNeedsProgressMonitor(true);
		IDialogSettings workbenchSettings= HybridUI.getDefault().getDialogSettings();
		IDialogSettings section= workbenchSettings.getSection(DIALOG_SETTINGS_KEY);
		setDialogSettings(section);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		initialSelection = selection;
	}

	@Override
	public boolean performFinish() {
		String projectName = pageOne.getProjectName();
		HybridProject project = HybridProject.getHybridProject(projectName);
		CordovaPluginManager pm = new CordovaPluginManager(project);
		
		switch (pageOne.getPluginSourceType()) {
		case PLUGIN_SOURCE_DIRECTORY:
			String directoryName = pageOne.getSelectedDirectory();
			try{
				pm.installPlugin(new File(directoryName));}
				catch(CoreException e){
					//TODO: Error/handling reporting
					return false;
				}
			break;
		case PLUGIN_SOURCE_GIT:
			String gitRepo = pageOne.getSpecifiedGitURL();
			try{
				pm.installPlugin(URI.create(gitRepo),null,null);
				}
				catch(CoreException e){
					//TODO: Error/handling reporting
					return false;
				}			
			break;
		case PLUGIN_SOURCE_REGISTRY:
			List<CordovaRegistryPluginVersion> plugins = pageTwo.getSelectedPluginVersions();
			CordovaPluginRegistryManager regMgr = new CordovaPluginRegistryManager("http://registry.cordova.io");
			for (CordovaRegistryPluginVersion cordovaRegistryPluginVersion : plugins) {
				try {
					pm.installPlugin(regMgr.getInstallationDirectory(cordovaRegistryPluginVersion));
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
			break;
		default:
			Assert.isTrue(false, "No valid plugin source can be determined");
			break;
		}
		savePageSettings();
		return true;
	}
	
	@Override
	public void addPages() {
		pageOne = new CordovaPluginSelectionPage("Cordova Plugin Selection Page", this.initialSelection);
		pageOne.setTitle("Install Cordova Plugin");
		pageOne.setDescription("Discover and Install Cordova Plugins");
		addPage(pageOne);
		pageTwo = new RegistryConfirmPage("Fetch from Registry");
		pageTwo.setTitle("Confirm plugins to be downloaded from registry");
		pageTwo.setDescription("Confirm the plugins to be downloaded and installed from registry or go back to select again.");
		addPage(pageTwo);
	}
	
	RegistryConfirmPage getRegistryConfirmPage(){
		return pageTwo;
	}
	
	private void savePageSettings() {
		IDialogSettings workbenchSettings = HybridUI.getDefault()
				.getDialogSettings();
		IDialogSettings section = workbenchSettings
				.getSection(DIALOG_SETTINGS_KEY);
		if (section == null) {
			section = workbenchSettings.addNewSection(DIALOG_SETTINGS_KEY);
		}
		setDialogSettings(section);
		pageOne.saveWidgetValues();
	}
	

}
