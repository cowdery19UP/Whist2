package edu.up.cs301.Whist;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by Kevin on 11/26/2017.
 */

public class WhistEasyComputerPlayer extends GameComputerPlayer {
    private int reactionTime = 1500;
    private Hand myHand = new Hand();
    private WhistGameState savedState;

    public WhistEasyComputerPlayer(String name) {
        super(name);
    }

    public void receiveInfo(GameInfo info){
        //check for null state
        if(info==null){
            return;
        }
        //check for correct instance
        if(!(info instanceof WhistGameState)){
            return;
        }
        //updates the player's gamestate
        savedState = (WhistGameState) info;
        //updates the player's hand
        myHand = savedState.getHand();
        //sleep....shhhhh
        sleep(reactionTime);

        //////////////////////////////////move handling//////////////////////////////
        //check if it is my turn
        if(savedState.getTurn()%4==playerNum) {
            makeMyMove(savedState.cardsInPlay.getSize());
        }
        ////////////////////////end move handling//////////////////////////////////////


    }//recieveInfo

    public void makeMyMove(int numCardsPlayed){
        Card cardToPlay = null;
        //key off of what turn we are in
        int turnInTrick = numCardsPlayed;
        //new trick, no one has played yet I am the lead player



        if (turnInTrick == 0) {
            cardToPlay = myHand.getRandomCard();
        }
        //only one player has played on the other team
        else if (turnInTrick == 1) {
            //
            if (!myHand.hasCardInSuit(savedState.leadSuit)) {
                cardToPlay = myHand.getRandomCard();
            }
            //else if we can follow suit, either try to win or play low
            else {
                cardToPlay = myHand.getRandomInSuit(savedState.leadSuit);
            }
        }
        //only 2 players have played, an opponent and an allie
        else if (turnInTrick == 2) {
            //if we cannot follow suit, play low
            if (!myHand.hasCardInSuit(savedState.leadSuit)) {
                cardToPlay = myHand.getRandomCard();
            }
            //else if we can follow suit, either try to win or play low
            else {
                //assign the opponent's card
                        cardToPlay = myHand.getRandomInSuit(savedState.leadSuit);
                    }
            }

        //all 3 other players have played, and it is down to me...
        else if (turnInTrick == 3) {
            //if we cannot follow suit, play low
            if (!myHand.hasCardInSuit(savedState.leadSuit)) {
                cardToPlay = myHand.getRandomCard();
            }
            //else if we can follow suit, either try to win or play low
            else {
                cardToPlay = myHand.getRandomInSuit(savedState.leadSuit);
            }
        }

        //after deciding which card to play, play the card and remove it from hand
        myHand.remove(cardToPlay);
        game.sendAction(new PlayCardAction(this,cardToPlay));
    }

    public Hand getMyHand(){ return myHand;}
}
