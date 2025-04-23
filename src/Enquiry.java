public class Enquiry {
    private static int counter = 1;
    private int id;
    private String applicantNric;
    private String projectName;
    private String question;
    private String reply;
    private boolean replied;

    public Enquiry(String applicantNric, String projectName, String question) {
        this.id = counter++;
        this.applicantNric = applicantNric;
        this.projectName = projectName;
        this.question = question;
        this.replied = false;
    }
    public int getId() { return id; }
    public String getApplicantNric() { return applicantNric; }
    public String getProjectName() { return projectName; }
    public String getQuestion() { return question; }
    public void setQuestion(String q) { question = q; }
    public boolean isReplied() { return replied; }
    public String getReply() { return reply; }
    public void setReply(String r) { reply = r; replied = true; }
}
