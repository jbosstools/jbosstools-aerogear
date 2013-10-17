package org.jboss.tools.aerogear.hybrid.test;

import org.jboss.tools.aerogear.hybrid.core.config.WidgetModelTest;
import org.jboss.tools.aerogear.hybrid.core.plugin.test.CordovaPluginRegistryTest;
import org.jboss.tools.aerogear.hybrid.core.plugin.test.InstallActionsTest;
import org.jboss.tools.aerogear.hybrid.core.plugin.test.PluginInstallationTests;
import org.jboss.tools.aerogear.hybrid.core.test.FileUtilsTest;
import org.jboss.tools.aerogear.hybrid.core.test.HybridProjectConventionsTest;
import org.jboss.tools.aerogear.hybrid.test.ios.pbxproject.PBXProjectTest;
import org.jboss.tools.aerogear.hybrid.ui.wizard.project.HybridProjectCreatorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ FileUtilsTest.class, HybridProjectCreatorTest.class, 
	WidgetModelTest.class, CordovaPluginRegistryTest.class,HybridProjectConventionsTest.class, 
	InstallActionsTest.class,PluginInstallationTests.class,PBXProjectTest.class})
public class AllHybridTests {

}
