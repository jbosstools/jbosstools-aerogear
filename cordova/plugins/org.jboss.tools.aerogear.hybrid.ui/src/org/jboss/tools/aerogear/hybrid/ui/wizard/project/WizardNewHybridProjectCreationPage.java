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
package org.jboss.tools.aerogear.hybrid.ui.wizard.project;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.jboss.tools.aerogear.hybrid.core.HybridProjectConventions;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;

public class WizardNewHybridProjectCreationPage extends WizardNewProjectCreationPage{
	private static final String IMAGE_WIZBAN = "/icons/wizban/newcordovaprj_wiz.png";
	private Text txtName;
	private Text txtID;
	
	public WizardNewHybridProjectCreationPage(String pageName) {
		super(pageName);
		setImageDescriptor(HybridUI.getImageDescriptor(HybridUI.PLUGIN_ID, IMAGE_WIZBAN));
	}

	public void createControl(Composite parent ){		
        super.createControl(parent);
        
         
        Group applicationGroup = new Group((Composite)getControl(), SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        applicationGroup.setLayout(layout);
        applicationGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        applicationGroup.setText("Mobile Application");
        
        Label lblName = new Label(applicationGroup, SWT.NONE);
        lblName.setText("Name:");
        
        txtName = new Text(applicationGroup, SWT.BORDER);
        txtName.addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		setPageComplete(validatePage());
        	}
        });
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
        Label lblId = new Label(applicationGroup, SWT.NONE);
        lblId.setText("ID:");
        
        txtID = new Text(applicationGroup, SWT.BORDER);
        txtID.addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		setPageComplete(validatePage());
        	}
        });
        txtID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        setPageComplete(validatePage());
        setErrorMessage(null);
        setMessage(null);
        Dialog.applyDialogFont(getControl());

		
	}
	
	@Override
	protected boolean validatePage() {
		boolean superValidate = super.validatePage();
		if(txtID == null || txtName == null ){//validate is actually called first time on super.createControl()
			return superValidate;             // in order to avoid NPEs for the half initialized UI we do a partial
		}                                     // until all UI components are in place.
		

		IStatus status = HybridProjectConventions.validateApplicationName(txtName.getText());
		if(status.getSeverity() == IStatus.ERROR ){
			setErrorMessage(status.getMessage());
			return false;
		}
		status = HybridProjectConventions.validateProjectID(txtID.getText());
		if(status.getSeverity() == IStatus.ERROR ){
			setErrorMessage(status.getMessage());
			return false;
		}		
        setErrorMessage(null);
        setMessage(null);
        return true;
	}
	
	public String getApplicationName(){
		return txtName.getText();
	}
	
	public String getApplicationID(){
		return txtID.getText();
	}

}
