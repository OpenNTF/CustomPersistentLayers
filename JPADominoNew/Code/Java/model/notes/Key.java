package model.notes;
/**
 * @author weihang chen
 */
import java.util.*;

public class Key {
	private Vector entries = new Vector();

	public Vector getEntries() {
		return entries;
	}

	public void appendEntry(String entry) {
		entries.addElement(entry);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("[");
		Enumeration e1 = entries.elements();

		while (e1.hasMoreElements()) {
			sb.append((String) e1.nextElement());
			sb.append(' ');
		}
		return (sb.toString() + "]");
	}

}
