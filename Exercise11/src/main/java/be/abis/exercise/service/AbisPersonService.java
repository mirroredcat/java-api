package be.abis.exercise.service;

import be.abis.exercise.exceptions.PersonAlreadyExistsException;
import be.abis.exercise.exceptions.PersonCannotBeDeletedException;
import be.abis.exercise.exceptions.PersonNotFoundException;
import be.abis.exercise.model.Person;
import be.abis.exercise.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AbisPersonService implements PersonService {

	@Autowired
	PersonRepository personRepository;
	
	@Override
	public ArrayList<Person> getAllPersons() {
		return personRepository.getAllPersons();
	}

	@Override
	public Person findPerson(int id) throws PersonNotFoundException {
		return personRepository.findPerson(id);
	}

	@Override
	public Person findPerson(String emailAddress, String passWord) throws PersonNotFoundException {
		return personRepository.findPerson(emailAddress, passWord);
	}

	@Override
	public List<Person> findPersonsByCompany(String companyName) {
		return personRepository.findPersonsByCompany(companyName);
	}

	@Override
	public void addPerson(Person p) throws IOException, PersonAlreadyExistsException {
		personRepository.addPerson(p);
	}

	@Override
	public Person deletePerson(int id) throws PersonCannotBeDeletedException {
		return personRepository.deletePerson(id);
	}

	@Override
	public void changePassword(Person p, String newPswd) throws IOException {
		personRepository.changePassword(p, newPswd);
	}

	@Override
	public Map<Integer, String> getApiKeys(){
		return personRepository.getApiKeys();
	}

}
