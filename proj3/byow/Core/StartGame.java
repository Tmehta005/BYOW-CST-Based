package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class StartGame implements Serializable {
    private static final File currentWorkingDirectory = new File(".");
    private Engine engine;
    private TERenderer te;
    private TreeWorld treeWorld;
    private TETile[][] world;
    private long seed;
    private static boolean gameOver;
    private Player player;
    private final int HEALTH = 5;
    private static int points = 0;
    private int lockedDoors = 4;
    private final char[] MOVEMENT_KEYS =
            new char[]{'w', 'a', 's', 'd', 'W', 'A','S', 'D'};
    private static int gameTime = 60000;
    private String playerName;



    public StartGame(Engine engine, TERenderer te) {
        this.engine = engine;
        this.te = te;
    }

    public StartGame(Engine engine, TERenderer te, TETile[][] world, Player player) {
        this.engine = engine;
        this.te = te;
        this.world = world;
        this.player = player;
    }

    public TETile[][] getWorld() {
        return world;
    }

    public Player getPlayer() {
        return player;
    }

    public void mainMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenRadius();
        StdDraw.setPenColor(Color.white);
        StdDraw.text((double) Engine.WIDTH / 2, 30, "CS61B The Game");
        StdDraw.text((double) Engine.WIDTH / 2, 25, "New Game (N)");
        StdDraw.text((double) Engine.WIDTH / 2, 20, "Load Game (L)");
        StdDraw.text((double) Engine.WIDTH / 2, 15, "Quit (Q)");
        StdDraw.text((double) Engine.WIDTH / 2, 10, "Shop (S)");
        StdDraw.show();
        StdDraw.clear();
    }

    public void start() throws IOException, InterruptedException {
        boolean mainMenu = true;
        while (mainMenu) {
            mainMenu();
            String initInput = nextInput(true);
            switch (initInput) {
                case "n", "N":
                    getName();
                    createGame();
                    playGame();
                    break;
                case "l", "L":
                    loadGame();
                    break;
                case "q", "Q":
                    mainMenu = false;
                    quitScreen("Exiting...");
                    break;
                case "S","s":
                    boolean shopScreen = true;
                    while (shopScreen){
                        shopScreen();
                        if (StdDraw.hasNextKeyTyped()) {
                            String option = nextInput(false);
                            shopOption(option);
                            shopScreen = false;
                        }
                    }

                    break;
            }

        }
        System.exit(0);
    }

    private void shopOption(String option) {
        if (option.equals("1") ){
            if (points >= 1) {
                gameTime = gameTime + 10000;
                points -= 5;
            }
            else{
                quitScreen("You don't have enough points.");
                StdDraw.clear();
            }
            return;
        }
        if (option.equals("2")){
            if (points >= 2) {
                gameTime = gameTime + 20000;
                points -= 10;
            }
            else {
                quitScreen("You don't have enough points.");
                StdDraw.clear();
            }
            return;
        }
        if (option.equals("3")){
            if (points >= 3) {
                gameTime = gameTime + 30000;
                points -= 15;
            }
            else{
                quitScreen("You don't have enough points.");
                StdDraw.clear();
            }
            return;
        }
        if (option.equals("4")){
            if (points >= 4){
                points += 1;
            }
            else{
                quitScreen("You don't have enough points.");
                StdDraw.clear();
            }
            return;
        }
    }

    public void shopScreen(){
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenRadius();
        StdDraw.setPenColor(Color.red);
        StdDraw.text((double) Engine.WIDTH - 10, 40, "Current Points:" + " " + points );
        StdDraw.setPenColor(Color.green);
        StdDraw.text((double) Engine.WIDTH / 2, 35, "Type which number you would like to purchase with your points!");
        StdDraw.setPenColor(Color.white);
        StdDraw.text((double) Engine.WIDTH / 2, 30, "1: Add 10 seconds to the game. (1 points)");
        StdDraw.text((double) Engine.WIDTH / 2, 25, "2: Add 20 seconds to the game. (2 points)");
        StdDraw.text((double) Engine.WIDTH / 2, 20, "3: Add 30 seconds to the game. (3 points)");
        StdDraw.text((double) Engine.WIDTH / 2, 15, "4: Free Win. (4 points)");
        StdDraw.show();
    }

    public void showName(String n) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenRadius();
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text((double) Engine.WIDTH / 2, 35, "Please enter your name.");
        StdDraw.text((double) Engine.WIDTH / 2, 25, "Press '/' or ';' when you are done.");
        StdDraw.text((double) Engine.WIDTH / 2, 15, n);
        StdDraw.show();
    }

    public void getName() {
        StringBuilder nameBuilder = new StringBuilder();
        char c = ' ';
        showName("");
        while (c != '/' && c != ';') {
            if (StdDraw.hasNextKeyTyped()) {
                c = StdDraw.nextKeyTyped();
                if (c != '/' && c != ';') {
                    nameBuilder.append(c);
                }
                showName(nameBuilder.toString());
            }
        }
        StdDraw.pause(1000);
        playerName = nameBuilder.toString();
    }

    public void createGame(){
        gameOver = false;
        String inputSeed = getSeed();
        seed = Long.parseLong(inputSeed.substring(1, inputSeed.length() - 1));
        treeWorld = new TreeWorld(seed, Engine.WIDTH,Engine.HEIGHT - 5);
        world = treeWorld.create(Engine.DIVISIONS);
        player = new Player(world, 0, 0, HEALTH);
        treeWorld.placePlayer(player);
        treeWorld.placeDoors(lockedDoors);
        treeWorld.placeFlowers(3);
        te.renderFrame(world);

    }

    private void playGame() throws IOException, InterruptedException {
        char[] inputs = new char[2];
        boolean endGame = false;
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        int currHealth = player.getHealth();
        int prevHealth;
        int tempX;
        int tempY;
        StringBuilder healthDisplay = new StringBuilder();
        healthDisplay.append("♥".repeat(Math.max(0, currHealth)));
        healthDisplay.append("♡".repeat(Math.max(0, HEALTH - currHealth)));
        long endTime = System.currentTimeMillis() + gameTime;

        while (!gameOver && !endGame && (System.currentTimeMillis() < endTime)) {
            tempX = mouseX;
            tempY = mouseY;
            prevHealth = currHealth;
            mouseX = (int) StdDraw.mouseX();
            mouseY = (int) StdDraw.mouseY();
            currHealth = player.getHealth();

            if (StdDraw.hasNextKeyTyped()) {
                inputs = getInputs(inputs);
                System.out.println(inputs[1]);
                for (char key : MOVEMENT_KEYS) {
                    if (inputs[1] == key) {
                        player.movePlayer(key);
                        te.renderFrame(world);
                        showHUD(mouseX, mouseY, healthDisplay);
                    }
                }
            }
            if (mouseX != tempX || mouseY != tempY) {
                StdDraw.clear();
                te.renderFrame(world);
                showHUD(mouseX, mouseY, healthDisplay);
            }
            if (shouldGameEnd(inputs)) {
                endGame = true;
            }
            if ((lockedDoors == 0 && player.health == 5) || winCheat(inputs)) {
                quitScreen("Great Job! You have won the game.");
                points += 1;
                playAgain("won");
            }
            if (player.currentTileOn.equals(Tileset.LOCKED_DOOR)){
                lockedDoors -= 1;
                startTreeGame();
                player.currentTileOn = Tileset.FLOOR;
                te.renderFrame(world);
                showHUD(mouseX, mouseY, healthDisplay);
            }
            if (player.currentTileOn.equals(Tileset.FLOWER)){
                if (player.health < HEALTH){
                    player.currentTileOn = Tileset.NOTHING;
                    player.health += 1;
                }
                else if (player.health == HEALTH){
                    quitScreen("You are already at maximum health!");
                    StdDraw.clear();
                }
            }
            if (currHealth != prevHealth) {
                StdDraw.clear();
                te.renderFrame(world);
                healthDisplay.delete(0, healthDisplay.length());
                healthDisplay.append("♥".repeat(Math.max(0, currHealth)));
                healthDisplay.append("♡".repeat(Math.max(0, HEALTH - currHealth)));
                showHUD(mouseX, mouseY, healthDisplay);
                prevHealth = currHealth;
            }
            if (player.health == 0 || loseCheat(inputs)){
                saveGame();
                StdDraw.pause(1000);
                quitScreen("Game Over, you lost! Your player has died :(");
                playAgain("lost");
            }
        }
        if (endGame) {
            saveGame();
            quitScreen("Saving and Exiting, " + playerName + "...");
            System.exit(0);
        }
        quitScreen("Sorry! Time is up");
    }

    public void showHUD(int mouseX, int mouseY, StringBuilder healthDisplay) {
        StdDraw.setPenColor(Color.WHITE);
        if (mouseX < Engine.WIDTH && mouseY < Engine.HEIGHT - 5) {
            StdDraw.textLeft(3, Engine.HEIGHT - 3, world[mouseX][mouseY].description());
        }
        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text((double) Engine.WIDTH / 2, Engine.HEIGHT - 3, playerName);
        StdDraw.setPenColor(Color.RED);
        StdDraw.textRight(Engine.WIDTH - 3, Engine.HEIGHT - 3, healthDisplay.toString());
        StdDraw.show();
    }

    public void playAgain(String condition) throws IOException, InterruptedException {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenRadius();
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text((double) Engine.WIDTH / 2, 35, "You have " + condition
                + " the game, " + playerName + ".");
        StdDraw.text((double) Engine.WIDTH / 2, 25, "Would you like to play again?");
        StdDraw.text((double) Engine.WIDTH / 2, 15, "Press y/Y for yes, n/N to exit, ");
        StdDraw.text((double) Engine.WIDTH / 2, 12, "and m/M to go back to the menu.");
        StdDraw.show();
        String s;
        while (true) {
            s = nextInput(false);
            switch (s) {
                case "Y", "y":
                    createGame();
                    playGame();
                    break;
                case "N", "n":
                    System.exit(0);
                    break;
                case "M", "m":
                    StdDraw.clear(Color.BLACK);
                    start();
                    break;
            }
        }
    }

    public void startTreeGame(){
        quitScreen("You have fifteen seconds to cut down all of the trees! Time starts now!");
        StdDraw.pause(500);
        StdDraw.clear();
        boolean treeGame = false;
        TreeWorld treeWorld = new TreeWorld(19290, Engine.WIDTH, Engine.HEIGHT);
        Room treeRoom = new Room(treeWorld.world, 20, 10, 30, 20);
        int numTreesCollected = 0;
        while(!treeGame) {
            int numTrees = 5;
            treeWorld.placeTrees(treeRoom,numTrees);
            Player otherPlayer = new Player(treeWorld.world, 0, 0, HEALTH);
            treeWorld.placePlayerIn(otherPlayer,treeRoom);
            te.renderFrame(treeWorld.world);
            numTreesCollected = collectTrees(otherPlayer,numTrees,treeWorld);
            if (numTreesCollected == 0){
                quitScreen("Great Job! You cut down all of the trees. Go find the next door!");
            }
            else{
                player.health -= 1;
                quitScreen("Game Over! You didn't cut down all the trees in time.");
            }
            treeGame = true;
        }
        StdDraw.clear();
    }

    private int collectTrees(Player p, int numTrees,TreeWorld treeWorld) {
        int num = numTrees;
        char[] inputs = new char[2];
        boolean gameOver = false;
        boolean moreTreesLeft = true;
        long endTime = System.currentTimeMillis() + 15000;
        while (System.currentTimeMillis() < endTime && moreTreesLeft) {
            if (StdDraw.hasNextKeyTyped()) {
                inputs = getInputs(inputs);
                p.movePlayer(inputs[1]);
            }
            if (p.currentTileOn.equals(Tileset.TREE)) {
                p.currentTileOn = Tileset.FLOOR;
                num--;
                if (num <= 0) {
                    moreTreesLeft = false;
                }
            }
            te.renderFrame(treeWorld.world);
        }
        return num;
    }

    public char[] getInputs(char[] arr) {
        char temp = arr[1];
        arr[1] = StdDraw.nextKeyTyped();
        arr[0] = temp;
        return arr;
    }

    public boolean shouldGameEnd(char[] inputs) {
        return inputs[0] == ':' && (inputs[1] == 'q' || inputs[1] == 'Q');
    }

    public boolean winCheat(char[] inputs) {
        return inputs[0] == '%' && (inputs[1] == 'Z'|| inputs[1] == 'z');
    }

    public boolean loseCheat(char[] inputs) {
        return inputs[0] == '*' && (inputs[1] == '_');
    }

    public void saveGame() {
        try {
            File currentGame = FileUtils.join(currentWorkingDirectory, "save.txt");
            FileUtils.writeObject(currentGame, this);
        } catch (IOException e) {
            System.out.println("Um... well, this is awkward. IOException.");
            System.out.println(e);
        }
    }

    public void loadGame() throws IOException, InterruptedException {
//        gameOver = false;
//        String seed = getSeed();
//        File toOpen = FileUtils.join(SAVE_DIRECTORY, seed + ".txt");
//        if (toOpen.exists()) {
//            StartGame loadGame = FileUtils.readObject(toOpen, StartGame.class);
//            loadGame.playGame();
//        } else {
//            quitScreen("Sorry, could not find seed. Goodbye.");
//            System.exit(0);
//        }

        File toOpen = FileUtils.join(currentWorkingDirectory, "save.txt");
        if (toOpen.exists()) {
            StartGame loadGame = FileUtils.readObject(toOpen, StartGame.class);
            world = loadGame.world;
            treeWorld = loadGame.treeWorld;
            seed = loadGame.seed;
            te.renderFrame(world);
            loadGame.playGame();
        } else {
            quitScreen("Sorry, could not find seed. Goodbye.");
            System.exit(0);
        }
    }

    private void quitScreen(String text) {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setPenRadius();
        StdDraw.text((double) Engine.WIDTH / 2,
                (double) Engine.HEIGHT / 2, text);
        StdDraw.show();
        StdDraw.pause(1000);
        StdDraw.clear();
    }

    public void showSeed(String seed) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenRadius();
        StdDraw.setPenColor(Color.white);
        StdDraw.text((double) Engine.WIDTH / 2, 30, "Please enter a random seed!");
        StdDraw.text((double) Engine.WIDTH / 2, 20, seed);
        StdDraw.show();
    }

    public String getSeed() {
        StringBuilder seed = new StringBuilder();
        String s = "";
        showSeed("");
        char c = ' ';
        while (c != 's' && c != 'S') {
            if (StdDraw.hasNextKeyTyped()) {
                c = StdDraw.nextKeyTyped();
                s = Character.toString(c);
                seed.append(s);
                showSeed(seed.toString());
            }
        }
        StdDraw.pause(1000);
        return seed.toString();
    }

    private String nextInput(boolean isMainMenu) {
        String ret = "";
        if (StdDraw.hasNextKeyTyped()) {
            char c = StdDraw.nextKeyTyped();
            String s = Character.toString(c);
            ret += s;
        }
        if (isMainMenu && StdDraw.isMousePressed()) {
            if (StdDraw.mouseX() > (double) Engine.WIDTH / 4
                    && StdDraw.mouseX() < (double) (Engine.WIDTH * 3) / 4) {
                if (StdDraw.mouseY() > (double) Engine.HEIGHT / 2
                        && StdDraw.mouseY() <= (double) (11 * Engine.HEIGHT) / 18) {
                    ret = "n";
                } else if (StdDraw.mouseY() > (double) (7 * Engine.HEIGHT) / 18
                        && StdDraw.mouseY() <= (double) Engine.HEIGHT / 2) {
                    ret = "l";
                } else if (StdDraw.mouseY() > (double) (5 * Engine.HEIGHT) / 18
                        && StdDraw.mouseY() <= (double) (7 * Engine.HEIGHT) / 18) {
                    ret = "q";
                } else if (StdDraw.mouseY() > (double) (3 * Engine.HEIGHT) / 18
                        && StdDraw.mouseY() <= (double) (5 * Engine.HEIGHT) / 18) {
                    ret = "s";
                }
            }
        }
        return ret;
    }
}