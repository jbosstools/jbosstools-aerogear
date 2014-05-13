package org.jboss.tools.cordavasim.eclipse.callbacks;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.jboss.tools.vpe.browsersim.eclipse.Activator;
import org.jboss.tools.vpe.browsersim.eclipse.launcher.ExternalProcessCallback;
import org.jboss.tools.vpe.browsersim.eclipse.launcher.TransparentReader;
import org.jboss.tools.vpe.cordovasim.eclipse.launch.internal.CordovaSimLauncher;

public class StopServerCallback implements ExternalProcessCallback {
	private static final String STOP_SERVER_COMMAND = "org.jboss.tools.vpe.cordavasim.command.stop.server:"; //$NON-NLS-1$
	
	@Override
	public String getCallbackId() {
		return STOP_SERVER_COMMAND;
	}

	@Override
	public void call(String lastString, TransparentReader reader) throws IOException {
		Server server = CordovaSimLauncher.getServer();
		if (server != null) {
			try {
				server.stop();
				server.join();
			} catch (Exception e) {
				Activator.logError(e.getMessage(), e);
			}
		}
	}

}
