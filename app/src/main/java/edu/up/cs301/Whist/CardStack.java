package edu.up.cs301.Whist;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Suit;

/**
 * Created by Samuel on 11/7/2017.
 */

public class CardStack {

    protected ArrayList<Card> stack = new ArrayList<Card>();

    public CardStack(){

    }//mainCtor

    public CardStack(CardStack orig){
        for(Card c: orig.stack){
            stack.add(c);
        }
    }//copyCtor

    /**
     * This method goes through the stack of cards and
     * returns the card that is the greatest value rank
     * @return - the highest value card in any suit
     */
    public Card getHighest(){
        //assigns the first card in the stack to be
        //the card to be returned
        Card highCard = stack.get(0);
        //indexes through the stack and if the c card is a higher
        //value, it becomes the highCard
        for(Card c: stack){
            if(c.getRank().value(14)>highCard.getRank().value(14)){
                highCard = c;
            }
        }
        return highCard;
    }
    /**
     * This method goes through the array of cards and
     * returns the card that is the greatest value rank
     * @return - the highest value card in any suit
     * @param inputStack -- an array of cards to sort
     */
    public Card getHighest(ArrayList<Card> inputStack){
        //assigns the first card in the stack to be
        //the card to be returned
        Card highCard = inputStack.get(0);
        //indexes through the stack and if the c card is a higher
        //value, it becomes the highCard
        for(Card c: inputStack){
            if(c.getRank().value(14)>highCard.getRank().value(14)){
                highCard = c;
            }
        }
        return highCard;
    }
    /**
     * This method goes through the stack of cards and
     * returns the card that is the least value rank
     * @return - the lowest value card in any suit
     */
    public Card getLowest(){
        //assigns the first card in the stack to be
        //the card to be returned
        Card lowCard = stack.get(0);
        //indexes through the stack and if the c card is a lower
        //value, it becomes the lowCard
        for(Card c: stack){
            if(c.getRank().value(14)<lowCard.getRank().value(14)){
                lowCard = c;
            }
        }
        return lowCard;
    }
    /**
     * This method goes through the array of cards and
     * returns the card that is the lowest value rank
     * @return - the lowest value card in any suit
     * @param inputStack -- an array of cards to sort
     */
    public Card getLowest(ArrayList<Card> inputStack){
        //assigns the first card in the stack to be
        //the card to be returned
        Card lowCard = inputStack.get(0);
        //indexes through the stack and if the c card is a lower
        //value, it becomes the lowCard
        for(Card c: inputStack){
            if(c.getRank().value(14)<lowCard.getRank().value(14)){
                lowCard = c;
            }
        }
        return lowCard;
    }

    /**
     * This method returns the highest value card
     * of a particular suit
     * @param suit -- the suit in which to get the high card
     * @return -- returns the highest card of that suit
     */
    public Card getHighestInSuit(Suit suit){
        //this is our array list of cards of a specific suit
        ArrayList<Card> cardsOfSuit = new ArrayList<Card>();
        //index through the stack and pull out the cards of the right suit
        for(Card c: stack){
            if(c.getSuit()==suit){
                cardsOfSuit.add(c);
            }
        }
        //this is the card we will be returning
        Card theChosenOne = cardsOfSuit.get(0);
        //index through the array of suited cards and find the highest one
        for(Card c: cardsOfSuit){
            if(c.getRank().value(14)>theChosenOne.getRank().value(14)){
                theChosenOne = c;
            }
        }
        return theChosenOne;
    }
    /**
     * This method returns the lowest value card
     * of a particular suit
     * @param suit -- the suit in which to get the lowest card
     * @return -- returns the lowest card of that suit
     */
    public Card getLowestInSuit(Suit suit){
        //this is our array list of cards of a specific suit
        ArrayList<Card> cardsOfSuit = new ArrayList<Card>();
        //index through the stack and pull out the cards of the right suit
        for(Card c: stack){
            if(c.getSuit()==suit){
                cardsOfSuit.add(c);
            }
        }
        //this is the card we will be returning
        Card theChosenOne = cardsOfSuit.get(0);
        //index through the array of suited cards and find the lowest one
        for(Card c: cardsOfSuit){
            if(c.getRank().value(14)<theChosenOne.getRank().value(14)){
                theChosenOne = c;
            }
        }
        return theChosenOne;
    }

    /**
     * This method removes a card from the stack
     * @param beGone -- the card to be removed
     */
    public void remove(Card beGone){
        stack.remove(beGone);
    }
    /**
     * This method adds a card to the stack
     * @param addMe -- the card to be added
     */
    public void add(Card addMe){
        stack.add(addMe);
    }

    public ArrayList<Card> getStack(){
        return this.stack;
    }
}
