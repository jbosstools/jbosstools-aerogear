package org.jboss.tools.aerogear.hybrid.ui.plugins.internal;

import static org.jboss.tools.aerogear.hybrid.ui.plugins.internal.CordovaPluginSelectionPage.PLUGIN_SOURCE_DIRECTORY;
import static org.jboss.tools.aerogear.hybrid.ui.plugins.internal.CordovaPluginSelectionPage.PLUGIN_SOURCE_GIT;
import static org.jboss.tools.aerogear.hybrid.ui.plugins.internal.CordovaPluginSelectionPage.PLUGIN_SOURCE_REGISTRY;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.Wizard;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;

public class CordovaPluginWizard extends Wizard {
	static final String IMAGE_WIZBAN = "/icons/wizban/cordova_plugin_wiz.png";
	private static final String DIALOG_SETTINGS_KEY = "CordovaPluginWizard";
	
	private CordovaPluginSelectionPage pageOne;
	private RegistryConfirmPage pageTwo;
	
	public CordovaPluginWizard() {
		setWindowTitle("Cordova Plugin Discovery");
		setNeedsProgressMonitor(true);
		IDialogSettings workbenchSettings= HybridUI.getDefault().getDialogSettings();
		IDialogSettings section= workbenchSettings.getSection(DIALOG_SETTINGS_KEY);
		setDialogSettings(section);
	}

	@Override
	public boolean performFinish() {
		switch (pageOne.getPluginSourceType()) {
		case PLUGIN_SOURCE_DIRECTORY:
			break;
		case PLUGIN_SOURCE_GIT:
			break;
		case PLUGIN_SOURCE_REGISTRY:
			break;
		default:
			Assert.isTrue(false, "No valid plugin source can be determined");
			break;
		}
		savePageSettings();
		return false;
	}
	
	@Override
	public void addPages() {
		pageOne = new CordovaPluginSelectionPage("Cordova Plugin Selection Page");
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
