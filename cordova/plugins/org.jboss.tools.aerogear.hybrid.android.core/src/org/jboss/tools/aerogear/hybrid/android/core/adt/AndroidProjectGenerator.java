package org.jboss.tools.aerogear.hybrid.android.core.adt;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.aerogear.hybrid.core.platform.AbstractPlatformProjectGenerator;

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
		ProjectCreator projectCreator = new ProjectCreator(sdkmanager,osSdkFolder, OutputLevel.NORMAL, logger);
		projectCreator.createProject(getDestination().getPath(), this.getProject().getName(), this.getProjectName(), "myApp",sdkmanager.getTargets()[0],false,null);
	}

	@Override
	protected String getTargetShortName() {
		return "android";
	}

	@Override
	protected void replaceCordovaPlatformFiles() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected File getPlatformWWWDirectory() {
		return new File(getDestination(),"assets/www");
	}

}
