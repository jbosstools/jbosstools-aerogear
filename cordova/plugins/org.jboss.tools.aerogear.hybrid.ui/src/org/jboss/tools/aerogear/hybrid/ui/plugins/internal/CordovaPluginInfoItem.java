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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaRegistryPluginInfo;

@SuppressWarnings("restriction")
public class CordovaPluginInfoItem extends BaseCordovaPluginItem<CordovaRegistryPluginInfo>{

	private final CordovaPluginCatalogViewer viewer;
	private Button checkbox;
	private boolean installed;

	public CordovaPluginInfoItem(Composite parent, CordovaRegistryPluginInfo element, CordovaPluginWizardResources resources, CordovaPluginCatalogViewer viewer, boolean installed) {
		super(parent,element,resources);
		this.viewer = viewer;
		this.installed = installed;
		createContent();
	}

	@Override
	protected void refresh() {
		if(checkbox != null ){
			viewer.modifySelection(getData(), checkbox.getSelection());
		}
	}
	
	private void createContent(){
		GridLayout layout = new GridLayout(3, false);
		layout.marginLeft = 7;
		layout.marginTop = 2;
//		layout.marginBottom = 2;
		setLayout(layout);
		
	

		final Composite checkboxContainer = new Composite(this, SWT.INHERIT_NONE);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.BEGINNING).span(1, 2).applyTo(checkboxContainer);
		GridLayoutFactory.fillDefaults().spacing(1, 1).numColumns(2).applyTo(checkboxContainer);

		checkbox = new Button(checkboxContainer, SWT.CHECK | SWT.INHERIT_FORCE);
		checkbox.setText(" "); //$NON-NLS-1$
		checkbox.setEnabled(!installed);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(checkbox);
		checkbox.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				viewer.modifySelection(getData(), checkbox.getSelection());
			}
		});

		final Label nameLabel = new Label(this, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.BEGINNING, SWT.CENTER).applyTo(nameLabel);
		nameLabel.setFont(resources.getSmallHeaderFont());
		String name = null;
		if(installed){
			name = NLS.bind("{0} (installed)", getData().getName());
		}else{
			name = getData().getName();
		}
		nameLabel.setText(name);
		
		final Label maintainerLabel = new Label(this, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.END, SWT.CENTER).applyTo(maintainerLabel);
		maintainerLabel.setFont(resources.getSubTextFont());
		Map<String, String> maintainers = getData().getMaintainers();
		Set<String> keys = maintainers.keySet();
		StringBuilder maints = new StringBuilder("By: ");
		StringBuilder maintToolTipText = new StringBuilder();
		for (String mail : keys) {
			maints.append(maintainers.get(mail)).append(" ");
			maintToolTipText.append(NLS.bind("{0} <{1}>", new String[]{maintainers.get(mail),mail}));
			if(keys.size() >1){
				maintToolTipText.append("\n");
			}
		}
		maintainerLabel.setText(maints.toString());
		maintainerLabel.setToolTipText(maintToolTipText.toString());
		
		
		final Label description = new Label(this, SWT.NULL | SWT.WRAP);
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).hint(100, SWT.DEFAULT).applyTo(description);
		String descriptionText = getData().getDescription();
		int maxDescriptionLength = 162;
		if (descriptionText == null) {
			descriptionText = ""; //$NON-NLS-1$
		}
		if (descriptionText.length() > maxDescriptionLength) {
			descriptionText = descriptionText.substring(0, maxDescriptionLength);
		}
		description.setText(descriptionText.replaceAll("(\\r\\n)|\\n|\\r", " ")); //$NON-NLS-1$ //$NON-NLS-2$
		
		final Label versionLbl = new Label(this, SWT.NONE);
		versionLbl.setFont(resources.getSubTextFont());
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(versionLbl);
		versionLbl.setText("Latest: "+ getData().getLatestVersion());
		
		List<String> keywords = getData().getKeywords();
		if (keywords != null) {
			int colSize = keywords == null ? 1 : keywords.size() + 1;
			Composite keywordsContainer = new Composite(this,
					SWT.INHERIT_NONE);
			GridDataFactory.swtDefaults().align(SWT.END, SWT.BEGINNING)
					.span(1, 1).applyTo(keywordsContainer);
			GridLayoutFactory.fillDefaults().spacing(1, 1).numColumns(colSize)
					.applyTo(keywordsContainer);

			final Label keywordLbl = new Label(keywordsContainer, SWT.NONE);
			keywordLbl.setFont(resources.getSubTextFont());
			keywordLbl.setText("keywords:");
			
			for (String string : keywords) {
				final Link hyperlink = new Link(keywordsContainer, SWT.NONE);
				hyperlink.setFont(resources.getSubTextFont());
				GridDataFactory.fillDefaults().grab(false, false)
						.applyTo(hyperlink);
				hyperlink.setText(NLS.bind("<a >{0}</a>", string));
				hyperlink.setData(string);
				hyperlink.addListener(SWT.Selection, new Listener() {
					
					@Override
					public void handleEvent(Event event) {
						Link link = (Link)event.widget;
						String keyword = (String) link.getData();
						viewer.applyFilter(keyword);
					}
				});
			}
		}
		
	}
	
	@Override
	public void updateColors(int index) {
		super.updateColors(index);
		if(installed){
			setForeground(resources.getDisabledColor());
		}else{
			setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		}
	}
	
	

}
