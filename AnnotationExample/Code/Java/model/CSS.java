package model;

/**
 * @author weihang chen
 */
import persistence.annotation.DominoEntity;

import com.ibm.commons.util.StringUtil;

import model.notes.ModelBase;

@DominoEntity(formName = "CSS")
public class CSS extends ModelBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5313465080829075636L;
	private static final String STATUSACTIVE = "inuse";
	private final String ITEM_NAME_CSSCONTENT = "CSSContent";
	private final String ITEM_NAME_CSSNAME = "CSSName";
	private final String ITEM_NAME_THEMEUNIQUEID = "ThemeUniqueId";
	private final String ITEM_NAME_RICHTEXT = "Body";
	private final String ITEM_NAME_INUSE = "CSSActive";

	public CSS() {

	}

	public CSS(Object doc) {
		super(doc);
		System.out.println("CREATE CSS INSTANCE");
	}

	public String getITEM_NAME_RICHTEXT() {
		return ITEM_NAME_RICHTEXT;
	}

	public boolean isActive() {
		String status = readString(ITEM_NAME_INUSE);
		return (StringUtil.equals(status, STATUSACTIVE));
	}

	public String getCSSContent() {
		return readString(ITEM_NAME_CSSCONTENT);
	}

	public void setCSSContent(String content) {
		writeValue(ITEM_NAME_CSSCONTENT, content);
	}

	public String getCSSName() {
		return readString(ITEM_NAME_CSSNAME);
	}

	public void setCSSName(String CSSName) {
		writeValue(ITEM_NAME_CSSNAME, CSSName);
	}

	public String getThemeUniqueId() {
		return readString(ITEM_NAME_THEMEUNIQUEID);
	}

	public void setThemeUniqueId(String themeUniqueId) {
		writeValue(ITEM_NAME_THEMEUNIQUEID, themeUniqueId);
	}

}
