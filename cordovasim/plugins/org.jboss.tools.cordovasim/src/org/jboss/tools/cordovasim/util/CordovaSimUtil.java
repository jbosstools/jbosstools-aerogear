package org.jboss.tools.cordovasim.util;


import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.jboss.tools.browsersim.browser.IBrowser;
import org.jboss.tools.browsersim.browser.PlatformUtil;
import org.jboss.tools.browsersim.browser.javafx.JavaFXBrowser;

public class CordovaSimUtil {
	/**
	 * Scrollbar is broken when some element overflows device width.
	 * Adding some styles fixes it.
	 * 
	 * @see JBIDE-17580
	 * 
	 * @param browser SWT.Webkit browser where application is shown
	 */
	public static void fixScrollbarStylesForMac(IBrowser browser) {
		if (PlatformUtil.OS_MACOSX.equals(PlatformUtil.getOs()) && !(browser instanceof JavaFXBrowser)) {
			browser.addLocationListener(new LocationAdapter() {
				@SuppressWarnings("nls")
				@Override
				public void changed(LocationEvent event) {
					IBrowser browser = (IBrowser) event.widget;
					if (browser != null) {
						browser.execute(
							"if (window._cordovaSim_ScrollBarStylesFixer === undefined) {"
								+"window._cordovaSim_ScrollBarStylesFixer = function () {"
								+	"document.removeEventListener('DOMSubtreeModified', window._cordovaSim_ScrollBarStylesFixer, false);"
								+	"var head = document.head;"
								+	"var style = document.createElement('style');"
								+	"style.type = 'text/css';"
								+	"style.id='browserSimStyles';"
								+	"head.appendChild(style);"
								+	"style.innerText='"
								+	"html, body {"
								+		"overflow-x: hidden;"
								+		"overflow-y: auto;"
								+	"}"
								+	"';"
								+"};"
								+ "document.addEventListener('DOMSubtreeModified', window._cordovaSim_ScrollBarStylesFixer, false);"
							+ "}"
						);
					}
				}
			});
		};
	}
}
