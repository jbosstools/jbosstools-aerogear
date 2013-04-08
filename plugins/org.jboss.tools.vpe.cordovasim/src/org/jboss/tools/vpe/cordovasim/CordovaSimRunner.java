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
import java.net.BindException;
import java.text.MessageFormat;

import org.eclipse.jetty.server.Server;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.vpe.browsersim.ui.events.ExitListener;
import org.jboss.tools.vpe.browsersim.ui.events.SkinChangeEvent;
import org.jboss.tools.vpe.browsersim.ui.events.SkinChangeListener;
import org.jboss.tools.vpe.cordovasim.events.RippleInjector;
import org.jboss.tools.vpe.browsersim.BrowserSimArgs;
import org.jboss.tools.vpe.browsersim.ui.BrowserSim;
import org.jboss.tools.vpe.browsersim.ui.ExceptionNotifier;

/**
 * @author Yahor Radtsevich (yradtsevich)
 * @author Ilya Buziuk (ibuziuk)
 */
public class CordovaSimRunner {
	private static final int PORT = 4400;
	private static CustomBrowserSim browserSim;
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		BrowserSimArgs browserSimArgs = BrowserSimArgs.parseArgs(args);
		BrowserSim.isStandalone = browserSimArgs.isStandalone();
		File file = new File(browserSimArgs.getPath());
		Server server = null;
		try {
			server = ServerCreator.createServer(file.getParent(), PORT);// XXX
			server.start();

			final Display display = Display.getDefault();
			Shell shell = new Shell(display);
			shell.setLayout(new FillLayout());
			final Browser browser = new Browser(shell, SWT.WEBKIT);
			browser.setUrl("http://localhost:" + PORT + "/" + file.getName() + "?enableripple=true");
			browser.addOpenWindowListener(new OpenWindowListener() {
				private Browser oldBrowser;

				@Override
				public void open(WindowEvent event) {
					if (browserSim == null || browserSim.getBrowser().isDisposed()
							|| browserSim.getBrowser().getShell().isDisposed()) {
						createBrowserSim(browser);
					} else if (oldBrowser == browserSim.getBrowser()) {
						browserSim.getBrowser().getShell().dispose();
						createBrowserSim(browser);
						browserSim.getBrowser().addLocationListener(new RippleInjector());
					} else if (oldBrowser != browserSim.getBrowser()) {
						browserSim.getBrowser().addLocationListener(new RippleInjector());
					}
					event.browser = browserSim.getBrowser();
					oldBrowser = browserSim.getBrowser();
				}
			});

			shell.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();
		} catch (BindException e) {
			showErrorMessage();
		} finally {
			if (server != null) {
				server.stop();
				server.join();
			}
		}
	}
	
	private static void showErrorMessage() throws Exception {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		String message = MessageFormat.format(Messages.ExceptionNotifier_PORT_IN_USE, PORT);
		ExceptionNotifier.showErrorMessage(shell, message);
		display.dispose();
	}

	private static void createBrowserSim(final Browser browser) {
		browserSim = new CustomBrowserSim("about:blank");
		browserSim.open();
		browserSim.addSkinChangeListener(new SkinChangeListener() {
			@Override
			public void skinChanged(SkinChangeEvent event) {
				browser.refresh();
			}
		});
		browserSim.addExitListener(new ExitListener() {
			
			@Override
			public void exit() {
				browser.getShell().dispose();
			}
		});
		browserSim.getBrowser().addLocationListener(new RippleInjector());
	}
	
}
