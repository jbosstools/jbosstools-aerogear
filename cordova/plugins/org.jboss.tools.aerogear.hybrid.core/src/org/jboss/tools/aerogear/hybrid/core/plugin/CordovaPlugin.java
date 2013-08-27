/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.aerogear.hybrid.core.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.tools.aerogear.hybrid.core.CordovaEngine;

public class CordovaPlugin {
	
	private String id;
	private String version;
	private String name;
	private String description;
	private String license;
	private String author;
	private String keywords;
	private List<CordovaEngine> supportedEngines;
	private String info;
	private List<PluginJavaScriptModule> modules;
	
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public void addSupportedEngine(CordovaEngine engine){
		if(supportedEngines == null ){
			supportedEngines = new ArrayList<CordovaEngine>();
		}
		supportedEngines.add(engine);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof CordovaPlugin ){
			CordovaPlugin that = (CordovaPlugin)obj;
			return this.getId().equals(that.getId());
		}
		return super.equals(obj);
	}

	public List<PluginJavaScriptModule> getModules() {
		return Collections.unmodifiableList(modules);
	}
	
	public void addModule(PluginJavaScriptModule module) {
		if(this.modules == null ){
			modules = new ArrayList<PluginJavaScriptModule>();
		}
		this.modules.add(module);
	}
	
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	

}
