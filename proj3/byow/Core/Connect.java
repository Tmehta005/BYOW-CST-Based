package byow.Core;

import byow.Core.TreeWorld;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;

public class Connect implements Serializable {
    public int centerX;
    public int centerY;
    static final int height = Engine.HEIGHT;
    static final int width = Engine.WIDTH;
    public Connect(TreeWorld.TreeZone zone){
        this.centerX = zone.getXOrigin() + zone.getWidth()/2;
        this.centerY = zone.getYOrigin() + zone.getHeight()/2;
    }

    int counter = 1;
    public void createConnections(TETile[][] world, TreeWorld.TreeZone zone){
        if (zone.left != null || zone.right != null) {

            Connect left = new Connect(zone.left);
            Connect right = new Connect(zone.right);

            int zone1X = right.centerX;
            int zone1Y = right.centerY;
            int zone2X = left.centerX;
            int zone2Y = left.centerY;

            if (zone1X == zone2X){
                connectVertical(zone1X, zone1Y, zone2Y, world);
            }
            if (zone1Y == zone2Y){
                connectHorizontal(zone1Y, zone1X, zone2X, world);
            }

            createConnections(world, zone.right);
            createConnections(world,zone.left);
        }
    }

    private void connectHorizontal(int zone1Y, int zone1X, int zone2X, TETile[][] world) {
        int upperBoundary = 0;
        int lowerBoundary = 0;

        if (zone1X > zone2X) {
            upperBoundary = zone2X;
            lowerBoundary = zone1X;
        } else {
            upperBoundary = zone1X;
            lowerBoundary = zone2X;
        }

        if (zone1Y == height - 1) {
            zone1Y -= 1;
        }
        if (zone1Y == 0) {
            zone1Y += 1;
        }

        for (int i = upperBoundary; i <= lowerBoundary; i++){
            boolean tileBeyondBoundary =
                    (i != upperBoundary && i != lowerBoundary)
                            || ((zone1Y - 1 >= 0 || zone1Y + 1 < world[0].length)
                            && ((i == upperBoundary && i - 1 >= 0
                            && (world[i - 1][zone1Y] != Tileset.NOTHING)
                            && (world[i - 1][zone1Y - 1] != Tileset.NOTHING
                            || world[i - 1][zone1Y + 1] != Tileset.NOTHING))
                            ||  (i == lowerBoundary && i + 1 <= world.length
                            && (world[i + 1][zone1Y] != Tileset.NOTHING)
                            && (world[i + 1][zone1Y - 1] != Tileset.NOTHING
                            || world[i + 1][zone1Y + 1] != Tileset.NOTHING))));

            boolean floorAboveWall = zone1Y + 2 < world[0].length
                    && world[i][zone1Y + 2].description().equals("floor");
            boolean floorMoreAboveWall = zone1Y + 3 < world[0].length
                    && world[i][zone1Y + 3].description().equals("floor");

            if (floorMoreAboveWall && tileBeyondBoundary) {
                world[i][zone1Y + 1] = Tileset.FLOOR;
                world[i][zone1Y + 2] = Tileset.FLOOR;
            } else if (floorAboveWall && tileBeyondBoundary) {
                world[i][zone1Y + 1] = Tileset.FLOOR;
            } else if (!world[i][zone1Y + 1].description().equals("floor")) {
                world[i][zone1Y + 1] = Tileset.WALL;
            }

            boolean floorBelowWall = zone1Y - 2 >= 0
                    && world[i][zone1Y - 2].description().equals("floor");
            boolean floorMoreBelowWall = zone1Y - 3 >= 0
                    && world[i][zone1Y - 3].description().equals("floor");

            if (floorMoreBelowWall && tileBeyondBoundary) {
                world[i][zone1Y - 1] = Tileset.FLOOR;
                world[i][zone1Y - 2] = Tileset.FLOOR;
            }  else if (floorBelowWall && tileBeyondBoundary) {
                world[i][zone1Y - 1] = Tileset.FLOOR;
            } else if (!world[i][zone1Y - 1].description().equals("floor")) {
                world[i][zone1Y - 1] = Tileset.WALL;
            }


            if (!world[i][zone1Y].description().equals("floor")) {
                boolean floorRightOfWall = (i + 1 < world.length)
                        && world[i + 1][zone1Y].description().equals("floor");
                boolean floorMoreRightOfWall = (i + 2 < world.length)
                        && world[i + 2][zone1Y].description().equals("floor");
                boolean floorLeftOfWall = (i - 1 >= 0)
                        && world[i - 1][zone1Y].description().equals("floor");
                boolean floorMoreLeftOfWall = (i - 2 >= 0)
                        && world[i - 2][zone1Y].description().equals("floor");

                if (i != upperBoundary && floorMoreRightOfWall) {
                    world[i][zone1Y] = Tileset.FLOOR;
                    world[i + 1][zone1Y] = Tileset.FLOOR;
                } else if (i != lowerBoundary && floorMoreLeftOfWall) {
                    world[i][zone1Y] = Tileset.FLOOR;
                    world[i - 1][zone1Y] = Tileset.FLOOR;
                } else if ((i == upperBoundary && !floorLeftOfWall)
                            || (i == lowerBoundary && !floorRightOfWall)) {
                    world[i][zone1Y] = Tileset.WALL;
                } else {
                    world[i][zone1Y] = Tileset.FLOOR;
                }
            }
        }

    }

