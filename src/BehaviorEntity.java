import processing.core.PImage;

import java.util.List;

public abstract class BehaviorEntity extends Entity{

    private double behaviorPeriod;

    public BehaviorEntity(String id, Point position, List<PImage> images, double behaviorPeriod) {
        super(id, position, images);
        this.behaviorPeriod =  behaviorPeriod;

    }

    public abstract void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler);

    public abstract void scheduleActions(EventScheduler scheduler, World world, ImageLibrary imageLibrary);

    public void scheduleBehavior(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduler.scheduleEvent(this, new Behavior(this, world, imageLibrary), behaviorPeriod);
    }

    public double getBehaviorPeriod() {
        return behaviorPeriod;
    }











}

