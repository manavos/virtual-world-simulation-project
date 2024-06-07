import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Frog extends AnimationEntity implements Moveable{


    public static final String FROG_KEY = "frog";


    int count = 0;
    public Frog(String id, Point position, List<PImage> images, double behaviorPeriod, double animationPeriod) {
        super(id, position, images, behaviorPeriod, animationPeriod);

    }


    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {

        Background bg = world.getBackgroundCell(getPosition());

        if (bg.getId().equals("water_tile") || bg.getId().equals("water") || bg.getId().equals("water_edge") ){
                    //don't do anything
        }

        else{
            Background background = new Background("slime", imageLibrary.get("slime"), 0);

            world.setBackgroundCell(getPosition(), background);


        }




        Optional<Entity> frogTarget = world.findNearest(getPosition(), new ArrayList<>(List.of(Fairy.class)));

        if (frogTarget.isPresent()) {

            Point tgtPos = frogTarget.get().getPosition();

            if (moveTo(world, frogTarget.get(), scheduler)) {
                AnimationEntity bee = new Bee(Bee.BEE_KEY + "_" + frogTarget.get().getId(), tgtPos, imageLibrary.get(Bee.BEE_KEY), 2.0, 1.0);

                world.addEntity(bee);
                bee.scheduleActions(scheduler, world, imageLibrary);

            }


//add water after going on water
//move to or execute behavior check if position is different than before and change background
            //if (!(world.getOccupant(getPosition()).get() instanceof Water)) {

        /*
            if (wasOnWater && !getPosition().equals(PREV_POINT) ){
                Entity water = new Water(Water.WATER_KEY, PREV_POINT, imageLibrary.get(Water.WATER_KEY));
                if (world.isOccupied(PREV_POINT) && !(world.getOccupant(PREV_POINT).get() instanceof Frog)){
                    world.removeEntity(scheduler, world.getOccupant(PREV_POINT).get());
                    if (!world.isOccupied(getPosition()) && !(world.getOccupant(getPosition()).get() instanceof Frog)) {
                        world.addEntity(this);
                    }

                }

                world.addEntity(water);

            }
            */




        }



        scheduleBehavior(scheduler, world, imageLibrary);


    }

//if touch water reset breath if touch bee then transform
    public boolean moveTo(World world, Entity target, EventScheduler scheduler) {

        if (getPosition().adjacentTo(target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;

        //} else if (breath >= 1){
            //return false;

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
        Predicate<Point> canPassThrough = point -> world.inBounds(point) && (!(world.isOccupied(point)) || (world.isOccupied(point) && world.getOccupant(point).get() instanceof Water)); //in bounds and if not occupied or if occupied has to be water
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








}
