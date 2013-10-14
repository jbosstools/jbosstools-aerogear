package org.jboss.tools.vpe.cordovasim;

import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.vpe.browsersim.model.preferences.CommonPreferences;
import org.jboss.tools.vpe.browsersim.model.preferences.SpecificPreferences;
import org.jboss.tools.vpe.browsersim.model.preferences.SpecificPreferencesStorage;
import org.jboss.tools.vpe.browsersim.ui.ManageDevicesDialog;
import org.jboss.tools.vpe.cordovasim.model.preferences.CordavaSimSpecificPreferencesStorage;
import org.jboss.tools.vpe.cordovasim.model.preferences.CordovaSimSpecificPreferences;

/**
 * @author Konstantin Marmalyukov (kmarmaliykov)
 */
public class CordovaSimManageDeviceDialog extends ManageDevicesDialog {

	public CordovaSimManageDeviceDialog(Shell parent, int style, CommonPreferences oldCommonPreferences,
			SpecificPreferences oldSpecificPreferences) {
		super(parent, style, oldCommonPreferences, oldSpecificPreferences);
	}

	@Override
	protected SpecificPreferencesStorage getSpecificPreferencesStorage() {
		return CordavaSimSpecificPreferencesStorage.INSTANCE;
	}
	
	
	@Override
	protected SpecificPreferences create(String selectedDeviceId, boolean useSkins, boolean enableLiveReload, int liveReloadPort, boolean enableTouchEvents) {
		return new CordovaSimSpecificPreferences(selectedDeviceId, useSkins, enableLiveReload, liveReloadPort, enableTouchEvents,
				oldSpecificPreferences.getOrientationAngle(), oldSpecificPreferences.getLocation(),
				((CordovaSimSpecificPreferences)oldSpecificPreferences).getCordovaBrowserLocation(),
				((CordovaSimSpecificPreferences)oldSpecificPreferences).getCordovaBrowserSize());
	}
}
