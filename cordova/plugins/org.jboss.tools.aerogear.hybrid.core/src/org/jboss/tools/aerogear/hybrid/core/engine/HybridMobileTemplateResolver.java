package org.jboss.tools.aerogear.hybrid.core.engine;

import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

public abstract class HybridMobileTemplateResolver {
	
	public static final String VAR_PACKAGE_NAME = "$package";
	public static final String VAR_APP_NAME = "$appname";
	
	
	public HybridMobileTemplateResolver(IPath libraryRoot) {
		
	}
	
	
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
	
	/**
	 * Checks if the underlying library compatible and 
	 * can support the platform.
	 * @return
	 */
	public abstract IStatus isLibraryConsistent();
	
}
