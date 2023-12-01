package lab.fcpsr.suprime.models;

public enum Role {
    ADMIN("Администратор"),
    MODERATOR("Модератор"),
    PUBLISHER("Публицист"),
    READER("Читатель");

    private String displayName;

    Role(String displayName){
        this.displayName = displayName;
    }
}
