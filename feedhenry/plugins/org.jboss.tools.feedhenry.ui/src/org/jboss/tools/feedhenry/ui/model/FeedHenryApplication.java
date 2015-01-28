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
package org.jboss.tools.feedhenry.ui.model;

import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.feedhenry.ui.FHPlugin;

import com.eclipsesource.json.JsonValue;

/**
 * An application on the FeedHenry system.
 * @author Gorkem Ercan
 *
 */
public class FeedHenryApplication {
	
	/**
	 * type value for full Cordova applications
	 */
	public static final String APP_TYPE_CORDOVA_ADVANCED = "client_advanced_hybrid";
	
	private String guid;
	private String title;
	private String type;
	private String repoUrl;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRepoUrl() {
		return repoUrl;
	}

	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}

	public String getGuid(){
		return this.guid;
	}
	
	public void setGuid(String id) {
		this.guid = id;
	}
	
	/**
	 * The name of the eclipse project. This is usually 
	 * the same value as title but this value should be used 
	 * when creating projects and directories for an application.
	 * 
	 * @return
	 */
	public String getEclipseProjectName(){
		return getTitle();
	}
	
	/**
	 * Finds the eclipse project that hosts this application on the current workspace
	 * or null if it is not imported to current workspace.
	 * @return project or null
	 */
	public IProject findEclipseProject(){
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects) {
			IFile f = project.getFile("www/fhconfig.json");
			if(f.isAccessible()){
				try{
					Reader reader = new InputStreamReader(f.getContents());
					JsonValue value = JsonValue.readFrom(reader);
					String appid = value.asObject().get("appid").asString();
					if(appid.equals(this.getGuid())){
						return project;
					}
				}catch(Exception e){
					FHPlugin.log(IStatus.ERROR, 
							NLS.bind("Error while parsing fhconfig.json on project {0}",project.getName()), e);
				}
			}
			
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "[FeedHenry Application: "+ title+" ]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if( guid != null && obj instanceof FeedHenryApplication){
			FeedHenryApplication that = (FeedHenryApplication)obj;
			return this.guid.equals(that.getGuid());
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		if(this.guid != null ){
			return guid.hashCode();
		}
		return super.hashCode();
	}

}
