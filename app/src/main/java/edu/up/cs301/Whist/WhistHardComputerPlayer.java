package edu.up.cs301.Whist;

import android.util.Log;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Suit;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by Kevin on 11/26/2017.
 * WhistHardComputerPlayer is designed to be as unbeatable as possible
 * It uses a CardStack called "hotCards" to determine which cards are
 * guaranteed to win a round and guesses when the opponents are out of
 * a particular suit.
 * It also has Low-Round functionality implemented
 */

public class WhistHardComputerPlayer extends WhistComputerPlayer {
    //the cardStack of hot cards
    public CardStack hotCards = new CardStack();
    //the reaction time of the player
    private int reactionTime = 2000;
    //the player hand
    private Hand myHand = new Hand();
    //the saved state
    private WhistGameState savedState;

    /**
     * The constructor for the player
     * @param name - the string name for the player
     */
    public WhistHardComputerPlayer(String name) {
        super(name);
    }

    /**
     * This method is called whenever the game sends an updated state to this player
     * @param info - this is the object received from the game
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
        Log.i("/////rinfo"+allPlayerNames[playerNum],"CardsinPlay: "+savedState.cardsInPlay.getSize());

        if(!savedState.grandingPhase) {
            //clears hotcards
            hotCards.removeAll();
            //sets the hotCards!
            setHotCards();
        }
        //////////////////////////////////move handling//////////////////////////////
        //check if it is my turn
        if(savedState.getTurn()%4==playerNum) {
            //sleep....shhhhh
            sleep(reactionTime);
            try {
                makeMyMove(savedState.cardsInPlay.getSize()); //get size cards in play
            } catch (NullPointerException nope){
                sleep(1000); //sleep shhhhhhhh
            }
        }
        ////////////////////////end move handling//////////////////////////////////////

    }//recieveInfo

    /**
     * This
     */
    public void setHotCards(){

        //create a new deck
        Deck cardsLeft = new Deck();
        //remove the cards that have been played this round to leave just the cards left
        ArrayList<Card> stackCopyCardsinPlay = (ArrayList<Card>)savedState.cardsInPlay.stack.clone();
        ArrayList<Card> stackCopyCardsLeft = (ArrayList<Card>)savedState.cardsPlayed.stack.clone();
        if(savedState.cardsPlayed.getSize()!=0) {
            for (Card d : stackCopyCardsinPlay) {
                for (Card c : stackCopyCardsLeft) {
                    if (d.equals(c)) {
                        cardsLeft.remove(c);
                        break;
                    }
                }
            }
        }
        ////////////adds the highest remaining card in each suit/////////////
        hotCards.add(cardsLeft.getHighestInSuit(Suit.Club)); //highest club in that suit
        hotCards.add(cardsLeft.getHighestInSuit(Suit.Heart)); //highest heart in that suit
        hotCards.add(cardsLeft.getHighestInSuit(Suit.Spade)); //highest spade in that suit
        hotCards.add(cardsLeft.getHighestInSuit(Suit.Diamond)); //highest diamond in that suit
        ////////////adds the highest remaining card in each suit/////////////


        /////////////////finds my opponent's cards///////////////////////////
        //// fires only if both opponents have played
        if(savedState.cardsByPlayerIdx[(playerNum+3)%4]!=null&&savedState.cardsByPlayerIdx[(playerNum+1)%4]!=null){
            //creates an array of 2 cards to hold the opponents' cards
            Card[] oppCards = new Card[2]; //Thank you based god
            oppCards[0] = savedState.cardsByPlayerIdx[(playerNum+1)%4]; //creates the turn cycle of given player
            oppCards[1] = savedState.cardsByPlayerIdx[(playerNum+3)%4]; //creates the turn cycle of given player
            ///////////if both opponents did not follow suit (therefore are out of that suit)///////////////////
            //add all of the cards of that suit to hotCards////////////////////
            if(!oppCards[0].getSuit().equals(savedState.leadSuit)&&!oppCards[1].getSuit().equals(savedState.leadSuit)){
                for(Card d: cardsLeft.stack){ //go through cards left
                    if(d.getSuit().equals(savedState.leadSuit)){ //add lead suit
                        hotCards.add(d); //add the highest card of that suit
                    }
                }
            }
            //////////END if both opponents are out of a suit, add cards in that suit//////////

            ///////if one opponent is out of a suit and another played high in that suit but could not win////////////
            else if (!oppCards[0].getSuit().equals(savedState.leadSuit)&&
                    (oppCards[1].getRank().value(14)>10 && //determines rank of the lead suit
                            oppCards[1].getRank().value(14)<savedState.cardsByPlayerIdx[(playerNum+2)%4].getRank().value(14))){
                //if it looks like those players are running out of that suit, add all the cards of that suit to hotCards
                for(Card d: cardsLeft.stack){ //iterate through the card stack of cards left
                    if(d.getSuit().equals(savedState.leadSuit)){ //add lead suit
                        hotCards.add(d); //add the highest card of that suit
                    }
                }
            }
            else if (!oppCards[1].getSuit().equals(savedState.leadSuit)&&
                    (oppCards[0].getRank().value(14)>10 && //determines rank of the lead suit
                            oppCards[0].getRank().value(14)<savedState.cardsByPlayerIdx[(playerNum+2)%4].getRank().value(14))){
                //if it looks like those players are running out of that suit, add all the cards of that suit to hotCards
                for(Card d: cardsLeft.stack){ //iterate through the card stack of cards left
                    if(d.getSuit().equals(savedState.leadSuit)){ //add lead suit
                        hotCards.add(d); //add the highest card of that suit
                    }
                }
            }
        }
        ///////if one opponent is out of a suit and another played high in that suit////////////

    }
    public boolean hasAHotCard(){
        //goes through the entire hotCard stack and compares to hand
        for(Card f: hotCards.stack){ //iterate through hot card card stack
            for (Card d: myHand.stack) { //iterate through hand
                //if we do have a hot card return true
                if(f.equals(d)){
                    return true;
                }
            }
        }
        //else if we don't have a single hot card return false
        return false;
    }
    public Card getHotCard(){
        for(Card c: myHand.stack){ //iterate through hot card card stack
            for(Card d: hotCards.stack){ //iterate through hand
                if(c.equals(d)){ //if they have that hot card, then return that guy
                    return c;
                }
            }
        }
        return null;
    }
    @Override
    public void makeMyMove(int numCardsPlayed){
        if(savedState.highGround) { //high round
            Card cardToPlay = null; //set it to nulllll
            if (savedState.grandingPhase) {
                makeBid(); //granding phase; make bid
            } else {
                //key off of what turn we are in
                int turnInTrick = numCardsPlayed;
                //new trick, no one has played yet I am the lead player
                //if we have a hotCard to play, play it
                if (turnInTrick == 0) {
                    if (hasAHotCard()) {
                        cardToPlay = getHotCard();
                    }
                    //if I don't have a hotcard, play the lowest card in the suit of my highest card
                    //to draw out the higher cards than mine
                    else {
                        cardToPlay = myHand.getLowestInSuit(myHand.getHighest().getSuit()); //get lowest in suit of hand
                    }
                }
                //only one player has played on the other team
                else if (turnInTrick == 1) {
                    //if we cannot follow suit, play low
                    if (!myHand.hasCardInSuit(savedState.leadSuit)) {
                        cardToPlay = myHand.getLowest(); //get low low low

                    }
                    //else if we can follow suit, either try to win or play low
                    else {
                        //assign the opponent's card
                        Card opponentCard = savedState.cardsInPlay.getCardByIndex(0);
                        //if we can win
                        if (opponentCard.getRank().value(14) < myHand.getHighestInSuit(savedState.leadSuit).getRank().value(14)) {
                            cardToPlay = myHand.getHighestInSuit(savedState.leadSuit); //get the highest in suit from hand
                        }
                        //if we cannot win
                        else cardToPlay = myHand.getLowestInSuit(savedState.leadSuit); //get the lowest card in suit from hand

                    }

                }
                //only 2 players have played, an opponent and an allie
                else if (turnInTrick == 2) {
                    //if we cannot follow suit, play low
                    if (!myHand.hasCardInSuit(savedState.leadSuit)) {
                        cardToPlay = myHand.getLowest();
                    }
                    //else if we can follow suit, either try to win or play low
                    else {
                        //assign the opponent's card
                        Card opponentCard = savedState.cardsInPlay.getCardByIndex(1); //the other guys
                        Card allieCard = savedState.cardsInPlay.getCardByIndex(0); //allie bro
                        //if we can win
                        if (opponentCard.getRank().value(14) < myHand.getHighestInSuit(savedState.leadSuit).getRank().value(14)) {
                            //if our allie is already winning the hand, play low to avoid wasting good cards
                            if (allieCard.getRank().value(14) > opponentCard.getRank().value(14)) {
                                cardToPlay = myHand.getLowestInSuit(savedState.leadSuit); //play that lowest in suit
                            }
                            //if our allie is not already winning the hand, win it for the glory of Mother Russia
                            else cardToPlay = myHand.getHighestInSuit(savedState.leadSuit); //get highest card in suit from hand
                        }
                        //if we cannot win, play low to save valuable cards
                        else
                            cardToPlay = myHand.getLowestInSuit(savedState.leadSuit); //get lowest in suit from hand
                    }
                }
                //all 3 other players have played, and it is down to me...
                else if (turnInTrick == 3) {
                    //if we cannot follow suit, play low
                    if (!myHand.hasCardInSuit(savedState.leadSuit)) {
                        cardToPlay = myHand.getLowest(); //get lowest card in hand of that suit
                    }
                    //else if we can follow suit, either try to win or play low
                    else {
                        //assign the opponent's card
                        Card opponentCard = savedState.cardsInPlay.getCardByIndex(0); //assign opponent 0
                        Card opponent2Card = savedState.cardsInPlay.getCardByIndex(2); //assign opponent 2
                        Card allieCard = savedState.cardsInPlay.getCardByIndex(1); //assign allie
                        //if we can win
                        if (opponentCard.getRank().value(14) < myHand.getHighestInSuit(savedState.leadSuit).getRank().value(14)
                                && opponent2Card.getRank().value(14) < myHand.getHighestInSuit(savedState.leadSuit).getRank().value(14)) {
                            //if our allie is already winning the hand, play low to avoid wasting good cards
                            if (allieCard.getRank().value(14) > Math.max(opponent2Card.getRank().value(14), opponentCard.getRank().value(14))) {
                                cardToPlay = myHand.getLowestInSuit(savedState.leadSuit);
                            }
                            //if our allie is not already winning the hand, win it for the glory of Mother Russia
                            else cardToPlay = myHand.getHighestInSuit(savedState.leadSuit); //get the highest card in that suit
                        }
                        //if we cannot win, play low to save valuable cards
                        else
                            cardToPlay = myHand.getLowestInSuit(savedState.leadSuit); //get the lowest card in that suit
                    }
                }
                //after deciding which card to play, play the card and remove it from hand
                myHand.remove(cardToPlay); //remove the card you just played
                game.sendAction(new PlayCardAction(this, cardToPlay)); //send tha action just do it
            }
        }
        else {
            lowRound(numCardsPlayed);
        }
    }

