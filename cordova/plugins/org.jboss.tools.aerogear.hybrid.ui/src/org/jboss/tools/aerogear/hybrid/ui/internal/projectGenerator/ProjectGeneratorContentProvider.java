/*******************************************************************************
 * Copyright (c) 2013,2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.aerogear.hybrid.ui.internal.projectGenerator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.extensions.PlatformSupport;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;
/**
 * IStructuredContentProvider implementation for the {@link PlatformSupport} extension point. 
 * If a List of ProjectGenerators are passed to a ContentViewer this ProjectGenerator will use the 
 * input otherwise it will query the  {@link PlatformSupport#EXTENSION_POINT_ID} for a list.
 * 
 * @author Gorkem Ercan
 *
 */
public class ProjectGeneratorContentProvider implements IStructuredContentProvider {

	private List<PlatformSupport> platforms;


	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(platforms == null ){
			platforms = HybridCore.getPlatformSupports();
		}
		ArrayList<PlatformSupport> elements = new ArrayList<PlatformSupport>();
		IEvaluationService service = (IEvaluationService)PlatformUI.getWorkbench().getService(IEvaluationService.class);
		for (PlatformSupport generator : platforms) {
			try {
				if(generator.isEnabled(service.getCurrentState())){
					elements.add(generator);
				}
				
			} catch (CoreException e) {
				HybridUI.log(IStatus.ERROR, "Error filtering objects", e);
			}
		}
		return elements.toArray();
	}

	@Override
	public void dispose() {
		
	}
}
