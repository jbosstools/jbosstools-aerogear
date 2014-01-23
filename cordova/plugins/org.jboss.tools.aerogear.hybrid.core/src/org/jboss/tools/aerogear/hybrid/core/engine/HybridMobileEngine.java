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
package org.jboss.tools.aerogear.hybrid.core.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.extensions.PlatformSupport;
import org.jboss.tools.aerogear.hybrid.engine.internal.cordova.CordovaEngineProvider;

public class HybridMobileEngine{
	
	private String id;
	private String name;
	private String version;
	private ArrayList<String> platforms = new ArrayList<String>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
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
	
	public HybridMobileLibraryResolver getPlatformLibraryResolver(String platformId){
        Assert.isNotNull(platformId);
        PlatformSupport platform = HybridCore.getPlatformSupport(platformId);
        if(platform == null ) return null;
        
        HybridMobileLibraryResolver resolver;
		try {
			resolver = platform.getLibraryResolver();
		} catch (CoreException e) {
			HybridCore.log(IStatus.ERROR,"Library resolver creation error ", e);
			return null;
		}
		IPath libraryRoot = new Path(CordovaEngineProvider.getLibFolder().toString());
		libraryRoot = libraryRoot.append(platformId).append(getId()).append(getVersion());
        resolver.init(libraryRoot, getVersion());
        return resolver;
	}
	
	public void addPlatform(String platform) {
		if(!platforms.contains(platform)){
			platforms.add(platform);
		}
	}
	
	public List<String> getPlatforms(){
		return Collections.unmodifiableList(platforms);
	}
	
	/**
	 * Checks if the underlying library compatible and 
	 * support the platforms of this engine.
	 * 
	 * @return status of the library
	 */
	public IStatus isLibraryConsistent(){
		List<String> pls = getPlatforms();
		MultiStatus status = new MultiStatus(HybridCore.PLUGIN_ID, 0, "The library can not support this application",null);
		for (String thePlatform : pls) {
			status.add(getPlatformLibraryResolver(thePlatform).isLibraryConsistent());
		}
		return status;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof HybridMobileEngine) ){
			return false;
		}
		HybridMobileEngine that = (HybridMobileEngine) obj;
		if(this.getId().equals(that.getId()) 
				&& this.getVersion().equals(that.getVersion())){
			return true;
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		if(this.getId() != null && this.getVersion() != null ){
			return this.getId().hashCode()+this.getVersion().hashCode();
		}
		return super.hashCode();
	}
	
}