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
package org.jboss.tools.aerogear.hybrid.ui.config.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.aerogear.hybrid.core.config.ConfigModel;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;

public class ConfigEditor extends FormEditor {

	private SourceEditor sourceEditor;
	private Widget widget;

	public ConfigEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if (input instanceof FileEditorInput) {
			IFile configFile = ((FileEditorInput) input).getFile();
			widget = ConfigModel.load(configFile.getLocation().toFile());
			if (widget == null ){
				// TODO: figure out what to do if the file is not a valid
				// config.xml
				// One idea is to copy an existing valid file...
			}
		}
		super.init(site, input);

	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addPages() {
		sourceEditor = new SourceEditor();
		try {
			addPage(new EssentialsPage(this));
			addPage(new PreferencesPage(this));
			addPage(new FeaturesPage(this));
			addPage(new IconsPage(this));
			int sourcePageIndex = addPage(sourceEditor, getEditorInput());
			
			setPageText(sourcePageIndex, "config.xml");
			firePropertyChange(PROP_TITLE);
			
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Widget getWidget() {
		return widget;
	}

}
