package org.jboss.tools.vpe.cordovasim;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.Rule;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.vpe.browsersim.model.Device;
import org.jboss.tools.vpe.browsersim.model.DevicesList;
import org.jboss.tools.vpe.browsersim.model.DevicesListStorage;
import org.jboss.tools.vpe.browsersim.ui.BrowserSim;

public class CordovaSim {
	private static final int PORT = 4400;
	private static BrowserSim browserSim;
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Server server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(PORT);
		server.addConnector(connector);

		ServletHolder userAgentServletHolder = new ServletHolder(new StaticResponseServlet("OK"));
		ServletHandler userAgentServletHandler = new ServletHandler();
		userAgentServletHandler.addServletWithMapping(userAgentServletHolder, "/ripple/user-agent");
		
		ServletHolder proxyServletHolder = new ServletHolder(new CrossOriginProxyServlet("tinyhippos_rurl"));
		proxyServletHolder.setAsyncSupported(true);
		ServletHandler proxyServletHandler = new ServletHandler();
		proxyServletHandler.addServletWithMapping(proxyServletHolder, "/ripple/xhr_proxy");
		
		
		ResourceHandler rippleResourceHandler = new ResourceHandler();
		rippleResourceHandler.setDirectoriesListed(true);
		rippleResourceHandler.setWelcomeFiles(new String[] { "index.html" });
		rippleResourceHandler.setResourceBase("./ripple-ui");
		ContextHandler rippleContextHandler = new ContextHandler("/ripple/assets");
		rippleContextHandler.setHandler(rippleResourceHandler);
		
		ResourceHandler wwwResourceHandler = new ResourceHandler();
		wwwResourceHandler.setDirectoriesListed(true);
		wwwResourceHandler.setResourceBase("./www");
		ContextHandler wwwContextHandler = new ContextHandler("/");
		wwwContextHandler.setHandler(wwwResourceHandler);
		

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] {
				userAgentServletHandler,
				wwwContextHandler,
				proxyServletHandler,
				rippleContextHandler,
				new DefaultHandler(),
			});
		
		RewriteHandler rewriteHandler = new RewriteHandler();
		rewriteHandler.setRewriteRequestURI(true);
		rewriteHandler.setRewritePathInfo(true);
		rewriteHandler.setHandler(handlers);
		rewriteHandler.addRule(new Rule() {
			@Override
			public String matchAndApply(String target, HttpServletRequest request,
					HttpServletResponse response) throws IOException {
				if ("true".equals(request.getParameter("enableripple"))) {
					return "/ripple/assets/index.html";
				} else {
					return null;
				}
			}
		});
		server.setHandler(rewriteHandler);

		server.start();
		
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Browser browser = new Browser(shell, SWT.WEBKIT);
		browser.setUrl("http://localhost:" + PORT + "/accelerometer.html?enableripple=true");
		browser.addOpenWindowListener(new OpenWindowListener() {
 			
			@Override
			public void open(WindowEvent event) {
				boolean STANDALONE = true;
				DevicesList devicesList = DevicesListStorage.loadUserDefinedDevicesList(STANDALONE);
				if (devicesList == null) {
					devicesList = DevicesListStorage.loadDefaultDevicesList();
				}
				
				Device defaultDevice = devicesList.getDevices().get(devicesList.getSelectedDeviceIndex());
				if (browserSim != null && browserSim.skin.getShell() != null) {
					browserSim.skin.getShell().dispose();
				}
				browserSim = new BrowserSim(display, "about:blank", STANDALONE);
				browserSim.initDevicesListHolder();
				browserSim.devicesListHolder.setDevicesList(devicesList);
				browserSim.initSkin(BrowserSim.getSkinClass(defaultDevice, devicesList.getUseSkins()), devicesList.getLocation());
				browserSim.devicesListHolder.notifyObservers();
				browserSim.skin.getBrowser().addLocationListener(new LocationAdapter() {
					public void changed(LocationEvent event) {
						Browser browser = (Browser) event.widget;
						browser.execute("if (window.opener.ripple) { window.opener.ripple('bootstrap').inject(window, document);}");
						browser.forceFocus();
					}
				});
				
				event.browser = browserSim.skin.getBrowser();				
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
