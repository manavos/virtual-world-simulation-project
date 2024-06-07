import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class slimeDude extends AnimationEntity implements Transformable, Moveable{
    public static final String SLIME_DUDE_KEY = "slimedude";

    private boolean cured;

    private boolean hasCure;

    public slimeDude(String id, Point position, List<PImage> images, double behaviorPeriod, double animationPeriod, boolean hasCure, boolean cured) {
        super(id, position, images,  behaviorPeriod, animationPeriod);
        this.hasCure = hasCure;
        this.cured = cured;
    }


    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        Optional<Entity> dudeTarget = findslimeDudeTarget(world);
        if (dudeTarget.isEmpty() || !moveTo(world, dudeTarget.get(), scheduler) || !transform(world, scheduler, imageLibrary)) {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
    }

    /** Returns the (optional) entity a Dude will path toward. */
    public Optional<Entity> findslimeDudeTarget(World world) {
        List<Class<?>> potentialTargets;

        if (hasCure) {
            potentialTargets = List.of(House.class);
        } else {
            potentialTargets = List.of(Flower.class);
        }

        return world.findNearest(getPosition(), potentialTargets);
    }

    /** Attempts to move the Dude toward a target, returning True if already adjacent to it. */
    public boolean moveTo(World world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacentTo(target.getPosition())) {
            if (target instanceof Flower || target instanceof House) {

                if (target instanceof Flower) {
                    Flower flower = (Flower) target;
                    flower.setHealth(flower.getHealth() - 1);
                    if (flower.getHealth() == 0){
                        hasCure = true;
                    }
                }

                if (target instanceof House) {
                    cured = true;

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

    public Point nextPosition(World world, Point destination) {
        // Differences between the destination and current position along each axis
        PathingStrategy pathingStrategy = new AStarPathingStrategy();
        Predicate<Point> canPassThrough = point -> world.inBounds(point) && !(world.isOccupied(point)); //in bounds and if not occupied or if occupied has to be a stump
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


    public boolean transform(World world, EventScheduler scheduler, ImageLibrary imageLibrary) {
        if (cured) {

            AnimationEntity dude = new Dude("dude", getPosition(), imageLibrary.get(Dude.DUDE_KEY), getBehaviorPeriod(), getAnimationPeriod(), 0, 3);

            world.removeEntity(scheduler, this);

            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageLibrary);

            return true;
            }



        return false;
    }



}
