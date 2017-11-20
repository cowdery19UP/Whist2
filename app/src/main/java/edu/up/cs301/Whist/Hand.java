package edu.up.cs301.Whist;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Suit;

/**
 * Created by Samuel on 11/7/2017.
 * Hand is given to each player, it contains 13 cards at the
 * start of play and 0 at the end of a round
 */

public class Hand extends CardStack {

    //these 4 arrays hold the different suits to make it
    //easier to organize them
    ///////////////////////////////////////////////////
    private ArrayList<Card> hearts = new ArrayList<Card>();
    private ArrayList<Card> clubs = new ArrayList<Card>();
    private ArrayList<Card> diamonds = new ArrayList<Card>();
    private ArrayList<Card> spades = new ArrayList<Card>();
    ///////////////////////////////////////////////////////
    //this array of cards is used in Patrick's mergeSort function
    private static Card[] T = new Card[52];
    /**
     * The constructor for the Hand
     */
    public Hand(){

    }

    /**
     * This method organizes the cards in the hand by their suit
     *  by using Patrick's mergeSort function
     */
    public void organizeBySuit(){
        for(Card c: stack){
            switch(c.getSuit()){
                case Club: clubs.add(c);
                    break;
                case Diamond: diamonds.add(c);
                    break;
                case Heart: hearts.add(c);
                    break;
                case Spade: spades.add(c);
            }
        }
        mergeSortCards(diamonds,0,diamonds.size());
        mergeSortCards(clubs,0,clubs.size());
        mergeSortCards(spades,0,spades.size());
        mergeSortCards(hearts,0,hearts.size());
        //clear the stack of cards to null
        stack.clear();
        //rebuild the stack from the ashes
        for(Card c: clubs){
            stack.add(c);
        }
        for(Card c: hearts){
            stack.add(c);
        }
        for(Card c: spades){
            stack.add(c);
        }
        for(Card c: diamonds){
            stack.add(c);
        }

    }



    /**
     * This is Patrick's high-minded mergeSort function for cards. Eat it.
     * @param list -- the arraylist of cards to be sorted
     * @param lo -- the lowest parameter of length
     * @param hi -- the highest parameter of length
     */
    private void mergeSortCards(ArrayList<Card> list, int lo, int hi){
        int mid = lo+hi/2;

        mergeSortCards(list, lo, mid);
        mergeSortCards(list, mid+1, hi);

        merge(list, lo, mid, hi);
    }

    /**
     * this is the rest of Patrick's high-minded mergsort for cards
     * @param list -- the arrayList of cards to be sorted
     * @param lo -- the low parameter of length
     * @param mid -- the mid parameter of length
     * @param hi -- the high paremeter of length
     */
    private void merge(ArrayList<Card> list, int lo, int mid, int hi){
        int i = lo;
        int j = mid+1;
        int k = lo;

        while(i<=mid || j<=hi){
            if(i>mid) T[k++] = list.get(j++);
            else if(j>hi) T[k++] = list.get(i++);
            else if(list.get(i).getRank().value(14) < list.get(j).getRank().value(14))T[k++] = list.get(i++);
            else T[k++] = list.get(j++);
        }

        for(i = lo; i<=hi; i++)list.add(i, T[i]);
    }
}
