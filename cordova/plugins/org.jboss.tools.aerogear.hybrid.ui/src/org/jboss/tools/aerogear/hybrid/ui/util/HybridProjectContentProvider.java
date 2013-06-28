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
package org.jboss.tools.aerogear.hybrid.ui.util;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;

public class HybridProjectContentProvider implements IStructuredContentProvider {
	
	private List<HybridProject> projects;
	
	@Override
	public void dispose() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		try {
			projects = (List<HybridProject>) newInput;
		} catch (ClassCastException e) {
			// Warn the developer that he is doing something wrong
			Assert.isTrue( false, "input is not of correct type this content provider can only work with List<HybridProject> type inputs");
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(projects == null ){
			projects = HybridCore.getHybridProjects();
		}
		return projects.toArray();
	}

}
