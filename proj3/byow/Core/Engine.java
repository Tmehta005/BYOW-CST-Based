package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Engine implements Serializable {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 45;
    public static final int HUD_OFFSET = 5;
    public static final int DIVISIONS = 4;
    public static final int MAX_HEALTH = 3;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() throws IOException, InterruptedException {
        ter.initialize(WIDTH, HEIGHT);
        StartGame s = new StartGame(this, ter);
        s.start();

    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

//         REMOVE LINE BELOW WHEN DONE
//        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = null;
        Player player = null;
        StartGame game = null;
        char ptr = input.charAt(0);
        input = input.substring(1);
        final File CWD = new File(".");

        while (!input.isEmpty()) {
            if (ptr == ':' && (input.charAt(0) == 'Q'
                    || input.charAt(0) == 'q')) {
                if (game != null) {
                    game.saveGame();
                }
                break;
            } else if (ptr == 'W' || ptr == 'w'
                    || ptr == 'A' || ptr == 'a'
                    || ptr == 'S' || ptr == 's'
                    || ptr == 'D' || ptr == 'd') {
                assert player != null;
                player.movePlayer(ptr);
                game.saveGame();
            } else if (ptr == 'N' || ptr == 'n') {
                StringBuilder seed = new StringBuilder();
                ptr = input.charAt(0);
                input = input.substring(1);
                while (ptr != 'S' && ptr != 's') {
                    seed.append(ptr);
                    ptr = input.charAt(0);
                    input = input.substring(1);
                }
                long lSeed = Long.parseLong(seed.toString());
                TreeWorld worldObject = new TreeWorld(lSeed, WIDTH, HEIGHT - HUD_OFFSET);
                world = worldObject.create(DIVISIONS);
                player = new Player(world, 0, 0, MAX_HEALTH);
                game = new StartGame(this, ter, world, player);
                game.saveGame();
            } else if (ptr == 'L' || ptr == 'l') {
                File toOpen = FileUtils.join(".", "save.txt");
                assert(toOpen.exists());
                game = FileUtils.readObject(toOpen, StartGame.class);
                world = game.getWorld();
                player = game.getPlayer();
            }
            if (input.length() > 0) {
                ptr = input.charAt(0);
                input = input.substring(1);
            }
        }
//         REMOVE LINE BELOW WHEN DONE
//        ter.renderFrame(world);
        return world;
    }
}
