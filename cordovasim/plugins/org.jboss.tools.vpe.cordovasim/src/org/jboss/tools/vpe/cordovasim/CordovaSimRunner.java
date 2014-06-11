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

import java.text.MessageFormat;

import org.eclipse.jetty.server.Server;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.vpe.browsersim.browser.ExtendedOpenWindowListener;
import org.jboss.tools.vpe.browsersim.browser.ExtendedWindowEvent;
import org.jboss.tools.vpe.browsersim.browser.IBrowser;
import org.jboss.tools.vpe.browsersim.browser.PlatformUtil;
import org.jboss.tools.vpe.browsersim.browser.WebKitBrowserFactory;
import org.jboss.tools.vpe.browsersim.browser.javafx.JavaFXBrowser;
import org.jboss.tools.vpe.browsersim.devtools.DevToolsDebuggerServer;
import org.jboss.tools.vpe.browsersim.model.preferences.SpecificPreferences;
import org.jboss.tools.vpe.browsersim.ui.CocoaUIEnhancer;
import org.jboss.tools.vpe.browsersim.ui.ExceptionNotifier;
import org.jboss.tools.vpe.browsersim.ui.events.ExitListener;
import org.jboss.tools.vpe.browsersim.ui.events.SkinChangeEvent;
import org.jboss.tools.vpe.browsersim.ui.events.SkinChangeListener;
import org.jboss.tools.vpe.browsersim.util.BrowserSimUtil;
import org.jboss.tools.vpe.cordovasim.events.RippleInjector;
import org.jboss.tools.vpe.cordovasim.model.preferences.CordavaSimSpecificPreferencesStorage;
import org.jboss.tools.vpe.cordovasim.model.preferences.CordovaSimSpecificPreferences;
import org.jboss.tools.vpe.cordovasim.plugin.inappbrowser.InAppBrowserLoader;
import org.jboss.tools.vpe.cordovasim.util.CordovaSimImageList;
import org.jboss.tools.vpe.cordovasim.util.CordovaSimUtil;
import org.jboss.tools.vpe.cordovasim.util.StartPageParametersUtil;

/**
 * @author Yahor Radtsevich (yradtsevich)
 * @author Ilya Buziuk (ibuziuk)
 */
public class CordovaSimRunner {
	public static final String PLUGIN_ID = "org.jboss.tools.vpe.cordovasim"; //$NON-NLS-1$
	private static final String STOP_SERVER_COMMAND = "org.jboss.tools.vpe.cordavasim.command.stop.server:"; //$NON-NLS-1$
	
