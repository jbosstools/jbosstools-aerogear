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
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;

public class EssentialsPage extends FormPage {
	private DataBindingContext m_bindingContext;
	
	private FormToolkit formToolkit;
	private Text txtIdtxt;
	private Text txtAuthorname;
	private Text txtDescription;
	private Text txtName;
	private Text txtEmail;
	private Text txtUrl;
	private Text txtVersion;
	private Text txtVersionCode;
	

	public EssentialsPage(FormEditor editor) {
		super(editor, "essentials", "Essentials");
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
		ColumnLayout columnLayout = new ColumnLayout();
		columnLayout.verticalSpacing = 10;
		columnLayout.horizontalSpacing = 10;
		columnLayout.maxNumColumns = 1;
		managedForm.getForm().getBody().setLayout(columnLayout);
		
		Section sctnNameAndDescription = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR);
		managedForm.getToolkit().paintBordersFor(sctnNameAndDescription);
		sctnNameAndDescription.setText("Name and Description");
		
		Composite composite_1 = managedForm.getToolkit().createComposite(sctnNameAndDescription, SWT.WRAP);
		managedForm.getToolkit().paintBordersFor(composite_1);
		sctnNameAndDescription.setClient(composite_1);
		composite_1.setLayout(new GridLayout(2, false));
		
		Label lblId = managedForm.getToolkit().createLabel(composite_1, "ID:", SWT.NONE);
		
		txtIdtxt = managedForm.getToolkit().createText(composite_1, "New Text", SWT.NONE);
		txtIdtxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtIdtxt.setText("");
		
		Label lblName = managedForm.getToolkit().createLabel(composite_1, "Name:", SWT.NONE);
		lblName.setSize(39, 14);
		
		txtName = managedForm.getToolkit().createText(composite_1, "New Text", SWT.NONE);
		txtName.setText("");
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblVersion = managedForm.getToolkit().createLabel(composite_1, "Version:", SWT.NONE);
		lblVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		txtVersion = managedForm.getToolkit().createText(composite_1, "New Text", SWT.NONE);
		txtVersion.setText("");
		txtVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblVersionCode = managedForm.getToolkit().createLabel(composite_1, "Version Code:", SWT.NONE);
		lblVersionCode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		txtVersionCode = managedForm.getToolkit().createText(composite_1, "New Text", SWT.NONE);
		txtVersionCode.setToolTipText("\u00DF");
		txtVersionCode.setText("");
		txtVersionCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		Label lblDescription = managedForm.getToolkit().createLabel(composite_1, "Description:", SWT.NONE);
		lblDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		txtDescription = managedForm.getToolkit().createText(composite_1, "New Text", SWT.MULTI);
		txtDescription.setText("");
		GridData gd_txtDescription = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtDescription.heightHint = 100;
		txtDescription.setLayoutData(gd_txtDescription);
		
		Section sctnAuthor = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR);
		managedForm.getToolkit().paintBordersFor(sctnAuthor);
		sctnAuthor.setText("Author");
		sctnAuthor.setExpanded(true);
		
		Composite composite = managedForm.getToolkit().createComposite(sctnAuthor, SWT.WRAP);
		managedForm.getToolkit().paintBordersFor(composite);
		sctnAuthor.setClient(composite);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblName_1 = managedForm.getToolkit().createLabel(composite, "Name:", SWT.NONE);
		lblName_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName_1.setBounds(0, 0, 59, 14);
		
		txtAuthorname = managedForm.getToolkit().createText(composite, "New Text", SWT.NONE);
		txtAuthorname.setText("");
		txtAuthorname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblEmail = managedForm.getToolkit().createLabel(composite, "Email:", SWT.NONE);
		lblEmail.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		txtEmail = managedForm.getToolkit().createText(composite, "New Text", SWT.NONE);
		txtEmail.setText("");
		txtEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblUrl = managedForm.getToolkit().createLabel(composite, "URL:", SWT.NONE);
		lblUrl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		txtUrl = managedForm.getToolkit().createText(composite, "New Text", SWT.NONE);
		txtUrl.setText("");
		txtUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		m_bindingContext = initDataBindings();
		
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxtIdtxtObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtIdtxt);
		IObservableValue idGetWidgetObserveValue = PojoProperties.value("id").observe(getWidget());
		bindingContext.bindValue(observeTextTxtIdtxtObserveWidget, idGetWidgetObserveValue, null, null);
		//
		IObservableValue observeTextTxtNameObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtName);
		IObservableValue nameGetWidgetObserveValue = PojoProperties.value("name").observe(getWidget());
		bindingContext.bindValue(observeTextTxtNameObserveWidget, nameGetWidgetObserveValue, null, null);
		//
		IObservableValue observeTextTxtDescriptionObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtDescription);
		IObservableValue descriptionGetWidgetObserveValue = PojoProperties.value("description").observe(getWidget());
		bindingContext.bindValue(observeTextTxtDescriptionObserveWidget, descriptionGetWidgetObserveValue, null, null);
		//
		IObservableValue observeTextTxtAuthornameObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtAuthorname);
		IObservableValue authornameGetWidgetObserveValue = PojoProperties.value("author.name").observe(getWidget());
		bindingContext.bindValue(observeTextTxtAuthornameObserveWidget, authornameGetWidgetObserveValue, null, null);
		//
		IObservableValue observeTextTxtEmailObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtEmail);
		IObservableValue authoremailGetWidgetObserveValue = PojoProperties.value("author.email").observe(getWidget());
		bindingContext.bindValue(observeTextTxtEmailObserveWidget, authoremailGetWidgetObserveValue, null, null);
		//
		IObservableValue observeTextTxtUrlObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtUrl);
		IObservableValue authorhrefGetWidgetObserveValue = PojoProperties.value("author.href").observe(getWidget());
		bindingContext.bindValue(observeTextTxtUrlObserveWidget, authorhrefGetWidgetObserveValue, null, null);
		//
		IObservableValue observeTextTxtVersionCodeObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtVersionCode);
		IObservableValue versionCodeGetWidgetObserveValue = PojoProperties.value("versionCode").observe(getWidget());
		bindingContext.bindValue(observeTextTxtVersionCodeObserveWidget, versionCodeGetWidgetObserveValue, null, null);
		//
		IObservableValue observeTextTxtVersionObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtVersion);
		IObservableValue versionGetWidgetObserveValue = PojoProperties.value("version").observe(getWidget());
		bindingContext.bindValue(observeTextTxtVersionObserveWidget, versionGetWidgetObserveValue, null, null);
		//
		return bindingContext;
	}
}
