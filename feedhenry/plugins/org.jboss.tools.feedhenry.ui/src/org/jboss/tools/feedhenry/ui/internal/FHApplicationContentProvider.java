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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.jboss.tools.feedhenry.ui.model.FeedHenryApplication;
import org.jboss.tools.feedhenry.ui.model.FeedHenryProject;

final class FHApplicationContentProvider implements ITreeContentProvider{
	private List<FeedHenryProject> projects;
	private String[] validTypes;
	private boolean showInvalids;

	@Override
	public void dispose() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		projects = (List<FeedHenryProject>) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(showInvalids){
			return projects.toArray(new FeedHenryProject[projects.size()]);
		}
		ArrayList<FeedHenryProject> validProjects = new ArrayList<FeedHenryProject>();
		for (FeedHenryProject feedHenryProject : projects) {
			if(hasValidApplications(feedHenryProject)){
				validProjects.add(feedHenryProject);
			}
		}
		return validProjects.toArray(new FeedHenryProject[validProjects.size()]);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof FeedHenryProject){
			FeedHenryProject prj = (FeedHenryProject)parentElement;
			List<FeedHenryApplication> apps = prj.getApplications();
			if(apps != null){
				if(showInvalids){
					return apps.toArray(new FeedHenryApplication[apps.size()]);
				}
				ArrayList<FeedHenryApplication> valids = new ArrayList<FeedHenryApplication>();
				for (FeedHenryApplication feedHenryApplication : apps) {
					if(FeedHenryApplicationSelectionPart.isValidApplication(feedHenryApplication, validTypes)){
						valids.add(feedHenryApplication);
					}
				}
				return valids.toArray(new FeedHenryApplication[valids.size()]);
			}
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof FeedHenryProject ){
			FeedHenryProject project = (FeedHenryProject) element;
			if(showInvalids){
				return project.getApplications() != null &&
						!project.getApplications().isEmpty();
			}
			return hasValidApplications(project);
		}
		return false;
	}

	/*package*/ void setValidProjectTypes(final String[] types) {
		validTypes = types;
	}
	

	/*package*/ void showInvalidProjectTypes(boolean show) {
		showInvalids = show;
	}

	private boolean hasValidApplications(FeedHenryProject project){
		if(validTypes == null ) return true;
		List<FeedHenryApplication> apps =  project.getApplications();
		for (FeedHenryApplication application : apps) {
			if(FeedHenryApplicationSelectionPart.isValidApplication(application,validTypes)){
				return true;
			}
		}
		return false;
	}
	
}