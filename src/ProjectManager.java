import java.time.LocalDate;import java.util.*;
public class ProjectManager {
    private Scanner sc = new Scanner(System.in);
    private LocalDate today = LocalDate.now();
    public void showApplicantMenu(Applicant app) {
        int choice;
        do {
            System.out.println("\n=== Applicant Menu ===");
            System.out.println("1. View Available Projects");
            System.out.println("2. Apply for BTO");
            System.out.println("3. View My Application");
            System.out.println("4. Request Withdrawal");
            System.out.println("5. View/Edit/Delete Enquiries");
            System.out.println("6. Change Password");
            System.out.println("7. Logout");
            choice = sc.nextInt(); sc.nextLine();
            switch(choice) {
                case 1: viewProjects(app); break;
                case 2: applyBTO(app); break;
                case 3: viewMyApplication(app); break;
                case 4: requestWithdrawal(app); break;
                case 5: manageEnquiries(app); break;
                case 6: changePassword(app); break;
            }
        } while(choice!=7);
    }
    private void requestWithdrawal(Applicant a) {
        Application app = a.getApplication();
        if (app == null || app.getStatus() == ApplicationStatus.PENDING) {
            System.out.println("No booked application to withdraw.");
            return;
        }
        app.requestWithdrawal();
        System.out.println("Withdrawal requested.");
    }
    private void viewProjects(Applicant app) {
        System.out.println("Available Projects:");
        for(Project p: DataStore.projects.values()) {
            if (p.isVisible(today) && eligible(app,p))
                System.out.printf("%s (%s): 2R(%d),3R(%d)%n",p.getName(),p.getNeighborhood(),p.getRemTwo(),p.getRemThree());
        }
    }
    private void viewMyApplication(Applicant app) {
        Application a = app.getApplication();
        if (a == null) {
            System.out.println("You have not applied for any project.");
            return;
        }
        System.out.println("=== Your Application ===");
        System.out.printf("Project: %s%n", a.getProjectName());
        System.out.printf("Flat type: %s%n", a.getFlatType());
        System.out.printf("Status: %s%n", a.getStatus());
    }
    private boolean eligible(Applicant a, Project p) {
        if(!p.isOpen(today)) return false;
        if(a.getRole().equals("Applicant")) {
            if(a.getMaritalStatus().equals("Single")) return a.getAge()>=35;
            else return a.getAge()>=21;
        }
        return true;
    }
    private void applyBTO(Applicant a) {
        // 1. Block if there is any active application
        Application existing = a.getApplication();
        if (existing != null
                && existing.getStatus() != ApplicationStatus.UNSUCCESSFUL)
        {
            System.out.println(
                    "Already applied to \""
                            + existing.getProjectName()
                            + "\" with status "
                            + existing.getStatus()
            );
            return;
        }

        // 2. Clear out a past unsuccessful application so we can reapply
        if (existing != null
                && existing.getStatus() == ApplicationStatus.UNSUCCESSFUL)
        {
            a.setApplication(null);
            DataStore.applications.remove(a.getNric());
        }

        // 3. Prompt for project name
        System.out.print("Project name: ");
        String projectName = sc.nextLine().trim();
        Project p = DataStore.projects.get(projectName);

        // 4. Single combined check for existence, eligibility, and visibility
        if (p == null
                || !eligible(a, p)
                || !p.isVisible(today))
        {
            System.out.println("Cannot apply: project not available.");
            return;
        }

        // 5. Determine flat type and create the new application
        FlatType ft = a.getMaritalStatus().equals("Single")
                ? FlatType.TWO_ROOM
                : chooseFlat();
        Application newApp = new Application(a.getNric(), projectName, ft);
        DataStore.applications.put(a.getNric(), newApp);
        a.setApplication(newApp);
        System.out.println("Applied.");
    }
    private FlatType chooseFlat() {
        System.out.print("Choose flat (2/3): "); int c=sc.nextInt(); sc.nextLine();
        return c==3?FlatType.THREE_ROOM:FlatType.TWO_ROOM;
    }
    private void manageEnquiries(Applicant a) {
        System.out.println("Enquiry Menu: 1.Add 2.View/Edit/Delete 3.Back"); int c=sc.nextInt(); sc.nextLine();
        if(c==1) {
            System.out.print("Project name: "); String pn=sc.nextLine();
            System.out.print("Question: "); String q=sc.nextLine();
            Enquiry e=new Enquiry(a.getNric(),pn,q);
            DataStore.enquiries.add(e);
            a.getEnquiries().add(e);
            System.out.println("Enquiry submitted. ID="+e.getId());
        } else if(c==2) {
            for(Enquiry e: a.getEnquiries())
                System.out.printf("ID:%d [%s] Q:%s Reply:%s%n",e.getId(),e.getProjectName(),e.getQuestion(),e.isReplied()?e.getReply():"(none)");
            System.out.print("Enter ID to edit/delete: "); int id=sc.nextInt(); sc.nextLine();
            Enquiry target=null; for(Enquiry e:a.getEnquiries()) if(e.getId()==id) target=e;
            if(target!=null && !target.isReplied()) {
                System.out.print("New Q (or blank to delete): "); String nq=sc.nextLine();
                if(nq.isBlank()) { DataStore.enquiries.remove(target); a.getEnquiries().remove(target); System.out.println("Deleted."); }
                else { target.setQuestion(nq); System.out.println("Updated."); }
            }
        }
    }
    private void changePassword(User u) {
        System.out.print("New password: ");
        String np = sc.nextLine().trim();
        LoginManager.changePassword(u, np);
        try {
            DataStore.saveUsers("users.txt");
            System.out.println("Password updated. Please login again.");
        } catch (Exception e) {
            System.err.println("Error saving new password: " + e.getMessage());
        }
        // force a logout by throwing our special exception
        throw new LogoutException();
    }

