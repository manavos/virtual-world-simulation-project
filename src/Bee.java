import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Bee extends AnimationEntity{

    public static final String BEE_KEY = "bee";
    public static final int BEE_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX = 0;
    public static final int BEE_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX = 1;
    public static final int BEE_PARSE_PROPERTY_COUNT = 2; //??????????


    public Bee(String id, Point position, List<PImage> images, double behaviorPeriod, double animationPeriod) {
        super(id, position, images, behaviorPeriod, animationPeriod);
    }

    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(getPosition(), new ArrayList<>(List.of(Stump.class)));
        //find next target

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (moveTo(world, fairyTarget.get(), scheduler)) {
                AnimationEntity sapling = new Flower(Flower.FLOWER_KEY + "_" + fairyTarget.get().getId(), tgtPos, imageLibrary.get(Flower.FLOWER_KEY), Flower.FLOWER_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX, Flower.FLOWER_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX);

                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageLibrary);
                //put something like this in mouse pressed
            }
        }

        scheduleBehavior(scheduler, world, imageLibrary);
    }

    public boolean moveTo(World world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacentTo(target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition());
            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public Point nextPosition(World world, Point destination) {
        // Differences between the destination and current position along each axis
        PathingStrategy pathingStrategy = new AStarPathingStrategy();
        Predicate<Point> canPassThrough = point -> world.inBounds(point) && !(world.isOccupied(point)); //in bounds and if not occupied
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




} //class
