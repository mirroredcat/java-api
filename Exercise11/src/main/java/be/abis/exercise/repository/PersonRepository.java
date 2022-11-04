package be.abis.exercise.repository;

import be.abis.exercise.exceptions.PersonAlreadyExistsException;
import be.abis.exercise.exceptions.PersonCannotBeDeletedException;
import be.abis.exercise.exceptions.PersonNotFoundException;
import be.abis.exercise.model.Person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface PersonRepository {
	
	    ArrayList<Person> getAllPersons();
	    Person findPerson(int id) throws PersonNotFoundException;
	    Person findPerson(String emailAddress, String passWord) throws PersonNotFoundException;
		List<Person> findPersonsByCompany(String companyName);
		public void addPerson(Person p) throws PersonAlreadyExistsException, IOException;
	    public Person deletePerson(int id) throws PersonCannotBeDeletedException;
	    void changePassword(Person p, String newPswd) throws IOException;
		Map<Integer, String> getApiKeys();

}
