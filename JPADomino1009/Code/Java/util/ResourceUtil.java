package util;

/**
 * @author weihang chen
 */
import com.ibm.xsp.model.domino.DominoUtils;

import exception.notes.ViewNotFoundException;

import lotus.domino.*;

public class ResourceUtil {
	public static View getViewByName(String viewName)
			throws ViewNotFoundException, NotesException {
		View view = null;
		Database db = DominoUtils.getCurrentDatabase();
		if (db.isOpen())
			view = db.getView(viewName);
		if (view == null)
			throw new ViewNotFoundException("view can not be found");
		if (db.isFTIndexed())
			db.updateFTIndex(true);
		return view;
	}

	public static View getViewByName1(Database db, String viewName)
			throws ViewNotFoundException, NotesException {
		if (db == null)
			return getViewByName(viewName);
		View view = null;
		if (!db.isOpen())
			db.open();
		if (!db.isOpen())
			Assert.notNull(null, "database can't be openned");
		if (db.isOpen()) {
			view = db.getView(viewName);
			if (view == null)
				throw new ViewNotFoundException("view can not be found");
			if (db.isFTIndexed())
				db.updateFTIndex(true);
		}
		return view;
	}

}
