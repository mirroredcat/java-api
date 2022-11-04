package be.abis.exercise.repository;

import be.abis.exercise.exceptions.PersonAlreadyExistsException;
import be.abis.exercise.exceptions.PersonCannotBeDeletedException;
import be.abis.exercise.exceptions.PersonNotFoundException;
import be.abis.exercise.model.Address;
import be.abis.exercise.model.Company;
import be.abis.exercise.model.Person;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PersonFileRepository implements PersonRepository {

	private ArrayList<Person> allPersons= new ArrayList<Person>();;
	private String fileLoc = "/temp/javacourses/personsAPI.csv";

	private Map<Integer, String> apiKeys = new HashMap<>();
	private String apiBase= "sodfisofjlsfjsljkf";


	@PostConstruct
	public void init(){
		this.readFile();
		for(Person p:allPersons){
			String s = apiBase + p.getPersonId();
			apiKeys.put(p.getPersonId(), s);
		}
	}

	@Override
	public ArrayList<Person> getAllPersons() {
		return allPersons;
	}

	public void readFile() {

		if (allPersons.size() != 0)
			allPersons.clear();
		BufferedReader br = null;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		try {
			br = new BufferedReader(new FileReader(fileLoc));
			String s = null;
			while ((s = br.readLine()) != null) {
				String[] vals = s.split(";");
				if (!vals[0].equals("")) {
					Address a = new Address();
					a.setStreet(!vals[10].equals("null") ? vals[10] : null);
					a.setNr(Integer.parseInt(!vals[11].equals("null") ? vals[11] : "0"));
					a.setZipcode(!vals[12].equals("null") ? vals[12] : null);
					a.setTown(!vals[13].equals("null") ? vals[13] : null);

					Company c = new Company();
					c.setName(!vals[7].equals("null") ? vals[7] : null);
					c.setTelephoneNumber(!vals[8].equals("null") ? vals[8] : null);
					c.setVatNr(!vals[9].equals("null") ? vals[9] : null);
					c.setAddress(a);

					Person p = new Person();
					p.setPersonId(!vals[0].equals("null") ? Integer.parseInt(vals[0]) : 0);
					p.setFirstName(!vals[1].equals("null") ? vals[1] : null);
					p.setLastName(!vals[2].equals("null") ? vals[2] : null);
					p.setBirthDate(LocalDate.parse(!vals[3].equals("null") ? vals[3] : "01/01/1900", dtf));
					p.setEmailAddress(!vals[4].equals("null") ? vals[4] : null);
					p.setPassword(!vals[5].equals("null") ? vals[5] : null);
					p.setLanguage(!vals[6].equals("null") ? vals[6] : null);
					p.setCompany(c);

					allPersons.add(p);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public Person findPerson(String emailAddress, String passWord) throws PersonNotFoundException {
		if (emailAddress == null || passWord == null) {
			return null;
		}

		this.readFile();
		// System.out.println("persons in PersonList" + allPersons);
		Iterator<Person> iter = allPersons.iterator();

		while (iter.hasNext()) {
			Person pers = iter.next();
			System.out.println("eep");
			if (pers.getEmailAddress().equalsIgnoreCase(emailAddress) && pers.getPassword().equals(passWord)) {
				return pers;
			}
		}
		throw new PersonNotFoundException("Login failed. Try Again.");
	}
	
	@Override
	public Person findPerson(int id) throws PersonNotFoundException {
		this.readFile();
		return allPersons.stream().filter(p->p.getPersonId()==id).findFirst().orElseThrow(()-> new PersonNotFoundException("Person with id " + id + " does not exist."));
	}

	public List<Person> findPersonsByCompany(String companyName){
		this.readFile();
		return allPersons.stream()
				.filter(p->p.getCompany().getName().equals(companyName))
				.collect(Collectors.toList());
	}

	@Override
	public void addPerson(Person p) throws PersonAlreadyExistsException, IOException {
		boolean b = false;
		this.readFile();
		Iterator<Person> iter = allPersons.iterator();
		PrintWriter pw = new PrintWriter(new FileWriter(fileLoc, true));
		while (iter.hasNext()) {
			Person pers = iter.next();
			if (pers.getEmailAddress().equalsIgnoreCase(p.getEmailAddress())) {
				throw new PersonAlreadyExistsException("you were already registered, login please");
			} else {
				b = true;
			}
		}
		if (b) {
			StringBuilder sb = this.parsePerson(p);
			pw.append("\n" + sb);
			allPersons.add(p);
			apiKeys.put(p.getPersonId(), apiBase+p.getPersonId());
		}
		pw.close();
	}

	@Override
	public Person deletePerson(int id) throws PersonCannotBeDeletedException {
		Iterator<Person> iter = allPersons.iterator();
		Person p= null;
		boolean b = false;
		while (iter.hasNext()) {
			Person pers = iter.next();
			if (pers.getPersonId() == id) {
				p = pers;
				System.out.println(p);
				iter.remove();
				b = true;
			}
		}


		if(b){
			try {
				this.writePersons();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return p;
			}
		} else {
			throw new PersonCannotBeDeletedException("The person you are trying to delete does not exist");
		}
		return null;
	}

	@Override
	public void changePassword(Person p, String newPswd) throws IOException {
		Iterator<Person> iter = allPersons.iterator();
		while (iter.hasNext()) {
			Person pers = iter.next();
			if (pers.getEmailAddress().equals(p.getEmailAddress())) {
				pers.setPassword(newPswd);
			}
		}
		this.writePersons();
	}

	private StringBuilder parsePerson(Person p) {
		StringBuilder sb = new StringBuilder();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		int nr = p.getCompany().getAddress().getNr();
		sb.append(p.getPersonId()).append(";").append(p.getFirstName()).append(";").append(p.getLastName() + ";").append(p.getBirthDate() != null ? p.getBirthDate().format(dtf) : null).append(";").append(p.getEmailAddress() + ";")
				.append(p.getPassword() + ";").append(p.getLanguage().toLowerCase() + ";")
				.append(p.getCompany().getName() + ";").append(p.getCompany().getTelephoneNumber() + ";")
				.append(p.getCompany().getVatNr() + ";").append(p.getCompany().getAddress().getStreet() + ";")
				.append((nr != 0 ? nr : null) + ";").append(p.getCompany().getAddress().getZipcode() + ";")
				.append(p.getCompany().getAddress().getTown());

		System.out.println(sb);
		return sb;
	}

	private void writePersons() throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(fileLoc));

		for (Person pe : allPersons) {
			StringBuilder sb = this.parsePerson(pe);
			pw.println(sb);
		}

		pw.close();
	}

	public Map<Integer, String> getApiKeys() {
		return apiKeys;
	}

}
