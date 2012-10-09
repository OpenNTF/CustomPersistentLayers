package model.notes;

/**
 * @author weihang chen
 */
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.*;

import persistence.annotation.support.DominoEntityHelper;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.model.domino.DominoUtils;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;

import exception.notes.DeleteException;
import exception.notes.InvalidStateException;
import exception.notes.ViewNotFoundException;

import util.Assert;
import util.JSFUtil;
import util.ResourceUtil;

import lotus.domino.*;
import lotus.domino.local.EmbeddedObject;

public class ModelBase extends ModelBaseAnnotation {
	/**
	 * Assert.notNull(db, "CouchDbConnector may not be null");
	 * Assert.notNull(type); Assert.isTrue(Documents.isNew(entity),
	 * "entity must be new");
	 */
	private static final long serialVersionUID = -126355568954326939L;
	/**
	 * 
	 */
	protected static final String ITEM_FORM_NAME = "Form";
	private String unid;
	public DominoDocument doc;

	private boolean changed = false;

	// this contructor is used for mock test
	protected ModelBase() {
		this(null);
	}

	// this constructor is used for creating a brand new java object, or
	// wrapping an existing dominoDocument into java object
	protected ModelBase(Object doc) {
		this.doc = initDoc(doc);

		// if its new document, no need to load children objects in initFields
//		if (doc != null)
//			initFields();
	}

	// if doc is null, create a new dominoDocument + set form
	protected DominoDocument initDoc(Object docObj) {
		DominoDocument dominoDoc = null;
		String formName = DominoEntityHelper.getFormName(this.getClass());
		if (StringUtil.isEmpty(formName) && !(docObj instanceof DominoDocument))
			throw new NullPointerException(
					"Java entity fals to be initialized, neither formName or wrapped dominoDocument can be found");
		try {
			if (!(docObj instanceof DominoDocument)) {
				String dbName = DominoEntityHelper.getDBName(this.getClass());
				Database targetDB = null;
				if (StringUtil.isEmpty(dbName)) {
					targetDB = DominoUtils.getCurrentDatabase();
				} else {
					targetDB=JSFUtil.doOpenDatabase(dbName);
					//targetDB = DominoUtils.openDatabaseByName(dbName);
				}
				String relativeDBPath = targetDB.getFilePath();
				dominoDoc = DominoDocument.wrap(relativeDBPath, targetDB, "",
						formName, "both", "force", false, null, null);

			} else
				dominoDoc = (DominoDocument) docObj;

			this.unid = dominoDoc.getDocument().getUniversalID();
			System.out.println("id: " + unid);

		} catch (Exception ne) {
			handleException(ne);
		}
		Assert.notNull(dominoDoc, "wrapped dominoDocument may not be null");
		return dominoDoc;
	}

	public String getUnid() {
		return unid;
	}

	public void setUnid(String unid) {
		this.unid = unid;
	}

	public DominoDocument getDoc() {
		return doc;
	}

	public void setDoc(DominoDocument doc) {
		this.doc = doc;
	}

	// all exception being thrown should be log here
	protected static void handleException(Exception ne) {
		ne.printStackTrace();
	}

	// CRUD - R
	protected Vector<?> readValues(String itemName) {
		Vector<?> retval = new Vector();
		try {
			checkState();
			retval = (Vector<?>) doc.getItemValue(itemName);
		} catch (Exception ne) {
			handleException(ne);
		}
		return retval;
	}

	protected String readString(String itemName) {
		String retval = "";
		try {
			checkState();
			retval = doc.getItemValueString(itemName);
		} catch (Exception ne) {
			handleException(ne);
		}
		return retval;
	}

	protected double readDouble(String itemName) {
		double retval = 0.0;
		try {
			checkState();
			retval = doc.getItemValueDouble(itemName);
		} catch (Exception ne) {
			handleException(ne);
		}
		return retval;
	}

	protected int readInteger(String itemName) {
		int retval = 0;
		try {
			checkState();
			retval = doc.getItemValueInteger(itemName);
		} catch (Exception ne) {
			handleException(ne);
		}
		return retval;
	}

