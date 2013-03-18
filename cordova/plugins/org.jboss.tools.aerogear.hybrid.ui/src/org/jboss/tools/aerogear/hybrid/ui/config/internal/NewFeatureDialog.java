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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.aerogear.hybrid.core.config.Feature;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.config.WidgetModel;

public class NewFeatureDialog extends Dialog {
	private Text txtURI;
	private Widget widget;
	private Feature feature;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public NewFeatureDialog(Shell parentShell, Widget widget) {
		super(parentShell);
		Assert.isNotNull(widget);
		this.widget = widget;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gl_container = new GridLayout(2, false);
		gl_container.marginTop = 10;
		container.setLayout(gl_container);
		
		Label lblUri = new Label(container, SWT.NONE);
		lblUri.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUri.setText("URI:");
		
		txtURI = new Text(container, SWT.BORDER);
		txtURI.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(txtURI.getText() != null && !txtURI.getText().isEmpty()){
					getButton(IDialogConstants.OK_ID).setEnabled(true);
					return;
				}
				getButton(IDialogConstants.OK_ID).setEnabled(false);
			}
		});
		txtURI.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		return container;
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
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 120);
	}
	
	@Override
	protected void okPressed() {
		String uri = txtURI.getText();
		
		if(uri == null || uri.isEmpty()){
			setReturnCode(Window.CANCEL);
			close();
			return;
		}
		feature = WidgetModel.getInstance().createFeature(widget);
		feature.setName(uri);
		
		super.okPressed();
	}
	
	/**
	 * Returns the feature created by this dialog or null
	 * if dialog cancelled
	 * @return new feature or null
	 */
	public Feature getFeature(){
		return feature;
	}

}