    private void connectVertical(int zone1X, int zone1Y, int zone2Y, TETile[][] world) {
        int upperBoundary = 0;
        int lowerBoundary = 0;
        if (zone1Y > zone2Y) {
            upperBoundary = zone2Y;
            lowerBoundary = zone1Y;
        } else {
            upperBoundary = zone1Y;
            lowerBoundary = zone2Y;
        }

        if (zone1X == width - 1) {
            zone1X -= 1;
        }
        if (zone1X == 0) {
            zone1X += 1;
        }

        for (int i = upperBoundary; i <= lowerBoundary; i++) {
            boolean tileBeyondBoundary =
                    (i != upperBoundary && i != lowerBoundary)
                            || ((zone1X - 1 >= 0 || zone1X + 1 < world.length)
                            && ((i == upperBoundary && i - 1 >= 0
                            && (world[zone1X][i - 1] != Tileset.NOTHING)
                            && (world[zone1X - 1][i - 1] != Tileset.NOTHING
                            || world[zone1X + 1][i - 1] != Tileset.NOTHING))
                            || (i == lowerBoundary && i + 1 < world[0].length
                            && (world[zone1X][i + 1] != Tileset.NOTHING)
                            && (world[zone1X - 1][i + 1] != Tileset.NOTHING
                            || world[zone1X + 1][i + 1] != Tileset.NOTHING))));

            boolean floorRightOfWall = zone1X + 2 < world.length
                    && (world[zone1X + 2][i].character() == '·');
            boolean floorMoreRightOfWall = zone1X + 3 < world.length
                    && (world[zone1X + 3][i].character() == '·');

            if (floorMoreRightOfWall && tileBeyondBoundary) {
                world[zone1X + 1][i] = Tileset.FLOOR;
                world[zone1X + 2][i] = Tileset.FLOOR;
            } else if (floorRightOfWall && tileBeyondBoundary) {
                world[zone1X + 1][i] = Tileset.FLOOR;
            } else if (!world[zone1X + 1][i].description().equals("floor")) {
                world[zone1X + 1][i] = Tileset.WALL;
            }

            boolean floorLeftOfWall = zone1X - 2 >= 0
                    && (world[zone1X - 2][i].character() == '·');
            boolean floorMoreLeftOfWall = zone1X - 3 >= 0
                    && (world[zone1X - 3][i].character() == '·');

            if (floorMoreLeftOfWall && tileBeyondBoundary) {
                world[zone1X - 1][i] = Tileset.FLOOR;
                world[zone1X - 2][i] = Tileset.FLOOR;
            } else if (floorLeftOfWall && tileBeyondBoundary) {
                world[zone1X - 1][i] = Tileset.FLOOR;
            } else if (!world[zone1X - 1][i].description().equals("floor")) {
                world[zone1X - 1][i] = Tileset.WALL;
            }

            if (!world[zone1X][i].description().equals("floor")) {
                boolean floorAboveWall = i + 1 < world[0].length
                        && world[zone1X][i + 1].character() == '·';
                boolean floorMoreAboveWall = i + 2 < world[0].length
                        && world[zone1X][i + 2].character() == '·';
                boolean floorBelowWall = i - 1 >= 0
                        && world[zone1X][i - 1].character() == '·';
                boolean floorMoreBelowWall = i - 2 >= 0
                        && world[zone1X][i - 2].character() == '·';

                if (i != upperBoundary && floorMoreAboveWall) {
                    world[zone1X][i] = Tileset.FLOOR;
                    world[zone1X][i + 1] = Tileset.FLOOR;
                } else if (i != lowerBoundary && floorMoreBelowWall) {
                    world[zone1X][i] = Tileset.FLOOR;
                    world[zone1X][i - 1] = Tileset.FLOOR;
                } else if ((i == upperBoundary && !floorBelowWall)
                        || (i == lowerBoundary && !floorAboveWall)) {
                    world[zone1X][i] = Tileset.WALL;
                } else {
                    world[zone1X][i] = Tileset.FLOOR;
                }
            }
        }
    }
}
