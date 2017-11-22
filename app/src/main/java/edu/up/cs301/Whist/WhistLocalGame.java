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
    private boolean newRound = false;
    private boolean grandingPhase = false;
    private Card[] cardsByPlayerIdx = new Card[4];

    public WhistLocalGame(){
        mainGameState = new WhistGameState();
    }

    public void beginNewRound(){
        ///////handling points///////////
        //begin by adding points to the team that won the most tricks in the round
        scoreTrick();
        //sleep for a little....shhhhh
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e){}
        //if team 1 had more tricks, they get points
        if(mainGameState.team1WonTricks>mainGameState.team2WonTricks){
           //score is doubled for a team that wins without granding
            if(!mainGameState.team1Granded){
                mainGameState.team1Points+=((mainGameState.team1WonTricks-6)*2);
            }
            else{
                mainGameState.team1Points+=((mainGameState.team1WonTricks-6));
            }
        }
        //else team 2 had more tricks, so they get points
        else{
            //score is doubled if team 2 wins when team 1 granded
            if(mainGameState.team1Granded){
                mainGameState.team2Points+=((mainGameState.team2WonTricks-6)*2);
            }
            else{
                mainGameState.team2Points+=((mainGameState.team2WonTricks-6));
            }
        }
        /////////////////////////////////Points Handled/////////////////////////////
        //get a new deck and deal new hands
        mainGameState.mainDeck = new Deck();
        for(Hand h: mainGameState.playerHands){
            h.removeAll();
            for(int i = 0; i<13;i++){
                h.add(mainGameState.mainDeck.dealRandomCard());
            }
        }
        //clears out the cards played and cards in play
        mainGameState.cardsPlayed.removeAll();
        mainGameState.cardsInPlay.removeAll();
        //sets the turn back to zero
        mainGameState.turn = 0;
        //sets the grand back to false
        mainGameState.team1Granded = false;
        //sets the tricks back to zero
        mainGameState.team1WonTricks = 0;
        mainGameState.team2WonTricks = 0;
        //reset the new round boolean
        newRound = false;
        //reset the leadPlayer to zero
        mainGameState.leadPlayer = 0;


    }

    /**
     * this method is called first to see if a move is legal.
     * @param playerIdx
     * 		the player's player-number (ID)
     * @return
     */
    @Override
    protected boolean canMove(int playerIdx){
        if(mainGameState.getTurn()==playerIdx) return true;
        else return false;
    }

    /**
     * this method is called after send updated states
     * @return
     */
    @Override
    protected String checkIfGameOver(){
        if(mainGameState.team1Points>=7){
            return "Team 1 Wins "+mainGameState.team1Points+" to "+mainGameState.team2Points+"!";
        }
        else if(mainGameState.team2Points>=7){
            return "Team 2 Wins "+mainGameState.team2Points+" to "+mainGameState.team1Points+"!";
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
        else if(thisPlayerIdx!=mainGameState.turn){
            //if it isn't technically that player's turn, but we are in bid phase
            if(action instanceof BidAction){
                //check to see if we are still within the bidding stage of the round
                if(mainGameState.getTurn()<4){
                    //lastly, increment the turn
                    incrementTurn();
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
            if(mainGameState.cardsPlayed.contains(playedCard)) return false;
            //this method assigns the lead suit of that trick
            if(mainGameState.cardsPlayed.getSize()%4==0){
                mainGameState.leadSuit = playedCard.getSuit();
            }
            //moves the played card onto the table and into the set of played cards
            mainGameState.cardsInPlay.add(playedCard);
            cardsByPlayerIdx[thisPlayerIdx] = playedCard;
            mainGameState.cardsPlayed.add(playedCard);


            //removes the card from the player's hand (regardles of CPU or human)
            mainGameState.playerHands[thisPlayerIdx].remove(playedCard);
            //lastly, set the turn
            incrementTurn();
            //after reaching 52 cards played, create a new round
            if(mainGameState.cardsPlayed.getSize()==52){newRound = true;}
            //after 4 moves, and not at the start of the round, set new trick to true
            else if (mainGameState.cardsPlayed.getSize()%4==0&&mainGameState.cardsPlayed.getSize()!=0){newTrick = true;}

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
        /////////////handle new trick////////////////
        if(newTrick){
            scoreTrick();

        }
        //////////////new Trick handled////////////////

        ////////////////handle new Round//////////////
        else if(newRound){
            beginNewRound();
        }
        //////////////handle new round/////////////////


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
        p.sendInfo(censoredState);
    }

    /**
     * This method is called anytime the turn reaches a multiple of 4
     */
    public void scoreTrick(){

        //determine which card and player won the trick
        Card winningCard = cardsByPlayerIdx[0];
        Integer winningPlayerIdx = 0;
        synchronized (winningPlayerIdx) {
            for (int i = 0; i < 4; i++) {
                if ((winningCard.getRank().value(14)  < cardsByPlayerIdx[i].getRank().value(14)) && (cardsByPlayerIdx[i].getSuit().equals(mainGameState.leadSuit))) {
                    winningCard = cardsByPlayerIdx[i];
                    winningPlayerIdx = i;
                }
            }

            //if the winning player was on team 2 (meaning it was either player 2 or 4)
            //add to their wonTricks

            if (winningPlayerIdx % 2 == 1) {
                mainGameState.team2WonTricks++;
            }
            //add to other team's wonTricks
            else {
                mainGameState.team1WonTricks++;
            }
        }
        //sleep thread to allow the user to get one last look at the completed trick
        try{Thread.sleep(3000);}
        catch (InterruptedException e){}

        //clears the cards in play
        mainGameState.cardsInPlay.removeAll();
        //sets the turn to establish who  is leading the next trick
        mainGameState.turn = winningPlayerIdx;
        //sets new trick to false
        newTrick = false;
        //sets the leadPlayer to the winningPlayerIdx
        mainGameState.leadPlayer = winningPlayerIdx;

    }

    public void incrementTurn(){
        mainGameState.turn ++;
        mainGameState.turn%=4;
        Log.i("gameTurn= ", ""+mainGameState.turn);
    }
}

