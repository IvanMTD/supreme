package lab.fcpsr.suprime.templates;

public enum Status {
    FEDERAL("Федеральный"),
    SPORT("Спартакиада"),
    UNIVERSE("Универсиада"),
    CHAMP("Чемпионат России");

    private final String title;

    Status(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }
}
