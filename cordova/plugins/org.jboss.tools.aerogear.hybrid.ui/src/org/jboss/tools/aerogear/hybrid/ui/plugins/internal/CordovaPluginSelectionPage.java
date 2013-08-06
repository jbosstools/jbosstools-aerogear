package org.jboss.tools.aerogear.hybrid.ui.plugins.internal;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;

public class CordovaPluginSelectionPage extends WizardPage {
	private static final String IMAGE_WIZBAN = "/icons/wizban/cordova_plugin_wiz.png";

	protected CordovaPluginSelectionPage(String pageName) {
		super(pageName);
		setImageDescriptor(HybridUI.getImageDescriptor(HybridUI.PLUGIN_ID, IMAGE_WIZBAN));
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("restriction")
	@Override
	public void createControl(Composite parent) {
		CordovaPlugingCatalogViewer viewer = new CordovaPlugingCatalogViewer();
		viewer.createControl(parent);
		setControl(viewer.getControl());
	}

}
