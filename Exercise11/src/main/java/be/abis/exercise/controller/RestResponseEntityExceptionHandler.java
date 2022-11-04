package be.abis.exercise.controller;

import be.abis.exercise.error.ApiError;
import be.abis.exercise.error.ValidationError;
import be.abis.exercise.exceptions.ApiKeyNotCorrectException;
import be.abis.exercise.exceptions.PersonAlreadyExistsException;
import be.abis.exercise.exceptions.PersonCannotBeDeletedException;
import be.abis.exercise.exceptions.PersonNotFoundException;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.Constraint;
import javax.validation.ConstraintViolationException;
import java.util.List;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid (MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request){
        ApiError err = new ApiError("invalid arguments", status.value(), ex.getMessage());
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ValidationError> validationErrorList = err.getInvalidParams();
        for(FieldError fe:fieldErrors){
            ValidationError validationError = new ValidationError();
            validationError.setName(fe.getField());
            validationError.setReason(fe.getDefaultMessage());
            validationErrorList.add(validationError);
        }
        headers.add("content-type", MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        return new ResponseEntity<Object>(err, headers, status);
    }



    @ExceptionHandler(value= PersonAlreadyExistsException.class)
    protected ResponseEntity<? extends Object> handlePersonAlreadyExists(PersonAlreadyExistsException paee, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ApiError err = new ApiError("person already exists", status.value(), paee.getMessage());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-type", MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        return new ResponseEntity<ApiError>(err, responseHeaders, status);
    }

    @ExceptionHandler(value= PersonCannotBeDeletedException.class)
    protected ResponseEntity<? extends Object> handlePersonCannotBeDeleted(PersonCannotBeDeletedException pcbde, WebRequest request){
        HttpStatus status = HttpStatus.GONE;
        ApiError err = new ApiError("person cannot be deleted", status.value(), pcbde.getMessage());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-type", MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        return new ResponseEntity<ApiError>(err, responseHeaders, status);
    }

    @ExceptionHandler(value= PersonNotFoundException.class)
    protected ResponseEntity<? extends Object> handlePersonNotFound(PersonNotFoundException pnfe, WebRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiError err = new ApiError("person not found", status.value(), pnfe.getMessage());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-type", MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        return new ResponseEntity<ApiError>(err, responseHeaders, status);
    }

    @ExceptionHandler(value= ConstraintViolationException.class)
    protected ResponseEntity<? extends Object> constraintValidation( ConstraintViolationException cve, WebRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiError err = new ApiError("id out of bounds", status.value(),cve.getMessage() );
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-type", MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        return new ResponseEntity<ApiError>(err, responseHeaders, status);
    }

    @ExceptionHandler(value= MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<? extends Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException matme, WebRequest request){
        ApiError err = new ApiError(matme.getName() + " should be of type " + matme.getRequiredType().getName(), HttpStatus.BAD_REQUEST.value(), matme.getMessage());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-type", MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        return new ResponseEntity<ApiError>(err, responseHeaders, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ApiKeyNotCorrectException.class)
    protected ResponseEntity<? extends Object> handleApiKeyNotCorrect(ApiKeyNotCorrectException aknce, WebRequest request){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ApiError err = new ApiError("unauthorized", status.value(), aknce.getMessage());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-type", MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        return new ResponseEntity<ApiError>(err, responseHeaders, status);
    }

}
