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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaPluginRegistryManager;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaRegistryPlugin;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaRegistryPluginInfo;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaRegistryPluginVersion;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;

@SuppressWarnings("restriction")
public class RegistryConfirmPage extends WizardPage {

	private CordovaPluginViewer pluginViewer;

	protected RegistryConfirmPage(String pageName) {
		super(pageName);
		setImageDescriptor(HybridUI.getImageDescriptor(HybridUI.PLUGIN_ID, CordovaPluginWizard.IMAGE_WIZBAN));

	}

	@Override
	public void createControl(Composite parent) {
		pluginViewer = new CordovaPluginViewer();
		pluginViewer.setHeaderVisible(false);
		pluginViewer.createControl(parent);
		setControl(pluginViewer.getControl());
	}
	
	void setSelectedPlugins(List<CordovaRegistryPluginInfo> selected){
		CordovaPluginRegistryManager client = new CordovaPluginRegistryManager("http://registry.cordova.io");
		ArrayList<CordovaRegistryPlugin> plugins = new ArrayList<CordovaRegistryPlugin>();
		for (CordovaRegistryPluginInfo cordovaPluginInfo : selected) {
			plugins.add(client.getCordovaPluginInfo(cordovaPluginInfo.getName()));
		}
		pluginViewer.getViewer().setInput(plugins);
	}
	
	public List<CordovaRegistryPluginVersion> getSelectedPluginVersions(){
			IStructuredSelection selection = (IStructuredSelection) pluginViewer.getSelection();
			if(selection == null || selection.isEmpty())
				return Collections.emptyList();
			return selection.toList();
	}

}
