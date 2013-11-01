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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.config.Access;
import org.jboss.tools.aerogear.hybrid.core.config.Feature;
import org.jboss.tools.aerogear.hybrid.core.config.Preference;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.config.WidgetModel;
import org.jboss.tools.aerogear.hybrid.ui.plugins.internal.LaunchCordovaPluginWizardAction;

public class PropertiesPage extends FormPage {
	
	private DataBindingContext m_bindingContext;

	private FormToolkit formToolkit;
	private Table preferencesTable;
	private Table accessTable;
	private TableViewer preferencesViewer;
	private TableViewer accessViewer;
	private TableViewer featuresTableViewer;
	private Table featuresTable;
	private Table paramsTable;

	private TableViewer featureParamsTableViewer;
	
	public PropertiesPage(FormEditor editor) {
		super(editor, "properties", "Platform Properties");
		formToolkit = editor.getToolkit();
	}
	
	private Widget getWidget(){
		return ((ConfigEditor)getEditor()).getWidget();
	}
	
	private WidgetModel getWidgetModel(){
		return ((ConfigEditor)getEditor()).getWidgetModel();
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		
		
		final ScrolledForm form = managedForm.getForm();
		
		formToolkit.decorateFormHeading( form.getForm());
		managedForm.getForm().setText(getTitle());
		{
			TableWrapLayout tableWrapLayout = new TableWrapLayout();
			tableWrapLayout.horizontalSpacing = 10;
			tableWrapLayout.verticalSpacing = 15;
			tableWrapLayout.makeColumnsEqualWidth = true;
			tableWrapLayout.numColumns = 2;
			managedForm.getForm().getBody().setLayout(tableWrapLayout);
		}
		
		PixelConverter converter = new PixelConverter(form);
		
		int topRowTableHeightHint = converter.convertHeightInCharsToPixels(10);//about 10 rows of data 
		int secondRowTableHeightHint =  converter.convertHeightInCharsToPixels(5);//about 10 rows of data 
		
		
		Section sctnFeatures = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR | Section.DESCRIPTION);
		TableWrapData twd_sctnFeatures = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL, 1, 1);
		twd_sctnFeatures.grabVertical = true;
		sctnFeatures.setLayoutData(twd_sctnFeatures);
		managedForm.getToolkit().paintBordersFor(sctnFeatures);
		sctnFeatures.setText("Features");
		sctnFeatures.setDescription("Define plug-ins to be used in this application");
		
		Composite featuresComposite = managedForm.getToolkit().createComposite(sctnFeatures, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(featuresComposite);
		sctnFeatures.setClient(featuresComposite);
		featuresComposite.setLayout(new GridLayout(2, false));
		
		featuresTableViewer = new TableViewer(featuresComposite, SWT.BORDER | SWT.FULL_SELECTION);
		featuresTable = featuresTableViewer.getTable();
		featuresTable.setLinesVisible(false);
		featuresTable.setHeaderVisible(false);
		GridData featureTableLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		
		featureTableLayoutData.heightHint= topRowTableHeightHint;
		featuresTable.setLayoutData(featureTableLayoutData);
		managedForm.getToolkit().paintBordersFor(featuresTable);

		TableViewerColumn tableViewerColumnURI = new TableViewerColumn(featuresTableViewer, SWT.NONE);
		TableColumn tblclmnFeatureURI = tableViewerColumnURI.getColumn();
		tblclmnFeatureURI.setWidth(200);

		Composite featureBtnsComposite= managedForm.getToolkit().createComposite(featuresComposite, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(featureBtnsComposite);
		featureBtnsComposite.setLayout(new FillLayout(SWT.VERTICAL));

		Button btnFeatureAdd = managedForm.getToolkit().createButton(featureBtnsComposite, "Add...", SWT.NONE);
		btnFeatureAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {	

				LaunchCordovaPluginWizardAction action =null;
				IResource resource = (IResource) getEditorInput().getAdapter(IResource.class);
				if(resource != null && HybridProject.getHybridProject(resource.getProject()) != null){
					action = new LaunchCordovaPluginWizardAction(HybridProject.getHybridProject(resource.getProject()));
				}
				else{
					action = new LaunchCordovaPluginWizardAction();
				}
				action.run();
			}
		});

		Button btnFeatureRemove = managedForm.getToolkit().createButton(featureBtnsComposite, "Remove", SWT.NONE);
		
		
		// Params section 
		
		Section sctnParams = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR| Section.DESCRIPTION);
		TableWrapData twd_sctnParams = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL, 1, 1);
		twd_sctnParams.grabVertical = true;
		sctnParams.setLayoutData(twd_sctnParams);
		managedForm.getToolkit().paintBordersFor(sctnParams);
		sctnParams.setText("Params");
		sctnParams.setDescription("Specify parameters for the selected plug-in");
		
		Composite paramsComposite = managedForm.getToolkit().createComposite(sctnParams, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(paramsComposite);
		sctnParams.setClient(paramsComposite);
		paramsComposite.setLayout(new GridLayout(2, false));

		

		featureParamsTableViewer = new TableViewer(paramsComposite, SWT.BORDER | SWT.FULL_SELECTION);
		featuresTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if( !(event.getSelection() instanceof IStructuredSelection)) return;
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				if(sel.isEmpty()) {
					return;
				}
				Feature feature = (Feature)sel.getFirstElement();
				featureParamsTableViewer.setInput(feature.getParams());
				feature.addPropertyChangeListener("param", new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent event) {
						featureParamsTableViewer.setInput(event.getNewValue());
					}
				});
			}
		});
		featureParamsTableViewer.setContentProvider(new IStructuredContentProvider() {
			private	Map<String, String> items;

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				this.items =(Map)newInput;
			}
			@Override
			public void dispose() {
			}
			@Override
			public Object[] getElements(Object inputElement) {
				return items.entrySet().toArray();
			}
		});

		paramsTable = featureParamsTableViewer.getTable();
		paramsTable.setLinesVisible(true);
		paramsTable.setHeaderVisible(true);
		GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, topRowTableHeightHint).applyTo(paramsTable);;
		managedForm.getToolkit().paintBordersFor(paramsTable);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(featureParamsTableViewer, SWT.NONE);
		TableColumn tblclmnName_1 = tableViewerColumn.getColumn();
		tblclmnName_1.setWidth(100);
		tblclmnName_1.setText("name");
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				Entry<String, String> entry = (Entry<String, String>) element;
				return entry.getKey();
			}
		});

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(featureParamsTableViewer, SWT.NONE);
		TableColumn tblclmnColumn = tableViewerColumn_1.getColumn();
		tblclmnColumn.setWidth(200);
		tblclmnColumn.setText("value");
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				Entry<String, String> entry = (Entry<String, String>) element;
				return entry.getValue();
			}
		});

		Composite featureParamBtnsComposite = new Composite(paramsComposite, SWT.NONE);
		managedForm.getToolkit().adapt(featureParamBtnsComposite);
		managedForm.getToolkit().paintBordersFor(featureParamBtnsComposite);
		featureParamBtnsComposite.setLayout(new FillLayout(SWT.VERTICAL));

		Button btnAdd = managedForm.getToolkit().createButton(featureParamBtnsComposite, "Add...", SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewNameValueDialog dialog = new NewNameValueDialog(getSite().getShell(),"New Parameter");
				if (dialog.open() == Window.OK ){
					IStructuredSelection sel = (IStructuredSelection) featuresTableViewer.getSelection();
					Feature selectedFeature = (Feature) sel.getFirstElement();
					selectedFeature.addParam(dialog.getName(), dialog.getValue());
				}
			}
		});

		Button btnRemove = managedForm.getToolkit().createButton(featureParamBtnsComposite, "Remove", SWT.NONE);
		new Label(featuresComposite, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) featuresTableViewer.getSelection();
				Feature selectedFeature = (Feature) sel.getFirstElement();
				sel = (IStructuredSelection)featureParamsTableViewer.getSelection();
				Entry<String,String > param = (Entry<String, String>) sel.getFirstElement();
				selectedFeature.removeParam(param.getKey());
			}
		});

		btnFeatureRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) featuresTableViewer.getSelection();
				if (selection.isEmpty())
					return;
				Feature feature = (Feature) selection.getFirstElement();
				getWidget().removeFeature(feature);
				featureParamsTableViewer.setInput(null);
			}
		});
		
		Section sctnPreferences = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR);
		TableWrapData twd_sctnPreferences = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL, 1, 1);
		twd_sctnPreferences.grabHorizontal = true;
		twd_sctnPreferences.grabVertical = true;
		sctnPreferences.setLayoutData(twd_sctnPreferences);
		managedForm.getToolkit().paintBordersFor(sctnPreferences);
		sctnPreferences.setText("Preferences");
		
		Composite composite = managedForm.getToolkit().createComposite(sctnPreferences, SWT.WRAP);
		managedForm.getToolkit().paintBordersFor(composite);
		sctnPreferences.setClient(composite);
		composite.setLayout(new GridLayout(2, false));
		
		preferencesViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		preferencesTable = preferencesViewer.getTable();
		preferencesTable.setLinesVisible(true);
		preferencesTable.setHeaderVisible(true);
		managedForm.getToolkit().paintBordersFor(preferencesTable);
		GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, secondRowTableHeightHint).applyTo(preferencesTable);
		
		TableViewerColumn tableViewerColumnName = new TableViewerColumn(preferencesViewer, SWT.NONE);
		TableColumn tblclmnName = tableViewerColumnName.getColumn();
		tblclmnName.setWidth(100);
		tblclmnName.setText("name");
		
		TableViewerColumn tableViewerColumnValue = new TableViewerColumn(preferencesViewer, SWT.NONE);
		TableColumn tblclmnValue = tableViewerColumnValue.getColumn();
		tblclmnValue.setWidth(100);
		tblclmnValue.setText("value");
		
		Composite composite_1 = managedForm.getToolkit().createComposite(composite, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite_1);
		composite_1.setLayout(new FillLayout(SWT.VERTICAL));

		Button btnPreferenceAdd = managedForm.getToolkit().createButton(composite_1, "Add...", SWT.NONE);
		btnPreferenceAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				NewNameValueDialog dialog = new NewNameValueDialog(getSite().getShell(),"New Preference");
				if (dialog.open() == Window.OK ){
					Preference pref = getWidgetModel().createPreference(getWidget());
					pref.setName(dialog.getName());
					pref.setValue(dialog.getValue());
					getWidget().addPreference(pref);
				}
			}
		});
				
		Button btnPreferenceRemove = managedForm.getToolkit().createButton(composite_1, "Remove", SWT.NONE);
		btnPreferenceRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)preferencesViewer.getSelection();
				if(selection.isEmpty() )
					return;
				Preference preference = (Preference)selection.getFirstElement();
				getWidget().removePreference(preference);
			}
		});
		
		Section sctnAccess = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR);
		TableWrapData twd_sctnAccess = new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1, 1);
		twd_sctnAccess.grabHorizontal = true;
		twd_sctnAccess.grabVertical = true;
		twd_sctnAccess.align = TableWrapData.FILL;
		twd_sctnAccess.valign = TableWrapData.FILL;
		sctnAccess.setLayoutData(twd_sctnAccess);
		managedForm.getToolkit().paintBordersFor(sctnAccess);
		sctnAccess.setText("Access");
		
		Composite compositea = managedForm.getToolkit().createComposite(sctnAccess, SWT.WRAP);
		managedForm.getToolkit().paintBordersFor(compositea);
		sctnAccess.setClient(compositea);
		compositea.setLayout(new GridLayout(2, false));
		
		accessViewer = new TableViewer(compositea, SWT.BORDER | SWT.FULL_SELECTION);
		accessTable = accessViewer.getTable();
		accessTable.setLinesVisible(true);
		accessTable.setHeaderVisible(true);
		managedForm.getToolkit().paintBordersFor(accessTable);
		GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, secondRowTableHeightHint).applyTo(accessTable);
		
		TableViewerColumn tableViewerColumnOrigin = new TableViewerColumn(accessViewer, SWT.NONE);
		TableColumn tblclmnOrigin = tableViewerColumnOrigin.getColumn();
		tblclmnOrigin.setWidth(100);
		tblclmnOrigin.setText("origin");
		
		TableViewerColumn tableViewerColumnSubdomains = new TableViewerColumn(accessViewer, SWT.NONE);
		TableColumn tblclmnSubdomains = tableViewerColumnSubdomains.getColumn();
		tblclmnSubdomains.setWidth(100);
		tblclmnSubdomains.setText("subdomains");
		
		TableViewerColumn tableViewerColumnBrowserOnly = new TableViewerColumn(accessViewer, SWT.NONE);
		TableColumn tblclmnNewColumn = tableViewerColumnBrowserOnly.getColumn();
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("browserOnly");
		
		Composite composite_2 = managedForm.getToolkit().createComposite(compositea, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite_2);
		composite_2.setLayout(new FillLayout(SWT.VERTICAL));
		
		Button btnAccessAdd = managedForm.getToolkit().createButton(composite_2, "Add...", SWT.NONE);
		btnAccessAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewAccessDialog dialog = new NewAccessDialog(getSite().getShell(), getWidgetModel());
				if(dialog.open() == Window.OK && dialog.getAccess() != null){
					getWidget().addAccess(dialog.getAccess());
				}
			}
		});
		
		Button btnAccessRemove = managedForm.getToolkit().createButton(composite_2, "Remove", SWT.NONE);
		btnAccessRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)accessViewer.getSelection();
				if(selection.isEmpty())
					return;
				Access access = (Access)selection.getFirstElement();
				getWidget().removeAccess(access);
			}
		});
		
		
		m_bindingContext = initDataBindings();
		
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap[] observeMaps = BeansObservables.observeMaps(listContentProvider.getKnownElements(), Preference.class, new String[]{"name", "value"});
		preferencesViewer.setLabelProvider(new ObservableMapLabelProvider(observeMaps));
		preferencesViewer.setContentProvider(listContentProvider);
		//
		IObservableList preferencesGetWidgetObserveList = BeanProperties.list("preferences").observe(getWidget());
		preferencesViewer.setInput(preferencesGetWidgetObserveList);
		//
		ObservableListContentProvider listContentProvider_1 = new ObservableListContentProvider();
		IObservableMap[] observeMaps_1 = BeansObservables.observeMaps(listContentProvider_1.getKnownElements(), Access.class, new String[]{"origin", "subdomains", "browserOnly"});
		accessViewer.setLabelProvider(new ObservableMapLabelProvider(observeMaps_1));
		accessViewer.setContentProvider(listContentProvider_1);
		//
		IObservableList accessesGetWidgetObserveList = BeanProperties.list("accesses").observe(getWidget());
		accessViewer.setInput(accessesGetWidgetObserveList);
		//
		ObservableListContentProvider listContentProvider_2 = new ObservableListContentProvider();
		IObservableMap observeMap = BeansObservables.observeMap(listContentProvider_2.getKnownElements(), Feature.class, "name");
		featuresTableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		featuresTableViewer.setContentProvider(listContentProvider_2);
		//
		IObservableList featuresGetWidgetObserveList = BeanProperties.list("features").observe(getWidget());
		featuresTableViewer.setInput(featuresGetWidgetObserveList);
		//
		return bindingContext;
	}
}
