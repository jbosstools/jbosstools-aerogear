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
package org.jboss.tools.aerogear.hybrid.ui.config.internal;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.jboss.tools.aerogear.hybrid.core.config.Feature;
import org.jboss.tools.aerogear.hybrid.core.config.Plugin;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;

public class FeaturesPage extends FormPage {

	private FormToolkit formToolkit;
	private Table table;
	private Table table_1;
	
	class FeaturesContentProvider implements IStructuredContentProvider{

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
			return getWidget().getFeatures().toArray();
		}
	}
	class PluginsContentProvider implements IStructuredContentProvider{

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
			return getWidget().getPlugins().toArray();
		}
		
	}
	class FeaturesLabelProvider extends LabelProvider implements ITableLabelProvider{

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			return ((Feature)element).getName();
		}
		
	}
	
	class PluginsLabelProvider extends LabelProvider implements ITableLabelProvider{

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Plugin plugin = (Plugin)element;
			switch(columnIndex){
			case 0: 
				return plugin.getName();
			case 1:
				return plugin.getVersion();
			default:
				return null;
			}
		}
		
	}
	
	public FeaturesPage(FormEditor editor) {
		super(editor, "features", "Featrues && Plugins" );
		formToolkit = editor.getToolkit();
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {	
		final ScrolledForm form = managedForm.getForm();
		formToolkit.decorateFormHeading( form.getForm());
		managedForm.getForm().setText(getTitle());
		ColumnLayout columnLayout = new ColumnLayout();
		columnLayout.maxNumColumns = 1;
		managedForm.getForm().getBody().setLayout(columnLayout);
		
		Section sctnFeatures = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR);
		managedForm.getToolkit().paintBordersFor(sctnFeatures);
		sctnFeatures.setText("Features");
		
		Composite composite = managedForm.getToolkit().createComposite(sctnFeatures, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite);
		sctnFeatures.setClient(composite);
		composite.setLayout(new GridLayout(2, false));
		
		TableViewer tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		managedForm.getToolkit().paintBordersFor(table);
		tableViewer.setContentProvider(new FeaturesContentProvider());
		tableViewer.setLabelProvider(new FeaturesLabelProvider());
		tableViewer.setInput(getWidget().getFeatures());
		
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnFeatureURI = tableViewerColumn.getColumn();
		tblclmnFeatureURI.setWidth(200);
		tblclmnFeatureURI.setText("URI");
		
		Composite composite_1 = managedForm.getToolkit().createComposite(composite, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite_1);
		composite_1.setLayout(new FillLayout(SWT.VERTICAL));
		
		Button btnAdd = managedForm.getToolkit().createButton(composite_1, "Add...", SWT.NONE);
		
		Button btnRemove = managedForm.getToolkit().createButton(composite_1, "Remove", SWT.NONE);
		
		Section sctnPlugins = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR);
		managedForm.getToolkit().paintBordersFor(sctnPlugins);
		sctnPlugins.setText("Plugins");
		
		Composite composite_2 = managedForm.getToolkit().createComposite(sctnPlugins, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite_2);
		sctnPlugins.setClient(composite_2);
		composite_2.setLayout(new GridLayout(2, false));
		
		TableViewer tableViewer_1 = new TableViewer(composite_2, SWT.BORDER | SWT.FULL_SELECTION);
		table_1 = tableViewer_1.getTable();
		table_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		managedForm.getToolkit().paintBordersFor(table_1);
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tblclmnName = tableViewerColumn_1.getColumn();
		tblclmnName.setWidth(100);
		tblclmnName.setText("name");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tblclmnVersion = tableViewerColumn_2.getColumn();
		tblclmnVersion.setWidth(100);
		tblclmnVersion.setText("version");
		tableViewer_1.setContentProvider(new PluginsContentProvider());
		tableViewer_1.setLabelProvider(new PluginsLabelProvider());
		tableViewer_1.setInput(getWidget().getPlugins());
		
		Composite composite_3 = managedForm.getToolkit().createComposite(composite_2, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite_3);
		composite_3.setLayout(new FillLayout(SWT.VERTICAL));
		
		Button btnAdd_1 = managedForm.getToolkit().createButton(composite_3, "Add...", SWT.NONE);
		
		Button btnRemove_1 = managedForm.getToolkit().createButton(composite_3, "Remove...", SWT.NONE);
		
		Button btnProperties = managedForm.getToolkit().createButton(composite_3, "Properties...", SWT.NONE);
		
	}
	private Widget getWidget(){
 		return ((ConfigEditor)getEditor()).getWidget();
	}
	
}
