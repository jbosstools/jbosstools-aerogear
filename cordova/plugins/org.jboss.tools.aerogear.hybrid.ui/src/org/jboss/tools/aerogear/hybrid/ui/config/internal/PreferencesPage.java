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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.jboss.tools.aerogear.hybrid.core.config.Access;
import org.jboss.tools.aerogear.hybrid.core.config.Preference;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;

public class PreferencesPage extends FormPage {

	private FormToolkit formToolkit;
	private Table table;
	private Table table_1;
	
	class PreferencesContentProvider implements IStructuredContentProvider{

		@Override
		public void dispose() {			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {	
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getWidget().getPreferences().toArray();
		}		
	}
	
	class PreferencesLabelProvider extends LabelProvider implements ITableLabelProvider{

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Preference pref = (Preference) element;
			switch (columnIndex) {
			case 0:
				return pref.getName();
				
			case 1:
				return pref.getValue();
				
			default:
				return null;
				
			}
			
		}
	
	}
    
	class AccessContentProvider implements IStructuredContentProvider{

		@Override
		public void dispose() {
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getWidget().getAccesses().toArray();
		}
	}
	
	class AccessLabelProvider extends LabelProvider implements ITableLabelProvider{

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Access access = (Access) element;
			switch (columnIndex) {
			case 0:
				return access.getOrigin();
			case 1:
				return Boolean.toString(access.isBrowserOnly());
			case 2: 
				return Boolean.toString(access.isSubdomains());
			default:
				return null;
			}

		}
		
	}
	
	
	public PreferencesPage(FormEditor editor) {
		super(editor, "preferences", "Preferences && Access");
		formToolkit = editor.getToolkit();
	}
	
	private Widget getWidget(){
		return ((ConfigEditor)getEditor()).getWidget();
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		
		
		final ScrolledForm form = managedForm.getForm();
		formToolkit.decorateFormHeading( form.getForm());
		managedForm.getForm().setText(getTitle());
		managedForm.getForm().getBody().setLayout(new ColumnLayout());
		
		Section sctnPreferences = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR);
		sctnPreferences.setLayoutData(new ColumnLayoutData());
		managedForm.getToolkit().paintBordersFor(sctnPreferences);
		sctnPreferences.setText("Preferences");
		
		Composite composite = managedForm.getToolkit().createComposite(sctnPreferences, SWT.WRAP);
		managedForm.getToolkit().paintBordersFor(composite);
		sctnPreferences.setClient(composite);
		composite.setLayout(new GridLayout(2, false));
		
		TableViewer preferencesViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table = preferencesViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		managedForm.getToolkit().paintBordersFor(table);
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(preferencesViewer, SWT.NONE);
		TableColumn tblclmnName = tableViewerColumn.getColumn();
		tblclmnName.setWidth(100);
		tblclmnName.setText("name");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(preferencesViewer, SWT.NONE);
		TableColumn tblclmnValue = tableViewerColumn_1.getColumn();
		tblclmnValue.setWidth(100);
		tblclmnValue.setText("value");
		preferencesViewer.setContentProvider(new PreferencesContentProvider());
		preferencesViewer.setLabelProvider(new PreferencesLabelProvider());
		preferencesViewer.setInput(getWidget().getPreferences());
		
		Composite composite_1 = managedForm.getToolkit().createComposite(composite, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite_1);
		composite_1.setLayout(new FillLayout(SWT.VERTICAL));
		
		Button btnAdd = managedForm.getToolkit().createButton(composite_1, "Add...", SWT.NONE);
		
		Button btnRemove = managedForm.getToolkit().createButton(composite_1, "Remove...", SWT.NONE);
		
		Button btnEdit = managedForm.getToolkit().createButton(composite_1, "Edit...", SWT.NONE);
		
		Section sctnAccess = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR);
		managedForm.getToolkit().paintBordersFor(sctnAccess);
		sctnAccess.setText("Access");
		
		Composite compositea = managedForm.getToolkit().createComposite(sctnAccess, SWT.WRAP);
		managedForm.getToolkit().paintBordersFor(compositea);
		sctnAccess.setClient(compositea);
		compositea.setLayout(new GridLayout(2, false));
		
		TableViewer tableViewer = new TableViewer(compositea, SWT.BORDER | SWT.FULL_SELECTION);
		table_1 = tableViewer.getTable();
		table_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		managedForm.getToolkit().paintBordersFor(table_1);
		tableViewer.setContentProvider(new AccessContentProvider());
		tableViewer.setLabelProvider(new AccessLabelProvider());
		tableViewer.setInput(getWidget().getPreferences());
		
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnOrigin = tableViewerColumn_2.getColumn();
		tblclmnOrigin.setWidth(100);
		tblclmnOrigin.setText("origin");
		
		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn = tableViewerColumn_4.getColumn();
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("browserOnly");
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnSubdomains = tableViewerColumn_3.getColumn();
		tblclmnSubdomains.setWidth(100);
		tblclmnSubdomains.setText("subdomains");
		
		Composite composite_2 = managedForm.getToolkit().createComposite(compositea, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite_2);
		composite_2.setLayout(new FillLayout(SWT.VERTICAL));
		
		Button btnAdd_1 = managedForm.getToolkit().createButton(composite_2, "Add...", SWT.NONE);
		
		Button btnRemove_1 = managedForm.getToolkit().createButton(composite_2, "Remove...", SWT.NONE);
		
		Button btnEdit_1 = managedForm.getToolkit().createButton(composite_2, "Edit...", SWT.NONE);
		
	}
}
