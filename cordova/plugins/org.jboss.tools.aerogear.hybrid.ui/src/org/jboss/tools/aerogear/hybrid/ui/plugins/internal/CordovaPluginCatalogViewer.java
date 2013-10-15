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
import org.eclipse.swt.widgets.Composite;
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
	
	private final SelectionProviderAdapter selectionProvider;
	private CordovaPluginWizardResources resources;
	private List<CordovaRegistryPluginInfo> selectedItems = new ArrayList<CordovaRegistryPluginInfo>();

	
	public CordovaPluginCatalogViewer() {
		selectionProvider = new SelectionProviderAdapter();
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
		if (selection) {
			selectedItems.add(element);
		}else{
			selectedItems.remove(element);
		}
		selectionProvider.setSelection(new StructuredSelection(selectedItems));
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
		return new CordovaPluginInfoItem(parent, SWT.NONE,
				(CordovaRegistryPluginInfo) element,resources, this);
	}
	
	

}
