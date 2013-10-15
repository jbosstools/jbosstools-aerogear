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

import static org.jboss.tools.aerogear.hybrid.core.platform.PlatformConstants.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.config.WidgetModel;
import org.jboss.tools.aerogear.hybrid.core.internal.libraries.CordovaLibraryJsContainerInitializer;
import org.jboss.tools.aerogear.hybrid.core.natures.HybridAppNature;
import org.jboss.tools.aerogear.hybrid.core.util.FileUtils;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;
import org.osgi.framework.Bundle;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class HybridProjectCreator {
	
	private static final String[] COMMON_PATHS={ DIR_DOT_CORDOVA, DIR_MERGES, 
		DIR_PLUGINS, DIR_WWW };
	
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
		addCommonFiles(project, appName, appID,new SubProgressMonitor(monitor, 5));
		addTemplateFiles(project, new SubProgressMonitor(monitor, 5));
		setUpJavaScriptProject(monitor, project);
		project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		updateConfig(project, appName, appID, new SubProgressMonitor(monitor, 5) );
		project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}


	private void setUpJavaScriptProject(IProgressMonitor monitor,
			IProject project) throws JavaScriptModelException {
		IIncludePathEntry entry = JavaScriptCore.newContainerEntry(new Path(CordovaLibraryJsContainerInitializer.CONTAINER_ID));
		IJavaScriptProject javascriptProject = JavaScriptCore.create(project);
		IIncludePathEntry[] entries = javascriptProject.getRawIncludepath();
		IIncludePathEntry[] newEntries = Arrays.copyOf(entries, entries.length +1);
		newEntries[newEntries.length -1 ] = entry;
		javascriptProject.setRawIncludepath(newEntries, monitor);
	}

	
	private void updateConfig(IProject project, String appName, String appID, IProgressMonitor  monitor) throws CoreException{
		HybridProject hybridProject = HybridProject.getHybridProject(project);
		try {
			WidgetModel model = WidgetModel.getModel(hybridProject);
			Widget w = model.getWidgetForEdit();
			w.setId(appID);
			w.setName(appName);
			model.save();
		} catch (CoreException e) {
			HybridCore.log(IStatus.ERROR, "Error updating application name and id to config.xml", e);
		}
		
	}


	private void addTemplateFiles(IProject project, IProgressMonitor monitor) throws CoreException{
		Bundle bundle = HybridUI.getDefault().getBundle();
	    URL source = bundle.getEntry("/templates/www");
	    IFolder folder = project.getFolder(DIR_WWW);
	    if (!folder.exists()){
	    	folder.create(true, true, monitor);
	    }
	    
		try {
			FileUtils.directoryCopy(source, FileUtils.toURL(folder.getLocation().toFile()));
			monitor.done();
		} catch (MalformedURLException e) {
			throw new CoreException(new Status(IStatus.ERROR, HybridUI.PLUGIN_ID, "Error adding template files", e));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, HybridUI.PLUGIN_ID, "Error adding template files", e));
		}
	}


	private void addCommonFiles(IProject project, String applicationName, String applicationID, IProgressMonitor monitor) throws CoreException{
		IFolder folder = project.getFolder(DIR_DOT_CORDOVA);
		if(folder != null && folder.exists()){
			JsonObject obj = new JsonObject();
			obj.addProperty("id", applicationID);
			obj.addProperty("name", applicationName);
			Gson gson = new Gson();
			String json = gson.toJson(obj);
			IFile file = folder.getFile("config.json");
			InputStream stream = null;
			try {
				stream = new ByteArrayInputStream(json.getBytes("utf-8"));
				file.create(stream,true,monitor);
			} catch (UnsupportedEncodingException e) {
				HybridUI.log(IStatus.ERROR, "Error while persisting config.json", e);
			}
			finally{
				if(stream !=null){
						try { stream.close();
						} catch (IOException e) {/*unhandled*/ }
				}
				monitor.done();
			}
		}
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