    public Hand getMyHand(){ return myHand;}

    public void makeBid(){
        //establishes an int for the average card value
        int avg = 0; //rip lil peep
        //gets the average card value by summing and dividing by 13
        for(Card d: myHand.stack){ //iterate through the hand
            avg+=d.getRank().value(14); //get average rank of player
        }
        avg/=13;
        //gets either a low club or a low spade for a high bid
        CardStack bidders = new CardStack(); //free tayk
        if(avg>7){
            bidders.add(myHand.getLowestInSuit(Suit.Club)); //add bid club in bidding phase
            bidders.add(myHand.getLowestInSuit(Suit.Spade)); //add bid spade in bidding phase
            game.sendAction(new BidAction(this,bidders.getLowest())); //send that action its lit
        }
        //gets either a low heart or a low diamond for a low bid
        else{
            bidders.add(myHand.getLowestInSuit(Suit.Heart)); //add bid heart in bidding phase
            bidders.add(myHand.getLowestInSuit(Suit.Diamond)); //add bid diamond in bidding phase
            game.sendAction(new BidAction(this,bidders.getLowest())); //send that action again
        }
    }

    //if we are in a low round
    public void lowRound(int numCardsPlayed){
        Card cardToPlay = null;

        //key off of what turn we are in
        int turnInTrick = numCardsPlayed;
        //new trick, no one has played yet I am the lead player
        //if we have a hotCard to play, play it
        if (turnInTrick == 0) {
            cardToPlay = myHand.getLowest(); //play lowest card in hand
        }
        //only one player has played on the other team
        else {
            Hand cardsInSuit = new Hand();
            for(Card c : myHand.stack){ //iterate through hand
                if(c.getSuit() == savedState.leadSuit) cardsInSuit.add(c); //add card in lead suit
            }
            if(cardsInSuit.getSize()==0) cardToPlay = myHand.getHighest(); //gethighest card

            cardsInSuit.organizeBySuit(); //organize the cards by suit

            for(int i = cardsInSuit.getSize()-1; i >= 0; i--){
                //this absurdly long if-statement checks if the card we're on is greater than the greatest card in suit on the table
                if(cardsInSuit.getCardByIndex(i).getRank().value(14) > savedState.cardsInPlay.getHighestInSuit(savedState.leadSuit).getRank().value(14)){
                    continue;
                }
                else{
                    //the first card that would not win
                    cardToPlay = cardsInSuit.getCardByIndex(i); //
                    break; //break it all...grrrr
                }
            }
            if(cardToPlay == null) cardToPlay = cardsInSuit.getHighest(); //if we have to win a hand, play high
        }
        //after deciding which card to play, play the card and remove it from hand
        myHand.remove(cardToPlay); //remove card played from hand
        game.sendAction(new PlayCardAction(this, cardToPlay)); //send that action....its the right thing to do

    }
}

