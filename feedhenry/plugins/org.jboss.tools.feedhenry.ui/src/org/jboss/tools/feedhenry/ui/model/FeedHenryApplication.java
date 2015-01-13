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
