public class HdbManager extends User {
    public HdbManager(String nric, String name, int age, String maritalStatus, String password) {
        super(nric,name,age,maritalStatus,password);
    }
    @Override public String getRole() { return "Manager"; }
}
