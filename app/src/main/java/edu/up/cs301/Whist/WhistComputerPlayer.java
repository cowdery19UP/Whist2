package edu.up.cs301.Whist;

import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by PatrickMaloney on 11/7/17.
 */

public class WhistComputerPlayer extends GameComputerPlayer {

    private double reactionTime;
    private Hand myHand = new Hand();
    private WhistGameState savedState;

    public WhistComputerPlayer(String name){
        super(name);
    }

    public void receiveInfo(GameInfo info){
        if(!(info instanceof WhistGameState)){
            return;
        }
        //updates the player's gamestate
        savedState = (WhistGameState) info;


        //TODO figure out a way to determine if a card in the cardsInPlay CardStack belongs to partner
        CardStack cardsOnTable = savedState.cardsInPlay;

    }

    public Hand getMyHand(){ return myHand;}
}

