/*******************************************************************************
 * Copyright (c) 2013,2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.aerogear.hybrid.ui.internal.engine;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.jboss.tools.aerogear.hybrid.core.engine.HybridMobileEngine;
import org.jboss.tools.aerogear.hybrid.core.extensions.ProjectGenerator;
import org.jboss.tools.aerogear.hybrid.engine.internal.cordova.CordovaEngineProvider;
import org.jboss.tools.aerogear.hybrid.ui.internal.projectGenerator.ProjectGeneratorContentProvider;
import org.jboss.tools.aerogear.hybrid.ui.internal.projectGenerator.ProjectGeneratorLabelProvider;

import com.github.zafarkhaja.semver.Version;

public class EngineDownloadDialog extends TitleAreaDialog{
	
	private ComboViewer versionViewer;
	private CheckboxTableViewer platformList;
	private CordovaEngineProvider engineProvider;
	private List<HybridMobileEngine> engines;
	private ProgressMonitorPart progressMonitorPart;
	

	
	private class VersionStringComparator implements Comparator<String>{

		@Override
		public int compare(String o1, String o2) {
			Version version1 = Version.valueOf(o1);
			Version version2 = Version.valueOf(o2);
			//This is reversed intentionally to sort the
			//latest version to the top
			return version2.compareTo(version1);
		}
		
	}
	
	private class ContentProviderSupportFilter extends ViewerFilter{

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			ProjectGenerator gen = (ProjectGenerator) element;
			
			return engineProvider.isSupportedPlatform(gen.getPlatformId()) 
					&& !isInstalled((String)parentElement, gen.getPlatformId());
		}
	}

	private class PlatformsLabelProvider extends ProjectGeneratorLabelProvider implements ITableLabelProvider{

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return super.getImage(element);
			default:
				return null;
			}
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return super.getText(element);
			default:
				return null;
			}
		}
	}
	
	private class DownloadableVersionsContentProvider implements IStructuredContentProvider{
		private String[] versions;

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			versions = (String[])newInput;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if(versions == null ){
				 engineProvider = new CordovaEngineProvider();
                try {
					versions = engineProvider.getDownloadableVersions();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return versions;
		}
		
	}

	public EngineDownloadDialog(Shell parent) {
		super(parent);
		setShellStyle(getShellStyle()| SWT.SHEET);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Download Hybrid Mobile Engine");
		setMessage("Download a new engine version or add a platform to an existing one");
		parent.getShell().setText("Download Hybrid Mobile Engine");
		
		
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);
		Label versionLbl = new Label(composite, SWT.NONE);
		versionLbl.setText("Version:");
		Combo versionCombo = new Combo(composite, SWT.READ_ONLY);
		versionViewer = new ComboViewer(versionCombo);
		versionViewer.setContentProvider(new DownloadableVersionsContentProvider());
		versionViewer.setComparator(new ViewerComparator(new VersionStringComparator()));
		versionViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if(event.getSelection().isEmpty()) return;
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				final String version = (String) sel.getFirstElement();
				platformList.setInput(version);
				validate();
			}
		});
	
		Table table= new Table(composite, SWT.CHECK | SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(table); 
		table.setHeaderVisible(true);
		table.setLinesVisible(false);	
	
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setWidth(120);
		col.setText("platform");
				
		platformList = new CheckboxTableViewer(table);
		// Use ProjectGeneratorContentProvider which gives us the supported platforms.
		// we then filter out the platforms that are not supported by the content provider 
		// and the already installed using the he ContentProviderSupportFilter
		platformList.setContentProvider(new ProjectGeneratorContentProvider());
		platformList.setFilters(new ViewerFilter[]{ new ContentProviderSupportFilter()});
		platformList.setLabelProvider(new PlatformsLabelProvider());

		createProgressMonitorPart(composite);
		
		engineProvider = new CordovaEngineProvider();
		try {
			versionViewer.setInput(engineProvider.getDownloadableVersions());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return composite;
	}

	private void createProgressMonitorPart(final Composite composite) {
		progressMonitorPart = new ProgressMonitorPart(composite, new GridLayout());
//		progressMonitorPart.attachToCancelComponent(getButton(IDialogConstants.CANCEL_ID));
		progressMonitorPart.setVisible(true);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(progressMonitorPart);
	}
	
	public void setVersion(String version){
		versionViewer.setSelection(new StructuredSelection(version));
		validate();
	}
	
	public String getVersion(){
		IStructuredSelection sel = (IStructuredSelection) versionViewer.getSelection();
		if(sel.isEmpty()) return null;
 		String version = (String) sel.getFirstElement();
		return version;
	}
	
	private boolean isInstalled(String version, String platformId){
		if(version == null || platformId == null ) return false;
		if(engines == null ){
			engines = engineProvider.getAvailableEngines();
		}
		for (HybridMobileEngine engine : engines) {
			if(engine.getVersion().equals(version) && engine.getPlatforms().contains(platformId)){
				return true;
			}
		}
		return false;
	}

	private void validate() {
		if(platformList.getElementAt(0) == null ){
			setErrorMessage("All supported platforms are already installed for "+engineProvider.getName() +" "+ getVersion() );
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			return;
		}
		setErrorMessage(null);
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}	
	
	@Override
	protected void okPressed() {
		Object[] checked = platformList.getCheckedElements();
		final String[] platforms = new String[checked.length];
		for (int i = 0; i < checked.length; i++) {
			ProjectGenerator gen = (ProjectGenerator) checked[i];
			platforms[i] = gen.getPlatformId();
		}
		final String version = getVersion();
		run(new IRunnableWithProgress() {
			
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {
				engineProvider.downloadEngine(version, monitor, platforms);
			}
		});
		super.okPressed();
	}
	
	private void run(IRunnableWithProgress runnable) {
		
		progressMonitorPart.attachToCancelComponent(getButton(IDialogConstants.CANCEL_ID));
		
		try {
			ModalContext.run(runnable, true, progressMonitorPart, getShell().getDisplay());
			
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		progressMonitorPart.removeFromCancelComponent(getButton(IDialogConstants.CANCEL_ID));
		
	}

}
