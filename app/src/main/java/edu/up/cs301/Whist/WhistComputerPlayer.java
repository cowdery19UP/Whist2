package edu.up.cs301.Whist;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by PatrickMaloney on 11/7/17.
 */

public class WhistComputerPlayer extends GameComputerPlayer {

    private int reactionTime = 5000;
    private Hand myHand = new Hand();
    private WhistGameState savedState;

    public WhistComputerPlayer(String name){
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
        //updates the cards on the table
        CardStack cardsOnTable = savedState.cardsInPlay;

        sleep(reactionTime);

        //////////////////////////////////move handling//////////////////////////////
        //key off of what turn we are in
        int turnInTrick = savedState.getTurn()%4;
        //new trick, no one has played yet
        if(turnInTrick==1){
            game.sendAction(new PlayCardAction(this,myHand.getHighest()));
        }
        //only one player has played on the other team
        else if(turnInTrick==2){
            //if we cannot follow suit, play low
            if(!myHand.hasCardInSuit(savedState.leadSuit)){
                game.sendAction(new PlayCardAction(this,myHand.getLowest()));
            }
            //else if we can follow suit, either try to win or play low
            else {
                //assign the opponent's card
                Card opponentCard = savedState.cardsInPlay.getCardByIndex(0);
                //if we can win
                if(opponentCard.getRank().value(14)>myHand.getHighestInSuit(savedState.leadSuit).getRank().value(14)){
                    game.sendAction(new PlayCardAction(this,myHand.getHighestInSuit(savedState.leadSuit)));
                }
                //if we cannot win
                else game.sendAction(new PlayCardAction(this,myHand.getLowestInSuit(savedState.leadSuit)));
            }

        }
        //only 2 players have played, an opponent and an allie
        else if (turnInTrick==3){
            //if we cannot follow suit, play low
            if(!myHand.hasCardInSuit(savedState.leadSuit)){
                game.sendAction(new PlayCardAction(this,myHand.getLowest()));
            }
            //else if we can follow suit, either try to win or play low
            else {
                //assign the opponent's card
                Card opponentCard = savedState.cardsInPlay.getCardByIndex(1);
                Card allieCard = savedState.cardsInPlay.getCardByIndex(0);
                //if we can win
                if(opponentCard.getRank().value(14)<myHand.getHighestInSuit(savedState.leadSuit).getRank().value(14)){
                    //if our allie is already winning the hand, play low to avoid wasting good cards
                    if(allieCard.getRank().value(14)>myHand.getHighestInSuit(savedState.leadSuit).getRank().value(14)){
                        game.sendAction(new PlayCardAction(this,myHand.getLowestInSuit(savedState.leadSuit)));
                    }
                    //if our allie is not already winning the hand, win it for the glory of Mother Russia
                    else game.sendAction(new PlayCardAction(this,myHand.getHighestInSuit(savedState.leadSuit)));
                }
                //if we cannot win, play low to save valuable cards
                else game.sendAction(new PlayCardAction(this,myHand.getLowestInSuit(savedState.leadSuit)));
            }
        }
        //all 3 other players have played, and it is down to me...
        else if (turnInTrick==0){
            //if we cannot follow suit, play low
            if(!myHand.hasCardInSuit(savedState.leadSuit)){
                game.sendAction(new PlayCardAction(this,myHand.getLowest()));
            }
            //else if we can follow suit, either try to win or play low
            else {
                //assign the opponent's card
                Card opponentCard = savedState.cardsInPlay.getCardByIndex(0);
                Card opponent2Card = savedState.cardsInPlay.getCardByIndex(2);
                Card allieCard = savedState.cardsInPlay.getCardByIndex(1);
                //if we can win
                if(opponentCard.getRank().value(14)<myHand.getHighestInSuit(savedState.leadSuit).getRank().value(14)
                        &&opponent2Card.getRank().value(14)<myHand.getHighestInSuit(savedState.leadSuit).getRank().value(14)){
                    //if our allie is already winning the hand, play low to avoid wasting good cards
                    if(allieCard.getRank().value(14)>myHand.getHighestInSuit(savedState.leadSuit).getRank().value(14)){
                        game.sendAction(new PlayCardAction(this,myHand.getLowestInSuit(savedState.leadSuit)));
                    }
                    //if our allie is not already winning the hand, win it for the glory of Mother Russia
                    else game.sendAction(new PlayCardAction(this,myHand.getHighestInSuit(savedState.leadSuit)));
                }
                //if we cannot win, play low to save valuable cards
                else game.sendAction(new PlayCardAction(this,myHand.getLowestInSuit(savedState.leadSuit)));
            }
        }

        //TODO figure out a way to determine if a card in the cardsInPlay CardStack belongs to partner


    }
    public Hand getMyHand(){ return myHand;}

}

