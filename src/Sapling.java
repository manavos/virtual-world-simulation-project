import processing.core.PImage;

import java.util.List;

public class Sapling extends AnimationEntity implements Transformable{
    public static final String SAPLING_KEY = "sapling";
    public static final int SAPLING_PARSE_PROPERTY_COUNT = 0;
    public static final int SAPLING_HEALTH_LIMIT = 5;
    public static final double SAPLING_ANIMATION_PERIOD = 0.0125; // Very small to react to health changes
    public static final double SAPLING_BEHAVIOR_PERIOD = 2.0;
    private int health;

    public Sapling(String id, Point position, List<PImage> images, double behaviorPeriod, double animationPeriod, int health) {
        super(id, position, images, SAPLING_BEHAVIOR_PERIOD, SAPLING_ANIMATION_PERIOD);
        this.health = health;
    }

    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        health = health + 1;
        if (!transform(world, scheduler, imageLibrary)) {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
    }

    public boolean transform(World world, EventScheduler scheduler, ImageLibrary imageLibrary) {
        if (health <= 0) {
            Entity stump = new Stump(Stump.STUMP_KEY + "_" + getId(), getPosition(), imageLibrary.get(Stump.STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        } else if (health >= SAPLING_HEALTH_LIMIT) {
            AnimationEntity tree = new Tree(
                    Tree.TREE_KEY + "_" + getId(),
                    getPosition(),
                    imageLibrary.get(Tree.TREE_KEY),
                    NumberUtil.getRandomDouble(Tree.TREE_RANDOM_BEHAVIOR_PERIOD_MIN, Tree.TREE_RANDOM_BEHAVIOR_PERIOD_MAX), NumberUtil.getRandomDouble(Tree.TREE_RANDOM_ANIMATION_PERIOD_MIN, Tree.TREE_RANDOM_ANIMATION_PERIOD_MAX),
                    NumberUtil.getRandomInt(Tree.TREE_RANDOM_HEALTH_MIN, Tree.TREE_RANDOM_HEALTH_MAX)
            );

            world.removeEntity(scheduler, this);

            world.addEntity(tree);
            tree.scheduleActions(scheduler, world, imageLibrary);

            return true;
        }

        return false;
    }

    public void updateImage(){
        if (health <= 0) {
            setImageIndex(0);
        } else if (health < SAPLING_HEALTH_LIMIT) {
            setImageIndex(getImages().size() * health/SAPLING_HEALTH_LIMIT);
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
