package org.jboss.tools.aerogear.hybrid.core.internal.util;

/**
 * Copied from org.eclipse.ui.internal.wizards.datatransfer.TarException.
 */
public class TarException extends Exception {
	/**
	 * Generated serial version UID for this class.
	 */
	private static final long serialVersionUID = 2886671254518853528L;

	/**
	 * Constructs a TarException with the specified detail string.
	 *
	 * @param s the detail string
	 */
	public TarException(String s) {
		super(s);
	}

	/**
	 * Constructs a TarException with the specified detail string.
	 *
	 * @param s the detail string
	 * @param cause the cause
	 */
	public TarException(String s, Throwable cause) {
		super(s, cause);
	}
}
