import java.time.LocalDate;
import java.util.*;
public class Project {
    private String name;
    private String neighborhood;
    private int totalTwo;
    private int totalThree;
    private int remTwo;
    private int remThree;
    private LocalDate openDate;
    private LocalDate closeDate;
    private String managerNric;
    private List<String> officerNrics;
    private boolean visible = false;

    public Project(String name, String neighborhood, int two, int three,
                   LocalDate openDate, LocalDate closeDate,
                   String managerNric, List<String> officers) {
        this.name = name;
        this.neighborhood = neighborhood;
        this.totalTwo = two; this.remTwo = two;
        this.totalThree = three; this.remThree = three;
        this.openDate = openDate; this.closeDate = closeDate;
        this.managerNric = managerNric;
        this.officerNrics = new ArrayList<>(officers);
    }
    public String getName() { return name; }
    public String getNeighborhood() { return neighborhood; }
    public boolean isOpen(LocalDate today) {
        return !today.isBefore(openDate) && !today.isAfter(closeDate);
    }
    public boolean isVisible(LocalDate today) {
        return visible && isOpen(today);
    }
    public void toggleVisible() { visible = !visible; }
    public int getRemTwo() { return remTwo; }
    public int getRemThree() { return remThree; }
    public boolean allocateFlat(FlatType ft) {
        if (ft == FlatType.TWO_ROOM && remTwo>0) { remTwo--; return true;}
        if (ft == FlatType.THREE_ROOM && remThree>0) { remThree--; return true;}
        return false;
    }
    public void restoreFlat(FlatType ft) {
        if (ft == FlatType.TWO_ROOM)   remTwo++;
        else                            remThree++;
    }
    public String getManagerNric() { return managerNric; }
    public List<String> getOfficerNrics() { return officerNrics; }
    public void addOfficer(String nric) { officerNrics.add(nric); }

    public void setRemTwo(int remTwo) {
        this.remTwo = remTwo;
    }

    public void setRemThree(int remThree) {
        this.remThree = remThree;
    }

    /** Expose the opening date. */
    public LocalDate getOpenDate() {
        return openDate;
    }

    /** Expose the closing date. */
    public LocalDate getCloseDate() {
        return closeDate;
    }
}
