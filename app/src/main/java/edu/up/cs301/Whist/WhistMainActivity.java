package edu.up.cs301.Whist;

import java.util.ArrayList;

import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.config.GameConfig;
import edu.up.cs301.game.config.GamePlayerType;

/**
 * Created by PatrickMaloney on 11/7/17.
 */

public class WhistMainActivity extends GameMainActivity {

    public static final int PORT_NUMBER = 2278;

    @Override
    public GameConfig createDefaultConfig(){
        // Define the allowed player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

        // Pig has two player types:  human and computer
        playerTypes.add(new GamePlayerType("Local Human Player") {
            public GamePlayer createPlayer(String name) {
                return new WhistHumanPlayer(name);
            }});
        playerTypes.add(new GamePlayerType("Computer Player") {
            public GamePlayer createPlayer(String name) {
                return new WhistComputerPlayer(name);
            }});
        playerTypes.add(new GamePlayerType("Computer Player") {
            public GamePlayer createPlayer(String name) {
                return new WhistComputerPlayer(name);
            }});
        playerTypes.add(new GamePlayerType("Computer Player") {
            public GamePlayer createPlayer(String name) {
                return new WhistComputerPlayer(name);
            }});

        // Create a game configuration class for Pig:
        GameConfig defaultConfig = new GameConfig(playerTypes, 4, 4, "Whist", PORT_NUMBER);
        defaultConfig.addPlayer("Human", 0); // player 1: a human player
        defaultConfig.addPlayer("Computer1", 1); // player 2: a computer player
        defaultConfig.addPlayer("Computer2",2);
        defaultConfig.addPlayer("Computer3",3);
        defaultConfig.setRemoteData("Remote Human Player", "", 0);


        return defaultConfig;

    }

    @Override
    public LocalGame createLocalGame() {
        return new WhistLocalGame();
    }
}
