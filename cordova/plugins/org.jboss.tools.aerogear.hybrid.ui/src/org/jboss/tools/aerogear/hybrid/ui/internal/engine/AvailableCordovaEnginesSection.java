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

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.jboss.tools.aerogear.hybrid.core.engine.HybridMobileEngine;
import org.jboss.tools.aerogear.hybrid.engine.internal.cordova.CordovaEngineProvider;

import com.github.zafarkhaja.semver.Version;

public class AvailableCordovaEnginesSection implements ISelectionProvider{
	

	private static final int TABLE_HEIGHT = 250;
	private static final int TABLE_WIDTH = 350;
	
	private ListenerList selectionListeners;
	private CheckboxTableViewer engineList;
	private ISelection prevSelection = new StructuredSelection();
	
	private class CordovaEnginesContentProvider implements IStructuredContentProvider{
		private List<HybridMobileEngine> engines;
		
		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this.engines = (List<HybridMobileEngine>) newInput;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if(engines == null ) return null;
			return engines.toArray();
		}
		
	}
	
	private class CordovaEngineLabelProvider extends LabelProvider implements ITableLabelProvider,IFontProvider{
		
		private Font boldFont;

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Assert.isTrue(element instanceof HybridMobileEngine);
			HybridMobileEngine engine = (HybridMobileEngine)element;
			switch (columnIndex) {
			case 0:
				return engine.getName()+" [" + engine.getVersion() +"]";
			case 1:
				 List<String> platforms =  engine.getPlatforms();
				 String platformString = "";
				 for (String string : platforms) {
					platformString += string +" ";
				}
				return platformString;
			default:
				Assert.isTrue(false);
			}
			return null;
		}

		@Override
		public Font getFont(Object element) {
			if(!engineList.getChecked(element))
				return null;
			if(boldFont == null ){
				FontDescriptor fontDescriptor = JFaceResources.getDialogFontDescriptor();
                fontDescriptor = fontDescriptor.setStyle(SWT.BOLD);
                boldFont = fontDescriptor.createFont(Display.getCurrent());
			}
			return boldFont;
		}
		
	}
	
	private class EngineVersionComparator extends ViewerComparator{
		private boolean descending = true;

		
	public EngineVersionComparator(boolean isDescending) {
		descending = isDescending;
	}


	@Override
		public int compare(Viewer viewer, Object o1, Object o2) {
			HybridMobileEngine e1 = (HybridMobileEngine) o1;
			HybridMobileEngine e2 = (HybridMobileEngine) o2;
			Version version1 = Version.valueOf(e1.getVersion());
			Version version2 = Version.valueOf(e2.getVersion());
			if(descending){
				return version2.compareTo(version1);
			}
			return version1.compareTo(version2);

		}
		
	}
	
	public AvailableCordovaEnginesSection() {
		this.selectionListeners = new ListenerList();
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);
		
		Label tableLbl = new Label(composite, SWT.NULL);
		tableLbl.setText("Available Engines: ");
		GridDataFactory.generate(tableLbl, 2, 1);
		
		Table table= new Table(composite, SWT.CHECK | SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		GridDataFactory.fillDefaults().hint(new Point(TABLE_WIDTH, TABLE_HEIGHT)).applyTo(table); 
		table.setHeaderVisible(true);
		table.setLinesVisible(true);	

		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setWidth(TABLE_WIDTH/2);
		col.setText("Name");
		col.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				EngineVersionComparator comp = (EngineVersionComparator) engineList.getComparator();
				engineList.setComparator(new EngineVersionComparator(!comp.descending));
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		col = new TableColumn(table, SWT.NONE);
		col.setWidth(TABLE_WIDTH/2);
		col.setText("Platforms");
		
		
		engineList = new CheckboxTableViewer(table);			
		engineList.setContentProvider(new CordovaEnginesContentProvider());
		engineList.setLabelProvider(new CordovaEngineLabelProvider());
		engineList.setComparator(new EngineVersionComparator(true));
		engineList.setUseHashlookup(true);
	
		engineList.addCheckStateListener(new ICheckStateListener(){
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					setSelection(new StructuredSelection(event.getElement()));
				} else {
					setSelection(new StructuredSelection());
				}
			}
		});
		
		engineList.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateButtons();
				
			}
		});
		
		Composite buttonsContainer = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.FILL ).applyTo(buttonsContainer);
		
		Button downloadBtn = new Button(composite, SWT.PUSH);
		downloadBtn.setText("Download...");
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(downloadBtn);;
		downloadBtn.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				EngineDownloadDialog downloadDialog = new EngineDownloadDialog(engineList.getControl().getShell());
				int status = downloadDialog.open();
				if(status == Window.OK){
					updateAvailableEngines();
				}
				
			}
		});
		updateAvailableEngines();
//		updateButtons();
		
	}

	private void updateButtons() {
		// TODO Auto-generated method stub
		
	}

	private void updateAvailableEngines() {
		CordovaEngineProvider provider = new CordovaEngineProvider();
		List<HybridMobileEngine> engines = provider.getAvailableEngines();
		engineList.setInput(engines);
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		return new StructuredSelection(engineList.getCheckedElements());
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionListeners.remove(selectionListeners);
	}
	

	@Override
	public void setSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			if (!selection.equals(prevSelection)) {
				prevSelection = selection;
				Object engine = ((IStructuredSelection)selection).getFirstElement();
				if (engine == null) {
					engineList.setCheckedElements(new Object[0]);
				} else {
					engineList.getTable().getItem(0).setChecked(true);
					engineList.setCheckedElements(new Object[]{engine});
					engineList.reveal(engine);
				}
				engineList.refresh(true);
				fireSelectionChanged();
			}
		}	
	}
	
	private void fireSelectionChanged() {
		SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());
		Object[] listeners = selectionListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			ISelectionChangedListener listener = (ISelectionChangedListener)listeners[i];
			listener.selectionChanged(event);
		}	
	}

}
