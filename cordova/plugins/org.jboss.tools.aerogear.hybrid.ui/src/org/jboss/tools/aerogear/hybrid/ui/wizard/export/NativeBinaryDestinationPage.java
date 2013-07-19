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

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.IOverwriteQuery;

public class NativeBinaryDestinationPage extends BaseExportWizardDestinationPage implements
		IOverwriteQuery {

	protected NativeBinaryDestinationPage(String pageName,
			IStructuredSelection selection) {
		super(pageName, selection);
	}

	@Override
	public String queryOverwrite(String pathString) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected IContentProvider getPlatformContentProvider() {
		return new ProjectGeneratorContentProvider();
	}

	@Override
	protected IBaseLabelProvider getPlatformLabelProvider() {
		return new ProjectGeneratorLabelProvider();
	}

}
