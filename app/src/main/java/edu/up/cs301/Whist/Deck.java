package edu.up.cs301.Whist;

import java.util.ArrayList;

import edu.up.cs301.card.Card;

/**
 * Created by Samuel on 11/9/2017.
 * This class serves as a holder for the 52 cards
 */

public class Deck extends CardStack {
    /**
     * This constructor instantiates a new deck of 52 cards
     */
    public Deck(){
        // creates a new deck with a stack of all 52 cards
        // using Professor Vegdahl's Card methods
        for (char s : "SHDC".toCharArray()) {
            for (char r : "KQJT98765432A".toCharArray()) {
                stack.add(Card.fromString(""+r+s));
            }
        }
    }//ctor


    /**
     * This method deals a random card from the cardstack
     * contained in Deck from a random index from 0-stack.size()
     *
     */
    public Card dealRandomCard(){
        int idx = (int)(Math.random()*stack.size()); //get a random index
        Card toReturn = stack.get(idx); //get the card at random index
        stack.remove(stack.get(idx)); //remove that card from the stack
        return toReturn; //return the card
    }


    public boolean contains(Card c){
        for(Card a: this.stack){
            if(c.getRank() == a.getRank() && c.getSuit() == a.getSuit()) return true; //if we find the card, return true
        }
        return false; //else, return false
    }
}
