/** A scheduled action to be carried out by a specific entity. */
public abstract class Action {

    private final Entity entity; //is entity an instance variable


    public Action(Entity entity) {
        this.entity = entity;
    }


    public abstract void execute(EventScheduler scheduler);

    public Entity getEntity() {
        return entity;
    }


}


