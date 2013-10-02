package org.jboss.tools.aerogear.hybrid.core.plugin.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.aerogear.hybrid.core.config.Access;
import org.jboss.tools.aerogear.hybrid.core.config.Preference;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.config.WidgetModel;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPlugin;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginManager;
import org.jboss.tools.aerogear.hybrid.core.plugin.FileOverwriteCallback;
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
	private final static String PLUGIN_DIR_VARIABLE = "VariablePlugin";
	private final static String PLUGIN_ID_VARIABLE = "org.jboss.variable";
	
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
		pm.installPlugin(new File(pluginsDirectroy,PLUGIN_DIR_CHILDBROWSER), new FileOverwriteCallback() {
			
			@Override
			public boolean isOverwiteAllowed(String[] files) {
				return true;
			}
		},
		new NullProgressMonitor());
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
	public void installVariablePluginTest() throws CoreException{
		CordovaPluginManager pm = getCordovaPluginManager();
		pm.installPlugin(new File(pluginsDirectroy,PLUGIN_DIR_VARIABLE), new FileOverwriteCallback() {
			
			@Override
			public boolean isOverwiteAllowed(String[] files) {
				return true;
			}
		},new NullProgressMonitor());
		IProject prj = project.getProject();
		IFolder plgFolder = prj.getFolder("/plugins/"+PLUGIN_ID_VARIABLE);
		assertNotNull(plgFolder);
		assertTrue(plgFolder.exists());
		WidgetModel model = WidgetModel.getModel(project.hybridProject());
		Widget widget = model.getWidgetForRead();
		List<Preference> prefs = widget.getPreferences();
		for (Preference preference : prefs) {
			if(preference.getName().equals("API_KEY")){
				return;
			}
		}
		fail("Replaced key is not found");
	}
	
	@Test
	public void gitInstallPluginTest() throws CoreException{
		CordovaPluginManager pm = getCordovaPluginManager();
		File repo = new File(pluginsDirectroy,PLUGIN_DIR_CHILDBROWSER);
		pm.installPlugin(repo.toURI(), "test_tag", null,new FileOverwriteCallback() {
			
			@Override
			public boolean isOverwiteAllowed(String[] files) {
				return true;
			}
		}, new NullProgressMonitor());
		IProject prj = project.getProject();
		IFolder plgFolder = prj.getFolder("/plugins/"+PLUGIN_ID_CHILDBROWSER);
		assertNotNull(plgFolder);
		assertTrue(plgFolder.exists());
		IFile file = plgFolder.getFile("test.file");
		assertTrue(file.exists());
		IFile anotherFile = plgFolder.getFile("anothertest.file");
		assertFalse(anotherFile.exists());
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
		pm.installPlugin(new File(pluginsDirectroy,PLUGIN_DIR_CHILDBROWSER),new FileOverwriteCallback() {
			
			@Override
			public boolean isOverwiteAllowed(String[] files) {
				return true;
			}
		}, new NullProgressMonitor());
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
		pm.installPlugin(new File(pluginsDirectroy,PLUGIN_DIR_CHILDBROWSER),new FileOverwriteCallback() {
			
			@Override
			public boolean isOverwiteAllowed(String[] files) {
				return true;
			}
		}, new NullProgressMonitor());
		assertFalse(pm.isPluginInstalled("my.madeup.id"));
		assertTrue(pm.isPluginInstalled(PLUGIN_ID_CHILDBROWSER));
	}
	

}
