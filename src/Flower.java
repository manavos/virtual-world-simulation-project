import processing.core.PImage;

import java.util.List;

public class Flower extends AnimationEntity implements Transformable{

    public static final String FLOWER_KEY = "flower";


    int health = 4;

    public Flower(String id, Point position, List<PImage> images, double behaviorPeriod, double animationPeriod) {
        super(id, position, images, behaviorPeriod, animationPeriod);
    }


    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        if (!transform(world, scheduler, imageLibrary)) {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
    }

    //immediately turning back to seed
    public boolean transform(World world, EventScheduler scheduler, ImageLibrary imageLibrary) {
        if (health <= 0) {
            Entity seed = new Seed(Seed.SEED_KEY + "_" + getId(), getPosition(), imageLibrary.get(Seed.SEED_KEY), Seed.SEED_BEHAVIOR_PERIOD, Seed.SEED_ANIMATION_PERIOD, 0);

            world.removeEntity(scheduler, this);

            world.addEntity(seed);

            return true;
        }

        return false;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

}
