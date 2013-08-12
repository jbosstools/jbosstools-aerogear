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
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginInfo;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginRegistryClient;
@SuppressWarnings("restriction")
public class CordovaPluginCatalogViewer extends FilteredViewer {
	

	private static class CordovaPluginInfoContentProvider implements
			IStructuredContentProvider {
		
		private final CordovaPluginRegistryClient client = new CordovaPluginRegistryClient("http://registry.cordova.io/");

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
	private List<CordovaPluginInfo> selectedItems = new ArrayList<CordovaPluginInfo>();

	
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
	
	void modifySelection ( CordovaPluginInfo element, boolean selection){
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
			protected ControlListItem<CordovaPluginInfo> doCreateItem(
					Composite parent, Object element) {
				return doCreateViewerItem(parent, element);
			}
		};
		viewer.setContentProvider(new CordovaPluginInfoContentProvider());
		viewer.setInput(new Object());
		return viewer;
	}
	
	private ControlListItem<CordovaPluginInfo> doCreateViewerItem(Composite parent, Object element ){
		return new CordovaPluginInfoItem(parent, SWT.NONE,
				(CordovaPluginInfo) element,resources, this);
	}

}
