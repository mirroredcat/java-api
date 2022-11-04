package be.abis.exercise.exceptions;

public class PersonAlreadyExistsException extends  Exception{
    public PersonAlreadyExistsException(String message) {
        super(message);
    }
}
