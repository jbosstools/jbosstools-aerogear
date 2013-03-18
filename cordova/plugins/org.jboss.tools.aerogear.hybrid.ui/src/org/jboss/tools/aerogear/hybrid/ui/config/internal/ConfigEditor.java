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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.config.WidgetModel;
import org.w3c.dom.Document;

public class ConfigEditor extends FormEditor {

	private SourceEditor sourceEditor;
	private WidgetModel configModel;
	private Widget model;

	public ConfigEditor() {
		configModel = WidgetModel.getInstance();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		sourceEditor.doSave(monitor);
	    Document document = sourceEditor.getSourceDocument();
	    model = configModel.load(document);

	}

	@Override
	public void doSaveAs() {
		
	}


	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	protected void addPages() {
		sourceEditor = new SourceEditor();
		try {
			addPage(new EssentialsPage(this));
			addPage(new PropertiesPage(this));
//			addPage(new FeaturesPage(this));
			addPage(new IconsPage(this));
			int sourcePageIndex = addPage(sourceEditor, getEditorInput());
			
			setPageText(sourcePageIndex, "config.xml");
			firePropertyChange(PROP_TITLE);
		    Document document = sourceEditor.getSourceDocument();
		    model = configModel.load(document);

		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Widget getWidget() {
		return model;
	}
	
	public WidgetModel getWidgetModel(){
		return configModel;
	}

}
