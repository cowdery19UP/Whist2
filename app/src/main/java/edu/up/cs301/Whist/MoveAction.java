package edu.up.cs301.Whist;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by Samuel on 11/10/2017.
 * @Author Steven R Vegdahl
 */

public class MoveAction extends GameAction {
    // the player who generated the request
    private GamePlayer player;

    /**
     * constructor for GameAction
     *
     * @param player
     * 		the player who created the action
     */
    public MoveAction(GamePlayer player) {
        super(player);
    }

    public boolean isCardPlayAction(){return false;}
    public boolean isBidAction(){return false;}
}