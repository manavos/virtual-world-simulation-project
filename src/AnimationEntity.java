import processing.core.PImage;

import java.util.List;

public abstract class AnimationEntity extends BehaviorEntity{

    private double animationPeriod;

    public AnimationEntity(String id, Point position, List<PImage> images, double behaviorPeriod, double animationPeriod) {

        super(id, position, images,  behaviorPeriod);
        this.animationPeriod = animationPeriod;

    }

    public double getAnimationPeriod() {
        return animationPeriod;
    }


    public void scheduleAnimation(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduler.scheduleEvent(this, new Animation(this, 0), animationPeriod);
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduleAnimation(scheduler, world, imageLibrary);
        scheduleBehavior(scheduler, world, imageLibrary);
    }

    public void updateImage(){
        setImageIndex(getImageIndex() + 1);
    }




}
