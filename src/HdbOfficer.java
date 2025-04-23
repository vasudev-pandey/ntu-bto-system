import java.util.*;

public class HdbOfficer extends Applicant {
    private OfficerRegistration registration;
    public HdbOfficer(String nric, String name, int age, String maritalStatus, String password) {
        super(nric,name,age,maritalStatus,password);
    }
    @Override public String getRole() { return "Officer"; }
    public OfficerRegistration getRegistration() { return registration; }
    public void setRegistration(OfficerRegistration reg) { this.registration = reg; }
}
