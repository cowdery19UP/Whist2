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
    //wonTricks was originally an arrayList of CardStacks, but that was
    //an inefficient use of memory considering it was only used as a number
    public int wonTricks = 0;

    public Team(GamePlayer p1, GamePlayer p2){
        thesePlayers[0] = p1;
        thesePlayers[1] = p2;
    }

    public void addPlayer(int indx, GamePlayer player){
        thesePlayers[indx] = player;
    }

    public void removePlayer(GamePlayer player){
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
    public int getWonTricks(){return wonTricks;}
    public boolean isGranded(){return granded;}
    public void resetGrand(){granded = false;}
    public void addPoints(int points){teamScore = teamScore+points;}
    public void clearTricks(){wonTricks = 0;}
}
