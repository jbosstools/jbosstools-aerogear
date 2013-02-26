/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim.eclipse.callbacks;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.vpe.cordovasim.eclipse.Activator;
import org.jboss.tools.vpe.cordovasim.eclipse.util.CordovaSimLauncher;
import org.jboss.tools.vpe.cordovasim.eclipse.util.TransparentReader;


/**
 * Handler for the BrowserSim commands printed to the console in the following form:
 * <pre>org.jboss.tools.vpe.browsersim.ui.BrowserSim.command.openFile:file:///path/to/file</code>
 * 
 * @author Yahor Radtsevich (yradtsevich)
 */
public class OpenFileCallback implements CordovaSimCallback {
	private static final String OPEN_FILE_COMMAND = CordovaSimLauncher.CORDOVASIM_CLASS_NAME + ".command.openFile:"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.cordovasim.eclipse.callbacks.BrowserSimCallback#getCallbackId()
	 */
	@Override
	public String getCallbackId() {
		return OPEN_FILE_COMMAND;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.cordovasim.eclipse.callbacks.BrowserSimCallback#callback(java.io.InputStreamReader)
	 */
	@Override
	public void call(final String lastString, TransparentReader reader) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				String fileNameToOpen = lastString.substring(OPEN_FILE_COMMAND.length());
				File fileToOpen = new File(fileNameToOpen);

				if (fileToOpen.exists() && fileToOpen.isFile()) {
					IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
					IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					IWorkbenchPage page = window != null ? window.getActivePage() : null;

					if (page != null) {
						try {
							IDE.openEditorOnFileStore(page, fileStore);
						} catch (PartInitException e) {
							Activator.logError(e.getMessage(), e);
						}
					} else {
						Exception e = new Exception("Cannot obtain workbench page");
						Activator.logError(e.getMessage(), e);
					}
				} else {
					FileNotFoundException e = new FileNotFoundException("Cannot open file: " + fileNameToOpen);
					Activator.logError(e.getMessage(), e);
				}
			}
		});
	}
}
