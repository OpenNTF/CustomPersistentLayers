package dao;

/**
 * @author weihang chen
 */

import java.util.Vector;

import com.ibm.xsp.model.domino.DominoUtils;

import lotus.domino.Database;
import model.Person;

public class PersonDaoImpl extends DaoBase implements PersonDao {

	public Vector<Person> getPeople() throws Exception {
		//hard code the db 
		Database personDb=DominoUtils.openDatabaseByName("names.nsf");
		Vector<Person> people = (Vector<Person>) findAllByKey(null, personDb,
				"People", Person.class);
		return people;
	}

}
