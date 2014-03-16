package org.jboss.tools.aerogear.hybrid.android.ui.internal.statushandler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.jboss.tools.aerogear.hybrid.android.ui.internal.preferences.AndroidPreferencePage;
import org.jboss.tools.aerogear.hybrid.ui.status.AbstractStatusHandler;

public class MissingSDKStatusHandler extends AbstractStatusHandler {

	@Override
	public void handle(IStatus status) {
		boolean define = MessageDialog.openQuestion(getShell(), "Missing Android SDK",
				"Location of the Android SDK must be defined. Define Now?");
		if(define){
			PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(getShell(), AndroidPreferencePage.PAGE_ID, 
					null, null);
			dialog.open();
		}
	}

	@Override
	public void handle(CoreException e) {
		handle(e.getStatus());
	}
	
	private Shell getShell(){
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if (windows.length > 0) {
				return windows[0].getShell();
			}
		} 
		return window.getShell();
	}
	


}
