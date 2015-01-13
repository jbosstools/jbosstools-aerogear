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

import java.util.List;
/**
 * A project on FeedHenry cloud
 * 
 * @author Gorkem Ercan
 *
 */
public class FeedHenryProject {
	
	private String title;
	private String guid;
	private List<FeedHenryApplication> applications;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public List<FeedHenryApplication> getApplications() {
		return applications;
	}

	public void setApplications(List<FeedHenryApplication> applications) {
		this.applications = applications;
	}
	
	@Override
	public String toString() {
		return "[FeedHenry Project: " + title + " ]";
	}

	@Override
	public boolean equals(Object obj) {
		if( guid != null && obj instanceof FeedHenryProject){
			FeedHenryProject that = (FeedHenryProject)obj;
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
