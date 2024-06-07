import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Dude extends AnimationEntity implements Transformable, Moveable{

    public static final String DUDE_KEY = "dude";
    public static final int DUDE_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX = 0;
    public static final int DUDE_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX = 1;
    public static final int DUDE_PARSE_PROPERTY_RESOURCE_LIMIT_INDEX = 2;
    public static final int DUDE_PARSE_PROPERTY_COUNT = 3;

    /** Number of resources collected by the entity. */
    private int resourceCount;

    /** Total number of resources the entity may hold. */
    private int resourceLimit;

    //switched behavior and animation order
    public Dude(String id, Point position, List<PImage> images, double behaviorPeriod, double animationPeriod, int resourceCount, int resourceLimit) {
        super(id, position, images,  behaviorPeriod, animationPeriod);
        this.resourceCount = resourceCount;
        this.resourceLimit = resourceLimit;
    }

    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        Background bg = world.getBackgroundCell(getPosition());
        if (bg.getId().equals("slime")) {
            AnimationEntity slimedude = new slimeDude("slimedude", getPosition(), imageLibrary.get(slimeDude.SLIME_DUDE_KEY), getBehaviorPeriod(), getAnimationPeriod(), false, false );

            world.removeEntity(scheduler, this);

            world.addEntity(slimedude);
            slimedude.scheduleActions(scheduler, world, imageLibrary);

            return;

        }

        Optional<Entity> dudeTarget = findDudeTarget(world);

        if (dudeTarget.isEmpty() || !moveTo(world, dudeTarget.get(), scheduler) || !transform(world, scheduler, imageLibrary))  {
            scheduleBehavior(scheduler, world, imageLibrary);
        }

    }




    /** Returns the (optional) entity a Dude will path toward. */
    public Optional<Entity> findDudeTarget(World world) {
        List<Class<?>> potentialTargets;

        if (resourceCount == resourceLimit) {
            potentialTargets = List.of(House.class);
        } else {
            potentialTargets = List.of(Tree.class, Sapling.class);
        }

        return world.findNearest(getPosition(), potentialTargets);
    }

    /** Attempts to move the Dude toward a target, returning True if already adjacent to it. */
    public boolean moveTo(World world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacentTo(target.getPosition())) {
            if (target instanceof Tree || target instanceof Sapling) {

                if (target instanceof Tree) {
                    Tree tree = (Tree) target;
                    tree.setHealth(tree.getHealth() - 1);
                }

                if (target instanceof Sapling) {
                    Sapling sapling = (Sapling) target;
                    sapling.setHealth(sapling.getHealth() - 1);

                }

            }
            return true;


        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }

            return false;
        }
    }

    /** Determines a Dude's next position when moving. */
    public Point nextPosition(World world, Point destination) {
        // Differences between the destination and current position along each axis
        PathingStrategy pathingStrategy = new AStarPathingStrategy();
        Predicate<Point> canPassThrough = point -> world.inBounds(point) && (!(world.isOccupied(point)) || (world.isOccupied(point) && world.getOccupant(point).get() instanceof Stump)); //in bounds and if not occupied or if occupied has to be a stump
        BiPredicate<Point, Point> withinReach = (a, b) -> ((a.x == b.x) && Math.abs(b.y - a.y) == 1) || ((a.y == b.y) && Math.abs(b.x - a.x) == 1);
        Function<Point, Stream<Point>> potentialNeighbors =  PathingStrategy.CARDINAL_NEIGHBORS;

        List<Point> path = pathingStrategy.computePath(getPosition(), destination, canPassThrough, withinReach, potentialNeighbors);

        if (path.isEmpty()) {
            // Logic if there is no path or at the destination
            return getPosition(); //returns current position

        } else {
            // Logic if there is a path
            return path.get(0); //returns only point in list

        }

    }

    /** Changes the Dude's graphics. */
    public boolean transform(World world, EventScheduler scheduler, ImageLibrary imageLibrary) {


        //else if ((world.isOccupied(getPosition()) && world.getOccupant(getPosition()).get() instanceof Slime)){ //if touches slime


        if (resourceCount < resourceLimit) {
            resourceCount += 1;
            if (resourceCount == resourceLimit) { //not changing to slimedude if transformed
                AnimationEntity dude = new Dude(getId(), getPosition(), imageLibrary.get(DUDE_KEY + "_carry"), getBehaviorPeriod(), getAnimationPeriod(), resourceCount, resourceLimit);

                world.removeEntity(scheduler, this);

                world.addEntity(dude);
                dude.scheduleActions(scheduler, world, imageLibrary);

                return true;
            }
        }

        else {

            Dude dude = new Dude(getId(), getPosition(), imageLibrary.get(DUDE_KEY), getBehaviorPeriod(), getAnimationPeriod(), 0, resourceLimit);

            world.removeEntity(scheduler, this);

            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageLibrary);

            return true;




        }



        return false;
    }



} //class
