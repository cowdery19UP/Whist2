package edu.up.cs301.Whist;

import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by PatrickMaloney on 11/7/17.
 */

public class WhistComputerPlayer extends GameComputerPlayer {

    private Hand myHand = new Hand();

    public WhistComputerPlayer(String name){
        super(name);
    }

    public void receiveInfo(GameInfo info){

    }

    public Hand getMyHand(){ return myHand;}
}

