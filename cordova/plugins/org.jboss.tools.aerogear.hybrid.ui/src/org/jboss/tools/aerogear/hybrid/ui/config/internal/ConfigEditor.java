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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.config.WidgetModel;

public class ConfigEditor extends FormEditor {

	private SourceEditor sourceEditor;
	private Widget widget;
	private WidgetModel model;
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		setTitle(input);
	}

	private void setTitle(IEditorInput input) {
		String title = null;
		Widget w = getWidget();
		if(w != null ){
			title = getWidget().getName();
		}
		if(title == null || title.isEmpty() ){
			IResource res = (IResource)input.getAdapter(IResource.class);
			if(res != null ){
				title = res.getProject().getName();
			}
		}
		setPartName(title);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		sourceEditor.doSave(monitor);
	}

	@Override
	public void doSaveAs() {
		//Not supported
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
		if (widget == null) {
			IFile file = (IFile) getEditorInput().getAdapter(IFile.class);
			if (file != null) {
				HybridProject prj = HybridProject.getHybridProject(file
						.getProject());
				WidgetModel model = WidgetModel.getModel(prj);
				try {
					widget = model.getWidgetForEdit();
				} catch (CoreException e) {
					HybridCore.log(IStatus.ERROR, "Error when retrieving the widget model", e);
				}
			}
		}
		return widget;
	}
	
	public WidgetModel getWidgetModel() {
		if (model == null) {
			IFile file = (IFile) getEditorInput().getAdapter(IFile.class);
			if (file != null) {
				HybridProject prj = HybridProject.getHybridProject(file
						.getProject());
				model = WidgetModel.getModel(prj);
			}
		}
		return model;
	}
	
}
