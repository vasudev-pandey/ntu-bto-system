public class Application {
    private String applicantNric;
    private String projectName;
    private FlatType flatType;
    private ApplicationStatus status;

    public Application(String applicantNric, String projectName, FlatType flatType) {
        this.applicantNric = applicantNric;
        this.projectName = projectName;
        this.flatType = flatType;
        this.status = ApplicationStatus.PENDING;
    }
    public String getApplicantNric() { return applicantNric; }
    public String getProjectName() { return projectName; }
    public FlatType getFlatType() { return flatType; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus s) { this.status = s; }
    private boolean withdrawalRequested = false;

    public void requestWithdrawal() {
        this.withdrawalRequested = true;
    }

    public boolean isWithdrawalRequested() {
        return withdrawalRequested;
    }

    public void clearWithdrawalRequest() {
        this.withdrawalRequested = false;
    }
}
