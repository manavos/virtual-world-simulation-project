import processing.core.PImage;

import java.util.List;

public class Flower extends AnimationEntity{

    public static final String FLOWER_KEY = "flower";
    public static final int FLOWER_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX = 0;
    public static final int FLOWER_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX = 1;
    public static final int FLOWER_PARSE_PROPERTY_COUNT = 2; //??????????

    //int health; turn into honey?????

    public Flower(String id, Point position, List<PImage> images, double behaviorPeriod, double animationPeriod) {
        super(id, position, images, behaviorPeriod, animationPeriod);
    }

}
