package dao;

/**
 * @author weihang chen
 */
public class DaoFactory {

	public static PersonDao getPersonDao() {
		return new PersonDaoImpl();
	}

	public static LocationDAO getLocationDao() {
		return new LocationDAOImpl();
	}

}
