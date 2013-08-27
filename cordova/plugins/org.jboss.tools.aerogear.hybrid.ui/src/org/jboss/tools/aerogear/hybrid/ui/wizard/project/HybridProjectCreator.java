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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.natures.HybridAppNature;
import org.jboss.tools.aerogear.hybrid.core.platform.PlatformConstants;
import org.jboss.tools.aerogear.hybrid.core.util.FileUtils;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;
import org.osgi.framework.Bundle;

public class HybridProjectCreator {
	
	private static final String[] COMMON_PATHS={ ".cordova", PlatformConstants.DIR_MERGES, 
		PlatformConstants.DIR_PLUGINS,
		PlatformConstants.DIR_WWW };
	
	/**
	 * Creates a hybrid project with the given name and location. Location can be null, if location is null 
	 * the default location will be used for creating the project. 
	 * @param projectName
	 * @param location
	 * @param monitor
	 * @throws CoreException
	 */
	public void createProject( String projectName, URI location, String appName, String appID, IProgressMonitor monitor ) throws CoreException {
		Assert.isNotNull(projectName, "Project name is null, can not create a project without a name");
		if(monitor == null )
			monitor = new NullProgressMonitor();
		
		IProject project = createBasicProject(projectName, location, monitor);
		addNature(project, new SubProgressMonitor(monitor, 5));
		addCommonPaths(project, new SubProgressMonitor(monitor, 5));
		addPlatformPaths(project, new SubProgressMonitor( monitor, 5));
		addCommonFiles(project, new SubProgressMonitor(monitor, 5));
		addTemplateFiles(project, new SubProgressMonitor(monitor, 5));
		project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		updateConfig(project, appName, appID, new SubProgressMonitor(monitor, 5) );
		project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}

	
	private void updateConfig(IProject project, String appName, String appID, IProgressMonitor  monitor) throws CoreException{
		HybridProject hybridProject = HybridProject.getHybridProject(project);
		try {
			Widget w = hybridProject.getWidget();
			w.setId(appID);
			w.setName(appName);
			hybridProject.saveWidget(w);
		} catch (CoreException e) {
			HybridCore.log(IStatus.ERROR, "Error updating application name and id to config.xml", e);
		}
		
	}


	private void addTemplateFiles(IProject project, IProgressMonitor monitor) throws CoreException{
		Bundle bundle = HybridUI.getDefault().getBundle();
	    URL source = bundle.getEntry("/templates/www");
	    IFolder folder = project.getFolder(PlatformConstants.DIR_WWW);
	    if (!folder.exists()){
	    	folder.create(true, true, monitor);
	    }
	    
		try {
			FileUtils.directoryCopy(source, folder.getLocation().toFile().toURL());
			monitor.done();
		} catch (MalformedURLException e) {
			throw new CoreException(new Status(IStatus.ERROR, HybridUI.PLUGIN_ID, "Error adding template files", e));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, HybridUI.PLUGIN_ID, "Error adding template files", e));
		}
	}


	private void addCommonFiles(IProject project, IProgressMonitor monitor) throws CoreException{
		//TODO: write config.json to do .cordova directory 
		//Should look sth like this
//			{
//			id:id,
//			name:name
//			} 
		
	}


	private void addPlatformPaths(IProject project, IProgressMonitor monitor) throws CoreException{
			//TODO: should we even bother to add platform paths now? 
		monitor.done();
	}


	private void addCommonPaths(IProject project, IProgressMonitor monitor) throws CoreException {
		SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, COMMON_PATHS.length);
		for (String path : COMMON_PATHS) {
			createFolder(project.getFolder(path),subMonitor);
			subMonitor.worked(1);
		}
		subMonitor.done();
	}

	private void createFolder(IFolder folder, IProgressMonitor monitor) throws CoreException {
		IContainer parent = folder.getParent();
		IFolder parentFolder = (IFolder)parent.getAdapter(IFolder.class);
		if ( parentFolder != null ) {
			createFolder(parentFolder, monitor);
		}
		if ( !folder.exists() ) {
			folder.create(false, true, monitor);
		}
	}

	private void addNature(IProject project, IProgressMonitor monitor) throws CoreException {
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
	    project.setDescription(description, monitor);
	    
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
