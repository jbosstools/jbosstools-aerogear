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
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class InAppBrowserLoader {

	public static boolean isInAppBrowserEvent(WindowEvent openWindowEvent) {
		Browser parentBrowser = (Browser) openWindowEvent.widget;
		return Boolean.TRUE.equals(parentBrowser.evaluate("return !!window.needToOpenInAppBrowser"));
	}
	
	public static void processInAppBrowser(final Browser rippleToolBarBrowser, final Browser browserSimBrowser, WindowEvent openWindowEvent) {
		rippleToolBarBrowser.execute("window.needToOpenInAppBrowser = false");
		final Composite browserSimParentComposite = browserSimBrowser.getParent();
		final Browser inAppBrowser = new Browser(browserSimParentComposite, SWT.WEBKIT);
		inAppBrowser.setLayoutData(browserSimBrowser.getLayoutData());
		browserSimBrowser.setParent(inAppBrowser); // hiding browserSim's browser by changing parent   
		
		openWindowEvent.browser = inAppBrowser;  
		browserSimParentComposite.layout();
	
		inAppBrowser.addCloseWindowListener(new CloseWindowListener() {			
			
			@Override
			public void close(WindowEvent event) {
				browserSimBrowser.setParent(browserSimParentComposite);
				inAppBrowser.dispose();
				browserSimParentComposite.layout();		
				rippleToolBarBrowser.execute("ripple('event').trigger('browser-close');"); // fire 'exit' event
			}
		});
		
		inAppBrowser.addLocationListener(new LocationListener() {
			
			@Override
			public void changing(LocationEvent event) {
				if (event.top) {
					if (isChildBrowserPluginPlugged(rippleToolBarBrowser)) {
						rippleToolBarBrowser.execute("ripple('emulatorBridge').window().ChildBrowser.onLocationChange('" + event.location  +"');"); // fire 'ChildBrowser.onLocationChange' event 
					} else {
						rippleToolBarBrowser.execute("ripple('event').trigger('browser-start');"); // fire 'loadstart' event
					}
				}
			}
			
			@Override
			public void changed(LocationEvent event) {
				if (event.top) {
					rippleToolBarBrowser.execute("ripple('event').trigger('browser-stop');"); //  fire 'loadstop' event
				}
			}
		});
				
		new ExecScriptFunction(browserSimBrowser, inAppBrowser, "csInAppExecScript");
	}
	
	private static boolean isChildBrowserPluginPlugged (Browser browser) {
		return (Boolean) browser.evaluate("return !! ripple('emulatorBridge').window().ChildBrowser");
	}
}

