package org.jboss.tools.aerogear.hybrid.ui.plugins.internal;

import java.util.List;

import org.eclipse.equinox.internal.p2.ui.discovery.util.ControlListItem;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPlugin;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginVersion;

@SuppressWarnings("restriction")
public class CordovaPluginItem extends ControlListItem<CordovaPlugin> {
	private static final String LABEL_LATEST_VERSION = "latest";
	private final CordovaPluginWizardResources resources;
	private Label description;
	private Label nameLabel;
	private Label licenseLbl;
	private Combo versionCombo;

	public CordovaPluginItem(Composite parent, int style, CordovaPlugin element, CordovaPluginWizardResources resources ) {
		super(parent, style, element);
		this.resources = resources;
		createContent();
	}

	@Override
	protected void refresh() {
		// TODO Auto-generated method stub

	}
	
	private void createContent(){
		GridLayout layout = new GridLayout(3, false);
		layout.marginLeft = 7;
		layout.marginTop = 2;
		layout.marginBottom = 2;
		setLayout(layout);

		Composite versionContainer = new Composite(this, SWT.INHERIT_NONE);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.BEGINNING).span(1, 2).applyTo(versionContainer);
		GridLayoutFactory.fillDefaults().spacing(1, 1).numColumns(2).applyTo(versionContainer);
		
		versionCombo = new Combo(versionContainer, SWT.READ_ONLY);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(versionCombo);
		versionCombo.add(LABEL_LATEST_VERSION);
		versionCombo.select(0);
		List<CordovaPluginVersion> versions = getData().getVersions();
		for ( CordovaPluginVersion cordovaPluginVersion : versions) {
			versionCombo.add(cordovaPluginVersion.getVersionNumber());
		}
		
		nameLabel = new Label(this, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.BEGINNING, SWT.CENTER).applyTo(nameLabel);
		nameLabel.setFont(resources.getSmallHeaderFont());
		nameLabel.setText(getData().getName());
		
		description = new Label(this, SWT.NULL | SWT.WRAP);
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).hint(100, SWT.DEFAULT).applyTo(description);
		
		final Composite detailsContainer = new Composite(this, SWT.INHERIT_NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER ).span(3, 1).applyTo(detailsContainer);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(detailsContainer);
		licenseLbl = new Label(detailsContainer, SWT.NONE);
		licenseLbl.setFont(resources.getSubTextFont());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(licenseLbl);
		updateValues();


		
	}

	private void updateValues() {
		String version = versionCombo.getText();
		if(version.equals(LABEL_LATEST_VERSION)){
			version = getData().getLatestVersion();
		}
		List<CordovaPluginVersion> versions = getData().getVersions();
		for ( CordovaPluginVersion cordovaPluginVersion : versions) {
			if(cordovaPluginVersion.getVersionNumber().equals(version)){
				setDescriptionText(cordovaPluginVersion.getDescription());
				licenseLbl.setText("License:"+cordovaPluginVersion.getLicense());
			}
		}
		
	}

	private void setDescriptionText(String descriptionText) {
		int maxDescriptionLength = 162;
		if (descriptionText == null) {
			descriptionText = ""; //$NON-NLS-1$
		}
		if (descriptionText.length() > maxDescriptionLength) {
			descriptionText = descriptionText.substring(0, maxDescriptionLength);
		}
		description.setText(descriptionText.replaceAll("(\\r\\n)|\\n|\\r", " ")); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