    public void showOfficerMenu(HdbOfficer off) {
        int choice;
        do {
            System.out.println("\n=== Officer Menu ===");
            System.out.println("1. Register for Project");
            System.out.println("2. View/Reply Enquiries");
            System.out.println("3. Manage Applications and Booking");
            System.out.println("4. Change Password");
            System.out.println("5. View Registration Status");
            System.out.println("6. View My Project Details");
            System.out.println("7. Logout");
            choice = sc.nextInt(); sc.nextLine();
            switch(choice) {
                case 1: registerOfficer(off); break;
                case 2: replyEnquiries(off); break;
                case 3: manageBookings(off); break;
                case 4: changePassword(off); break;
                case 5: viewRegistrationStatus(off); break;
                case 6: viewOfficerProject(off); break;
            }
        } while(choice!=7);
    }

    private void viewRegistrationStatus(HdbOfficer off) {
        OfficerRegistration reg = off.getRegistration();
        if (reg == null) {
            System.out.println("You are not registered for any project.");
        } else {
            System.out.println("=== Registration Status ===");
            System.out.println("Project: " + reg.getProjectName());
            System.out.println("Status : " + reg.getStatus());
        }
    }

    private void registerOfficer(HdbOfficer off) {
        // 1) Read the desired project
        System.out.print("Project name: ");
        String pn = sc.nextLine().trim();
        Project newProj = DataStore.projects.get(pn);
        if (newProj == null || !newProj.isVisible(today)) {
            System.out.println("Cannot register for that project.");
            return;
        }

        // 2) If already registered to another project, enforce no date overlap
        OfficerRegistration existing = off.getRegistration();
        if (existing != null) {
            Project oldProj = DataStore.projects.get(existing.getProjectName());
            LocalDate oldStart = oldProj.getOpenDate();
            LocalDate oldEnd   = oldProj.getCloseDate();
            LocalDate newStart = newProj.getOpenDate();
            LocalDate newEnd   = newProj.getCloseDate();

            boolean overlap = !( newEnd.isBefore(oldStart) || newStart.isAfter(oldEnd) );
            if (overlap) {
                System.out.println("Cannot register: you already handle “"
                        + oldProj.getName()
                        + "” from " + oldStart + " to " + oldEnd
                        + ", which overlaps this project’s period.");
                return;
            }
        }

        // 3) Proceed to register
        OfficerRegistration reg = new OfficerRegistration(off.getNric(), pn);
        DataStore.registrations.add(reg);
        off.setRegistration(reg);
        System.out.println("Registration pending approval.");
    }
    private void replyEnquiries(HdbOfficer off) {
        OfficerRegistration reg = off.getRegistration();
        if (reg == null || reg.getStatus() != ApplicationStatus.SUCCESSFUL) {
            System.out.println("Cannot reply to enquiries: you have no approved registration.");
            return;
        }

        String proj = reg.getProjectName();
        System.out.println("Pending enquiries for project “" + proj + "”:");
        for (Enquiry e : DataStore.enquiries) {
            if (e.getProjectName().equals(proj) && !e.isReplied()) {
                System.out.printf("ID:%d Q:%s%nReply: ", e.getId(), e.getQuestion());
                String r = sc.nextLine();
                e.setReply(r);
                System.out.println("Replied.");
            }
        }
    }
    private void manageBookings(HdbOfficer off) {
        OfficerRegistration reg = off.getRegistration();
        if (reg == null || reg.getStatus() != ApplicationStatus.SUCCESSFUL) {
            System.out.println("Cannot manage bookings: you have no approved registration.");
            return;
        }

        String proj = reg.getProjectName();
        System.out.println("Pending applications for “" + proj + "”:");
        for (Application app : DataStore.applications.values()) {
            // use proj here, not off.getRegistration() again
            if (app.getProjectName().equals(proj)
                    && app.getStatus() == ApplicationStatus.SUCCESSFUL)
            {
                System.out.printf("Applicant:%s Flat:%s%nBook? (y/n): ",
                        app.getApplicantNric(), app.getFlatType());
                if (sc.nextLine().equalsIgnoreCase("y")) {
                    Project p = DataStore.projects.get(proj);
                    app.setStatus(ApplicationStatus.BOOKED);
                    Applicant a = (Applicant) DataStore.users.get(app.getApplicantNric());

                    // === Booking Receipt ===
                    System.out.println("Booked.");
                    System.out.println("\n=== Booking Receipt ===");
                    System.out.printf("Name    : %s%n", a.getName());
                    System.out.printf("NRIC    : %s%n", a.getNric());
                    System.out.printf("Age     : %d%n", a.getAge());
                    System.out.printf("Marital : %s%n", a.getMaritalStatus());
                    System.out.printf("Flat    : %s%n", app.getFlatType());
                    System.out.printf("Project : %s%n%n", proj);
                }
            }
        }
    }
    public void showManagerMenu(HdbManager mgr) {
        int choice;
        do {
            System.out.println("\n=== Manager Menu ===");
            System.out.println("1. Create/Edit/Delete Projects");
            System.out.println("2. Toggle Visibility");
            System.out.println("3. Approve Officer Registrations");
            System.out.println("4. Approve/Reject Applications & Withdrawals");
            System.out.println("5. Generate Reports");
            System.out.println("6. View/Reply to Enquiries");
            System.out.println("7. Change Password");
            System.out.println("8. View All Projects");
            System.out.println("9. View My Projects");
            System.out.println("10. Logout");
            choice = sc.nextInt(); sc.nextLine();
            switch(choice) {
                case 1: crudProjects(mgr); break;
                case 2: toggleVisibility(mgr); break;
                case 3: approveRegistrations(mgr); break;
                case 4: approveApplications(mgr); break;
                case 5: generateReports(mgr); break;
                case 6: replyAsManager(mgr); break;
                case 7: changePassword(mgr); break;
                case 8: viewAllProjects(); break;
                case 9: viewMyProjects(mgr); break;
            }
        } while(choice!=10);
    }
    private void replyAsManager(HdbManager mgr) {
        System.out.println("\n=== All Project Enquiries ===");
        for (Enquiry e : DataStore.enquiries) {
            // skip enquiries for projects this manager does NOT own
            Project qProj = DataStore.projects.get(e.getProjectName());
            if (qProj == null || !qProj.getManagerNric().equals(mgr.getNric())) {
                continue;
            }
            System.out.printf("ID:%d  Project:%s%n", e.getId(), e.getProjectName());
            System.out.printf("  Q: %s%n", e.getQuestion());
            if (e.isReplied()) {
                System.out.printf("  Reply: %s%n%n", e.getReply());
            } else {
                System.out.print("  Reply now? (y/n): ");
                String resp = sc.nextLine().trim();
                if (resp.equalsIgnoreCase("y")) {
                    System.out.print("  Your reply: ");
                    String r = sc.nextLine();
                    e.setReply(r);
                    System.out.println("  Replied.\n");
                } else {
                    System.out.println("  Skipped.\n");
                }
            }
        }
    }
    private void crudProjects(HdbManager mgr) {
        System.out.println("1.Create 2.Edit 3.Delete 4.Back"); int c=sc.nextInt(); sc.nextLine();
        if(c==1) {
            System.out.print("Name: "); String name=sc.nextLine();
            System.out.print("Neighborhood: "); String nb=sc.nextLine();
            System.out.print("Flats TWO: "); int t2=sc.nextInt(); System.out.print(" THREE: "); int t3=sc.nextInt(); sc.nextLine();
            System.out.print("Open Date (yyyy-MM-dd): "); String od=sc.nextLine();
            System.out.print("Close Date: "); String cd=sc.nextLine();
            LocalDate newOpen  = LocalDate.parse(od);
            LocalDate newClose = LocalDate.parse(cd);
            for (Project existing : DataStore.projects.values()) {
                if (existing.getManagerNric().equals(mgr.getNric())) {
                    // overlap if NOT (newClose < existing.open OR newOpen > existing.close)
                    if (!(newClose.isBefore(existing.getOpenDate())
                            || newOpen.isAfter(existing.getCloseDate()))) {
                        System.out.println("Cannot create: overlapping active project exists.");
                        return;
                    }
                }
            }
            if (DataStore.projects.containsKey(name)) {
                System.out.println("Cannot create: project name already exists.");
                return;
            }
            Project p=new Project(name,nb,t2,t3,LocalDate.parse(od),LocalDate.parse(cd),mgr.getNric(),new ArrayList<>());
            DataStore.projects.put(name,p);
            System.out.println("Created.");
        } else if(c==2) {
            System.out.print("Project to edit: "); String pn=sc.nextLine(); Project p=DataStore.projects.get(pn);
            if(p!=null && p.getManagerNric().equals(mgr.getNric())) {
                System.out.print("New TWO: "); p.setRemTwo(sc.nextInt()); System.out.print("NEW THREE: "); p.setRemThree(sc.nextInt()); sc.nextLine();
                System.out.println("Updated.");
            }
        } else if(c==3) {
            System.out.print("Project to delete: "); String pn=sc.nextLine();
            DataStore.projects.remove(pn);
            System.out.println("Deleted.");
        }
    }
    private void toggleVisibility(HdbManager mgr) {
        System.out.print("Project: "); String pn=sc.nextLine(); Project p=DataStore.projects.get(pn);
        if(p!=null && p.getManagerNric().equals(mgr.getNric())) { p.toggleVisible(); System.out.println("Toggled."); }
    }
    private void approveRegistrations(HdbManager mgr) {
        for(OfficerRegistration r: DataStore.registrations) {
            Project p=DataStore.projects.get(r.getProjectName());
            if(p.getManagerNric().equals(mgr.getNric()) && r.getStatus()==ApplicationStatus.PENDING) {
                System.out.printf("Officer:%s for %s Approve? (y/n): ",r.getOfficerNric(),r.getProjectName());
                if(sc.nextLine().equalsIgnoreCase("y")) { r.setStatus(ApplicationStatus.SUCCESSFUL); p.addOfficer(r.getOfficerNric()); }
                else r.setStatus(ApplicationStatus.UNSUCCESSFUL);
            }
        }
    }
    private void approveApplications(HdbManager mgr) {
        // Handle booked withdrawals first
        // Handle withdrawal requests first
        for (Application app : DataStore.applications.values()) {
            if ((app.getStatus() == ApplicationStatus.SUCCESSFUL
                    || app.getStatus() == ApplicationStatus.BOOKED)
                    && app.isWithdrawalRequested())
            {
                System.out.printf(
                        "Approve withdrawal for %s (currently %s)? (y/n): ",
                        app.getApplicantNric(),
                        app.getStatus()
                );
                if (sc.nextLine().equalsIgnoreCase("y")) {
                    // 1) Look up the project
                    Project proj = DataStore.projects.get(app.getProjectName());
                    // 2) Restore exactly one flat of the reserved type
                    proj.restoreFlat(app.getFlatType());
                    // 3) Demote the application
                    app.setStatus(ApplicationStatus.UNSUCCESSFUL);
                }
                // 4) Clear the withdrawal flag no matter what
                app.clearWithdrawalRequest();
            }
        }

        // Then handle new applications
        for (Application app : DataStore.applications.values()) {
            Project p = DataStore.projects.get(app.getProjectName());
            if (p.getManagerNric().equals(mgr.getNric())
                    && app.getStatus() == ApplicationStatus.PENDING)
            {
                System.out.printf("Applicant:%s Flat:%s Approve? (y/n): ",
                        app.getApplicantNric(), app.getFlatType());
                if (sc.nextLine().equalsIgnoreCase("y")) {
                    if (p.allocateFlat(app.getFlatType())) {
                        app.setStatus(ApplicationStatus.SUCCESSFUL);
                    } else {
                        System.out.println("No more units available; marking unsuccessful.");
                        app.setStatus(ApplicationStatus.UNSUCCESSFUL);
                    }
                } else {
                    app.setStatus(ApplicationStatus.UNSUCCESSFUL);
                }
            }
        }
    }

