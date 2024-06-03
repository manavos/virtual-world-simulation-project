public interface Moveable {

    public boolean moveTo(World world, Entity target, EventScheduler scheduler);

    public Point nextPosition(World world, Point destination);

}
