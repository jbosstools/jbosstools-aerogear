package org.jboss.tools.aerogear.hybrid.ui.plugins.internal;

import org.eclipse.jface.wizard.Wizard;

public class CordovaPluginWizard extends Wizard {
	
	private CordovaPluginSelectionPage pageOne;
	
	public CordovaPluginWizard() {
		setWindowTitle("Cordova Plugin Discovery");
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void addPages() {
		pageOne = new CordovaPluginSelectionPage("Cordova Plugin Selection Page");
		pageOne.setTitle("Install Cordova Plugin");
		pageOne.setDescription("Discover and Install Cordova Plugins");
		addPage(pageOne);
	}

}
