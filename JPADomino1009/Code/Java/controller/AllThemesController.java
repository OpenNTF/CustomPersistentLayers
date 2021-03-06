package controller;

/**
 * @author weihang chen
 */
import dao.DaoFactory;
import dao.PersonDao;
import dao.ThemeDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.faces.context.FacesContext;

import persistence.annotation.support.JavaBeanFactory;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.acl.NoAccessSignal;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.model.domino.DominoDocumentData;
import com.ibm.xsp.model.domino.DominoUtils;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;
import com.ibm.xsp.util.DataPublisher;

import lotus.domino.Database;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.Session;
import model.CSS;
import model.Person;
import model.Theme;

public class AllThemesController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1999072042736004730L;
	public List myList;

	public AllThemesController() {
		myList = new ArrayList();
	}

	public List getMyList() {
		return myList;
	}

	public void setMyList(List myList) {
		this.myList = myList;
	}

	// get all themes
	public Vector<Theme> getAllThemes() {
		ThemeDao themeDao = DaoFactory.getThemeDao();
		Vector<Theme> themes = new Vector<Theme>();
		try {
			themes = themeDao.getAllThemes();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return themes;
	}

	// get all themes
	public Vector<Person> getPeople() {
		PersonDao personDao = DaoFactory.getPersonDao();
		Vector<Person> people = new Vector<Person>();
		try {
			people = personDao.getPeople();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return people;
	}

	// hard coded persist example
	public void hardcodPersist() {
		ThemeDao themeDao = DaoFactory.getThemeDao();
		try {
			themeDao.persist();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Theme getThemeById(String uniqueId) {
		ThemeDao themeDao = DaoFactory.getThemeDao();
		Theme theme = null;
		try {
			theme = themeDao.getThemeByUniqueId(uniqueId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return theme;
	}

	public List<CSS> getCSSsByThemeName(String themeName) {
		List<CSS> list = new ArrayList<CSS>();
		Vector<Theme> themes = getAllThemes();
		for (Theme theme : themes) {
			String tmp = theme.getThemeName();
			if (StringUtil.equals(tmp, themeName))
				return theme.getCSSList2();
		}
		return list;
	}

	public void hardCodedCreate() {
		try {
			Theme theme = JavaBeanFactory.getProxy(Theme.class);
			theme.setThemeName("newthemeHAHAHAH");
			theme.setThemeType(4);
			String foreignKey = theme.getUnid();
			ArrayList<CSS> list = new ArrayList<CSS>();
			CSS css = new CSS();
			css.setCSSContent("color:redHAHAHA");
			css.setCSSName("newcssHHAHHA");
			css.setThemeUniqueId(foreignKey);
			list.add(css);
			CSS css1 = new CSS(null);
			css1.setCSSContent("color:blueHAHHA");
			css1.setCSSName("newcss2HAHA");
			css1.setThemeUniqueId(foreignKey);
			list.add(css1);
			theme.setCSSList2(list);
			css.persist();
			css1.persist();
			theme.persist();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Theme createNewTheme() {
		System.out.println("create new theme");
		Theme theme = JavaBeanFactory.getProxy(Theme.class);
		DataPublisher localDataPublisher = ((FacesContextEx) FacesContext
				.getCurrentInstance()).getDataPublisher();
		List localList = localDataPublisher.createShadowedList();

		localDataPublisher.pushObject(localList, "jingjing", theme);
		// localDataPublisher.pushObject(localList, arrayOfString[i] +
		// ".READONLY", localBoolean);

		return theme;
	}

	public void addTheme(Theme theme) {
		myList.add(theme);

	}

	public void saveThemes() {
		for (Object theme : myList) {
			((Theme) theme).persist();
		}
	}

}
