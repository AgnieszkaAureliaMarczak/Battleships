package battleships;

import java.util.Random;

public class ComputerAlgorithm {

    public static Random random = new Random();

    static int drawVerticalCoordinate() {
        return random.nextInt(10);
    }

    static int drawHorizontalCoordinate() {
        return random.nextInt(10);
    }
}
