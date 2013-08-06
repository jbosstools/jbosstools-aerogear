package org.jboss.tools.aerogear.hybrid.ui.plugins.internal;

import org.eclipse.equinox.internal.p2.ui.discovery.util.ControlListItem;
import org.eclipse.equinox.internal.p2.ui.discovery.util.ControlListViewer;
import org.eclipse.equinox.internal.p2.ui.discovery.util.FilteredViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPlugin;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginRegistryClient;
@SuppressWarnings("restriction")
public class CordovaPlugingCatalogViewer extends FilteredViewer {
	
	private CordovaPluginWizardResources resources;

	private static class CordovaPluginContentProvider implements
			IStructuredContentProvider {
		
		private final CordovaPluginRegistryClient client = new CordovaPluginRegistryClient();

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
			return client.retrievePluginList().toArray();
		}

	}

	@Override
	protected StructuredViewer doCreateViewer(Composite container) {
		resources = new CordovaPluginWizardResources(container.getDisplay());
		
		StructuredViewer viewer = new ControlListViewer(container, SWT.BORDER) {

			@Override
			protected ControlListItem<CordovaPlugin> doCreateItem(
					Composite parent, Object element) {
				return new CordovaPluginItem(parent, SWT.NONE,
						(CordovaPlugin) element,resources);
			}
		};
		viewer.setContentProvider(new CordovaPluginContentProvider());
		viewer.setInput(new Object());
		return viewer;
	}

}
