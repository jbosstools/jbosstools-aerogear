package org.jboss.tools.aerogear.hybrid.ui.plugins.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.equinox.internal.p2.ui.discovery.util.ControlListItem;
import org.eclipse.equinox.internal.p2.ui.discovery.util.ControlListViewer;
import org.eclipse.equinox.internal.p2.ui.discovery.util.FilteredViewer;
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
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaPluginRegistryManager;
@SuppressWarnings("restriction")
public class CordovaPluginCatalogViewer extends FilteredViewer {
	

	private static class CordovaPluginInfoContentProvider implements
			IStructuredContentProvider {
		
		private final CordovaPluginRegistryManager client = new CordovaPluginRegistryManager("http://registry.cordova.io/");

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object[] getElements(Object inputElement) {
			return client.retrievePluginInfos().toArray();
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
	protected StructuredViewer doCreateViewer(Composite container) {
		resources = new CordovaPluginWizardResources(container.getDisplay());
		
		StructuredViewer viewer = new ControlListViewer(container, SWT.BORDER) {

			@Override
			protected ControlListItem<CordovaRegistryPluginInfo> doCreateItem(
					Composite parent, Object element) {
				return doCreateViewerItem(parent, element);
			}
		};
		viewer.setContentProvider(new CordovaPluginInfoContentProvider());
		viewer.setInput(new Object());
		return viewer;
	}
	
	private ControlListItem<CordovaRegistryPluginInfo> doCreateViewerItem(Composite parent, Object element ){
		return new CordovaPluginInfoItem(parent, SWT.NONE,
				(CordovaRegistryPluginInfo) element,resources, this);
	}

}
