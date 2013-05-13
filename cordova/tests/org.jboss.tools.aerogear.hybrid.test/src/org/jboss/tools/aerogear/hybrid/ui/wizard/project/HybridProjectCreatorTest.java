package org.jboss.tools.aerogear.hybrid.ui.wizard.project;

import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.natures.HybridAppNature;
import org.jboss.tools.aerogear.hybrid.core.platform.PlatformConstants;
import org.junit.BeforeClass;
import org.junit.Test;

public class HybridProjectCreatorTest {
	private static final String PROJECT_NAME = "TestProject";
	private static final String APP_NAME = "Test App";
	private static final String APP_ID = "Test id";


	private IProject getTheProject() {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject theProject = workspaceRoot.getProject(PROJECT_NAME);
		return theProject;
	}

	@BeforeClass
	public static void createTestProject() throws CoreException{
		HybridProjectCreator creator = new HybridProjectCreator();
		creator.createProject(PROJECT_NAME, null, APP_NAME, APP_ID, new NullProgressMonitor());
	}

	@Test
	public void createProjectTest() {
		IProject theProject = getTheProject();
		assertTrue(theProject.exists());
	}

	@Test
	public void projectNatureTest() throws CoreException{
		IProject theProject = getTheProject();
		assertTrue(theProject.hasNature(HybridAppNature.NATURE_ID));
	}
	
	@Test
	public void directoryStructureTest(){
		IProject theProject = getTheProject();
		
		String[] paths={ ".cordova", PlatformConstants.DIR_MERGES, "plugins", PlatformConstants.DIR_WWW };//Copied from HybridProjectCreator
		for (int i = 0; i < paths.length; i++) {
			IFolder folder = theProject.getFolder( paths[i]);
			assertTrue(paths[i]+ " is not created. ", folder.exists());
		}
	}
	
	@Test
	public void essentialFilesTest(){
		IProject theProject = getTheProject();
		IFile file = theProject.getFile("/www/config.xml");
		assertTrue(file.exists());
		file= theProject.getFile("/www/index.html");
		assertTrue(file.exists());
	}
}
