/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.aerogear.hybrid.ui.wizard.export;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.extensions.NativeProjectBuilder;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;

public class NativeBinaryDestinationPage extends BaseExportWizardDestinationPage implements
		IOverwriteQuery {

	private static final String IMAGE_WIZBAN = "/icons/wizban/exportnativeprj_wiz.png";
	
	protected NativeBinaryDestinationPage(String pageName,
			IStructuredSelection selection) {
		super(pageName, selection);
		setImageDescriptor(HybridUI.getImageDescriptor(HybridUI.PLUGIN_ID, IMAGE_WIZBAN));
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		getPlatformsGroup().getTableViewer().setInput(HybridCore.getNativeProjectBuilders());
	}
	

	@Override
	public String queryOverwrite(String pathString) {

		final MessageDialog dialog = new MessageDialog(getShell(), 
				"Overwrite Files?", 
				null, 
				 pathString+ " already exists. Would you like to overwrite it?",
				 MessageDialog.QUESTION,
				 new String[] { IDialogConstants.YES_LABEL,
	                        IDialogConstants.NO_LABEL,
	                        IDialogConstants.CANCEL_LABEL },
	                        0);
		String[] response = new String[] { YES,  NO, CANCEL };
        //most likely to be called from non-ui thread
		getControl().getDisplay().syncExec(new Runnable() {
            public void run() {
                dialog.open();
            }
        });
        return dialog.getReturnCode() < 0 ? CANCEL : response[dialog
                .getReturnCode()];
	}
	
	@Override
	protected IContentProvider getPlatformContentProvider() {
		return new NativeProjectBuilderContentProvider();
	}

	@Override
	protected IBaseLabelProvider getPlatformLabelProvider() {
		return new NativeProjectBuilderLabelProvider();
	}
	
	public List<NativeProjectBuilder> getSelectedPlatforms(){
		Object[] checked = getPlatformsGroup().getTableViewer().getCheckedElements();
		ArrayList<NativeProjectBuilder> builders = new ArrayList<NativeProjectBuilder>(checked.length);
		for (int i = 0; i < checked.length; i++) {
			builders.add((NativeProjectBuilder)checked[i]);
		}
		return builders;
	}

}
