package be.abis.exercise.model;

import javax.validation.constraints.Size;

public class Password {

    @Size(min=5, message="pass must be at least 5 characters long")
    private String pass;

    public Password(String pass) {
        this.pass = pass;
    }

    public Password() {
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
