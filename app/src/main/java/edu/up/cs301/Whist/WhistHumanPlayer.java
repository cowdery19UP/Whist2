package edu.up.cs301.Whist;

import android.view.View;

import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by PatrickMaloney on 11/7/17.
 */

public class WhistHumanPlayer extends GameHumanPlayer {
    private GameMainActivity activity;

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

    }

    @Override
    public View getTopView(){

        //return activity.findViewById(R.id.top_gui_layout);
        return null;
    }
}
