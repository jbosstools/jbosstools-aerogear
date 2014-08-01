/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *       Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.aerogear.hybrid.ui.plugins.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.equinox.internal.p2.ui.discovery.util.ControlListItem;
import org.eclipse.equinox.internal.p2.ui.discovery.util.ControlListViewer;
import org.eclipse.equinox.internal.p2.ui.discovery.util.FilteredViewer;
import org.eclipse.equinox.internal.p2.ui.discovery.util.PatternFilter;
import org.eclipse.equinox.internal.p2.ui.discovery.util.SelectionProviderAdapter;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaRegistryPluginInfo;

@SuppressWarnings("restriction")
public class CordovaPluginCatalogViewer extends FilteredViewer {
	
	
	private static class CordovaPluginInfoContentProvider implements
			IStructuredContentProvider {
		
		private Object[] pluginInfos;
		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this.pluginInfos = (Object[]) newInput;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return pluginInfos;
		}
	}
	
	/**
	 * Adds a show installed field and filter installed plug-ins according to the 
	 * project that was set.
	 */
	public static final int FILTER_INSTALLED = 1 << 1;
	
	private final SelectionProviderAdapter selectionProvider;
	private CordovaPluginWizardResources resources;
	private List<CordovaRegistryPluginInfo> selectedItems = new ArrayList<CordovaRegistryPluginInfo>();
	private HybridProject project;
	private int style;
	private InstalledPluginFilter installedPluginsFilter;

	private Button showInstalledBtn;

	
	public CordovaPluginCatalogViewer(int style) {
		this.style = style;
		selectionProvider = new SelectionProviderAdapter();
		setAutomaticFind(false);
	}
	
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionProvider.addSelectionChangedListener(listener);
	}
	
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionProvider.removeSelectionChangedListener(listener);
	}
	
	public IStructuredSelection getSelection() {
		return (IStructuredSelection) selectionProvider.getSelection();
	}
	
	void modifySelection ( CordovaRegistryPluginInfo element, boolean selection){
		if (selection && !selectedItems.contains(element)) {
			selectedItems.add(element);
			selectionProvider.setSelection(new StructuredSelection(selectedItems));
		}
		if(!selection && selectedItems.contains(element)){
			selectedItems.remove(element);
			selectionProvider.setSelection(new StructuredSelection(selectedItems));
		}
	}

	@Override
	protected void doCreateHeaderControls(Composite parent) {
		if( (style & FILTER_INSTALLED) != 0 ){
			showInstalledBtn = new Button(parent, SWT.CHECK);
			showInstalledBtn.setText("Show Installed");
			showInstalledBtn.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					updateInstalledPluginsFilter();
				}
			});
		}
	}
	
	@Override
	protected PatternFilter doCreateFilter() {
		return new CordovaPluginFilter();
	}
	
	@Override
	protected StructuredViewer doCreateViewer(Composite container) {
		resources = new CordovaPluginWizardResources(container.getDisplay());
		
		StructuredViewer viewer = new ControlListViewer(container, SWT.BORDER) {
			@Override
			protected void doUpdateContent() {
				super.doUpdateContent();
				ScrolledComposite scroll = getControl();
				Composite control = (Composite)scroll.getContent();
				if(control.getChildren().length > 0){
					Point size = control.computeSize(scroll.getClientArea().width - 20, SWT.DEFAULT, true);
					control.setSize(size);
					scroll.setMinSize(size);
				}
			}
			
			@Override
			protected ControlListItem<CordovaRegistryPluginInfo> doCreateItem(
					Composite parent, Object element) {
				return doCreateViewerItem(parent, element);
			}
		};
		
		CordovaPluginInfoContentProvider provider = new CordovaPluginInfoContentProvider();
		viewer.setContentProvider(provider);
		return viewer;
	}
	
	private ControlListItem<CordovaRegistryPluginInfo> doCreateViewerItem(Composite parent, Object element ){
		CordovaRegistryPluginInfo pluginInfo = (CordovaRegistryPluginInfo) element;
		boolean installed = false;
		if(project != null ){
			installed = project.getPluginManager().isPluginInstalled(pluginInfo.getName());
		}
		return new CordovaPluginInfoItem(parent, pluginInfo,resources, this, installed);
	}
	
	public void applyFilter(String filterText){
		this.setFilterText(filterText);
	}


	public HybridProject getProject() {
		return project;
	}


	public void setProject(HybridProject project) {
		this.project = project;
		updateInstalledPluginsFilter();
	}
	
	private void updateInstalledPluginsFilter(){
		if(( style & FILTER_INSTALLED) == 0 ) return;
		BusyIndicator.showWhile(this.getControl().getDisplay(), new Runnable() {
			
			@Override
			public void run() {
				if( showInstalledBtn.getSelection() ){
					if(installedPluginsFilter != null ){
						getViewer().removeFilter(installedPluginsFilter);
						installedPluginsFilter = null;
					}
				}else{
					if(installedPluginsFilter == null ){
						installedPluginsFilter = new InstalledPluginFilter();
						installedPluginsFilter.setProject(project);
						getViewer().addFilter(installedPluginsFilter);
					}else{
						installedPluginsFilter.setProject(project);
						getViewer().refresh();
					}
				}
			}
		});
	}

}
