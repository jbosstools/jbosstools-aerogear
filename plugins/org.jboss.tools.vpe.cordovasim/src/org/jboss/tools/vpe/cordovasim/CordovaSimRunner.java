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
package org.jboss.tools.vpe.cordovasim;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.vpe.browsersim.BrowserSimArgs;
import org.jboss.tools.vpe.browsersim.ui.BrowserSim;

/**
 * @author Yahor Radtsevich (yradtsevich)
 * @author Ilya Buziuk (ibuziuk)
 */
public class CordovaSimRunner {
	private static final int PORT = 4400;
	private static BrowserSim browserSim;
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		BrowserSimArgs browserSimArgs = BrowserSimArgs.parseArgs(args);
		BrowserSim.isStandalone = browserSimArgs.isStandalone();
		File file = new File(browserSimArgs.getPath());
		
		Server server = ServerCreator.createServer(file.getParent(), PORT);// XXX
		server.start();
		
		final Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Browser browser = new Browser(shell, SWT.WEBKIT);
		browser.setUrl("http://localhost:" + PORT + "/" + file.getName() + "?enableripple=true");
		browser.addOpenWindowListener(new OpenWindowListener() {
 			
			@Override
			public void open(WindowEvent event) {
				if (browserSim != null && browserSim.getBrowser().getShell() != null) { //XXX
					browserSim.getBrowser().getShell().dispose();
				}
				browserSim = new BrowserSim("about:blank");
				browserSim.open();
				browserSim.getBrowser().addLocationListener(new LocationAdapter() {
					public void changed(LocationEvent event) {
						Browser browser = (Browser) event.widget;
						browser.execute("if (window.opener.ripple) { window.opener.ripple('bootstrap').inject(window, document);}");
						browser.forceFocus();
					}
				});
				event.browser = browserSim.getBrowser();				
			}
		});
		
		shell.open();
		
		while (!shell.isDisposed()) {
		  if (!display.readAndDispatch())
		     display.sleep();
		}
		display.dispose(); 
		
		server.stop();
		server.join();
	}
}
