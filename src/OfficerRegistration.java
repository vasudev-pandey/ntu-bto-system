public class OfficerRegistration {
    private String officerNric;
    private String projectName;
    private ApplicationStatus status; // reuse PENDING/APPROVED(RENAME)/REJECTED
    public OfficerRegistration(String nric, String projectName) {
        this.officerNric = nric;
        this.projectName = projectName;
        this.status = ApplicationStatus.PENDING;
    }
    public String getOfficerNric() { return officerNric; }
    public String getProjectName() { return projectName; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus s) { status = s; }
}
