import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Bee extends AnimationEntity implements Moveable{

    public static final String BEE_KEY = "bee";


    public Bee(String id, Point position, List<PImage> images, double behaviorPeriod, double animationPeriod) {
        super(id, position, images, behaviorPeriod, animationPeriod);
    }

    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        Optional<Entity> beeTarget = world.findNearest(getPosition(), new ArrayList<>(List.of(Tree.class)));
        //find next target

        if (beeTarget.isPresent()) {
            Point tgtPos = beeTarget.get().getPosition();

            if (moveTo(world, beeTarget.get(), scheduler)) {
                AnimationEntity seed = new Seed(Seed.SEED_KEY + "_" + beeTarget.get().getId(), tgtPos, imageLibrary.get(Seed.SEED_KEY), Seed.SEED_BEHAVIOR_PERIOD, Seed.SEED_ANIMATION_PERIOD, 0);

                world.addEntity(seed);
                seed.scheduleActions(scheduler, world, imageLibrary);
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
