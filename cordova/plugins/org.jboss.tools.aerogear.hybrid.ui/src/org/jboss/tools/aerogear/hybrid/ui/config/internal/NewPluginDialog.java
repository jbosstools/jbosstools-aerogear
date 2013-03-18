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

import java.util.AbstractMap.SimpleEntry;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.aerogear.hybrid.core.config.Plugin;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.config.WidgetModel;

public class NewPluginDialog extends TitleAreaDialog {
	private DataBindingContext m_bindingContext;
	private Text txtName;
	private Text txtVersion;
	private Text txtParamName;
	private Text txtParamValue;
	private WritableList parameters = new WritableList();
	private Widget widget;
	private Table paramsTable;
	private TableViewer tableViewer;
	private Plugin plugin;
	

	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public NewPluginDialog(Shell parentShell, Widget widget) {
		super(parentShell);
		this.widget = widget;
	
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("New Plugin");
		setMessage("Create a plugin definition");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lblName = new Label(container, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblName.setText("Name:");

		txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Label lblVersion = new Label(container, SWT.NONE);
		lblVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblVersion.setText("Version:");

		txtVersion = new Text(container, SWT.BORDER);
		txtVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Group grpParameters = new Group(container, SWT.NONE);
		grpParameters.setLayout(new GridLayout(2, false));
		grpParameters.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 2, 1));
		grpParameters.setText("Parameters");

		Composite composite = new Composite(grpParameters, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));

		Label lblParamName = new Label(composite, SWT.NONE);
		lblParamName.setText("Name:");

		txtParamName = new Text(composite, SWT.BORDER);
		txtParamName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		txtParamName.setBounds(0, 0, 64, 19);

		Label lblParamValue = new Label(composite, SWT.NONE);

		lblParamValue.setText("Value:");

		txtParamValue = new Text(composite, SWT.BORDER);
		txtParamValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		new Label(composite, SWT.NONE);

		Button btnAdd = new Button(composite, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setErrorMessage(null);
				String paramName = txtParamName.getText();
				String paramValue =  txtParamValue.getText();
				if(paramName == null || paramName.isEmpty() ){
					setErrorMessage("Parameters must have names");
					return;
				}
				if(paramValue == null || paramValue.isEmpty() ){
					setErrorMessage("Parameters must have values");
					return;
				}
				SimpleEntry<String, String > entry = new SimpleEntry<String, String>(paramName,paramValue);
				parameters.add(entry);
				txtParamName.setText("");
				txtParamValue.setText("");
				
				
			}
		});
		btnAdd.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		btnAdd.setText("Add");
		
		tableViewer = new TableViewer(grpParameters, SWT.BORDER | SWT.FULL_SELECTION);
		paramsTable = tableViewer.getTable();
		paramsTable.setLinesVisible(true);
		paramsTable.setHeaderVisible(true);
		paramsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnName = tableViewerColumn.getColumn();
		tblclmnName.setWidth(100);
		tblclmnName.setText("name");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnValue = tableViewerColumn_1.getColumn();
		tblclmnValue.setWidth(100);
		tblclmnValue.setText("value");

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		m_bindingContext = initDataBindings();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 350);
	}
	
	@Override
	protected void okPressed() {
		setErrorMessage(null);
		String name = txtName.getText();
		String version = txtVersion.getText();
		if(name == null || name.isEmpty()){
			setErrorMessage("Plugin is missing a name");
			return;
		}
		plugin = WidgetModel.getInstance().createPlugin(widget);
		if(plugin != null ){
			plugin.setName(name);
			plugin.setVersion(version);
			for(int i =0; i< parameters.size(); i++){
				@SuppressWarnings("unchecked")
				SimpleEntry< String, String> param = (SimpleEntry<String, String>) parameters.get(i);
				plugin.addParam(param.getKey(), param.getValue());
			}
			
		}
		
		super.okPressed();
	}
	
	public Plugin getPlugin(){
		return plugin;
	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap[] observeMaps = PojoObservables.observeMaps(listContentProvider.getKnownElements(), SimpleEntry.class, new String[]{"key", "value"});
		tableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMaps));
		tableViewer.setContentProvider(listContentProvider);
		//
		tableViewer.setInput(parameters);
		//
		return bindingContext;
	}
}
