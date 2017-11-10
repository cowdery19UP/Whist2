package edu.up.cs301.Whist;

import edu.up.cs301.game.GamePlayer;

/**
 * Created by Samuel on 11/10/2017.
 */

public class PlayCardAction extends MoveAction {
    // the player who generated the request
    private GamePlayer player;

    /**
     * constructor for GameAction
     *
     * @param player
     * 		the player who created the action
     */
    public PlayCardAction(GamePlayer player) {
        super(player);
    }

    public boolean isCardPlayAction(){return true;}
    public boolean isBidAction(){return false;}
}
