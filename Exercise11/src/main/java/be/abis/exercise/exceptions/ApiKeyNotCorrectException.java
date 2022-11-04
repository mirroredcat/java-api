package be.abis.exercise.exceptions;

public class ApiKeyNotCorrectException extends Exception{
    public ApiKeyNotCorrectException(String message) {
        super(message);
    }
}
