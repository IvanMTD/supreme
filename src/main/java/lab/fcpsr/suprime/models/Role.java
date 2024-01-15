package lab.fcpsr.suprime.models;

public enum Role {
    ADMIN("Администратор"),
    MAIN_MODERATOR("Главный Редактор"),
    MODERATOR("Редактор"),
    PUBLISHER("Публицист"),
    READER("Читатель");

    private String displayName;

    Role(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
