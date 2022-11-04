package be.abis.exercise.exceptions;

public class PersonCannotBeDeletedException extends Exception{
    public PersonCannotBeDeletedException(String message) {
        super(message);
    }
}
