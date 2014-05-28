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
package org.jboss.tools.aerogear.hybrid.ui.internal.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.tools.aerogear.hybrid.core.platform.PlatformConstants;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;

public class HybridMobilePreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	

	public HybridMobilePreferencePage() {
		super(GRID);
		setPreferenceStore(HybridUI.getDefault().getPreferenceStore());
		setDescription("Settings for Hybrid Mobile Application development");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		BooleanFieldEditor savePluginVersions = new BooleanFieldEditor(PlatformConstants.PREF_SHRINKWRAP_PLUGIN_VERSIONS,"Save installed plugin versions to config.xml (shrinkwrap)", this.getFieldEditorParent());
		addField(savePluginVersions);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		
	}
	
}