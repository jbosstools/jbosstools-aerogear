package org.jboss.tools.aerogear.android.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidProjectGenerator;

public class AndroidLaunchShortcut implements ILaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		
		IStructuredSelection ssel = (IStructuredSelection) selection;
		Object selected = ssel.getFirstElement();
		IResource res = (IResource) selected;
		IProject project = res.getProject();
		
		AndroidProjectGenerator projectGenerator = new AndroidProjectGenerator(project, null);
		try {
			projectGenerator.generateNow(new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		// TODO Auto-generated method stub

	}

}
