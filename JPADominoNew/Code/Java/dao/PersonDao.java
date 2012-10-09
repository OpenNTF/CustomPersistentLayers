package dao;

import java.util.Vector;

import model.Person;

public interface PersonDao {
	public Vector<Person> getPeople() throws Exception;

}
