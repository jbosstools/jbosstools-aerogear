package org.jboss.tools.aerogear.hybrid.ui.plugins.internal;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.aerogear.hybrid.core.platform.PlatformConstants;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginInfo;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;
import org.jboss.tools.aerogear.hybrid.ui.wizard.export.DirectorySelectionGroup;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class CordovaPluginSelectionPage extends WizardPage {

	static final int PLUGIN_SOURCE_REGISTRY =1;
	static final int PLUGIN_SOURCE_GIT =2;
	static final int PLUGIN_SOURCE_DIRECTORY =3;
	
	
	private TabFolder tabFolder;
	private TabItem registryTab;
	private CordovaPluginCatalogViewer catalogViewer;
	private TabItem gitTab;
	private TabItem directoryTab;
	private DirectorySelectionGroup destinationDirectoryGroup;

	protected CordovaPluginSelectionPage(String pageName) {
		super(pageName);
		setImageDescriptor(HybridUI.getImageDescriptor(HybridUI.PLUGIN_ID, CordovaPluginWizard.IMAGE_WIZBAN));
	}

	@SuppressWarnings("restriction")
	@Override
	public void createControl(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setBounds(0, 39, 148, 52);
		
		setControl(tabFolder);
		
		registryTab = new TabItem(tabFolder, SWT.NONE);
		registryTab.setText("Registry");
		catalogViewer = new CordovaPluginCatalogViewer();
		catalogViewer.createControl(tabFolder);
		registryTab.setControl(catalogViewer.getControl());
		
		gitTab = new TabItem(tabFolder, SWT.NONE);
		gitTab.setText("Git");
		directoryTab = new TabItem(tabFolder, SWT.NONE);
		directoryTab.setText("Directory");
		
		destinationDirectoryGroup = new DirectorySelectionGroup(tabFolder, SWT.NONE);
		destinationDirectoryGroup.setText("Plugin:");
		destinationDirectoryGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		destinationDirectoryGroup.addListener(SWT.Modify, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				setPageComplete(validatePage());
			}


		});
		directoryTab.setControl(destinationDirectoryGroup);
		tabFolder.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				setPageComplete(validatePage());
			}
		});
		restoreWidgetValues();
	}
	
	@Override
	public IWizardPage getNextPage() {
		if( getSelectedTabItem()!=registryTab){
			return null;
		}
		CordovaPluginWizard wiz = (CordovaPluginWizard) getWizard();
		RegistryConfirmPage confirmPage = wiz.getRegistryConfirmPage();
		confirmPage.setSelectedPlugins(getCheckedCordovaRegistryItems());
		return super.getNextPage();
	}
	
	
	
	public int getPluginSourceType(){
		TabItem selected = getSelectedTabItem();
		if(selected == gitTab )
			return PLUGIN_SOURCE_GIT;
		if(selected == directoryTab )
			return PLUGIN_SOURCE_DIRECTORY;
		return PLUGIN_SOURCE_REGISTRY; // defaults to registry;
	}
	
	private List<CordovaPluginInfo> getCheckedCordovaRegistryItems(){
		IStructuredSelection selection = catalogViewer.getSelection();
		if(selection == null || selection.isEmpty())
			return Collections.emptyList();
		return selection.toList();
	}
	
	private TabItem getSelectedTabItem(){
		TabItem[] selections = tabFolder.getSelection();
		Assert.isTrue(selections.length>0);
		return selections[0];
	}
	
	private boolean validatePage() {
		boolean valid = false;
		switch (getPluginSourceType()) {
		case PLUGIN_SOURCE_DIRECTORY:
			valid = validateDirectroyTab();
			break;
		case PLUGIN_SOURCE_GIT:	
			valid = true; //TODO: implement
			break;
		case PLUGIN_SOURCE_REGISTRY:
			valid = true; //TODO implement
			break;
		}
		if(valid){
			setMessage(null,NONE);
		}
		return valid;
	}
	private boolean validateDirectroyTab(){
		String directory = destinationDirectoryGroup.getValue();
		if(directory == null || directory.isEmpty() ){
			setMessage("Select the directory for the Cordova plugin",ERROR);
			return false;
		}
		File pluginFile = new File(directory);
		if(!DirectorySelectionGroup.isValidDirectory(pluginFile)){
			setMessage(directory +" is not a valid directory",ERROR);
			return false;
		}
		if(!pluginFile.isDirectory()){
			setMessage("Select an existing directory", ERROR);
			return false;
		}
		File pluginXML = new File(pluginFile, PlatformConstants.FILE_XML_PLUGIN);
		if(!pluginXML.isFile()){
			setMessage("Specified directory is not a valid plugin directory",ERROR);
			return false;
		}
		return true;
		
	}
	
	void saveWidgetValues(){
		IDialogSettings settings = getDialogSettings();
		if(settings != null ){
			destinationDirectoryGroup.saveHistory(settings);
		}
	}
	
	private void restoreWidgetValues(){
		IDialogSettings settings = getDialogSettings();
		if(settings != null ){
			destinationDirectoryGroup.restoreHistory(settings);
		}
	}
	
	
}
