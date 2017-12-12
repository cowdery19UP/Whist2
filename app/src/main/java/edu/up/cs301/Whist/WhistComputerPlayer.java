package edu.up.cs301.Whist;

import android.util.Log;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Suit;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.config.GameConfig;
import edu.up.cs301.game.config.GamePlayerType;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by PatrickMaloney on 11/7/17.
 * WhistComputerPlayer is the medium difficulty computer player
 * It plays with basic game logic and Low-Round ability, but does not play hotCards
 */

public class WhistComputerPlayer extends GameComputerPlayer {

    private int reactionTime = 2000;
    private Hand myHand = new Hand();
    private WhistGameState savedState;

    public WhistComputerPlayer(String name){
        super(name);
    }
    /**
     * This method recieves the gamestate from the game
     * and informs the computer player what to do
     * @param info - the updated gamestate
     */
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
            try {
                makeMyMove(savedState.cardsInPlay.getSize()); //get ize of the cards in play
            } catch (NullPointerException nope){
                sleep(1000); // sleep shhhhh
            }

        }
        ////////////////////////end move handling//////////////////////////////////////

    }//recieveInfo
    public void makeBid(){
        //establishes an int for the average card value
        int avg = 0;
        //gets the average card value by summing and dividing by 13
        for(Card d: myHand.stack){ //go through hand
            avg+=d.getRank().value(14); //gets the average of the given rank
        }
        avg/=13;
        //gets either a low club or a low spade for a high bid
        CardStack bidders = new CardStack();
        if(avg>7){
            bidders.add(myHand.getLowestInSuit(Suit.Club)); //get lowest club in bidding phase
            bidders.add(myHand.getLowestInSuit(Suit.Spade)); //get lowest spade in bidding phase
            game.sendAction(new BidAction(this,bidders.getLowest())); //send the action
        }
        //gets either a low heart or a low diamond for a low bid
        else{
            bidders.add(myHand.getLowestInSuit(Suit.Heart)); //get lowest heart in bidding phase
            bidders.add(myHand.getLowestInSuit(Suit.Diamond)); //get lowest diamond in bidding phase
            game.sendAction(new BidAction(this,bidders.getLowest())); //sdnd the action
        }
    }

    public void makeMyMove(int numCardsPlayed){
        if(savedState.highGround) {
            Card cardToPlay = null; //set cards to play to null
            if (savedState.grandingPhase) { //check to see if we are in the granding phase
                makeBid(); // make bid in the granding phase
            } else {
                /////////handling non-granding PlayCard actions//////////
                //key off of what turn we are in
                int turnInTrick = numCardsPlayed;
                //new trick, no one has played yet I am the lead player
                if (turnInTrick == 0) {
                    cardToPlay = myHand.getHighest(); //get highest card in hand
                }
                //only one player has played on the other team
                else if (turnInTrick == 1) {
                    //if we cannot follow suit, play low
                    if (!myHand.hasCardInSuit(savedState.leadSuit)) {
                        cardToPlay = myHand.getLowest(); //get lowest card in suit
                    }
                    //else if we can follow suit, either try to win or play low
                    else {
                        //assign the opponent's card
                        Card opponentCard = savedState.cardsInPlay.getCardByIndex(0);
                        //if we can win
                        if (opponentCard.getRank().value(14) < myHand.getHighestInSuit(savedState.leadSuit).getRank().value(14)) {
                            cardToPlay = myHand.getHighestInSuit(savedState.leadSuit);// get the highest card in suit from hand
                        }
                        //if we cannot win
                        else
                            cardToPlay = myHand.getLowestInSuit(savedState.leadSuit); // get the lowest card in the lead suit
                    }

                }
                //only 2 players have played, an opponent and an allie
                else if (turnInTrick == 2) {
                    //if we cannot follow suit, play low
                    if (!myHand.hasCardInSuit(savedState.leadSuit)) {
                        cardToPlay = myHand.getLowest(); // get lowest card in your hand
                    }
                    //else if we can follow suit, either try to win or play low
                    else {
                        //assign the opponent's card
                        Card opponentCard = savedState.cardsInPlay.getCardByIndex(1);
                        Card allieCard = savedState.cardsInPlay.getCardByIndex(0);
                        //if we can win
                        if (opponentCard.getRank().value(14) < myHand.getHighestInSuit(savedState.leadSuit).getRank().value(14)) {
                            //if our allie is already winning the hand, play low to avoid wasting good cards
                            if (allieCard.getRank().value(14) > opponentCard.getRank().value(14)) {
                                cardToPlay = myHand.getLowestInSuit(savedState.leadSuit);
                            }
                            //if our ally is not already winning the hand, win it for the glory of Mother Russia
                            else
                                cardToPlay = myHand.getHighestInSuit(savedState.leadSuit);
                        }
                        //if we cannot win, play low to save valuable cards
                        else
                            cardToPlay = myHand.getLowestInSuit(savedState.leadSuit);
                    }
                }
                //all 3 other players have played, and it is down to me...
                else if (turnInTrick == 3) {
                    //if we cannot follow suit, play low
                    if (!myHand.hasCardInSuit(savedState.leadSuit)) {
                        cardToPlay = myHand.getLowest();
                    }
                    //else if we can follow suit, either try to win or play low
                    else {
                        //assign the opponent's card
                        Card opponentCard = savedState.cardsInPlay.getCardByIndex(0);
                        //assign opponent 2
                        Card opponent2Card = savedState.cardsInPlay.getCardByIndex(2);
                        //assign opponent 1
                        Card allieCard = savedState.cardsInPlay.getCardByIndex(1);
                        //if we can win
                        //determines if you have the highest card of that suit
                        if (opponentCard.getRank().value(14) < myHand.getHighestInSuit(savedState.leadSuit).getRank().value(14)
                                && opponent2Card.getRank().value(14) < myHand.getHighestInSuit(savedState.leadSuit).getRank().value(14)) {
                            //if our allie is already winning the hand, play low to avoid wasting good cards
                            if (allieCard.getRank().value(14) > Math.max(opponent2Card.getRank().value(14), opponentCard.getRank().value(14))) {
                                cardToPlay = myHand.getLowestInSuit(savedState.leadSuit);
                            }
                            //if our allie is not already winning the hand, win it for the glory of Mother Russia
                            else
                                cardToPlay = myHand.getHighestInSuit(savedState.leadSuit);
                        }
                        //if we cannot win, play low to save valuable cards
                        else
                            cardToPlay = myHand.getLowestInSuit(savedState.leadSuit);
                    }
                }
                //after deciding which card to play, play the card and remove it from hand
                myHand.remove(cardToPlay);
                //send the action of car played
                game.sendAction(new PlayCardAction(this, cardToPlay));
                //////////////handling non-granding PlayCard actions//////////////////

            }
        }
        else {
            //play according to low round rules
            lowRound(numCardsPlayed);
        }

    }

    public Hand getMyHand(){ return myHand;} //gets your hand

    //if we are in a low round
    public void lowRound(int numCardsPlayed){
        Card cardToPlay = null; //set card to null

        //key off of what turn we are in
        int turnInTrick = numCardsPlayed;
        //new trick, no one has played yet I am the lead player
        //if we have a hotCard to play, play it
        if (turnInTrick == 0) {
            cardToPlay = myHand.getLowest(); // get lowest card in hand
        }
        //only one player has played on the other team
        else {
            Hand cardsInSuit = new Hand(); // go through cards in that suit
            for(Card c : myHand.stack){
                if(c.getSuit() == savedState.leadSuit) cardsInSuit.add(c); // get the lead card in suit
            }
            if(cardsInSuit.getSize()==0) cardToPlay = myHand.getHighest(); // play highest card

            cardsInSuit.organizeBySuit(); // organize the cards in that suit

            for(int i = cardsInSuit.getSize()-1; i >= 0; i--){
                //this absurdly long if-statement checks if the card we're on is greater than the greatest card in suit on the table
                if(cardsInSuit.getCardByIndex(i).getRank().value(14) > savedState.cardsInPlay.getHighestInSuit(savedState.leadSuit).getRank().value(14)){
                    continue;
                }
                else{
                    //the first card that would not win
                    cardToPlay = cardsInSuit.getCardByIndex(i);
                    break;
                }
            }
            if(cardToPlay == null) cardToPlay = cardsInSuit.getHighest(); //if we have to win a hand, play high
        }
        //after deciding which card to play, play the card and remove it from hand
        myHand.remove(cardToPlay);
        game.sendAction(new PlayCardAction(this, cardToPlay));// play the card
    }
}

