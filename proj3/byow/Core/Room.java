package byow.Core;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.Random;

/** A room is an open space surrounded by walls
 * that a player can walk around in. */
public class Room implements Serializable {

    /** The TETile grid representing the world
     * the room is being constructed in. */
    private TETile[][] world;

    /** The leftmost column of a room. */
    public int xOrigin;

    /** The bottom row of a room. */
    public int yOrigin;

    /** The horizontal length of a room. */
    public int width;

    /** The vertical length of a room. */
    public int height;

    private final int minDifference = 5;

    private final int minLength = 5;

    /** Creates an empty room with the origin tile set at (X, Y),
     * with horizontal length WIDTH and vertical length HEIGHT. */
    public Room(TETile[][] world, int x, int y, int width, int height) {
        this.world = world;
        this.xOrigin = x;
        this.yOrigin = y;
        this.width = Math.min(width, world.length - xOrigin - 1);
        this.height = Math.min(height, world[0].length - yOrigin - 1);

        boolean minOriginRequirement = xOrigin < world.length - minDifference
                && yOrigin < world[0].length - minDifference;
        boolean minLengthRequirement = width >= minLength
                && height >= minLength;

        if (minOriginRequirement && minLengthRequirement) {
            constructRoom();
        }
    }

    public void constructRoom() {
        boolean noOverlap = true;
        for (int i = xOrigin; i <= width + xOrigin; i++) {
            for (int j = yOrigin; j <= height + yOrigin; j++) {
                if (world[i][j].character() != ' ') {
                    noOverlap = false;
                }
            }
        }
        if (noOverlap) {
            TETile coloredWalls = TETile.colorVariant(Tileset.WALL, 100, 100, 100, new Random());
            buildLoop(coloredWalls, xOrigin, yOrigin, 1, height);
            buildLoop(coloredWalls, xOrigin, yOrigin, width, 1);
            buildLoop(coloredWalls, xOrigin + width - 1, yOrigin, 1, height);
            buildLoop(coloredWalls, xOrigin, yOrigin + height - 1, width, 1);

            buildLoop(Tileset.FLOOR, xOrigin + 1, yOrigin + 1,
                    width - 2, height - 2);
        }
    }

    public void buildLoop(TETile tile, int xStart,
                          int yStart, int assignedWidth, int assignedHeight) {
        for (int i = xStart; i < assignedWidth + xStart; i++) {
            for (int j = yStart; j < assignedHeight + yStart; j++) {
                world[i][j] = tile;
            }
        }
    }



}
