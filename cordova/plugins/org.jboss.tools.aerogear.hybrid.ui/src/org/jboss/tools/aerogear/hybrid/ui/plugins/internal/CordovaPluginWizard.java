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
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
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
	private List<CordovaRegistryPluginVersion> plugins;
	private HybridProject fixedProject;
	
	private class PluginInstallOperation extends WorkspaceModifyOperation{
		
		private CordovaPluginManager pm;
		private int opType;
		private File dir;
		private URI gitRepo;
		private List<CordovaRegistryPluginVersion> plugins;
		
		private PluginInstallOperation(CordovaPluginManager pm){
			this.pm = pm;
		}
		
		public PluginInstallOperation(File directory, CordovaPluginManager pm ){
			this(pm);
			this.dir = directory;
			opType = PLUGIN_SOURCE_DIRECTORY;
		}
		
		public PluginInstallOperation(URI gitRepo, CordovaPluginManager pm ){
			this(pm);
			this.gitRepo = gitRepo;
			opType = PLUGIN_SOURCE_GIT;
		}
		
		public PluginInstallOperation(List<CordovaRegistryPluginVersion> plugins, CordovaPluginManager pm ){
			this(pm);
			this.plugins = plugins;
			opType = PLUGIN_SOURCE_REGISTRY;
		}

		@Override
		protected void execute(IProgressMonitor monitor) throws CoreException,
				InvocationTargetException, InterruptedException {
			
			switch (opType){
			case PLUGIN_SOURCE_DIRECTORY:
				pm.installPlugin(this.dir,monitor);
				break;
			case PLUGIN_SOURCE_GIT:
				pm.installPlugin(this.gitRepo,null,null,monitor );
				break;
			case PLUGIN_SOURCE_REGISTRY:
				CordovaPluginRegistryManager regMgr = new CordovaPluginRegistryManager(CordovaPluginRegistryManager.DEFAULT_REGISTRY_URL);
				for (CordovaRegistryPluginVersion cordovaRegistryPluginVersion : plugins) {
					pm.installPlugin(regMgr.getInstallationDirectory(cordovaRegistryPluginVersion,monitor),monitor);
				}
				break;
			default:
				Assert.isTrue(false, "No valid plugin source can be determined");
				break;
			}
		}
	}
	
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
	/**
	 * Causes the wizard to work with a fixed project, and does not enable
	 * users to select a different project to operate on.
	 * @param project
	 */
	public void init(HybridProject project){
		this.fixedProject = project;
	}
	
	@Override
	public boolean performFinish() {	
		HybridProject project = HybridProject.getHybridProject(pageOne.getProjectName());
		if(project == null )
			return false;
		CordovaPluginManager pm = new CordovaPluginManager(project);
		PluginInstallOperation op = null;
		switch (pageOne.getPluginSourceType()) {
		case PLUGIN_SOURCE_DIRECTORY:
			File directory = new File(pageOne.getSelectedDirectory());
			op=new PluginInstallOperation(directory, pm);
			break;
		case PLUGIN_SOURCE_GIT:
			URI uri = URI.create(pageOne.getSpecifiedGitURL());
			op = new PluginInstallOperation(uri, pm);
			break;
		case PLUGIN_SOURCE_REGISTRY:
			List<CordovaRegistryPluginVersion> plugins = pageTwo.getSelectedPluginVersions();
			op = new PluginInstallOperation(plugins, pm);
			break;
		default:
			Assert.isTrue(false, "No valid plugin source can be determined");
		}
		
		try {
			getContainer().run(true, true, op);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		savePageSettings();
		return true;
	}
	
	@Override
	public void addPages() {
		if(fixedProject == null ){
			pageOne = new CordovaPluginSelectionPage("Cordova Plugin Selection Page", this.initialSelection);
		}else{
			pageOne = new CordovaPluginSelectionPage("Cordova Plugin Selection Page", fixedProject);
		}
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
	
	HybridProject getFixedProject(){
		return fixedProject;
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
