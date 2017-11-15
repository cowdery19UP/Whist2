package edu.up.cs301.Whist;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by Patrick Maloney on 11/7/17.
 *
 */

public class WhistLocalGame extends LocalGame {

    WhistGameState mainGameState;

    public WhistLocalGame(){
        mainGameState = new WhistGameState();
    }

    public void newRound(){
        //sets the turn back to zero
        mainGameState.turn = 0;
        ///////handling points///////////
        //begin by adding points to the team that won the most tricks in the round
        if(mainGameState.teams[0].getWonTricks()>mainGameState.teams[1].getWonTricks()){
           //score is doubled for a team that wins without granding
            if(!mainGameState.teams[0].isGranded()){
                mainGameState.teams[0].addPoints(2*mainGameState.teams[0].getWonTricks());
            }
            else{
                mainGameState.teams[0].addPoints(mainGameState.teams[0].getWonTricks());
            }
        }
        else{
            if(!mainGameState.teams[1].isGranded()){
                mainGameState.teams[1].addPoints(2*mainGameState.teams[1].getWonTricks());
            }
            else{
                mainGameState.teams[1].addPoints(mainGameState.teams[1].getWonTricks());
            }
        }
        /////////////////////////////////

    }


    @Override
    protected boolean canMove(int playerIdx){
        if(mainGameState.getTurn()==playerIdx) return true;
        else return false;
    }

    @Override
    protected String checkIfGameOver(){
        if(mainGameState.teams[0].getTeamScore()>=7){
            return "Team 1 Wins!";
        }
        else if(mainGameState.teams[1].getTeamScore()>=7){
            return "Team 2 Wins!";
        }
        else return null;
    }

    @Override
    protected boolean makeMove(GameAction action){

        if(action instanceof BidAction){
            //check to see if we are still within the first stage of the round
            if(mainGameState.getTurn()<4){
                return true;
            }
            else return false;
        }
        if(action instanceof PlayCardAction){
            //TODO need to code in all the cases for playing a card
        }


        return false;
    }

    @Override
    protected void sendUpdatedStateTo(GamePlayer p){

    }
}

