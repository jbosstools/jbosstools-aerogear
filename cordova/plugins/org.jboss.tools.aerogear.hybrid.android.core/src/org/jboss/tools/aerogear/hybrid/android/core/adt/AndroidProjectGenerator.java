package org.jboss.tools.aerogear.hybrid.android.core.adt;


import static org.jboss.tools.aerogear.hybrid.core.util.FileUtils.directoryCopy;
import static org.jboss.tools.aerogear.hybrid.core.util.FileUtils.fileCopy;
import static org.jboss.tools.aerogear.hybrid.core.util.FileUtils.toURL;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.aerogear.hybrid.android.core.AndroidCore;
import org.jboss.tools.aerogear.hybrid.core.platform.AbstractPlatformProjectGenerator;
import org.osgi.framework.Bundle;

import com.android.ide.eclipse.adt.AdtPlugin;
import com.android.sdklib.SdkManager;
import com.android.sdklib.internal.project.ProjectCreator;
import com.android.sdklib.internal.project.ProjectCreator.OutputLevel;
import com.android.utils.ILogger;

public class AndroidProjectGenerator extends AbstractPlatformProjectGenerator{

	public AndroidProjectGenerator(IProject project, File generationFolder) {
		super(project, generationFolder);
	}
	class Logger implements ILogger{

		@Override
		public void error(Throwable arg0, String arg1, Object... arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void info(String arg0, Object... arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void verbose(String arg0, Object... arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void warning(String arg0, Object... arg1) {
			// TODO Auto-generated method stub
			
		}
		
	}

	@Override
	protected void generateNativeFiles() throws IOException {
		
		final String osSdkFolder = AdtPlugin.getOsSdkFolder();
		final Logger logger = new Logger();
		SdkManager sdkmanager = SdkManager.createManager(osSdkFolder,logger );
		
		// Create the basic android project
		ProjectCreator projectCreator = new ProjectCreator(sdkmanager,osSdkFolder, OutputLevel.NORMAL, logger);
		//TODO: Use config.xml for project name and package values
		projectCreator.createProject(getDestination().getPath(), this.getProject().getName(), this.getProjectName(), "myApp",sdkmanager.getTargets()[0],false,null);
		
		//Move cordova library to libs
		Bundle bundle = AndroidCore.getContext().getBundle();
		fileCopy(getTemplateFile("/templates/CordovaLib/cordova-2.5.0.jar"), 
				toURL(new File(getDestination(),"libs/cordova.jar")));
		directoryCopy(getTemplateFile("/templates/project/res/"),
				toURL(new File(getDestination(),"res")));
	
	}

	@Override
	protected String getTargetShortName() {
		return "android";
	}

	@Override
	protected void replaceCordovaPlatformFiles() throws IOException {
		fileCopy(getTemplateFile("/templates/CordovaLib/cordova.android.js"), 
				toURL(new File(getPlatformWWWDirectory(),"cordova.js")));
	}

	private URL getTemplateFile(String path){
		Bundle bundle = AndroidCore.getContext().getBundle();
		return bundle.getEntry(path);
	}
	
	
	@Override
	protected File getPlatformWWWDirectory() {
		return new File(getDestination(),"assets/www");
	}

}
