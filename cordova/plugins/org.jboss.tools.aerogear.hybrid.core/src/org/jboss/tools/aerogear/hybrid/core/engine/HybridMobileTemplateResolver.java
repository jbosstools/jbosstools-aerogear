package org.jboss.tools.aerogear.hybrid.core.engine;

import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;

public abstract class HybridMobileTemplateResolver {
	
	public HybridMobileTemplateResolver(IPath libraryRoot) {
		
	}
	
	/**
	 * Initialize the resolver for the project.
	 * 
	 * @param project
	 */
	public abstract void initialize(HybridProject project);
	
	/**
	 * Returns the URL of the file requested from engine. Destination 
	 * must be a relative path on the target platform's project structure.
	 * May return null if a corresponding file can not be found on the 
	 * engine. 
	 * 
	 * @param destination relative path on target structure
	 * @return URL to the corresponding file on the engine or null
	 */
	public abstract URL getTemplateFile(IPath destination);
	
}
