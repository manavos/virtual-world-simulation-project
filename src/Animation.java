public class Animation extends Action {

    private int repeatCount;


    public Animation(Entity entity, int repeatCount) {
        super(entity);
        this.repeatCount = repeatCount;
    }


    public void execute(EventScheduler scheduler) {
        AnimationEntity entity = (AnimationEntity) getEntity();
        entity.updateImage();

        if (repeatCount != 1) {
            scheduler.scheduleEvent(getEntity(), new Animation(getEntity(), Math.max(this.repeatCount - 1, 0)), entity.getAnimationPeriod());
        }
    }






}
