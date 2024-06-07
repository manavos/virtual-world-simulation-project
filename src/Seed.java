import processing.core.PImage;

import java.util.List;

public class Seed extends AnimationEntity implements Transformable{
    public static final String SEED_KEY = "seed";
    public static final int SEED_PARSE_PROPERTY_COUNT = 0;
    public static final int SEED_HEALTH_LIMIT = 4;
    public static final double SEED_ANIMATION_PERIOD = 0.0125; // Very small to react to health changes
    public static final double SEED_BEHAVIOR_PERIOD = 2.0;
    private int health;

    public Seed(String id, Point position, List<PImage> images, double behaviorPeriod, double animationPeriod, int health) {
        super(id, position, images, SEED_BEHAVIOR_PERIOD, SEED_ANIMATION_PERIOD);
        this.health = health;
    }

    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        health = health + 1;
        if (!transform(world, scheduler, imageLibrary)) {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
    }

    public boolean transform(World world, EventScheduler scheduler, ImageLibrary imageLibrary) {
       if (health >= SEED_HEALTH_LIMIT) {
            AnimationEntity flower = new Flower(
                    Flower.FLOWER_KEY + "_" + getId(),
                    getPosition(),
                    imageLibrary.get(Flower.FLOWER_KEY),
                    0.313, 0.625);

            world.removeEntity(scheduler, this);

            world.addEntity(flower);
            flower.scheduleActions(scheduler, world, imageLibrary);

            return true;
        }

        return false;
    }

    public void updateImage(){
         if (health < SEED_HEALTH_LIMIT) {
            setImageIndex(getImages().size() * health/SEED_HEALTH_LIMIT);
        } else {
            setImageIndex(getImages().size() - 1);
        }

    }


    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }


}

