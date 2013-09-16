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

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.aerogear.hybrid.core.config.Preference;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.config.WidgetModel;

public class NewPreferenceDialog extends TitleAreaDialog {
	private Text txtName;
	private Text txtValue;
	private Preference preference;
	private Widget widget;
	private WidgetModel model;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public NewPreferenceDialog(Shell parentShell, WidgetModel widgetModel) {
		super(parentShell);
		setShellStyle(SWT.APPLICATION_MODAL);
		Assert.isNotNull(widget);
		try {
			this.widget = widgetModel.getWidgetForEdit();
			this.model = widgetModel;
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("New Preference");
		setMessage("Create a new feature for the target platform");

		Composite contents = new Composite(parent, SWT.NONE);
		contents.setLayoutData(new GridData(GridData.FILL_BOTH));
		contents.setLayout(new GridLayout(2, false));

		Label lblName = new Label(contents, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblName.setText("Name:");

		txtName = new Text(contents, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label lblValue = new Label(contents, SWT.NONE);
		lblValue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblValue.setText("Value:");

		txtValue = new Text(contents, SWT.BORDER);
		txtValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		return contents;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	@Override
	protected void okPressed() {
		setMessage(null);
		String name = txtName.getText();
		String value = txtValue.getText();
		
		if(name == null || name.isEmpty()){
			setMessage("Name can not be empty" , IMessageProvider.ERROR);
			return;
		}
		
		preference = model.createPreference(widget);
		preference.setName(name);
		preference.setValue(value);
		super.okPressed();
	}
	
	public Preference getPreference(){
		return preference;
	}
	
}
