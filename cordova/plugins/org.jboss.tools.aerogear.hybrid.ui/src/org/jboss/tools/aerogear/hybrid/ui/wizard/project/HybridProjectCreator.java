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
package org.jboss.tools.aerogear.hybrid.ui.wizard.project;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.jboss.tools.aerogear.hybrid.core.natures.HybridAppNature;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;
import org.osgi.framework.Bundle;

public class HybridProjectCreator {
	
	private static final String[] COMMON_PATHS={ ".cordova", "platforms", "plugins", "www"};
	
	/**
	 * Creates a hybrid project with the given name and location. Location can be null, if location is null 
	 * the default location will be used for creating the project. 
	 * @param projectName
	 * @param location
	 * @param monitor
	 * @throws CoreException
	 */
	public void createProject( String projectName, URI location, IProgressMonitor monitor ) throws CoreException {
		Assert.isNotNull(projectName, "Project name is null, can not create a project without a name");
		
		IProject project = createBasicProject(projectName, location, monitor);
		addNature(project);
		addCommonPaths(project);
		addPlatformPaths(project);
		addCommonFiles(project);
		addTemplateFiles(project);
	}

	
	private void addTemplateFiles(IProject project) {
		// TODO Auto-generated method stub
		
	}


	private void addCommonFiles(IProject project) throws CoreException{
		copyFile(project, "/res/config.xml", "/www/config.xml");
		//TODO: write config.json to do .cordova directory 
		//Should look sth like this
//			{
//			id:id,
//			name:name
//			} 
		
	}
    private void copyFile(IProject project, String bundleLocation, String projectLocation) throws CoreException {

    	Bundle bundle = HybridUI.getDefault().getBundle();
	    URL url = bundle.getEntry(bundleLocation);
	    IFile file = project.getFile(projectLocation);
		if (!file.exists()) {
			try{
				file.create(url.openStream(), false, null);
			}catch(IOException ios){
				new CoreException(new Status(IStatus.ERROR, HybridUI.PLUGIN_ID, "Could not create config.xml file", ios));
			}
		}
	    
	
    }

	private void addPlatformPaths(IProject project) throws CoreException{
			//TODO: should we even bother to add platform paths now? 
	}


	private void addCommonPaths(IProject project) throws CoreException {
		for (String path : COMMON_PATHS) {
			createFolder(project.getFolder(path));
		}
		
	}

	private void createFolder(IFolder folder) throws CoreException {
		IContainer parent = folder.getParent();
		IFolder parentFolder = (IFolder)parent.getAdapter(IFolder.class);
		if ( parentFolder != null ) {
			createFolder(parentFolder);
		}
		if ( !folder.exists() ) {
			folder.create(false, true, null);
		}
	}

	private void addNature(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
	    String[] oldNatures = description.getNatureIds();
	    List<String> natureList =  new ArrayList<String>();
	    natureList.addAll(Arrays.asList(oldNatures));
	    
		if( !project.hasNature(HybridAppNature.NATURE_ID ) ){
			natureList.add(HybridAppNature.NATURE_ID);
		}
		
		if( !project.hasNature( JavaScriptCore.NATURE_ID )){
			natureList.add(JavaScriptCore.NATURE_ID);
		}
		
	    description.setNatureIds(natureList.toArray(new String[natureList.size()]));
	    project.setDescription(description, null);
	    
	}
	


	private IProject createBasicProject( String name, URI location, IProgressMonitor monitor ) throws CoreException {
		
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject newProject = workspaceRoot.getProject(name);
		
		if ( !newProject.exists() ){
			IProjectDescription description = newProject.getWorkspace().newProjectDescription(name);
			if( location != null ){
				description.setLocationURI(location);
			}
			
			newProject.create(description, monitor);
			if( !newProject.isOpen() ){
				newProject.open(monitor);
			}
		}
		
		return newProject;
	}
	
}