    private void generateReports(HdbManager mgr) {
        System.out.println("\n=== Generate Booking Report ===");
        System.out.print("Filter by marital status (Single/Married/All): ");
        String filter = sc.nextLine().trim();
        System.out.println("\nName,NRIC,Age,Marital,Flat,Project");
        for (Application app : DataStore.applications.values()) {
            if (app.getStatus() != ApplicationStatus.BOOKED) continue;
            Applicant a = (Applicant) DataStore.users.get(app.getApplicantNric());
            // apply filter
            if (!filter.equalsIgnoreCase("All")
                    && !a.getMaritalStatus().equalsIgnoreCase(filter))
            {
                continue;
            }
            // print CSV line
            System.out.printf("%s,%s,%d,%s,%s,%s%n",
                    a.getName(),
                    a.getNric(),
                    a.getAge(),
                    a.getMaritalStatus(),
                    app.getFlatType(),
                    app.getProjectName()
            );
        }
    }

    private void viewOfficerProject(HdbOfficer off) {
        var reg = off.getRegistration();
        if (reg == null || reg.getStatus() != ApplicationStatus.SUCCESSFUL) {
            System.out.println("No approved registration found.");
            return;
        }
        var p = DataStore.projects.get(reg.getProjectName());
        if (p == null) {
            System.out.println("Project data missing.");
        } else {
            displayProjectDetails(p);
        }
    }

    // Utility to print full project information
    private void displayProjectDetails(Project p) {
        System.out.printf(
                "Name: %s%nNeighborhood: %s%nOpen: %s to %s%nVisible: %b%n2‑Room left: %d%n3‑Room left: %d%n%n",
                p.getName(),
                p.getNeighborhood(),
                p.getOpenDate(),
                p.getCloseDate(),
                p.isVisible(LocalDate.now()),
                p.getRemTwo(),
                p.getRemThree()
        );
    }
    private void viewAllProjects() {
        DataStore.projects.values().forEach(this::displayProjectDetails);
    }

    private void viewMyProjects(HdbManager mgr) {
        DataStore.projects.values().stream()
                .filter(p -> p.getManagerNric().equals(mgr.getNric()))
                .forEach(this::displayProjectDetails);
    }
}
