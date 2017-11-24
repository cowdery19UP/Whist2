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
        playerTypes.add(new GamePlayerType("Bomputer Player") {
            public GamePlayer createPlayer(String name) {
                return new WhistComputerPlayer(name);
            }});


        // Create a game configuration class for Pig:
        GameConfig defaultConfig = new GameConfig(playerTypes, 4, 4, "Minnesota Whist", PORT_NUMBER);
        defaultConfig.addPlayer("Steve", 0); // player 1: a human player
        defaultConfig.addPlayer("Kevin", 1); // player 2: a computer player
        defaultConfig.addPlayer("Andrew",1);
        defaultConfig.addPlayer("Patrick",1);
        defaultConfig.setRemoteData("Sam", "", 0);


        return defaultConfig;

    }

    @Override
    public LocalGame createLocalGame() {
        return new WhistLocalGame();
    }
}
