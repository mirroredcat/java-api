package be.abis.exercise.controller;


import be.abis.exercise.exceptions.ApiKeyNotCorrectException;
import be.abis.exercise.exceptions.PersonAlreadyExistsException;
import be.abis.exercise.exceptions.PersonCannotBeDeletedException;
import be.abis.exercise.exceptions.PersonNotFoundException;
import be.abis.exercise.model.EmailPass;
import be.abis.exercise.model.Person;
import be.abis.exercise.model.Persons;
import be.abis.exercise.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import be.abis.exercise.model.Password;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping( "/persons")
@EnableGlobalMethodSecurity(jsr250Enabled=true)
public class PersonController {

    @Autowired
    PersonService personService;


    @GetMapping("")
    @RolesAllowed("USER")
    public List<Person> getAllPersons(){
        return personService.getAllPersons();
    }


    @GetMapping(path="/company", produces= MediaType.APPLICATION_XML_VALUE)
    @RolesAllowed("USER")

    public Persons findPersonsByCompanyName(@RequestParam("companyname") String companyName ){
        List<Person> people = personService.findPersonsByCompany(companyName);
        Persons persons = new Persons();
        persons.setPersons(people);
        return persons;
    }

    @GetMapping(path="/{id}")
    @RolesAllowed("USER")

    public Person findPersonById(@PathVariable @Max(value=10, message="Id's only go up to 10")  int id) throws PersonNotFoundException {
            return personService.findPerson(id);
    }

    @PostMapping(path="",consumes={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @RolesAllowed("USER")

    public void addPerson(@Valid @RequestBody Person person) throws IOException, PersonAlreadyExistsException {
            personService.addPerson(person);
    }

    /*
    @PostMapping("{id}")
    public void changePasswordByBody(@PathVariable int id, @Valid @RequestBody Person p) throws PersonNotFoundException {
        Person foundP = personService.findPerson(id);
        try {
            personService.changePassword(foundP, p.getPassword());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

     */

    @PostMapping("/login")
    public ResponseEntity<? extends Object> loginWithUsernameAndPass(@RequestBody EmailPass ep) throws PersonNotFoundException {
        Person p = personService.findPerson(ep.getEmailAddress(), ep.getPassword());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("api-key", personService.getApiKeys().get(p.getPersonId()));
        return new ResponseEntity<>(p, responseHeaders, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    @RolesAllowed("ADMIN")
    public Person deletePerson(@PathVariable int id) throws PersonCannotBeDeletedException {
            return personService.deletePerson(id);
    }

    @PatchMapping("/change-password/{id}")
    public ResponseEntity<? extends Object> changePasswordWithPatch(@PathVariable int id, @Valid @RequestBody Password pass, @RequestHeader MultiValueMap<String, String> headers) throws PersonNotFoundException, ApiKeyNotCorrectException {
        Person foundP;
        boolean keyOk = false;
        if(headers.containsKey("api-key")){
            String auth = headers.get("api-key").get(0);
            boolean tokenOk = this.checkToken(id, auth);
            if (tokenOk) keyOk=true;
        }

        if (keyOk) {
            try {
                foundP = personService.findPerson(id);
                personService.changePassword(foundP, pass.getPass());
                return new ResponseEntity<Void>(HttpStatus.OK);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new ApiKeyNotCorrectException("gud API key plox");
        }
    }

    private boolean checkToken(int id, String token){
        boolean tokenOk = false;
        Map<Integer, String> keys = personService.getApiKeys();
        if(token != null && keys.get(id).equals(token)) tokenOk=true;
        return tokenOk;
    }
}
