package org.jboss.tools.aerogear.hybrid.ui.plugins.internal;

import java.util.List;

import org.eclipse.equinox.internal.p2.ui.discovery.util.ControlListItem;
import org.eclipse.equinox.internal.p2.ui.discovery.util.ControlListViewer;
import org.eclipse.equinox.internal.p2.ui.discovery.util.FilteredViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaRegistryPlugin;

public class CordovaPluginViewer extends FilteredViewer {
	private CordovaPluginWizardResources resources;
	
	private static class CordovaPluginContentProvider implements
	IStructuredContentProvider {
		private List<CordovaRegistryPlugin> items;

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this.items = (List<CordovaRegistryPlugin>) newInput;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if(items == null || items.isEmpty())
				return new Object[0];
			return items.toArray();
		}
		
	}


	@Override
	protected StructuredViewer doCreateViewer(Composite container) {
		resources = new CordovaPluginWizardResources(container.getDisplay());
		StructuredViewer viewer = new ControlListViewer(container, SWT.BORDER) {

			@Override
			protected ControlListItem<CordovaRegistryPlugin> doCreateItem(
					Composite parent, Object element) {
				return new CordovaPluginItem(parent, SWT.NULL, (CordovaRegistryPlugin)element,resources);
			}
		};
		viewer.setContentProvider(new CordovaPluginContentProvider());
		return viewer;
	}

}
