package org.jboss.tools.vpe.cordovasim.eclipse.launch.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

/**
 * Utility class for IResources
 * @author xcoulon
 *
 */
public class ResourceUtils {
	
	/**
	 * <p>Locates the {@link IResource} associated with the given path, where the first segment is the project name, followed by containers and ending with the file name.<p>
	 * <p>For example: <code>/project/dir_A/dir_B/file.html</code>
	 * @param location
	 * @return the {@link IResource} or null if the matching {@link IResource} does not exist 
	 */
	public static IResource retrieveResource(final String location) {
		// try as a file first
		final Path path = new Path(location);
		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if(file != null && file.exists()) {
			return file;
		}
		// try as a folder
		final IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
		if(folder != null && folder.exists()) {
			return folder;
		}
		return null;
	}

}
