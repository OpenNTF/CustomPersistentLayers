package dao;
/**
 * @author weihang chen
 */
public class DaoFactory {


	public static ThemeDao getThemeDao() {
		return new ThemeDaoImpl();
	}
	
	public static PersonDao getPersonDao() {
		return new PersonDaoImpl();
	}
	public static LocationDAO getLocationDao() {
		return new LocationDAOImpl();
	}
	
}
