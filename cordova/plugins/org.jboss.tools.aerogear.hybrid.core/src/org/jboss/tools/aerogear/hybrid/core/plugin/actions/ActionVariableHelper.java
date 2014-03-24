package org.jboss.tools.aerogear.hybrid.core.plugin.actions;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.config.Preference;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.config.WidgetModel;

public final class ActionVariableHelper {
	
	
	
	public static String replaceVariables(HybridProject project, String xml ) throws CoreException{
		WidgetModel model = WidgetModel.getModel(project);
		Widget widget = model.getWidgetForRead();
		xml = xml.replaceAll("\\$PACKAGE_NAME", widget.getId());
		List<Preference> preferences = widget.getPreferences();
		for ( Preference preference : preferences) {
			String preferenceKey = "\\$"+preference.getName();
			if(xml.contains(preferenceKey)){
				xml = xml.replaceAll(preferenceKey, preference.getValue());
			}
		}
		return xml;
	}

}
