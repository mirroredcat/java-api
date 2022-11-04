package be.abis.exercise.model;

public class EmailPass {

    private String emailAddress;
    private String password;

    public EmailPass(String email, String password) {
        this.emailAddress = email;
        this.password = password;
    }

    public EmailPass() {
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String email) {
        this.emailAddress = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
