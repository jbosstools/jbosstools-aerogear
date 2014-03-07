/*******************************************************************************
 * Copyright (c) 2013, 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *       Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.aerogear.hybrid.test;

import org.jboss.tools.aerogear.hybrid.core.config.WidgetModelTest;
import org.jboss.tools.aerogear.hybrid.core.plugin.test.CordovaPluginRegistryTest;
import org.jboss.tools.aerogear.hybrid.core.plugin.test.InstallActionsTest;
import org.jboss.tools.aerogear.hybrid.core.plugin.test.PluginInstallationTests;
import org.jboss.tools.aerogear.hybrid.core.test.FileUtilsTest;
import org.jboss.tools.aerogear.hybrid.core.test.HybridProjectConventionsTest;
import org.jboss.tools.aerogear.hybrid.core.test.TestBundleHttpStorage;
import org.jboss.tools.aerogear.hybrid.test.ios.pbxproject.PBXProjectTest;
import org.jboss.tools.aerogear.hybrid.ui.wizard.project.HybridProjectCreatorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ FileUtilsTest.class, HybridProjectCreatorTest.class, 
	WidgetModelTest.class, CordovaPluginRegistryTest.class,HybridProjectConventionsTest.class, 
	InstallActionsTest.class,PluginInstallationTests.class,PBXProjectTest.class,IntegrityTest.class, TestBundleHttpStorage.class})
public class AllHybridTests {

}
