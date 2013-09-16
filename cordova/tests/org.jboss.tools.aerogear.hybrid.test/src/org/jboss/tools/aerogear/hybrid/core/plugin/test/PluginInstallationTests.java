package org.jboss.tools.aerogear.hybrid.core.plugin.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.aerogear.hybrid.core.config.Access;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.config.WidgetModel;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPlugin;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginManager;
import org.jboss.tools.aerogear.hybrid.core.util.FileUtils;
import org.jboss.tools.aerogear.hybrid.test.Activator;
import org.jboss.tools.aerogear.hybrid.test.TestProject;
import org.jboss.tools.aerogear.hybrid.test.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("restriction")
public class PluginInstallationTests {
	
	private static File pluginsDirectroy;
	private TestProject project;
	private final static String PLUGIN_DIR_CHILDBROWSER = "ChildBrowser";
	private final static String PLUGIN_ID_CHILDBROWSER = "com.phonegap.plugins.childbrowser";
	
	@BeforeClass
	public static void setUpPlugins() throws IOException{
		URL pluginsDir = Activator.getDefault().getBundle().getEntry("/plugins");
		File tempDir =TestUtils.getTempDirectory();
		pluginsDirectroy = new File(tempDir, "plugins");
		FileUtils.directoryCopy(pluginsDir, FileUtils.toURL(pluginsDirectroy));
		
	}
	
	@Before
	public void setUpTestProject(){
		project = new TestProject();
	}
	
	@After
	public void cleanProject() throws CoreException{
		if(this.project != null ){
			this.project.delete();
			this.project = null;
		}
	}

	private CordovaPluginManager getCordovaPluginManager() {
		CordovaPluginManager pm = project.hybridProject().getPluginManager();		
		assertNotNull(pm);
		return pm;
	}
	
	@Test
	public void installPluginTest() throws CoreException{
		CordovaPluginManager pm = getCordovaPluginManager();
		pm.installPlugin(new File(pluginsDirectroy,PLUGIN_DIR_CHILDBROWSER), new NullProgressMonitor());
		IProject prj = project.getProject();
		IFolder plgFolder = prj.getFolder("/plugins/"+PLUGIN_ID_CHILDBROWSER);
		assertNotNull(plgFolder);
		assertTrue(plgFolder.exists());
		WidgetModel model = WidgetModel.getModel(project.hybridProject());
		Widget widget = model.getWidgetForRead();
		List<Access> aces = widget.getAccesses();
		int found =0;
		for (Access access : aces) {
			if("build.phonegap.com".equals(access.getOrigin()) ||
					"s3.amazonaws.com".equals(access.getOrigin()))
				found++;
		}
		assertEquals(2, found);
		
	}

	@Test
	public void listNoPluginsTest() throws CoreException{
		CordovaPluginManager pm = getCordovaPluginManager();
		List<CordovaPlugin> plugins = pm.getInstalledPlugins();
		assertTrue(plugins.isEmpty());
	}
	
	@Test
	public void listPluginsTest() throws CoreException{
		CordovaPluginManager pm = getCordovaPluginManager();
		pm.installPlugin(new File(pluginsDirectroy,PLUGIN_DIR_CHILDBROWSER), new NullProgressMonitor());
		List<CordovaPlugin> plugins = pm.getInstalledPlugins();
		boolean found = false;
		for (CordovaPlugin cordovaPlugin : plugins) {
			if(PLUGIN_ID_CHILDBROWSER.equals(cordovaPlugin.getId())){
				found = true;
			}
		}
		assertTrue("installed plugin not listed",found);
		assertTrue(pm.isPluginInstalled(PLUGIN_ID_CHILDBROWSER));
	}
	
	@Test
	public void pluginNotInstalledTest() throws CoreException{
		CordovaPluginManager pm = getCordovaPluginManager();
		pm.installPlugin(new File(pluginsDirectroy,PLUGIN_DIR_CHILDBROWSER), new NullProgressMonitor());
		assertFalse(pm.isPluginInstalled("my.madeup.id"));
		assertTrue(pm.isPluginInstalled(PLUGIN_ID_CHILDBROWSER));
	}
	

}
