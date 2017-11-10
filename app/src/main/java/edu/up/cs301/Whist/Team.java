package edu.up.cs301.Whist;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;

/**
 * Created by Samuel on 11/9/2017.
 */

public class Team {
    private GamePlayer[] thesePlayers = new GamePlayer[2];
    private int teamScore = 0;
    private boolean granded = false;
    private ArrayList<CardStack> wonTricks = new ArrayList<CardStack>();

    public Team(GamePlayer p1, GamePlayer p2){
        thesePlayers[0] = p1;
        thesePlayers[1] = p2;
    }

    private void addPlayer(int indx, GamePlayer player){
        thesePlayers[indx] = player;
    }

    private void removePlayer(GamePlayer player){
        if(thesePlayers[0].equals(player)){
            thesePlayers[0] = null;
        }
        else if(thesePlayers[1].equals(player)){
            thesePlayers[1] = null;
        }
        else{
            //the player was not found! Try being better!!
        }
    }
    public boolean hasPlayer(GamePlayer player){
        if(thesePlayers[0].equals(player)||thesePlayers[1].equals(player)){
            return true;
        }
        else return false;
    }

    public int getTeamScore(){return teamScore;}
    public int getWonTricks(){return wonTricks.size();}
    public boolean isGranded(){return granded;}
    public void addPoints(int points){teamScore = teamScore+points;}
}
