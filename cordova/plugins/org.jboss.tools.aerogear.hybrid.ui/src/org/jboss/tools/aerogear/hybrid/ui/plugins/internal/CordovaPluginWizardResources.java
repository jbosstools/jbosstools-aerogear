package org.jboss.tools.aerogear.hybrid.ui.plugins.internal;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class CordovaPluginWizardResources {
	private final Display display;
	private final ResourceManager resourceManager;
	private final FontDescriptor h1FontDescriptor;
	private final FontDescriptor h2FontDescriptor;
	private final FontDescriptor subFontDescriptor;
	
	public CordovaPluginWizardResources(Display display) {
		this.display = display;
		this.resourceManager = new LocalResourceManager(JFaceResources.getResources(display));
		this.h1FontDescriptor = createFontDescriptor(SWT.BOLD, 1.35f);
		this.h2FontDescriptor = createFontDescriptor(SWT.BOLD, 1.25f);
		this.subFontDescriptor = createFontDescriptor(SWT.NONE, 0.75f);
//		this.disabledColorDescriptor = new RGB(0x69, 0x69, 0x69);
	}

	private FontDescriptor createFontDescriptor(int style, float heightMultiplier) {
		Font baseFont = JFaceResources.getDialogFont();
		FontData[] fontData = baseFont.getFontData();
		FontData[] newFontData = new FontData[fontData.length];
		for (int i = 0; i < newFontData.length; i++) {
			newFontData[i] = new FontData(fontData[i].getName(), (int) (fontData[i].getHeight() * heightMultiplier), fontData[i].getStyle() | style);
		}
		return FontDescriptor.createFrom(newFontData);
	}

	public Font getHeaderFont() {
		return resourceManager.createFont(h1FontDescriptor);
	}

	public Font getSmallHeaderFont() {
		return resourceManager.createFont(h2FontDescriptor);
	}
	
	public Font getSubTextFont(){
		return resourceManager.createFont(subFontDescriptor);
	}

	
}
