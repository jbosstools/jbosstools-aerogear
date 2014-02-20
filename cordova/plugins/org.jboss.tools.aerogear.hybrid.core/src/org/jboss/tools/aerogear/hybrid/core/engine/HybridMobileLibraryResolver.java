package org.jboss.tools.aerogear.hybrid.core.engine;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

public abstract class HybridMobileLibraryResolver {
	
	public static final String VAR_PACKAGE_NAME = "$package";
	public static final String VAR_APP_NAME = "$appname";
	protected IPath libraryRoot;
	protected String version;
	
	/**
	 * 
	 * @param engine
	 */
	public void init(IPath libraryRoot){
		this.libraryRoot = libraryRoot;
		this.version = detectVersion();
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
	
	/**
	 * Pre-compiles the library so that it is ready to be used.
	 * @param monitor
	 * @throws CoreException
	 */
	public abstract void preCompile(IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Returns true if this library needs to be precompiled before it can be used. 
	 * @return
	 */
	public abstract boolean needsPreCompilation();
	
	/**
	 * Detects the version of the engine from layout
	 * @return
	 */
	public abstract String detectVersion();
	
}
