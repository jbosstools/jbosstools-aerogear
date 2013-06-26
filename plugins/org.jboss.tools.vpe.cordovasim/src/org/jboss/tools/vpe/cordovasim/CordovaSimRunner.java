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

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.vpe.browsersim.BrowserSimArgs;
import org.jboss.tools.vpe.browsersim.browser.PlatformUtil;
import org.jboss.tools.vpe.browsersim.model.preferences.SpecificPreferences;
import org.jboss.tools.vpe.browsersim.ui.CocoaUIEnhancer;
import org.jboss.tools.vpe.browsersim.ui.ExceptionNotifier;
import org.jboss.tools.vpe.browsersim.ui.events.ExitListener;
import org.jboss.tools.vpe.browsersim.ui.events.SkinChangeEvent;
import org.jboss.tools.vpe.browsersim.ui.events.SkinChangeListener;
import org.jboss.tools.vpe.cordovasim.events.RippleInjector;
import org.jboss.tools.vpe.cordovasim.model.preferences.CordavaSimSpecificPreferencesStorage;
import org.jboss.tools.vpe.cordovasim.model.preferences.CordovaSimSpecificPreferences;
import org.jboss.tools.vpe.cordovasim.util.CordovaSimImageList;

/**
 * @author Yahor Radtsevich (yradtsevich)
 * @author Ilya Buziuk (ibuziuk)
 */
public class CordovaSimRunner {
	private static CustomBrowserSim browserSim;
	private static final String[] CORDOVASIM_ICONS = {"icons/cordovasim_36px.png", "icons/cordovasim_48px.png", "icons/cordovasim_72px.png", "icons/cordovasim_96px.png"};

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (PlatformUtil.OS_MACOSX.equals(PlatformUtil.getOs())) {
			CocoaUIEnhancer.initializeMacOSMenuBar(Messages.CordovaSim_CORDOVA_SIM);
		}
		CordovaSimArgs cordovaSimArgs = CordovaSimArgs.parseArgs(args);
		BrowserSimArgs.standalone = CordovaSimArgs.standalone;
		int port = Integer.parseInt(cordovaSimArgs.getPort());
		File rootFolder = new File(cordovaSimArgs.getRootFolder());
		Server server = null;
		try {
			server = ServerCreator.createServer(rootFolder.getAbsolutePath(), port);// XXX
			server.start();
			Connector connector = server.getConnectors()[0];
			port = connector.getLocalPort(); // for the case if port equals 0 is requested (any free port)

			final Display display = Display.getDefault();
			final Shell shell = new Shell(display);
			setShellAttributes(shell);
			shell.setLayout(new FillLayout());
			final Browser browser = new Browser(shell, SWT.WEBKIT);
			browser.setUrl("http://localhost:" + port + "/" + cordovaSimArgs.getStartPage() + "?enableripple=true");
			shell.addListener(SWT.Close, new Listener() {
				@Override
				public void handleEvent(Event event) {
					browserSim.getBrowser().getShell().close();
				}
			});
			
			final CordovaSimSpecificPreferences sp = loadPreferences();
			
			if (sp.getCordovaBrowserSize() != null) {
				shell.setSize(sp.getCordovaBrowserSize());
			} else {
				sp.setCordovaBrowserSize(shell.getSize());
			}
			if (sp.getCordovaBrowserLocation() != null) {
				shell.setLocation(sp.getCordovaBrowserLocation());
			} else {
				sp.setCordovaBrowserLocation(shell.getLocation());
			}
			
			browser.addOpenWindowListener(new OpenWindowListener() {
				private Browser oldBrowser;

				@Override
				public void open(WindowEvent event) {
					if (browserSim == null || browserSim.getBrowser().isDisposed()
							|| browserSim.getBrowser().getShell().isDisposed()) {
						createBrowserSim(sp, browser);
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
			shell.addControlListener(new ControlAdapter() {
				@Override
				public void controlMoved(ControlEvent e) {
					if (browserSim != null) {
						browserSim.getSpecificPreferences().setCordovaBrowserLocation(shell.getLocation());
					}
					super.controlMoved(e);
				}
			});
			shell.addListener(SWT.Resize, new Listener() {
				public void handleEvent(Event e) {
					if (browserSim != null) {
						browserSim.getSpecificPreferences().setCordovaBrowserSize(shell.getSize());
					}
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

	private static void createBrowserSim(final SpecificPreferences sp, final Browser browser) {
		Shell parentShell = browser.getShell();
		if (parentShell != null) {
			browserSim = new CustomBrowserSim("about:blank", parentShell);
			browserSim.open(sp, null);
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
	
	private static Image[] initImages(Shell shell) {
		CordovaSimImageList imageList = new CordovaSimImageList(shell);
		Image[] icons = new Image[CORDOVASIM_ICONS.length];
		for (int i = 0; i < CORDOVASIM_ICONS.length; i++) {
			icons[i] = imageList.getImage(CORDOVASIM_ICONS[i]);
		}

		return icons;
	}


	private static CordovaSimSpecificPreferences loadPreferences() {
		CordovaSimSpecificPreferences sp = (CordovaSimSpecificPreferences) CordavaSimSpecificPreferencesStorage.INSTANCE.load();
		if (sp == null) {
			sp = (CordovaSimSpecificPreferences) CordavaSimSpecificPreferencesStorage.INSTANCE.loadDefault();
		}
		return sp;
	}


	private static void setShellAttributes(Shell shell) {
		Image[] icons = initImages(shell);
		shell.setImages(icons);
		shell.setText(Messages.CordovaSim_CORDOVA_SIM);
	}
}
