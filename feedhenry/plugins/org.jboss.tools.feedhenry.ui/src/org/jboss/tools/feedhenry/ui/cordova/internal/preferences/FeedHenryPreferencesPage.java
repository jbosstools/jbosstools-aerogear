/*******************************************************************************
 * Copyright (c) 2014,2015 Red Hat, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * 	Contributors:
 * 		 Red Hat Inc. - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.jboss.tools.feedhenry.ui.cordova.internal.preferences;

import static org.jboss.tools.feedhenry.ui.cordova.internal.preferences.FHPreferenceConstants.PREF_API_KEY;
import static org.jboss.tools.feedhenry.ui.cordova.internal.preferences.FHPreferenceConstants.PREF_TARGET_URL;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.tools.feedhenry.ui.FHPlugin;
/**
 * 
 * Properties for connecting to FeedHenry
 * 
 * @author Gorkem Ercan
 *
 */
public class FeedHenryPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage{
	
	
	private static final String PAGE_DESCRIPTION = "FeedHendry connection preferences";
	private static final String LABEL_API_KEY = "API Key:";
	private static final String LABEL_TARGET_URL = "Target URL:";
	private static final String MESSAGE_TARGET_URL = "https://my.feedhenry.url";
	
	private class URLFieldEditor extends StringFieldEditor{
		public URLFieldEditor(String property, String label, Composite parent) {
			super(property, label, parent);
		}
		
		@Override
		protected boolean doCheckState() {
			final String url = getStringValue();
			if(url != null && !url.isEmpty()){
				try {
					new URL(url);
				} catch (MalformedURLException e) {
					return false;
				}
			}
			return true;
		}
	}

	public FeedHenryPreferencesPage() {
		super(GRID);
		setDescription(PAGE_DESCRIPTION);
		setPreferenceStore(FHPlugin.getDefault().getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		final Composite parent = getFieldEditorParent();
		parent.setFont(getControl().getFont());
		
		// FH target URL
		final URLFieldEditor targetUrlField = new URLFieldEditor(PREF_TARGET_URL, LABEL_TARGET_URL, parent);
		targetUrlField.setValidateStrategy(StringFieldEditor.VALIDATE_ON_FOCUS_LOST);
		targetUrlField.setPreferenceStore(getPreferenceStore());
		targetUrlField.setPage(this);
		targetUrlField.load();
		targetUrlField.setPropertyChangeListener(this);
		targetUrlField.getTextControl(parent).setMessage(MESSAGE_TARGET_URL);
		addField(targetUrlField);
		
		// API key
		final StringFieldEditor apikeyField = new StringFieldEditor(PREF_API_KEY, LABEL_API_KEY, parent);
		apikeyField.setPreferenceStore(getPreferenceStore());
		apikeyField.setPage(this);
		apikeyField.load();
		addField(apikeyField);
	}
}
