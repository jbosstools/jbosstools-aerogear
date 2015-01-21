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
package org.jboss.tools.feedhenry.ui.internal;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jboss.tools.feedhenry.ui.model.FeedHenryApplication;
import org.jboss.tools.feedhenry.ui.model.FeedHenryProject;

public class FeedHenryApplicationSelectionPart {
	
	private final FeedHenryApplicationSelector selector;
	private CheckboxTreeViewer appsList;
	private FHAppLabelProvider labelProvider;
	private FHApplicationContentProvider contentProvider;
	private String label;
	private IFilter filter;
	private IFilter disabledFilter;
	
	
	
	/*package*/ FeedHenryApplicationSelectionPart(FeedHenryApplicationSelector selector) {
		this.selector = selector;
	}
	
	/*package*/ void createContent(final Composite parent){
		
		final Composite appsCompsite = new Composite(parent, SWT.NULL);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(appsCompsite);
		GridLayoutFactory.fillDefaults().applyTo(appsCompsite);
		
		if(label != null && !label.isEmpty()){
			Label title = new Label(appsCompsite, SWT.NULL);
			title.setText(label);
			GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(title);
		}
		
		
		appsList = new CheckboxTreeViewer(appsCompsite);
		PixelConverter pc = new PixelConverter(appsList.getControl());
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL)
			.hint(pc.convertWidthInCharsToPixels(25),pc.convertHeightInCharsToPixels(10)).applyTo(appsList.getControl());
		
		appsList.setContentProvider(getContentProvider());
		appsList.setLabelProvider(getLabelProvider());
		
		appsList.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object element = event.getElement();
				if( element instanceof FeedHenryApplication){
					FeedHenryApplication app = (FeedHenryApplication) element;
					if(disabledFilter == null || !disabledFilter.select(app)){
						selector.selectionChanged();
						return;
					}
				}
				if(element instanceof FeedHenryProject){
					setCheckStateforApps((FeedHenryProject) element, event.getChecked());
					selector.selectionChanged();
					return;
				}
				if(event.getChecked()){
					event.getCheckable().setChecked(element, false);
				}
			}
		});
		
	}
	
	/*package*/ Object[] getCheckedElements(){
		Assert.isNotNull(appsList, "Must be called after createContent");
		return appsList.getCheckedElements();
	}
	
	/*package*/ void setInput(final List<FeedHenryProject> projects){
		Assert.isNotNull(appsList, "Must be called after createContent");
		appsList.setInput(projects);
	}


	/*package*/ void setLabelProvider(FHAppLabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}
	
	/*package*/ void setContentProvider(FHApplicationContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}
	/*package*/ void setLabel(String string){
		this.label = string;
	}
	/*package*/ void setProjectFilter(IFilter filter){
		this.filter = filter;
	}
	/*package*/ void setDisabledFilter(IFilter filter) {
		this.disabledFilter = filter;
	}
	
	private IContentProvider getContentProvider(){
		return contentProvider;
	}
	
	private IBaseLabelProvider getLabelProvider(){
		return labelProvider;
	}
	
	private void setCheckStateforApps(FeedHenryProject project, boolean state){
		final List<FeedHenryApplication> apps = project.getApplications();
		for (FeedHenryApplication application : apps) {
			if((filter != null && !filter.select(application)) || 
				(disabledFilter != null && disabledFilter.select(application))){
				continue;
			}
			appsList.setChecked(application, state);
		}
	}


}
