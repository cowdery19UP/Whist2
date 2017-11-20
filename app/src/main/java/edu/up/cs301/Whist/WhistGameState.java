package edu.up.cs301.Whist;


import android.util.Log;

import edu.up.cs301.card.Suit;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.infoMsg.GameState;

/**
 * Created by Samuel on 11/9/2017.
 */

public class WhistGameState extends GameState {
    //turn integer incremented from 0-52 in a round
    public int turn = 0;
    //a cardStack of the cards in play on the TABLE
    public CardStack cardsInPlay;
    //a card stack of the cards that have been played in the ENTIRE ROUND
    public CardStack cardsPlayed;
    //an array of hands one for each player
    public Hand[] playerHands = new Hand[4];
    //the cardStack of hot cards
    public CardStack hotCards;
    //the main deck the game is using
    public Deck mainDeck;
    //the leadingSuit of the trick
    public Suit leadSuit;
    //the index of the leading player
    public int leadPlayer;
    //the array of 2 teams in the game
    public Team[] teams = new Team[2];
    ///////////////substitute for Team Class////////////
    public int team1WonTricks = 0;
    public int team2WonTricks = 0;
    public int team1Points = 0;
    public int team2Points = 0;
    public boolean team1Granded = false;
    ///////////////substitute for Team Class/////////////

    public WhistGameState(){
        //initial gameState
        Log.i("CreatedNewState","new");
        cardsInPlay = new CardStack();
        cardsPlayed = new CardStack();
        hotCards = new CardStack();
        mainDeck  = new Deck();
        //deals the cards to the playerHands
        ///////lesson learned: cannot use for each loops to instantiate items in an array./////
        for(int i = 0; i<playerHands.length;i++){
            playerHands[i] = new Hand();
            for(int j = 0; j<13; j++){
                playerHands[i].add(mainDeck.dealRandomCard());
            }
        }
    }
    public GameInfo sendGameState(){
        return this;
    }

    public int getTurn(){return turn;}

    public Hand getHand(){
        for(Hand d: playerHands){
            if(d!=null){
                return d;
            }
        }
        return null;
    }


}
