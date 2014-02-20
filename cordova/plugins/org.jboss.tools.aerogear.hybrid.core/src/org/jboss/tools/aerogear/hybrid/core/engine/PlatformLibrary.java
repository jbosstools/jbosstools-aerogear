package org.jboss.tools.aerogear.hybrid.core.engine;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.extensions.PlatformSupport;

public class PlatformLibrary{
	private String platformId;
	private IPath location;
	private HybridMobileLibraryResolver resolver;
	
	public PlatformLibrary(String platformId, IPath location){
		this.platformId = platformId;
		this.location = location;
	}
	
	public String getPlatformId() {
		return platformId;
	}


	public IPath getLocation() {
		return location;
	}

	public HybridMobileLibraryResolver getPlatformLibraryResolver(){
        Assert.isNotNull(platformId);
        if(resolver == null){
        	PlatformSupport platform = HybridCore.getPlatformSupport(platformId);
        	if(platform == null ) return null;
        	try {
        		resolver = platform.getLibraryResolver();
        		resolver.init(location);
        	} catch (CoreException e) {
        		HybridCore.log(IStatus.ERROR,"Library resolver creation error ", e);
        		return null;
        	}
        }
        return resolver;
	}
}