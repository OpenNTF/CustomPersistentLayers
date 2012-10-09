package controller;

import javax.annotation.PostConstruct;

import model.Person;

public class TestController {
	@PostConstruct
	public static Person getPerson(){
		
		Person person=new Person();
		person.getName();
		//person.getValue("name");
		return person;
	}

}
