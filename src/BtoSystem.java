import java.util.Scanner;

public class BtoSystem {
    public static void main(String[] args) {
        try {
            DataStore.loadUsers("users.txt");
            DataStore.loadProjects("projects.txt");
        } catch(Exception e) {
            System.err.println("Initialization error: " + e.getMessage());
            return;
        }

        Scanner sc = new Scanner(System.in);
        ProjectManager pm = new ProjectManager();

        while (true) {
            System.out.print("NRIC: ");
            String nric = sc.nextLine().trim();

            // 1. NRIC format check
            if (!nric.matches("^[ST]\\d{7}[A-Z]$")) {
                System.out.println("Invalid NRIC format.");
                continue;
            }

            System.out.print("Password: ");
            String pw = sc.nextLine();

            // 2. Attempt login
            User u = LoginManager.login(nric, pw);
            if (u == null) {
                System.out.println("Login failed.");
                continue;
            }

            // 3. Route into the correct menu and catch logout requests
            try {
                switch (u.getRole()) {
                    case "Applicant":
                        pm.showApplicantMenu((Applicant) u);
                        break;
                    case "Officer":
                        pm.showOfficerMenu((HdbOfficer) u);
                        break;
                    case "Manager":
                        pm.showManagerMenu((HdbManager) u);
                        break;
                }
            } catch (LogoutException ex) {
                // Swallow the exception and restart at login prompt
                continue;
            }
        }
    }
}