	// CRUD - R/U
	protected void writeValue(String itemName, Object value) {
		try {
			checkState();
			if (value == null) {
				doc.removeItem(itemName);
			}
			doc.replaceItemValue(itemName, value);
		} catch (Exception ne) {
			handleException(ne);
		}
		changed = true;
	}

	public void deleteAttachments(String richTextItemName) {
		Document document = doc.getDocument();
		try {
			RichTextItem body = (RichTextItem) document
					.getFirstItem(richTextItemName);
			if (body == null)
				return;
			RichTextNavigator rtnav = body.createNavigator();
			RichTextRange range = body.createRange();
			ArrayList<String> deleteAttachments = new ArrayList<String>();
			if (rtnav.findFirstElement(RichTextItem.RTELEM_TYPE_FILEATTACHMENT)) {
				do {
					Base attachment = rtnav.getElement();
					String attachmentName = attachment.toString();
					deleteAttachments.add(attachmentName);
					range.setBegin(rtnav);
					range.setEnd(rtnav);
					range.remove();
				} while (rtnav
						.findNextElement(RichTextItem.RTELEM_TYPE_FILEATTACHMENT));
			}
			deleteAllAttachments(document, deleteAttachments);
		} catch (Exception e) {
			handleException(e);
		}
	}

	protected void deleteAllAttachments(Document doc,
			ArrayList<String> deleteAttachments) throws NotesException {
		for (String attachmentName : deleteAttachments) {
			lotus.domino.EmbeddedObject e = doc.getAttachment(attachmentName);
			if (e != null)
				e.remove();
		}
	}

	public void createAttachment(String richTextItemName,
			String attachmentName, String filePath) {
		Document document = doc.getDocument();
		try {
			RichTextItem body = (RichTextItem) document
					.getFirstItem(richTextItemName);
			if (body == null)
				body = document.createRichTextItem(richTextItemName);
			body.embedObject(EmbeddedObject.EMBED_ATTACHMENT, null, filePath,
					attachmentName);
		} catch (Exception e) {
			handleException(e);
		}
	}

	// CRUD - C/U
	public void close() {
		if (doc == null)
			return;
		try {
			if (changed == true) {
				// restore wrapped document
				// doc.restoreWrappedDocument();
				doc.getDocument().computeWithForm(true, true);
				doc.save();
			}
			doc.recycle();
			doc = null;
		} catch (NotesException ne) {
			handleException(ne);
		}
	}

	public void persist() {
		try {
			checkState();
			doc.getDocument().computeWithForm(true, true);
			doc.save();
			persistRefDocuments();
		} catch (Exception ne) {
			handleException(ne);
		}
	}

	protected long getEntityCount(Key key, String viewName)
			throws ViewNotFoundException {
		long n = 0;
		try {
			View lup = ResourceUtil.getViewByName(viewName);
			if (lup != null) {
				DocumentCollection dc = lup.getAllDocumentsByKey(key
						.getEntries(), true);
				n = dc.getCount();
				dc.recycle();
			}
		} catch (NotesException ne) {
			handleException(ne);
		}
		return n;
	}

	// CRUD - D
	public boolean delete() throws DeleteException, InvalidStateException,
			NotesException {
		this.checkState();
		boolean ret = false;
		try {
			synchronized (this) {
				ret = doc.getDocument().remove(true);
				doc = null;
			}
		} catch (NotesException ne) {
			handleException(ne);
		}
		return ret;
	}

	// if not even restore wrappeddocument returns something, there must be
	// something wrong
	public void checkState() throws InvalidStateException, NotesException {
		if (doc.getDocument().isDeleted())
			doc.restoreWrappedDocument();
		if (doc.getDocument() == null || doc.getDocument().isDeleted())
			throw new InvalidStateException("Business Object with id ["
					+ this.getUnid() + "] is in an invalid state");
	}

	public String getDocumentUnidSignature() {
		return "unid";
	}

}