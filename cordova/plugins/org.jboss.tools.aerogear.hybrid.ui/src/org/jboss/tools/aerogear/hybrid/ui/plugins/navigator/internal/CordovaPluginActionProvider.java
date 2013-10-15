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
package org.jboss.tools.aerogear.hybrid.ui.plugins.navigator.internal;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPlugin;

public class CordovaPluginActionProvider extends CommonActionProvider {
	
	private static class UninstallAction extends Action{
		public UninstallAction() {
			super("Remove Cordova Plugin");
		}
		
		@Override
		public void run() {
			IStructuredSelection selection = getSelection();
			if(selection.isEmpty())
				return;
			Object o = selection.getFirstElement();
			if(o instanceof CordovaPlugin ){
				final CordovaPlugin plugin = (CordovaPlugin) o;
				final HybridProject project = HybridProject.getHybridProject(plugin.getFolder().getProject());
				try {
					PlatformUI.getWorkbench().getProgressService().run(false, true, new IRunnableWithProgress() {
						
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException,
								InterruptedException {
							try {
								project.getPluginManager().unInstallPlugin(plugin.getId(), new NullProgressMonitor());
							} catch (CoreException e) {
								throw new InvocationTargetException(e);
							}
							
						}
					});
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		private IStructuredSelection getSelection(){
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if(window != null ){
				ISelection selection = window.getSelectionService().getSelection();
				if(selection instanceof IStructuredSelection)
					return (IStructuredSelection)selection;
			}
			return StructuredSelection.EMPTY;
		}
		
	}

	public CordovaPluginActionProvider() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void fillContextMenu(IMenuManager menu) {
		menu.add(new UninstallAction());
	}

}
