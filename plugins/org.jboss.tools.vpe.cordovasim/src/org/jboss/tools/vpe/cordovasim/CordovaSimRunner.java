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
import java.util.Arrays;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.vpe.browsersim.BrowserSimArgs;
import org.jboss.tools.vpe.browsersim.browser.PlatformUtil;
import org.jboss.tools.vpe.browsersim.ui.events.ExitListener;
import org.jboss.tools.vpe.browsersim.ui.events.SkinChangeEvent;
import org.jboss.tools.vpe.browsersim.ui.events.SkinChangeListener;
import org.jboss.tools.vpe.cordovasim.events.RippleInjector;
import org.jboss.tools.vpe.browsersim.ui.BrowserSim;
import org.jboss.tools.vpe.browsersim.ui.CocoaUIEnhancer;
import org.jboss.tools.vpe.browsersim.ui.ExceptionNotifier;

/**
 * @author Yahor Radtsevich (yradtsevich)
 * @author Ilya Buziuk (ibuziuk)
 */
public class CordovaSimRunner {
	private static CustomBrowserSim browserSim;
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (PlatformUtil.OS_MACOSX.equals(PlatformUtil.getOs())) {
			CocoaUIEnhancer.initializeMacOSMenuBar(Messages.CordovaSim_CORDOVA_SIM);
		}
		CordovaSimArgs cordovaSimArgs = CordovaSimArgs.parseArgs(args);
		BrowserSim.isStandalone = cordovaSimArgs.isStandalone();
		int port = Integer.parseInt(cordovaSimArgs.getPort());
		File rootFolder = new File(cordovaSimArgs.getRootFolder());
		Server server = null;
		try {
			server = ServerCreator.createServer(rootFolder.getAbsolutePath(), port);// XXX
			server.start();
			Connector connector = server.getConnectors()[0];
			port = connector.getLocalPort(); // for the case if port equals 0 is requested (any free port)

			final Display display = Display.getDefault();
			Shell shell = new Shell(display);
			shell.setLayout(new FillLayout());
			final Browser browser = new Browser(shell, SWT.WEBKIT);
			browser.setUrl("http://localhost:" + port + "/" + cordovaSimArgs.getStartPage() + "?enableripple=true");
			shell.addListener(SWT.Close, new Listener() {
				@Override
				public void handleEvent(Event event) {
					browserSim.getBrowser().getShell().close();
				}
			});
			browser.addOpenWindowListener(new OpenWindowListener() {
				private Browser oldBrowser;

				@Override
				public void open(WindowEvent event) {
					if (browserSim == null || browserSim.getBrowser().isDisposed()
							|| browserSim.getBrowser().getShell().isDisposed()) {
						createBrowserSim(browser);
					} else if (oldBrowser == browserSim.getBrowser()) {
						browserSim.reinitSkin();
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
			showPortInUseMessage(port);
		} finally {
			if (server != null) {
				server.stop();
				server.join();
			}
		}
	}
	
	private static void showPortInUseMessage(int port) throws Exception {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		String message = MessageFormat.format(Messages.ExceptionNotifier_PORT_IN_USE, port);
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
