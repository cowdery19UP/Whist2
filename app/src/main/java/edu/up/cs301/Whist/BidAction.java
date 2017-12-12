package edu.up.cs301.Whist;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;

/**
 * Created by Samuel on 11/10/2017.
 */

public class BidAction extends PlayCardAction {
    /**
     * constructor for GameAction
     *
     * @param player
     * 		the player who created the action
     * 	@param c
     * 	    the card associated with the action
     */
    public BidAction(GamePlayer player,Card c) {
        super(player,c);
    }

    //boolean methods to remove ambiguity with instanceof and inheritance
    public boolean isCardPlayAction(){return false;}
    public boolean isBidAction(){return true;}
}
