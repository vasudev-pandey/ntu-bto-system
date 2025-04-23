import java.util.Objects;
public abstract class User {
    protected String nric;
    protected String name;
    protected int age;
    protected String maritalStatus;
    protected String password;

    public User(String nric, String name, int age, String maritalStatus, String password) {
        this.nric = nric;
        this.name = name;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.password = password;
    }
    public String getNric() { return nric; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getMaritalStatus() { return maritalStatus; }
    public boolean checkPassword(String pw) { return Objects.equals(password, pw); }
    public void setPassword(String pw) { this.password = pw; }
    public abstract String getRole();
}
