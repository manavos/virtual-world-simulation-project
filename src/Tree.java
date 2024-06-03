import processing.core.PImage;

import java.util.List;

public class Tree extends AnimationEntity implements Transformable{

    public static final String TREE_KEY = "tree";
    public static final int TREE_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX = 0;
    public static final int TREE_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX = 1;
    public static final int TREE_PARSE_PROPERTY_HEALTH_INDEX = 2;
    public static final int TREE_PARSE_PROPERTY_COUNT = 3;
    public static final double TREE_RANDOM_ANIMATION_PERIOD_MIN = 0.1;
    public static final double TREE_RANDOM_ANIMATION_PERIOD_MAX = 1.0;
    public static final double TREE_RANDOM_BEHAVIOR_PERIOD_MIN = 0.01;
    public static final double TREE_RANDOM_BEHAVIOR_PERIOD_MAX = 0.10;
    public static final int TREE_RANDOM_HEALTH_MIN = 1;
    public static final int TREE_RANDOM_HEALTH_MAX = 3;

    private int health;

    public Tree(String id, Point position, List<PImage> images,  double behaviorPeriod, double animationPeriod, int health) {
        super(id, position, images, behaviorPeriod, animationPeriod);
        this.health = health;
    }

    /** Executes Tree specific Logic. */
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        if (!transform(world, scheduler, imageLibrary)) {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
    }

    /** Checks the Tree's health and transforms accordingly, returning true if successful. */
    public boolean transform(World world, EventScheduler scheduler, ImageLibrary imageLibrary) {
        if (health <= 0) {
            Entity stump = new Stump(Stump.STUMP_KEY + "_" + getId(), getPosition(), imageLibrary.get(Stump.STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

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
