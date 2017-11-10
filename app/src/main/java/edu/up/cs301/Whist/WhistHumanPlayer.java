package edu.up.cs301.Whist;

import android.view.View;

import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by PatrickMaloney on 11/7/17.
 */

public class WhistHumanPlayer extends GameHumanPlayer {
    private GameMainActivity activity;
    private Hand myHand = new Hand();
    private WhistGameState savedState;

    public WhistHumanPlayer(String name){
        super(name);
    }

    public void setAsGui(GameMainActivity activity){

        // remember the activity
        this.activity = activity;

        // Load the layout resource for our GUI
        activity.setContentView(R.layout.whist_layout);
    }

    @Override
    public void receiveInfo(GameInfo info){
        info = (WhistGameState) info;

    }

    @Override
    public View getTopView(){

        //return activity.findViewById(R.id.top_gui_layout);
        return null;
    }
    //returns the player hand
    public Hand getMyHand(){ return myHand;}

    /**
     * This method checks to see if this player is on the same
     * team as the other player fed into the method
     * @param otherplayer -- the other player to compare this player to
     * @return -- returns true if the players are on the same team
     */
    public boolean sameTeam(GamePlayer otherplayer){
        //if team 1 contains our player check to see if it also contains otherplayer
        if(savedState.teams[0].hasPlayer(this)){
            if(savedState.teams[0].hasPlayer(otherplayer)){
                return true;
            }
            else return false;
        }
        //if team 2 contains our player check to see if it also contains otherplayer
        else if (savedState.teams[1].hasPlayer(this)){
            if(savedState.teams[1].hasPlayer(otherplayer)){
                return true;
            }
            else return false;
        }
        else return false;
    }
}
