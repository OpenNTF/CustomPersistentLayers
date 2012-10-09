package dao;

/**
 * @author weihang chen
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.ibm.commons.util.StringUtil;

import model.CSS;
import model.Theme;
import model.notes.Key;
import model.resource.ViewsEnum;

public class ThemeDaoImpl extends DaoBase implements ThemeDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8316178055820303257L;

	/**
	 * method showing how children objects - CSS are loaded
	 */
	@SuppressWarnings("unchecked")
	public Vector<Theme> getAllThemes() throws Exception {
		Vector<Theme> themes = (Vector<Theme>) findAllByKey(null, null,
				ViewsEnum.Theme.name(), Theme.class);
		for (Theme theme : themes) {
			System.out
					.println("----------------------------DAO--------------------Eager loaded ArrayList");
			ArrayList<CSS> cssList2 = theme.getCSSList2();
			if (cssList2 != null) {
				System.out.println("ArrayList: cssList size: " + cssList2.size());
				for (CSS css : cssList2) {
					css.checkState();
					System.out.println("<ArrayList css name: " + css.getCSSName());
					System.out.println("ArrayList css content: " + css.getCSSContent()+">");
				}
			}
			System.out
					.println("----------------------------END!!!--------------------");

			System.out
					.println("----------------------------DAO--------------------LAZY loaded Set");
			Set<CSS> cssList1 = theme.getCSSList1();
			if (cssList1 != null) {
				System.out.println("Set: cssList size: " + cssList1.size());
				for (CSS css : cssList1) {
					css.checkState();
					System.out.println("<Set css name: " + css.getCSSName());
					System.out.println("Set css content: " + css.getCSSContent()+">");
				}
			}
			System.out
					.println("----------------------------END!!!--------------------");

		}
		return themes;
	}

	public Theme getThemeByUniqueId(String uniqueId) throws Exception {
		Key key = new Key();
		key.appendEntry(uniqueId);
		Theme theme = (Theme) findOneByKey(key, null, ViewsEnum.Theme.name(),
				Theme.class);
		return theme;
	}

	@SuppressWarnings("unchecked")
	public void persist() throws Exception {
		Vector<Theme> themes = (Vector<Theme>) findAllByKey(null, null,
				ViewsEnum.Theme.name(), Theme.class);
		for (Theme theme : themes) {
			System.out
					.println("----------------------------PERSIST--------------------SAVEUPDATE FROM OWNER OBJECT IS CASCADED TO CHILDREN OBJECTS");
			List<CSS> cssList1 = theme.getCSSList2();
			if (cssList1 != null) {
				System.out.println("DAO: cssList size: " + cssList1.size());
				for (CSS css : cssList1) {
					css.checkState();
					String cssContent = css.getCSSContent();
					if (StringUtil.equals(cssContent, "weihang"))
						css.setCSSContent("Sweden");
					else if (StringUtil.equals(cssContent, "Sweden"))
						css.setCSSContent("weihang");
					else if (!StringUtil.equals(cssContent, "Sweden")
							&& !StringUtil.equals(cssContent, "weihang"))
						css.setCSSContent("weihang");
				}
			}
			theme.persist();
			System.out
					.println("----------------------------END!!!--------------------");

		}
	}

}
