package org.jboss.tools.cordavasim.eclipse.callbacks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.vpe.browsersim.eclipse.launcher.BrowserSimLauncher;
import org.jboss.tools.vpe.browsersim.eclipse.launcher.ExternalProcessCallback;
import org.jboss.tools.vpe.browsersim.eclipse.launcher.TransparentReader;
import org.jboss.tools.vpe.cordovasim.eclipse.launch.internal.CordovaSimLauncher;

public class CordovaSimRestartCallback implements ExternalProcessCallback{
	//if you change this parameter, see also @org.jbosstools.browsersim.ui.BrowserSim
	private static final String NOT_STANDALONE = BrowserSimLauncher.NOT_STANDALONE;	
	private static final String CORDOVASIM_RESTART_COMMAND = "org.jboss.tools.vpe.cordavasim.command.restart:"; //$NON-NLS-1$
	private static final String CORDOVASIM_RESTART_COMMAND_END = "org.jboss.tools.vpe.cordavasim.command.restart.end"; //$NON-NLS-1$
	
	@Override
	public String getCallbackId() {
		return CORDOVASIM_RESTART_COMMAND;
	}

	@Override
	public void call(String lastString, TransparentReader reader) throws IOException {
		List<String> parameters = new ArrayList<String>();
		parameters.add(NOT_STANDALONE);
		String nextLine;
		while ((nextLine = reader.readLine(false)) != null && !nextLine.startsWith(CORDOVASIM_RESTART_COMMAND_END)) {
			parameters.add(nextLine);
		}

		CordovaSimLauncher.launchCordovaSim(parameters);
	}

}