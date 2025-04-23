import java.util.*;
public class Applicant extends User {
    private Application application;
    private List<Enquiry> enquiries = new ArrayList<>();

    public Applicant(String nric, String name, int age, String maritalStatus, String password) {
        super(nric, name, age, maritalStatus, password);
    }
    @Override public String getRole() { return "Applicant"; }

    public Application getApplication() { return application; }
    public void setApplication(Application app) { this.application = app; }
    public List<Enquiry> getEnquiries() { return enquiries; }
}
