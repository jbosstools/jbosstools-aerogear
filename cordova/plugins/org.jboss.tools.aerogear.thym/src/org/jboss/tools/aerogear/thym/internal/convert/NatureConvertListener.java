/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.aerogear.thym.internal.convert;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.thym.core.natures.HybridAppNature;
import org.eclipse.ui.IStartup;
import org.jboss.tools.aerogear.thym.ThymPlugin;

public class NatureConvertListener implements IResourceChangeListener, IStartup {
	
	private static final String LEGACY_NATURE_ID = "org.jboss.tools.aerogear.hybrid.core.HybridAppNature";
	private static NatureConvertListener instance;

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta[] projectDeltas = event.getDelta().getAffectedChildren();
		for (IResourceDelta delta : projectDeltas) {
			if (delta.getResource().getType() == IResource.PROJECT) {
				final IProject project = delta.getResource().getProject();
				try {
					if (project.isOpen() && project.hasNature(LEGACY_NATURE_ID)
							&& !project.hasNature(HybridAppNature.NATURE_ID)) {
						WorkspaceJob job = new WorkspaceJob(
								"Convert to Thym nature") {

							@Override
							public IStatus runInWorkspace(
									IProgressMonitor monitor)
									throws CoreException {
								IProjectDescription desc = project
										.getDescription();
								String[] natures = desc.getNatureIds();
								for (int i = 0; i < natures.length; i++) {
									if (natures[i].equals(LEGACY_NATURE_ID)) {
										natures[i] = HybridAppNature.NATURE_ID;
									}
								}
								desc.setNatureIds(natures);
								project.setDescription(desc, monitor);
								return Status.OK_STATUS;
							}
						};
						job.schedule();
					}
				} catch (CoreException e) {
					ThymPlugin.log(IStatus.WARNING,
							"error while reading natures", e);
				}
			}
		}
	}

	@Override
	public void earlyStartup() {
		this.instance = new NatureConvertListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(instance,IResourceChangeEvent.POST_CHANGE);
	}
	
	public static final NatureConvertListener getInstance(){
		return NatureConvertListener.instance;
	}

}
