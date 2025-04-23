import java.nio.file.*;import java.time.LocalDate;import java.time.format.DateTimeFormatter;
import java.util.*;
public class DataStore {
    public static Map<String, User> users = new HashMap<>();
    public static Map<String, Project> projects = new HashMap<>();
    public static Map<String, Application> applications = new HashMap<>();
    public static List<Enquiry> enquiries = new ArrayList<>();
    public static List<OfficerRegistration> registrations = new ArrayList<>();
    private static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static void loadUsers(String path) throws Exception {
        for (String line: Files.readAllLines(Path.of(path))) {
            if (line.isBlank()) continue;
            String[] parts = line.split(",");
            String n=parts[0], nm=parts[1], pw=parts[4], role=parts[5];
            int age=Integer.parseInt(parts[2]); String ms=parts[3];
            User u;
            switch(role) {
                case "Applicant":
                    u = new Applicant(n, nm, age, ms, pw);
                    break;

                // handle both labels for officer
                case "Officer":
                case "HDBOfficer":
                    u = new HdbOfficer(n, nm, age, ms, pw);
                    break;

                // handle both labels for manager
                case "Manager":
                case "HDBManager":
                    u = new HdbManager(n, nm, age, ms, pw);
                    break;

                default:
                    continue;   // skip unknown roles
            }
            users.put(n, u);
        }
    }
    /**
     * If raw is already an NRIC in users, return it.
     * Otherwise scan users by name and return matching NRIC.
     */
    private static String resolveNameOrNric(String raw) {
        raw = raw.trim();
        if (users.containsKey(raw)) {
            return raw;
        }
        for (User u : users.values()) {
            if (u.getName().equals(raw)) {
                return u.getNric();
            }
        }
        return raw;  // fallback
    }

    /**
     * Given an Officers: line like "Daniel,Emily", resolve each to an NRIC.
     */
    private static List<String> resolveOfficerNames(String ofs) {
        List<String> officerNrics = new ArrayList<>();
        for (String raw : ofs.split(",")) {
            raw = raw.trim();
            if (users.containsKey(raw)) {
                officerNrics.add(raw);
            } else {
                for (User u : users.values()) {
                    if (u.getName().equals(raw)) {
                        officerNrics.add(u.getNric());
                        break;
                    }
                }
            }
        }
        return officerNrics;
    }

    public static void loadProjects(String path) throws Exception {
        List<String> lines = Files.readAllLines(Path.of(path));
        String name=null, nb=null, flats=null, od=null, cd=null, mgr=null, ofs=null;
        for (String l: lines) {
            l=l.trim(); if (l.isEmpty()) continue;
            if (l.startsWith("Project Name:")) name=l.split(":",2)[1].trim();
            else if (l.startsWith("Neighborhood:")) nb=l.split(":",2)[1].trim();
            else if (l.startsWith("Flats:")) flats=l.split(":",2)[1].trim();
            else if (l.startsWith("Open Date:")) od=l.split(":",2)[1].trim();
            else if (l.startsWith("Close Date:")) cd=l.split(":",2)[1].trim();
            else if (l.startsWith("Manager:")) mgr=l.split(":",2)[1].trim();
            else if (l.startsWith("Officers:")) {
                ofs = l.split(":",2)[1].trim();

                // — all seven fields are now filled, so parse right here: —
                String[] parts = flats.split(";");
                int two = 0, three = 0;
                for (String p : parts) {
                    String[] kv = p.split(":");
                    if (kv[0].equals("TWO_ROOM"))   two   = Integer.parseInt(kv[1]);
                    if (kv[0].equals("THREE_ROOM")) three = Integer.parseInt(kv[1]);
                }
                LocalDate o = LocalDate.parse(od, df);
                LocalDate c = LocalDate.parse(cd, df);

                // resolve manager → NRIC (same as you have)
                String managerNric = resolveNameOrNric(mgr);

                // resolve officers → NRIC list
                List<String> officerNrics = resolveOfficerNames(ofs);

                // create & store the project
                Project pr = new Project(name, nb, two, three, o, c, managerNric, officerNrics);
                projects.put(name, pr);

                // seed registrations
                for (String offNric : officerNrics) {
                    User u = users.get(offNric);
                    if (u instanceof HdbOfficer) {
                        OfficerRegistration reg = new OfficerRegistration(offNric, name);
                        reg.setStatus(ApplicationStatus.SUCCESSFUL);
                        registrations.add(reg);
                        ((HdbOfficer)u).setRegistration(reg);
                    }
                }

                // reset for the next block
                name = nb = flats = od = cd = mgr = ofs = null;
            }
            else if (name!=null && ofs!=null) {
                String[] parts=flats.split(";");
                int two=0, three=0;
                for(String p: parts) {String[] kv=p.split(":"); if(kv[0].equals("TWO_ROOM")) two=Integer.parseInt(kv[1]);
                    if(kv[0].equals("THREE_ROOM")) three=Integer.parseInt(kv[1]);}
                LocalDate o=LocalDate.parse(od,df), c=LocalDate.parse(cd,df);
                String managerNric = mgr;
                if (!users.containsKey(managerNric)) {
                    for (User u : users.values()) {
                        if (u.getName().equals(managerNric)) {
                            managerNric = u.getNric();
                            break;
                        }
                    }
                }

                // 2. Resolve officers from name→NRIC
                List<String> officerNrics = new ArrayList<>();
                for (String raw : ofs.split(",")) {
                    raw = raw.trim();
                    if (users.containsKey(raw)) {
                        officerNrics.add(raw);
                    } else {
                        for (User u : users.values()) {
                            if (u.getName().equals(raw)) {
                                officerNrics.add(u.getNric());
                                break;
                            }
                        }
                    }
                }

                // 3. Create the project
                Project pr = new Project(name, nb, two, three, o, c, managerNric, officerNrics);
                projects.put(name, pr);

                for (String offNric : officerNrics) {
                    User u = users.get(offNric);
                    if (u instanceof HdbOfficer) {
                        OfficerRegistration reg = new OfficerRegistration(offNric, name);
                        reg.setStatus(ApplicationStatus.SUCCESSFUL);
                        registrations.add(reg);
                        // link it back to the officer object
                        ((HdbOfficer)u).setRegistration(reg);
                    }
                }


                name=nb=flats=od=cd=mgr=ofs=null;
            }
        }
    }
    public static void saveUsers(String path) throws Exception {
        List<String> out=new ArrayList<>();
        for (User u: users.values()) {
            out.add(String.join(",",u.nric,u.name,String.valueOf(u.age),u.maritalStatus,u.password,u.getRole()));
        }
        Files.write(Path.of(path), out);
    }
    // Persistence for others omitted for brevity
}
