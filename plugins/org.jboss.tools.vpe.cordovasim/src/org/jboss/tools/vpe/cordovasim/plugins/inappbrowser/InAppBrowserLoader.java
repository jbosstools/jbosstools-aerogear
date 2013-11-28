/*******************************************************************************
 * Copyright (c) 2007-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim.plugins.inappbrowser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.vpe.browsersim.browser.ExtendedCloseWindowListener;
import org.jboss.tools.vpe.browsersim.browser.ExtendedWindowEvent;
import org.jboss.tools.vpe.browsersim.browser.IBrowser;
import org.jboss.tools.vpe.browsersim.browser.WebKitBrowserFactory;
import org.jboss.tools.vpe.browsersim.model.Device;
import org.jboss.tools.vpe.browsersim.util.BrowserSimUtil;
import org.jboss.tools.vpe.cordovasim.CustomBrowserSim;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class InAppBrowserLoader {

	@SuppressWarnings("nls")
	public static boolean isInAppBrowserEvent(ExtendedWindowEvent openWindowEvent) {
		IBrowser parentBrowser = (IBrowser) openWindowEvent.widget;
		return Boolean.TRUE.equals(parentBrowser.evaluate("return !!window.needToOpenInAppBrowser"));
	}
	
	@SuppressWarnings("nls")
	public static void processInAppBrowser(final IBrowser rippleToolSuiteBrowser, final CustomBrowserSim browserSim,
			ExtendedWindowEvent openWindowEvent) {
		rippleToolSuiteBrowser.execute("window.needToOpenInAppBrowser = false"); 
		
		final IBrowser browserSimBrowser = browserSim.getBrowser();
		final Composite browserSimParentComposite = browserSimBrowser.getParent();
		Device device = browserSim.getCurrentDevice();
		
		final IBrowser inAppBrowser = createInAppBrowser(browserSimParentComposite, browserSimBrowser, device); 
		browserSim.setInAppBrowser(inAppBrowser);
		
		browserSimBrowser.setParent((Composite)inAppBrowser); // hiding browserSim's browser by changing it's parent   
		openWindowEvent.browser = inAppBrowser;  
		browserSimParentComposite.layout();
		
		BrowserSimUtil.setCustomScrollbarStylesForWindows(inAppBrowser);
	
		inAppBrowser.addCloseWindowListener(new ExtendedCloseWindowListener() {
			@Override
			public void close(ExtendedWindowEvent event) {
				browserSim.setInAppBrowser(null);
				browserSimBrowser.setParent(browserSimParentComposite);
				inAppBrowser.dispose();
				browserSimParentComposite.layout();		
				rippleToolSuiteBrowser.execute("ripple('event').trigger('browser-close');"); // fire 'exit' for inAppBrowser
				rippleToolSuiteBrowser.execute("ripple('emulatorBridge').window().ChildBrowser.onClose();"); // fire 'close' for childBrowser
			}
		});
		
		inAppBrowser.addDisposeListener(new DisposeListener() { // prevent permanent crashes on windows after skin changing
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				browserSimBrowser.setParent(browserSimParentComposite);
				browserSimParentComposite.layout();		
			}
		});
		
		inAppBrowser.addLocationListener(new LocationListener() {
			
			@Override
			public void changing(LocationEvent event) {
				rippleToolSuiteBrowser.execute("ripple('emulatorBridge').window().ChildBrowser.onLocationChange('"
						+ event.location + "');"); // fire 'ChildBrowser.onLocationChange' event
				rippleToolSuiteBrowser.execute("ripple('event').trigger('browser-start');"); // fire 'loadstart' event
			}
			
			@Override
			public void changed(LocationEvent event) {
				if (event.top) {
					rippleToolSuiteBrowser.execute("ripple('event').trigger('browser-stop');"); //  fire 'loadstop' event
				}
			}
			
		});
				
		browserSimBrowser.registerBrowserFunction("csInAppExecScript", new ExecScriptFunction(browserSimBrowser, inAppBrowser));
	}

	private static IBrowser createInAppBrowser(Composite browserSimParentComposite, IBrowser browserSimBrowser,
			Device device) {
		IBrowser inAppBrowser = new WebKitBrowserFactory().createBrowser(browserSimParentComposite, SWT.NONE);
		inAppBrowser.setUserAgent(device.getUserAgent());
		inAppBrowser.setLayoutData(browserSimBrowser.getLayoutData());
		return inAppBrowser;
	}
	
}

