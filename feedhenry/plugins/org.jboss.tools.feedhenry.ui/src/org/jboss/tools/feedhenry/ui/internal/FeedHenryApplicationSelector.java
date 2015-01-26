/*******************************************************************************
 * Copyright (c) 2014,2015 Red Hat, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * 	Contributors:
 * 		 Red Hat Inc. - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.jboss.tools.feedhenry.ui.internal;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.statushandlers.StatusManager;
import org.jboss.tools.feedhenry.ui.FHPlugin;
import org.jboss.tools.feedhenry.ui.cordova.internal.preferences.FHPreferences;
import org.jboss.tools.feedhenry.ui.model.FeedHenry;
import org.jboss.tools.feedhenry.ui.model.FeedHenryApplication;
import org.jboss.tools.feedhenry.ui.model.FeedHenryException;
import org.jboss.tools.feedhenry.ui.model.FeedHenryProject;
/**
 * UI component for selecting {@link FeedHenryApplication}s 
 * 
 * @author Gorkem Ercan
 *
 */
public class FeedHenryApplicationSelector {
	
	/**
	 * Callback for changes on the selected application list.
	 * 
	 * @author Gorkem Ercan
	 *
	 */
	public static interface SelectionChangeCallback{
		public void selectionChanged(List<FeedHenryApplication> newSelection);
	}
	
	private final FeedHenryApplicationSelectionPart block;
	private final FHAppLabelProvider labelProvider = new FHAppLabelProvider();
	private final FHApplicationContentProvider contentProvider = new FHApplicationContentProvider();
	private SelectionChangeCallback selectionCallback;
	private List<FeedHenryProject> projects;
	
	/**
	 * Default constructor
	 */
	public FeedHenryApplicationSelector(){
		block = new FeedHenryApplicationSelectionPart(this);
		block.setContentProvider(contentProvider);
		block.setLabelProvider(labelProvider);		
	}
	
	/**
	 * Creates the UI. 
	 * Must be called from UI (main) thread.
	 * 
	 * @param parent
	 * @return selector
	 * @throws CoreException 
	 */
	public FeedHenryApplicationSelector createSelectorUI(final Composite parent) {
		Assert.isTrue(parent.getDisplay().getThread() == Thread.currentThread(), "Must be called from the UI thread");
		block.createContent(parent);
		if(projects != null ){
			block.setInput(projects);
		}
		return this;
	}
	
	/**
	 * Retrieves the application list from the FeedHenry instance. 
	 * User the context to run the retrieval job. The retrieved 
	 * projects are populated to the UI if UI was created  earlier.
	 * 
	 * {@see #createSelectorUI(Composite)}  
	 * @param context
	 * @return 
	 */
	public FeedHenryApplicationSelector retrieveProjects(IRunnableContext context){
		Assert.isNotNull(context);
		try {
			context.run(true, true, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						prepareInput(monitor);
						if(block != null ){
							Display display = PlatformUI.getWorkbench().getDisplay();
							display.asyncExec(new Runnable() {
								@Override
								public void run() {
									block.setInput(projects);
								}
							});
						}
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
				}
			});
		} catch (InvocationTargetException e){
			if(FHErrorHandler.handle(e)){
				retrieveProjects(context);
			}
		}catch(InterruptedException e) {
			FHPlugin.log(IStatus.INFO, "Project retrieaval interrupted", e);
		}
		return this;
	}
	
	/**
	 * Sets the filter that determines which projects are displayed.
	 * 
	 * @param filter
	 * @return
	 */
	public FeedHenryApplicationSelector setProjectsFilter(IFilter filter){
		this.contentProvider.setProjectFilter(filter);
		this.block.setProjectFilter(filter);
		return this;
	}
	
	/**
	 * Sets a filter that is used to determine the projects that will be 
	 * disabled. For a project to be seen disabled it needs to be also 
	 * selected  by the projects filter. 
	 * @see #setProjectsFilter(IFilter) 
	 * @param filter
	 * @return
	 */
	public FeedHenryApplicationSelector setDisabledProjectsFilter(IFilter filter){
		this.labelProvider.setDisabledItemsFilter(filter);
		this.block.setDisabledFilter(filter);
		return this;
	}
	
	/**
	 * Callback for changes on the selected application list. Only one callback 
	 * is allowed.
	 * 
	 * @param cb
	 * @return selector
	 */
	public FeedHenryApplicationSelector setSelectionChangeCallback(SelectionChangeCallback cb){
		selectionCallback = cb;
		return this;
	}
	
	/**
	 * Returns a list of selected applications.
	 * 
	 * @return selector
	 */
	public List<FeedHenryApplication> getSelectedApplications(){
		Object[] checked = block.getCheckedElements();
		if(checked == null || checked.length <1){
			return Collections.emptyList();
		}
		final List<FeedHenryApplication> selectedApps = new ArrayList<>();
		for (int i = 0; i < checked.length ; i++) {
			if(checked[i] instanceof FeedHenryApplication){
				selectedApps.add((FeedHenryApplication)checked[i]);
			}
		}
		return selectedApps;
	}

	/**
	 * Sets the label to be displayed.
	 * 
	 * @param label
	 * @return selector
	 */
	public FeedHenryApplicationSelector setLabel(String label) {
		block.setLabel(label);
		return this;
	}
	
	/*package*/ void selectionChanged(){
		if(selectionCallback != null ){
			selectionCallback.selectionChanged(getSelectedApplications());
		}
	}
	
	private void prepareInput(IProgressMonitor monitor) throws CoreException{
		FeedHenry fh = new FeedHenry();
		FHPreferences prefs = FHPreferences.getPreferences();
		String feedHenryURL = prefs.getFeedHenryURL();
		if(feedHenryURL == null || feedHenryURL.isEmpty()){
			throw new CoreException(new Status(IStatus.ERROR, FHPlugin.PLUGIN_ID, FHErrorHandler.ERROR_INVALID_PREFERENCES, "FeedHenry URL preference is empty. Specify FeedHenry URL on preferences.",null));
		}
		try {
			if(monitor.isCanceled()){
				throw new OperationCanceledException();
			}
			projects = fh.setFeedHenryURL(new URL(feedHenryURL))
					.setAPIKey(prefs.getUserAPIKey()).listProjects();
		} catch (MalformedURLException e) {
			throw new CoreException(new Status(IStatus.ERROR, FHPlugin.PLUGIN_ID, FHErrorHandler.ERROR_INVALID_PREFERENCES, NLS.bind("{0} is not a valid URL", feedHenryURL),e));
		} catch (FeedHenryException e) {
			throw new CoreException(new Status(IStatus.ERROR,FHPlugin.PLUGIN_ID, e.getCode(), e.getMessage(),e));
		}
	}
}
