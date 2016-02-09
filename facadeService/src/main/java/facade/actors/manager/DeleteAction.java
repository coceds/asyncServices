package facade.actors.manager;

public class DeleteAction implements Action {

    private final String uuid;

    public DeleteAction(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
