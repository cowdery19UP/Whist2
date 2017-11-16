package edu.up.cs301.Whist;


import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.infoMsg.GameState;

/**
 * Created by Samuel on 11/9/2017.
 */

public class WhistGameState extends GameState {
    public int turn;
    public CardStack cardsInPlay = new CardStack();
    public CardStack cardsPlayed = new CardStack();
    private Hand[] playerHands = new Hand[4];
    public Team[] teams = new Team[2];
    public CardStack hotCards = new CardStack();
    public Deck mainDeck = new Deck();


    public GameInfo sendGameState(){
        return this;
    }

    public int getTurn(){return turn;}

    public Hand getHand(int idx){
        return playerHands[idx];
    }

}
