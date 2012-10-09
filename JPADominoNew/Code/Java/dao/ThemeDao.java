package dao;
import java.util.Vector;

import model.Theme;
public interface ThemeDao {
	public Vector<Theme> getAllThemes() throws Exception;
	public Theme getThemeByUniqueId(String uniqueId) throws Exception;
	public void persist() throws Exception;

}
