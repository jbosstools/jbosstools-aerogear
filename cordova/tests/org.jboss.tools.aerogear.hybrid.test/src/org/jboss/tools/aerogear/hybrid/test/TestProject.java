package org.jboss.tools.aerogear.hybrid.test;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.natures.HybridAppNature;
import org.jboss.tools.aerogear.hybrid.core.platform.PlatformConstants;
import org.jboss.tools.aerogear.hybrid.ui.wizard.project.HybridProjectCreator;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class TestProject {
	//copied from org.jboss.tools.aerogear.hybrid.ui.wizard.project.HybridProjectCreator
	private static final String[] COMMON_PATHS={ ".cordova", PlatformConstants.DIR_MERGES, 
		PlatformConstants.DIR_PLUGINS,
		PlatformConstants.DIR_WWW };
	
	public static final String PROJECT_NAME = "HybridToolsTest";
	public static final String APPLICATION_NAME = "Test application";
	public static final String APPLICATION_ID = "hybrid.tools.test";
	
	@SuppressWarnings("restriction")
	public TestProject(){
		HybridProjectCreator projectCreator = new HybridProjectCreator();
		try {
			projectCreator.createProject(PROJECT_NAME, null, APPLICATION_NAME, APPLICATION_ID, new NullProgressMonitor());
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public IStatus isProjectValid() throws CoreException{
		IProject project = getProject();
		if( !project.hasNature(HybridAppNature.NATURE_ID ) ){
			return error("project does not have hybrid application nature");
		}
		if( !project.hasNature( JavaScriptCore.NATURE_ID )){
			return error("project does not have javascript nature");
		}
		for (int i = 0; i < COMMON_PATHS.length; i++) {
			IResource resource = project.findMember(COMMON_PATHS[i]);
			if(resource == null || !resource.exists()){
				error("Project is missing "+ COMMON_PATHS[i] );
			}
		}
		Document doc;
		try {
			doc = loadConfigXML();
		} catch (Exception e) {
			return error("error parsing config.xml");
		}
		String id = doc.getDocumentElement().getAttribute("id");
		if( !APPLICATION_ID.equals(id)){
			error("wrong application id");
		}
		NodeList nodes = doc.getDocumentElement().getElementsByTagName("name");
		if(nodes.getLength()< 1){
			return error("Application name is not updated"); 
		}
		String name = nodes.item(0).getTextContent();
		if( !APPLICATION_NAME.equals(name)){
			return error("Wrong application name");
		}
		
		
		return Status.OK_STATUS;
	}


	public IProject getProject() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(PROJECT_NAME);
		return project;
	}
	
	public HybridProject hybridProject()
	{
		IProject prj = getProject();
		return HybridProject.getHybridProject(prj);
	}
	
	public void delete() throws CoreException{
		getProject().delete(true, true, new NullProgressMonitor());
	}

	private  Document loadConfigXML() throws Exception {
	    DocumentBuilder db;
		DocumentBuilderFactory dbf =DocumentBuilderFactory.newInstance();
	
	    	db = dbf.newDocumentBuilder();
	    	IFile file =  getProject().getFile("/www/config.xml");
	    	if(file == null )
	    		return null;
	    	return db.parse(file.getContents()); 
		
	}
	
	private Status error(String message) {
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
	}

}
