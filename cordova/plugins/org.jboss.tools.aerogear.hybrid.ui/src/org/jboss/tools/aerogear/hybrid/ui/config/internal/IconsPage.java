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
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.aerogear.hybrid.core.config.ImageResourceBase;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;

public class IconsPage extends FormPage {
	private DataBindingContext m_bindingContext;

	private FormToolkit formToolkit; 
	private Table table;
	private Table table_1;
	private Text txtWidth;
	private Text txtHeight;
	private Text txtPlatform;
	private Text txtDensity;
	private ImageResourceBase currentlySelected;
	
	
	class IconsContentProvider implements IStructuredContentProvider{

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
			return getWidget().getIcons().toArray();
		}
		
	}
	
	class SplashScreensContentProvider implements IStructuredContentProvider{

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
			return getWidget().getSplashes().toArray();
		}
		
	}
	
	class ImageResourceLabelProvider extends LabelProvider implements ITableLabelProvider{

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			return ((ImageResourceBase)element).getSrc();
		}
		
	}
	
	public IconsPage(FormEditor editor) {
		super(editor, "icons", "Icons && Splash Screen");
		formToolkit = editor.getToolkit();
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {	
		final ScrolledForm form = managedForm.getForm();
		formToolkit.decorateFormHeading( form.getForm());
		managedForm.getForm().setText(getTitle());
		ColumnLayout columnLayout = new ColumnLayout();
		columnLayout.minNumColumns = 2;
		columnLayout.maxNumColumns = 2;
		managedForm.getForm().getBody().setLayout(columnLayout);
		
		Section sctnIcons = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR);
		managedForm.getToolkit().paintBordersFor(sctnIcons);
		sctnIcons.setText("Icons");
		
		Composite composite = managedForm.getToolkit().createComposite(sctnIcons, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite);
		sctnIcons.setClient(composite);
		composite.setLayout(new GridLayout(2, false));
		
		final TableViewer tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				currentlySelected = (ImageResourceBase) tableViewer.getElementAt(table.getSelectionIndex());
				updateDetails();
			}
		});
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		managedForm.getToolkit().paintBordersFor(table);
		tableViewer.setContentProvider(new IconsContentProvider());
		tableViewer.setLabelProvider(new ImageResourceLabelProvider());
		tableViewer.setInput(getWidget().getIcons());
		
		Composite composite_3 = managedForm.getToolkit().createComposite(composite, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite_3);
		composite_3.setLayout(new FillLayout(SWT.VERTICAL));
		
		Button btnAdd = managedForm.getToolkit().createButton(composite_3, "Add...", SWT.NONE);
		
		Button btnRemove = managedForm.getToolkit().createButton(composite_3, "Remove", SWT.NONE);
		
		Section sctnSplashes = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR);
		managedForm.getToolkit().paintBordersFor(sctnSplashes);
		sctnSplashes.setText("Splash Screens");
		
		Composite composite_1 = managedForm.getToolkit().createComposite(sctnSplashes, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite_1);
		sctnSplashes.setClient(composite_1);
		composite_1.setLayout(new GridLayout(2, false));
		
		final TableViewer tableViewer_1 = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		table_1 = tableViewer_1.getTable();
		table_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		managedForm.getToolkit().paintBordersFor(table_1);
		tableViewer_1.setContentProvider(new SplashScreensContentProvider());
		tableViewer_1.setLabelProvider(new ImageResourceLabelProvider());
		tableViewer_1.setInput(getWidget().getSplashes());
		tableViewer_1.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				currentlySelected = (ImageResourceBase) tableViewer_1.getElementAt(table_1.getSelectionIndex());
				updateDetails();
			}
		});
		
		
		Composite composite_4 = managedForm.getToolkit().createComposite(composite_1, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite_4);
		composite_4.setLayout(new FillLayout(SWT.VERTICAL));
		
		Button btnAdd_1 = managedForm.getToolkit().createButton(composite_4, "Add...", SWT.NONE);
		
		Button btnRemove_1 = managedForm.getToolkit().createButton(composite_4, "Remove", SWT.NONE);
		
		Section sctnImageDetails = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR);
		managedForm.getToolkit().paintBordersFor(sctnImageDetails);
		sctnImageDetails.setText("Image Details");
		
		Composite composite_2 = managedForm.getToolkit().createComposite(sctnImageDetails, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite_2);
		sctnImageDetails.setClient(composite_2);
		composite_2.setLayout(new GridLayout(2, false));
		
		Label lblWidth = managedForm.getToolkit().createLabel(composite_2, "Width:", SWT.NONE);
		lblWidth.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		txtWidth = managedForm.getToolkit().createText(composite_2, "New Text", SWT.NONE);
		txtWidth.setText("");
		txtWidth.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		
		Label lblHeight = managedForm.getToolkit().createLabel(composite_2, "Height:", SWT.NONE);
		lblHeight.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		txtHeight = managedForm.getToolkit().createText(composite_2, "New Text", SWT.NONE);
		txtHeight.setText("");
		txtHeight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPlatform = managedForm.getToolkit().createLabel(composite_2, "Platform:", SWT.NONE);
		lblPlatform.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		txtPlatform = managedForm.getToolkit().createText(composite_2, "New Text", SWT.NONE);
		txtPlatform.setText("");
		txtPlatform.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblDensity = managedForm.getToolkit().createLabel(composite_2, "Density:", SWT.NONE);
		lblDensity.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		txtDensity = managedForm.getToolkit().createText(composite_2, "New Text", SWT.NONE);
		txtDensity.setText("");
		txtDensity.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		m_bindingContext = initDataBindings();
	}
	
	private void updateDetails(){
		String s = currentlySelected.getDensity()==null?"":currentlySelected.getDensity();
		txtDensity.setText(s);
		s = currentlySelected.getPlatform() == null? "":currentlySelected.getDensity();
		txtHeight.setText(Integer.toString(currentlySelected.getHeight()));
		txtWidth.setText(Integer.toString(currentlySelected.getWidth()));
	}
	
	private Widget getWidget(){
 		return ((ConfigEditor)getEditor()).getWidget();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxtWidthObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtWidth);
		IObservableValue widthCurrentlySelectedObserveValue = PojoProperties.value("width").observe(currentlySelected);
		bindingContext.bindValue(observeTextTxtWidthObserveWidget, widthCurrentlySelectedObserveValue, null, null);
		//
		IObservableValue observeTextTxtHeightObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtHeight);
		IObservableValue heightCurrentlySelectedObserveValue = PojoProperties.value("height").observe(currentlySelected);
		bindingContext.bindValue(observeTextTxtHeightObserveWidget, heightCurrentlySelectedObserveValue, null, null);
		//
		IObservableValue observeTextTxtPlatformObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtPlatform);
		IObservableValue platformCurrentlySelectedObserveValue = PojoProperties.value("platform").observe(currentlySelected);
		bindingContext.bindValue(observeTextTxtPlatformObserveWidget, platformCurrentlySelectedObserveValue, null, null);
		//
		IObservableValue observeTextTxtDensityObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtDensity);
		IObservableValue densityCurrentlySelectedObserveValue = PojoProperties.value("density").observe(currentlySelected);
		bindingContext.bindValue(observeTextTxtDensityObserveWidget, densityCurrentlySelectedObserveValue, null, null);
		//
		return bindingContext;
	}
}
