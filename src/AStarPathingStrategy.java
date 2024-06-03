import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy {

    /**
     * Return a list containing a single point representing the next step toward a goal
     * If the start is within reach of the goal, the returned list is empty.
     *
     * @param start the point to begin the search from
     * @param end the point to search for a point within reach of
     * @param canPassThrough a function that returns true if the given point is traversable
     * @param withinReach a function that returns true if both points are within reach of each other
     * @param potentialNeighbors a function that returns the neighbors of a given point, as a stream
     */
    public List<Point> computePath(
            Point start,
            Point end,
            Predicate<Point> canPassThrough,
            BiPredicate<Point, Point> withinReach,
            Function<Point, Stream<Point>> potentialNeighbors
    ) {
        //hashcode for points in hashmaps
        List<Point> openSet = new ArrayList<>();
        List<Point> closedSet = new ArrayList<>();
        List<Point> path = new ArrayList<>();
        HashMap<Point, Point> previousMapping = new HashMap<>();
        HashMap<Point, Integer> gScore = new HashMap<>();
        HashMap<Point, Integer> hScore = new HashMap<>();
        HashMap<Point, Integer> fScore = new HashMap<>();


        previousMapping.put(start, null);
        gScore.put(start, 0);
        hScore.put(start, start.manhattanDistanceTo(end));
        fScore.put(start, gScore.get(start) + hScore.get(start));

        Predicate<Point> notinClosedSet = point -> !(closedSet.contains(point));
        openSet.add(start);
        Point current;

        while (!openSet.isEmpty()) {
            current = openSet.get(0);
            closedSet.add(current);
            openSet.remove(0);

            if (withinReach.test(current, end)){
                path.add(current);
                while (previousMapping.get(current) != start){ //while point is not starting point
                    path.add(0, previousMapping.get(current));
                    current = previousMapping.get(current);

                }

                return path;
            }

            List<Point> neighborslist = potentialNeighbors.apply(current)
                    .filter(notinClosedSet)
                    .filter(canPassThrough)
                    .toList();


            for (Point neighbor : neighborslist){
                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                    gScore.put(neighbor, gScore.get(current) + 1);
                    hScore.put(neighbor, neighbor.manhattanDistanceTo(end));
                    fScore.put(neighbor, gScore.get(neighbor) + hScore.get(neighbor));
                    previousMapping.put(neighbor, current);

                }


                else{ //if already in openSet
                    int gScore1 = gScore.get(current) + 1; //getScore of current point

                    if (gScore.get(neighbor) > gScore1){ //compare old vs new
                        gScore.put(neighbor, gScore1);
                        hScore.put(neighbor, neighbor.manhattanDistanceTo(end));
                        fScore.put(neighbor, gScore.get(neighbor) + hScore.get(neighbor));
                        previousMapping.put(neighbor, current);

                    }


                }

            }

            openSet.sort(Comparator.comparing(fScore::get));
        }

        return List.of(); //no path
    }
}