	private static CustomBrowserSim browserSim;
	private static final String[] CORDOVASIM_ICONS = {"icons/cordovasim_36px.png", "icons/cordovasim_48px.png", "icons/cordovasim_72px.png", "icons/cordovasim_96px.png"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	private static boolean isJavaFxAvailable;
	private static boolean isWebKitAvailable;
	
	static {
		if (PlatformUtil.OS_MACOSX.equals(PlatformUtil.getOs())) {
			CocoaUIEnhancer.initializeMacOSMenuBar(Messages.CordovaSim_CORDOVA_SIM);
		}
	}

	static { 
		String platform = PlatformUtil.getOs();
		isJavaFxAvailable = false;
		
		boolean isLinux = PlatformUtil.OS_LINUX.equals(platform);
		
		// Trying to load javaFx libs except Linux GTK3 case
		if (!(isLinux && !BrowserSimUtil.isRunningAgainstGTK2())) {
			isJavaFxAvailable = BrowserSimUtil.loadJavaFX();
		}

		isWebKitAvailable = BrowserSimUtil.isWebkitAvailable();
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		CordovaSimArgs.parseArgs(args);
		startCordovaSim();
	}
	
	private static void startCordovaSim() throws Exception {
		Display display = Display.getDefault();
		try {
			if (!isJavaFxAvailable && !isWebKitAvailable) {
				String errorMessage = ""; //$NON-NLS-1$
				String os = PlatformUtil.getOs();
				if (PlatformUtil.OS_LINUX.equals(os)) {
					errorMessage = MessageFormat.format(
							org.jboss.tools.vpe.browsersim.ui.Messages.BrowserSim_NO_WEB_ENGINES_LINUX,
							Messages.CordovaSim_CORDOVA_SIM);
				} else if(PlatformUtil.OS_WIN32.equals(os)) {
					errorMessage = MessageFormat.format(
							org.jboss.tools.vpe.browsersim.ui.Messages.BrowserSim_NO_WEB_ENGINES_WINDOWS,
							Messages.CordovaSim_CORDOVA_SIM);
				}
				throw new SWTError(errorMessage);
			}
			
			Shell shell = createCordovaSim(display);
			while (!shell.isDisposed()) {
				if (!shell.getDisplay().readAndDispatch())
					shell.getDisplay().sleep();
			}
		} catch (SWTError e) {
			ExceptionNotifier.showBrowserSimLoadError(new Shell(Display.getDefault()), e, Messages.CordovaSim_CORDOVA_SIM);
		} catch (Throwable t) {
			CordovaSimLogger.logError(t.getMessage(), t);
		} finally {
			if (!CordovaSimArgs.isRestartRequired()) { 
				sendStopServerCommand(); // If no need to restart CS with a new engine - stop the server
			} else {
				if (display != null) {
					display.dispose();
				}
			}
		}
	}

	private static void createBrowserSim(final SpecificPreferences sp, final IBrowser rippleToolBarBrowser, final String homeUrl) {
		Shell parentShell = rippleToolBarBrowser.getShell();
		if (parentShell != null) {
			browserSim = new CustomBrowserSim(homeUrl, parentShell);
			browserSim.setRippleToolBarBrowser(rippleToolBarBrowser);
			browserSim.open(sp, null);
			
			browserSim.addSkinChangeListener(new SkinChangeListener() {
				@Override
				public void skinChanged(SkinChangeEvent event) {
					rippleToolBarBrowser.refresh();
				}
			});
			
			browserSim.addExitListener(new ExitListener() {
				@Override
				public void exit() {
					rippleToolBarBrowser.getShell().dispose();
				}
			});
			
			IBrowser browser = browserSim.getBrowser();
			browser.addLocationListener(new RippleInjector());			
			browser.registerBrowserFunction("csProceessUnsupportedPluginsPopUp", new ProceessUnsupportedPluginsPopUp(browserSim)); //$NON-NLS-1$
			CordovaSimUtil.fixScrollbarStylesForMac(browser);
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
	
	private static Shell createCordovaSim(Display display) throws Exception {
		final CordovaSimSpecificPreferences sp = loadPreferences();
		
		if (!isWebKitAvailable) {
			if (isJavaFxAvailable) {
				sp.setJavaFx(true);
			}
		}
		if (!isJavaFxAvailable) {
			sp.setJavaFx(false);
		}

		final Shell shell = new Shell(display);
		setShellAttributes(shell);
		shell.setLayout(new FillLayout());
		
		
		final IBrowser rippleToolBarBrowser = new WebKitBrowserFactory().createBrowser(shell, SWT.WEBKIT, sp.isJavaFx());
		final String homeUrl = CordovaSimArgs.getHomeUrl();
		
		String startPageParameters = StartPageParametersUtil.getStartPageParameters(homeUrl);
		if (startPageParameters != null) {
			processStartPageParameters(rippleToolBarBrowser, startPageParameters);
		}
					
		rippleToolBarBrowser.setUrl(StartPageParametersUtil.getRippleHomeUrl(homeUrl));
		
		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				browserSim.getBrowser().getShell().close();
			}
		});
		
		if (sp.getCordovaBrowserSize() != null) {
			shell.setSize(sp.getCordovaBrowserSize());
		} else {
			sp.setCordovaBrowserSize(shell.getSize());
		}
		
		Point location = sp.getCordovaBrowserLocation();
		if (location != null) {
			BrowserSimUtil.setShellLocation(shell, shell.getSize(), location);
		} else {
			sp.setCordovaBrowserLocation(shell.getLocation());
		}
		
		rippleToolBarBrowser.addOpenWindowListener(new ExtendedOpenWindowListener() {
			private IBrowser oldBrowser;

			@Override
			public void open(ExtendedWindowEvent event) {
				if (InAppBrowserLoader.isInAppBrowserEvent(event) && (browserSim != null)) {
					InAppBrowserLoader.processInAppBrowser(rippleToolBarBrowser, browserSim, event);
				} else {
					if (browserSim == null || browserSim.getBrowser().isDisposed()
						|| browserSim.getBrowser().getShell().isDisposed()) {
						createBrowserSim(sp, rippleToolBarBrowser, homeUrl);
					} else if (oldBrowser == browserSim.getBrowser()) {
						browserSim.reinitSkin();
						browserSim.getBrowser().addLocationListener(new RippleInjector());
					} else if (oldBrowser != browserSim.getBrowser()) {
						browserSim.getBrowser().addLocationListener(new RippleInjector());
					}					
					event.browser = browserSim.getBrowser();
					oldBrowser = browserSim.getBrowser();
					
					try {
			            if (browserSim.getBrowser() instanceof JavaFXBrowser  && !Server.STARTED.equals(DevToolsDebuggerServer.getServerState())) {
			                DevToolsDebuggerServer.startDebugServer(((JavaFXBrowser)browserSim.getBrowser()).getDebugger());
			            }					
			        } catch (Exception e) {
						CordovaSimLogger.logError(e.getMessage(), e);
					}
				}
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
		return shell;
	}
	
	// JBIDE-16389 Query parameters are not allowed in the runtime configuration for CordovaSim
	private static void processStartPageParameters(final IBrowser rippleToolBarBrowser, final String startPageParameters) { 
		final String addingStartPageParametersFunction = "window._startPageParameters = '" + startPageParameters + "';"; //$NON-NLS-1$ //$NON-NLS-2$
		rippleToolBarBrowser.addLocationListener(new LocationAdapter() {
			@Override
			public void changed(LocationEvent event) {
				rippleToolBarBrowser.execute(addingStartPageParametersFunction);
			}
		});
	}


	private static void sendStopServerCommand() {
		System.out.println(STOP_SERVER_COMMAND + " Server on port " + CordovaSimArgs.getPort() + " was stopped"); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
