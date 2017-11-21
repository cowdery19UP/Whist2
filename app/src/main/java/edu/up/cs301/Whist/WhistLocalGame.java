package edu.up.cs301.Whist;

import android.util.Log;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by Patrick Maloney on 11/7/17.
 *
 */

public class WhistLocalGame extends LocalGame {

    private WhistGameState mainGameState;
    private boolean newTrick = false;

    public WhistLocalGame(){
        mainGameState = new WhistGameState();
        if(players!=null){Log.i("players not null",""+players[0].toString());}
        else Log.i("players null","sadface");
    }

    public void newRound(){
        //sets the turn back to zero
        mainGameState.turn = 0;
        ///////handling points///////////
        //begin by adding points to the team that won the most tricks in the round
        if(mainGameState.team1WonTricks>mainGameState.team2WonTricks){
           //score is doubled for a team that wins without granding
            if(!mainGameState.team1Granded){
                addPoints(1,mainGameState.team1WonTricks*2);
            }
            else{
                addPoints(1,mainGameState.team1WonTricks);
            }
        }
        else{
            if(!mainGameState.team1Granded){
                addPoints(2,mainGameState.team1WonTricks*2);
            }
            else{
                addPoints(2,mainGameState.team1WonTricks);
            }
        }
        for(Team t: mainGameState.teams){
            t.resetGrand();
            t.clearTricks();
        }

        /////////////////////////////////

    }

    /**
     * this method is called first to see if a move is legal.
     * @param playerIdx
     * 		the player's player-number (ID)
     * @return
     */
    @Override
    protected boolean canMove(int playerIdx){
        if(mainGameState.getTurn()%4==playerIdx) return true;
        else return false;
    }

    /**
     * this method is called after send updated states
     * @return
     */
    @Override
    protected String checkIfGameOver(){
        if(mainGameState.team1Points>=7){
            return "Team 1 Wins!";
        }
        else if(mainGameState.team2Points>=7){
            return "Team 2 Wins!";
        }
        else return null;
    }

    /**
     * This method alters the state based on the action received
     */
    @Override
    protected boolean makeMove(GameAction action){

        if(!(action instanceof MoveAction)){
            return false;
        }
        //safe casting of the sent in action into a MoveAction
        MoveAction theAction = (MoveAction) action;
        // get the index of the player making the move; return false
        int thisPlayerIdx = getPlayerIdx(theAction.getPlayer());
        if (thisPlayerIdx < 0) { // illegal player
            return false;
        }
        //check to see who's turn it is
        else if(thisPlayerIdx!=mainGameState.turn%4){
            //if it isn't technically that player's turn, but we are in bid phase
            if(action instanceof BidAction){
                //check to see if we are still within the bidding stage of the round
                if(mainGameState.getTurn()<4){
                    //lastly, increment the turn
                    mainGameState.turn++;
                    sendAllUpdatedState();
                    return true;
                }
                //if we are past the bidding phase and it is not your turn
                else return false;
            }

        }
        //check for an instance of PlayCardAction
        if(action instanceof PlayCardAction){
            Card playedCard = ((PlayCardAction) theAction).getCard();
            //this method assigns the lead suit of that trick
            if(mainGameState.cardsPlayed.getSize()%4==0){
                mainGameState.leadSuit = playedCard.getSuit();
            }
            //moves the played card onto the table and into the set of played cards
            mainGameState.cardsInPlay.add(playedCard);
            mainGameState.cardsPlayed.add(playedCard);
            //removes the card from the player's hand (regardles of CPU or human)
            if(theAction.getPlayer()instanceof WhistHumanPlayer){
                mainGameState.playerHands[thisPlayerIdx].remove(playedCard);
            }
            else if(theAction.getPlayer()instanceof WhistComputerPlayer){
                mainGameState.playerHands[thisPlayerIdx].remove(playedCard);
            }
            else return false;
            //lastly, set the turn
            mainGameState.turn++;
            //after 4 moves, set new trick to true
            if(mainGameState.cardsPlayed.getSize()%4==0&&mainGameState.turn!=0){newTrick = true;}
            sendAllUpdatedState();
            return true;
        }

        return false;
    }

    /**
     * This method needs to null out all information that isn't supposed to be known
     * by certain players - TESTED--SUCCESS
     * @param p
     */
    @Override
    protected void sendUpdatedStateTo(GamePlayer p){
        ////////////////handle new Round//////////////
        if(mainGameState.cardsPlayed.getSize()==52){
            newRound();
        }
        //////////////handle new round/////////////////

        /////////////handle new trick////////////////
        if(newTrick){
            synchronized (mainGameState) {
                scoreTrick();
            }
        }
        //////////////new Trick handled////////////////

        //copy the state to edit and null information
        WhistGameState censoredState = new WhistGameState(mainGameState);
        //get the idx of the player p
        int idx = getPlayerIdx(p);
        //null out the other player's hands --works!
        for(int i = 0; i<mainGameState.playerHands.length;i++){
            if(i!=idx){
                censoredState.playerHands[i]=null;
            }
        }
        //TODO Patrick: need to re-work your sorting algorithm. Currently it crashes the game...
        //mainGameState.playerHands[idx].organizeBySuit();
        p.sendInfo(censoredState);
    }

    /**
     * This method is called anytime the turn reaches a multiple of 4
     */
    public void scoreTrick(){
        //determine which card and player won the trick
        Card winningCard = mainGameState.cardsInPlay.getCardByIndex(0);
        int winningPlayerIdx = 0;
        for (int i = 0; i < 4; i++) {
            if (winningCard.getRank().value(14) < mainGameState.cardsInPlay.getCardByIndex(i).getRank().value(14)) {
                winningCard = mainGameState.cardsInPlay.getCardByIndex(i);
                winningPlayerIdx = i;
            }
        }
        //if the winning player was on team 2 (meaning it was either player 2 or 4)
        //add to their wonTricks
        if((winningPlayerIdx+1)%2==0){
            mainGameState.team2WonTricks++;
        }
        //add to other team's wonTricks
        else{
            mainGameState.team1WonTricks++;
        }
        //sleep thread to allow the user to get one last look at the completed trick
        try{Thread.sleep(3000);}
        catch (InterruptedException e){}

        //clears the cards in play
        mainGameState.cardsInPlay.removeAll();
        mainGameState.turn = winningPlayerIdx;
        newTrick = false;
        mainGameState.leadPlayer = winningPlayerIdx;

    }

    //TODO figure out how the heck 'teams' work
    private void setTeams(){
        if(mainGameState!=null) {
            mainGameState.teams[0] = new Team(players[0], players[2]);
            mainGameState.teams[1] = new Team(players[1], players[3]);
        }
        else Log.d("SetTeams","null GameState");
    }

    public void addPoints(int teamIncx, int points){
        switch (teamIncx){
            case 1: mainGameState.team1Points = mainGameState.team1Points+points;
                break;
            case 2: mainGameState.team2Points = mainGameState.team2Points+points;
                break;
        }
    }
}

