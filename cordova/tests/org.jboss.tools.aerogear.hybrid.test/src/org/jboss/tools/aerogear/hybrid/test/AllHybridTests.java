package org.jboss.tools.aerogear.hybrid.test;

import org.jboss.tools.aerogear.hybrid.core.config.WidgetModelTest;
import org.jboss.tools.aerogear.hybrid.core.test.FileUtilsTest;
import org.jboss.tools.aerogear.hybrid.ui.wizard.project.HybridProjectCreatorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ FileUtilsTest.class, HybridProjectCreatorTest.class, WidgetModelTest.class})
public class AllHybridTests {

}
