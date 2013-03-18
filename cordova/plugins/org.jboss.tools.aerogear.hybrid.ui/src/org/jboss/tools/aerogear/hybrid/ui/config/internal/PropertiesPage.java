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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.jboss.tools.aerogear.hybrid.core.config.Access;
import org.jboss.tools.aerogear.hybrid.core.config.Feature;
import org.jboss.tools.aerogear.hybrid.core.config.Plugin;
import org.jboss.tools.aerogear.hybrid.core.config.Preference;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;

public class PropertiesPage extends FormPage {
	private DataBindingContext m_bindingContext;

	private FormToolkit formToolkit;
	private Table preferencesTable;
	private Table accessTable;
	private TableViewer preferencesViewer;
	private TableViewer accessViewer;
	private TableViewer featuresTableViewer;
	private Table featuresTable;
	private Table pluginsTable;
	private TableViewer pluginsTableViewer;
	
	public PropertiesPage(FormEditor editor) {
		super(editor, "properties", "Platform Properties");
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
		{
			TableWrapLayout tableWrapLayout = new TableWrapLayout();
			tableWrapLayout.horizontalSpacing = 10;
			tableWrapLayout.verticalSpacing = 15;
			tableWrapLayout.makeColumnsEqualWidth = true;
			tableWrapLayout.numColumns = 2;
			managedForm.getForm().getBody().setLayout(tableWrapLayout);
		}
		
		Section sctnPreferences = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR);
		TableWrapData twd_sctnPreferences = new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1, 1);
		twd_sctnPreferences.grabVertical = true;
		twd_sctnPreferences.align = TableWrapData.FILL;
		twd_sctnPreferences.valign = TableWrapData.FILL;
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
		preferencesTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		managedForm.getToolkit().paintBordersFor(preferencesTable);
		
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
						
						NewPreferenceDialog dialog = new NewPreferenceDialog(getSite().getShell(), getWidget());
						if (dialog.open() == Window.OK &&  dialog.getPreference() != null ){
							getWidget().addPreference(dialog.getPreference());
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
				
				Button btnPreferenceEdit = managedForm.getToolkit().createButton(composite_1, "Edit...", SWT.NONE);
		
		Section sctnFeatures = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR);
		TableWrapData twd_sctnFeatures = new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1, 1);
		twd_sctnFeatures.grabVertical = true;
		twd_sctnFeatures.valign = TableWrapData.FILL;
		twd_sctnFeatures.align = TableWrapData.FILL;
		sctnFeatures.setLayoutData(twd_sctnFeatures);
		managedForm.getToolkit().paintBordersFor(sctnFeatures);
		sctnFeatures.setText("Features");
		
		Composite featuresComposite = managedForm.getToolkit().createComposite(sctnFeatures, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(featuresComposite);
		sctnFeatures.setClient(featuresComposite);
		featuresComposite.setLayout(new GridLayout(2, false));
		
		featuresTableViewer = new TableViewer(featuresComposite, SWT.BORDER | SWT.FULL_SELECTION);
		featuresTable = featuresTableViewer.getTable();
		featuresTable.setLinesVisible(true);
		featuresTable.setHeaderVisible(true);
		featuresTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		managedForm.getToolkit().paintBordersFor(featuresTable);

		TableViewerColumn tableViewerColumnURI = new TableViewerColumn(featuresTableViewer, SWT.NONE);
		TableColumn tblclmnFeatureURI = tableViewerColumnURI.getColumn();
		tblclmnFeatureURI.setWidth(200);
		tblclmnFeatureURI.setText("URI");
		
		Composite featureBtnsComposite= managedForm.getToolkit().createComposite(featuresComposite, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(featureBtnsComposite);
		featureBtnsComposite.setLayout(new FillLayout(SWT.VERTICAL));
		
		Button btnFeatureAdd = managedForm.getToolkit().createButton(featureBtnsComposite, "Add...", SWT.NONE);
		btnFeatureAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewFeatureDialog dialog = new NewFeatureDialog(getSite().getShell(), getWidget());
				if(dialog.open() == Window.OK && dialog.getFeature() != null ){
					getWidget().addFeature(dialog.getFeature());
				}
			}
		});
		
		Button btnFeatureRemove = managedForm.getToolkit().createButton(featureBtnsComposite, "Remove", SWT.NONE);
		btnFeatureRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) featuresTableViewer.getSelection();
				if (selection.isEmpty())
					return;
				Feature feature = (Feature) selection.getFirstElement();
				getWidget().removeFeature(feature);
			}
		});
		
		Section sctnPlugins = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR);
		TableWrapData twd_sctnPlugins = new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1, 1);
		twd_sctnPlugins.grabVertical = true;
		twd_sctnPlugins.valign = TableWrapData.FILL;
		twd_sctnPlugins.align = TableWrapData.FILL;
		sctnPlugins.setLayoutData(twd_sctnPlugins);
		managedForm.getToolkit().paintBordersFor(sctnPlugins);
		sctnPlugins.setText("Plugins");
		
		Composite pluginsSctnComposite = managedForm.getToolkit().createComposite(sctnPlugins, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(pluginsSctnComposite);
		sctnPlugins.setClient(pluginsSctnComposite);
		pluginsSctnComposite.setLayout(new GridLayout(2, false));
		
		pluginsTableViewer = new TableViewer(pluginsSctnComposite, SWT.BORDER | SWT.FULL_SELECTION);
		pluginsTable = pluginsTableViewer.getTable();
		pluginsTable.setLinesVisible(true);
		pluginsTable.setHeaderVisible(true);
		pluginsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		managedForm.getToolkit().paintBordersFor(pluginsTable);
		
		TableViewerColumn tableViewerColumnPluginName = new TableViewerColumn(pluginsTableViewer, SWT.NONE);
		TableColumn tblclmnPluginName = tableViewerColumnPluginName.getColumn();
		tblclmnPluginName.setWidth(100);
		tblclmnPluginName.setText("name");
		
		TableViewerColumn tableViewerColumnVersion = new TableViewerColumn(pluginsTableViewer, SWT.NONE);
		TableColumn tblclmnVersion = tableViewerColumnVersion.getColumn();
		tblclmnVersion.setWidth(100);
		tblclmnVersion.setText("version");
		
		Composite composite_3 = managedForm.getToolkit().createComposite(pluginsSctnComposite, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite_3);
		composite_3.setLayout(new FillLayout(SWT.VERTICAL));
		
		Button btnPluginAdd = managedForm.getToolkit().createButton(composite_3, "Add...", SWT.NONE);
		btnPluginAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewPluginDialog dialog = new NewPluginDialog(getSite().getShell(), getWidget());
				if (dialog.open() == Window.OK && dialog.getPlugin() != null){
					getWidget().addPlugin(dialog.getPlugin());
				}
			}
		});
		
		Button btnPluginRemove = managedForm.getToolkit().createButton(composite_3, "Remove...", SWT.NONE);
		btnPluginRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) pluginsTableViewer.getSelection();
				if( selection.isEmpty() )
					return;
				Plugin plugin = (Plugin) selection.getFirstElement();
				getWidget().removePlugin(plugin);
			}
		});
		
		Button btnPluginProperties = managedForm.getToolkit().createButton(composite_3, "Properties...", SWT.NONE);
		
		Section sctnAccess = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR);
		TableWrapData twd_sctnAccess = new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1, 1);
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
		accessTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		managedForm.getToolkit().paintBordersFor(accessTable);
		
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
				NewAccessDialog dialog = new NewAccessDialog(getSite().getShell(), getWidget());
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
		
		Button btnAccessEdit = managedForm.getToolkit().createButton(composite_2, "Edit...", SWT.NONE);
		
		
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
		ObservableListContentProvider listContentProvider_3 = new ObservableListContentProvider();
		IObservableMap[] observeMaps_2 = BeansObservables.observeMaps(listContentProvider_3.getKnownElements(), Plugin.class, new String[]{"name", "version"});
		pluginsTableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMaps_2));
		pluginsTableViewer.setContentProvider(listContentProvider_3);
		//
		IObservableList pluginsGetWidgetObserveList = BeanProperties.list("plugins").observe(getWidget());
		pluginsTableViewer.setInput(pluginsGetWidgetObserveList);
		//
		return bindingContext;
	}
}
