package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreeWorld implements Serializable {




    public class TreeZone implements Serializable {
        /** The leftmost column of the treeZone. */
        private int xOrigin;
        /** The bottom row of the treeZone. */
        private int yOrigin;
        /** The horizontal length of a treeZone. */
        private int width;
        /** The vertical length of a treeZone. */
        private int height;

        public TreeZone left;

        public TreeZone right;

        private Room roomOrHallway;

        TreeZone(int x, int y, int width, int height) {
            this.xOrigin = x;
            this.yOrigin = y;
            this.width = width;
            this.height = height;
        }

        public int getXOrigin(){
            return this.xOrigin;
        }

        public int getYOrigin(){
            return this.yOrigin;
        }

        public int getWidth(){
            return this.width;
        }

        public int getHeight(){
            return this.height;
        }
    }
    private TreeZone root;
    public List<TreeZone> leafList;
    /** The number of leaf TreeZones that a world consists of */
    private int numLeafs;
    /** A specific instance of a world.
     * Note: Must be of type long, not integer. */
    private long seed;
    public TETile[][] world;
    private Random random;
    private int width;
    private int height;

    public TreeWorld(long seed, int width, int height) {
        this.width = width;
        this.height = height;
        root = new TreeZone(0, 0, this.width, this.height);
        world = new TETile[width][height];
        leafList = new ArrayList<>();
        leafList.add(this.root);
        this.seed = seed;
        random = new Random(this.seed);
        createBlankWorld();
    }


    public void createBlankWorld() {
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void split(int divisions, TreeZone zone) {
        if (divisions <= 0) {
            makeLeafRoom(zone);
            return;
        }

        int splitDecision = random.nextInt(2);
        double ratio = ((double) zone.width / zone.height);
        if (splitDecision == 0 && ratio < 1.5) {
            splitHorizontally(zone);
        } else {
            splitVertically(zone);
        }
        split(divisions - 1, zone.left);
        split(divisions - 1, zone.right);
    }

    public void makeLeafRoom(TreeZone zone) {
        int xOffset = 1 + RandomUtils.uniform(random, 1, 4);
        int yOffset = 1 + RandomUtils.uniform(random, 1, 4);

        int wLowerBound = (zone.width / 2) + 2;
        int wUpperBound = Math.max((3 * zone.width) / 5, wLowerBound + 2);
        int hLowerBound = (zone.height / 2) + 2;
        int hUpperBound = Math.max((3 * zone.height) / 5, hLowerBound + 2);

        int roomWidth = RandomUtils.uniform(random, wLowerBound, wUpperBound);
        int roomHeight = RandomUtils.uniform(random, hLowerBound, hUpperBound);
        zone.roomOrHallway = new Room(world, xOffset + zone.xOrigin,
                yOffset + zone.yOrigin, roomWidth, roomHeight);
    }

    public void splitHorizontally(TreeZone zone) {
        int topOffset = random.nextInt(9 * zone.height,
                13 * zone.height) / 20;
        zone.left = new TreeZone(zone.xOrigin, zone.yOrigin, zone.width, topOffset);
        zone.right = new TreeZone(zone.xOrigin,
                zone.yOrigin + topOffset, zone.width, zone.height - topOffset);
    }

    public void splitVertically(TreeZone zone) {
        int rightOffset = random.nextInt(9 * zone.width,
                13  * zone.width) / 20;
        zone.left = new TreeZone(zone.xOrigin, zone.yOrigin, rightOffset, zone.height);
        zone.right = new TreeZone(zone.xOrigin + rightOffset,
                zone.yOrigin, zone.width - rightOffset, zone.height);
    }

    public TETile[][] create(int divisions) {
        Connect connectedWorld = new Connect(root);
        split(divisions, root);
        connectedWorld.createConnections(world, root);
        return world;
    }

    public TreeZone getRandomRoom(){
        int randomCoordinates = random.nextInt(leafList.size());
        TreeZone randomRoom = leafList.get(randomCoordinates);
        return randomRoom;
    }

    public int[] roomCoordinates(TreeZone room) {
        for (int i = room.xOrigin; i < room.width; i++) {
            for (int j = room.yOrigin; j < room.height; j++) {
                if (world[i][j].description().equals("floor")) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{1,1};
    }

    public void placePlayer(Player newPlayer){
//        int i = random.nextInt(0,Engine.WIDTH);
//        int j = random.nextInt(0,Engine.HEIGHT);
//        TETile newTile = world[i][j];
//        while(newTile != Tileset.FLOOR){
//            i = random.nextInt(0,Engine.WIDTH);
//            j =random.nextInt(0,Engine.HEIGHT);
//            newTile = world[i][j];
//        }
//        Player newPlayer = new Player(world,i,j);
//        newPlayer.movePlayer(i,j);


        TreeZone room = getRandomRoom();
        int[] coordinates = roomCoordinates(room);
        newPlayer.movePlayer(coordinates[0],coordinates[1]);
    }

    public void placeDoors(int numDoors){
        int counter = 0;
        while (counter < numDoors){
            int[] coordinates = getRandomCoordinates();
            world[coordinates[0]][coordinates[1]] = Tileset.LOCKED_DOOR;
            counter += 1;
        }
    }
    public void placeFlowers(int i) {
        int counter = 0;
        while (counter < i){
            int[] coordinates = getRandomCoordinates();
            world[coordinates[0]][coordinates[1]] = Tileset.FLOWER;
            counter += 1;
        }
    }


    public int[] getRandomCoordinates(){
        int x = random.nextInt(width);
        int y = random.nextInt(height);

        if (!world[x][y].description().equals("floor")){
            return getRandomCoordinates();
        }

        return new int[]{x, y};
    }

    public int[] treeGameCoordinates(Room room){
        int x = random.nextInt(room.width);
        int y = random.nextInt(room.height);
        if (!world[x][y].description().equals("floor")){
            return getRandomCoordinates();
        }

        return new int[]{x, y};
    }


    public void placeTrees(Room room, int numTrees) {
        int counter = 0;
        while (counter < numTrees) {
            int[] coordinates = treeGameCoordinates(room);
            world[coordinates[0]][coordinates[1]] = Tileset.TREE;
            counter += 1;
        }
    }


    public void placePlayerIn(Player player,Room room){
        int[] coordinates = treeGameCoordinates(room);
        player.movePlayer(coordinates[0],coordinates[1]);
    }


}
