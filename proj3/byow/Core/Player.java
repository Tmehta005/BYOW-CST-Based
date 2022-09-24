package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;

public class Player implements Serializable {

    private final TETile AVATAR = Tileset.AVATAR;
    private final TETile[] PASSABLE_TILES = new TETile[]{Tileset.FLOOR,
            Tileset.GRASS, Tileset.FLOWER, Tileset.SAND,
            Tileset.UNLOCKED_DOOR, Tileset.NOTHING,Tileset.TREE,Tileset.LOCKED_DOOR};
    private TETile[][] world;
    public TETile currentTileOn;
    public int health;
    private int xPos;
    private int yPos;
    public Player(TETile[][] worldGrid, int xOrigin, int yOrigin, int health) {
        world = worldGrid;
        currentTileOn = worldGrid[xOrigin][yOrigin];
        xPos = xOrigin;
        yPos = yOrigin;
        this.health = health;
    }

    public TETile[][] getWorld() {
        return world;
    }

    public void movePlayer(char key) {
        switch(key) {
            case 'w', 'W':
                if (yPos + 1 < world[0].length
                        && isPassable(world[xPos][yPos + 1])) {
                    movePlayer(0,1);
                }
                break;
            case 'a', 'A':
                if (xPos - 1 >= 0 && isPassable(world[xPos - 1][yPos])) {
                    movePlayer(-1,0);
                }
                break;
            case 's', 'S':
                if (yPos - 1 >= 0 && isPassable(world[xPos][yPos - 1])) {
                    movePlayer(0,-1);
                }
                break;
            case 'd', 'D':
                if (xPos + 1 < world.length
                        && isPassable(world[xPos + 1][yPos])) {
                    movePlayer(1,0);
                }
                break;
        }
    }

    public void movePlayer(int deltaX, int deltaY) {
        world[xPos][yPos] = currentTileOn;
        xPos += deltaX;
        yPos += deltaY;
        currentTileOn = world[xPos][yPos];
        world[xPos][yPos] = AVATAR;
    }

    public boolean isPassable(TETile tile) {
        for (TETile pt : PASSABLE_TILES) {
            if (pt.equals(tile)) {
                return true;
            }
        }
        return false;
    }

    public void damagePlayer() {
        health--;
    }

    public void healPlayer() {
        health++;
    }

    public int getHealth() {
        return health;
    }
}
