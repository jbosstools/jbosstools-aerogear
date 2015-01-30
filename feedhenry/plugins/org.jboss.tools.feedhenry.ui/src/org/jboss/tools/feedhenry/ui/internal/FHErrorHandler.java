/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * 	Contributors:
 * 		 Red Hat Inc. - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.jboss.tools.feedhenry.ui.internal;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.jboss.tools.feedhenry.ui.FHPlugin;
import org.jboss.tools.feedhenry.ui.cordova.internal.preferences.FHPreferenceConstants;

public class FHErrorHandler {
	
	public static final int ERROR_INVALID_PREFERENCES = 10;
	public static final int ERROR_UNAUTHORIZED = 401;
	
	public static boolean handle(Throwable e){
		if(e instanceof InvocationTargetException){
			Throwable cause = e.getCause();
			return handle(cause);
		}
		else if(e instanceof CoreException){
			CoreException ce = (CoreException)e;
			return handle(ce.getStatus());
		}else if(e instanceof TransportException){
			displayGitTransportMessage(e.getMessage());
			Status s = new Status(IStatus.INFO, FHPlugin.PLUGIN_ID, e.getMessage(), e);
			StatusManager.getManager().handle(s,StatusManager.LOG);
			return false;
		}else{
			Status s = new Status(IStatus.ERROR, FHPlugin.PLUGIN_ID, e.getMessage(), e);
			return handle(s);
		}
	}
	
	public static boolean handle(IStatus status){
		int code = status.getCode();
		switch (code) {
		case ERROR_UNAUTHORIZED: 
			return displayPreferences("Unauthorized", "You are not authorized to complete this action. This may indicate a problem with your API key. "
					+ "Would you like to correct it on preferences now ?");
		case ERROR_INVALID_PREFERENCES:
			return displayPreferences("Invalid Preferences","FeedHenry connection preferences are undefined or invalid. Correct preferences now?");
		default:
			StatusManager.getManager().handle(status);
			return false;
		}
	}
	
	private static boolean displayPreferences(String title, String message){
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		boolean define = MessageDialog.openQuestion(shell,title,message);
		if(define){
			PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(shell,
					FHPreferenceConstants.PREFERENCES_PAGE_ID, 
					null, null);
			dialog.open();
			return true;
		}
		return false;
	
	}
	
	private static void displayGitTransportMessage(String message){
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		MessageDialog.openError(shell, "Git Communication Error", 
				"Problem when cloning the application. This can be due to a network problem or missing security credentials. Refer to error log for details.");
	}
}
