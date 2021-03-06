package edu.up.cs301.Whist;

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Suit;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.R;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by Patrick Maloney on 11/7/17.
 * This is our Local Game. It controls all player interactions and logic.
 */

public class WhistLocalGame extends LocalGame {
    //the main GameState for the game containing all the information about players and cards
    private WhistGameState mainGameState;
    //the booleans that determine when to score tricks and start new rounds
    private boolean newTrick = false;
    private boolean newRound = false;

    /**
     * This is the basic constructor for the local game, it creates a new
     * gamestate and away we go
     */
    public WhistLocalGame(){
        mainGameState = new WhistGameState();
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
     * If either team has at least 7 points the game is over
     * @return
     */
    @Override
    protected String checkIfGameOver(){
        //
        if(mainGameState.team1Points>=7){
            WhistMainActivity.mySoundpool.play(WhistMainActivity.soundId[0], 1, 1, 1, 0, 1.0f);
            return ""+playerNames[0]+" and "+playerNames[2]+" win "+mainGameState.team1Points+" to "+mainGameState.team2Points+"!";

        }
        else if(mainGameState.team2Points>=7){
            WhistMainActivity.mySoundpool.play(WhistMainActivity.soundId[6], 1, 1, 1, 0, 1.0f);
            return ""+playerNames[1]+" and "+playerNames[3]+" win "+mainGameState.team2Points+" to "+mainGameState.team1Points+"!";
        }
        else return null;
    }

    /**
     * This method is called when the granding phase ends and all 4 players
     * have bid. It assigns the team that granded and assigns the lead to the
     * player to the right of the player who granded.
     */
    public void handleGranding(){
        //index through each card on the table and find if there are any high bids
        int grandedPlayer = 0;
        for(Card c: mainGameState.cardsInPlay.stack){
            if(c.getSuit().equals(Suit.Club)||c.getSuit().equals(Suit.Spade)){
                //if we find any high bids, the round is played high
                mainGameState.highGround = true;
                //set the turn to the player to the right of whoever granded
                mainGameState.turn = (grandedPlayer+1)%4;
                mainGameState.leadPlayer = (grandedPlayer+1)%4;
                break;//function of the loop is over. Break out
            }
            grandedPlayer++;
        }

        Log.i("GrandedPlayer: ",""+grandedPlayer);
        for(int i = grandedPlayer; i < 4; i++) mainGameState.cardsInPlay.stack.set(i, null);
        sendAllUpdatedState();
        try{
            Thread.sleep(3000);
        }catch (InterruptedException e){}
        mainGameState.grandingPhase = false;
        //assumes a low round at first
        mainGameState.highGround = false;
        //remove all the cards in play
        mainGameState.leadSuit = null;
        //assigns the lead suit as null to avoid errors in leadSuit
        mainGameState.leadSuit = null;
        //plays the audio for the beginning of a round.
        WhistMainActivity.mySoundpool.play(WhistMainActivity.soundId[4], 1, 1, 1, 0, 1.0f);
        //sleep to allow all threads to catch up.
        mainGameState.cardsInPlay.removeAll();
        sendAllUpdatedState();
    }
    /**
     * This method alters the state based on the action received
     */
    @Override
    protected boolean makeMove(GameAction action){
        if(!(action instanceof MoveAction)){
            return false;
        }
        if(mainGameState.cardsInPlay.getSize()==4){
            return false;
        }
        //safe casting of the sent in action into a MoveAction
        MoveAction theAction = (MoveAction) action;
        // get the index of the player making the move; return false
        int thisPlayerIdx = getPlayerIdx(theAction.getPlayer());
        if (thisPlayerIdx < 0) { // illegal player
            return false;
        }
        /////////////////////////////GRANDING/////////////////////////
        if(theAction.isBidAction()){
            if(!mainGameState.grandingPhase){
                //updates the state of the player making the wrong move
                sendUpdatedStateTo(action.getPlayer());
                //no granding allowed outside of granding phase!
                return false;
            }
            //we received a high bid action within granding phase
            else if(((BidAction) action).getCard().getSuit().equals(Suit.Club)||
                    ((BidAction) action).getCard().getSuit().equals(Suit.Spade)){
                ///////////grand the appropriate team//////////
                //if this bid action was from a member of team 2
                if(mainGameState.getTurn()%2==1&&!mainGameState.team1Granded){
                    mainGameState.team2Granded = true;
                }
                //else it was a granding action from team 1 and team 2 hasn't granded yet
                else if (!mainGameState.team2Granded){
                    mainGameState.team1Granded = true;
                }
                else{
                    //disregard this bid, a team has already granded
                }
                ////////////////////////One team is granded by now//////////////////
            }
            ///////////if a high bid was not received, no teams are granded////////

            //add the bid card to cards in play
            mainGameState.cardsInPlay.add(((BidAction) action).getCard());
            mainGameState.cardsByPlayerIdx[getPlayerIdx(theAction.getPlayer())] =((BidAction) action).getCard();
            //finally, increment the turn
            incrementTurn();
            //if we have reached all cards in play, handle it.
            if(mainGameState.cardsInPlay.getSize()==4){
                handleGranding();
            }
            //send updated states and return true
            sendAllUpdatedState();
            return true;
        }//end instanceof BidAction
        /////////////////////////////END  GRANDING/////////////////////////

        /////////////////////////////PLAYCARD ACTIONS/////////////////////////
        //check for an instance of PlayCardAction
        else if(theAction.isCardPlayAction()){
            if(mainGameState.grandingPhase){
                //updates the state of the player making the wrong move
                sendUpdatedStateTo(action.getPlayer());
                //disallow PlayCardActions during granding phase
                Log.i("MakeMove - badState",""+action.getPlayer().toString());
                return false;
            }
            Card playedCard = theAction.getCard();
            if(playedCard==null){
                return false;
            }
            //disallow doubled cards
            if(mainGameState.cardsPlayed.contains(playedCard)) return false;
            //if there are no cards in play yet, this is the lead card. Assigns leadsuit
            if(mainGameState.cardsInPlay.getSize()==0){
                mainGameState.leadSuit = playedCard.getSuit();
            }
            //fun stuff! Audio sounds!
            if(playedCard.getRank().value(14)==10){//thats a ten
                WhistMainActivity.mySoundpool.play(WhistMainActivity.soundId[5], 1, 1, 1, 0, 1.0f);
            }
            else if(playedCard.getSuit()!=mainGameState.leadSuit&&mainGameState.cardsPlayed.getSize()<40){//played out of suit, get some help!
                WhistMainActivity.mySoundpool.play(WhistMainActivity.soundId[2], 1, 1, 1, 0, 1.0f);
            }
            else if(playedCard.getRank().value(14)==14) {//played an ace! WoW!
                WhistMainActivity.mySoundpool.play(WhistMainActivity.soundId[1], 1, 1, 1, 0, 1.0f);
            }
            else if(playedCard.getRank().value(14)==3) {//played a 3...illuminati confirmed...
                WhistMainActivity.mySoundpool.play(WhistMainActivity.soundId[7], 1, 1, 1, 0, 1.0f);
            }
            else if(playedCard.getRank().value(14)==12) {//played a queen, is this the real life?
                WhistMainActivity.mySoundpool.play(WhistMainActivity.soundId[8], 1, 1, 1, 0, 1.0f);
            }

            //moves the played card onto the table and into the set of played cards
            mainGameState.cardsInPlay.add(playedCard);
            mainGameState.cardsByPlayerIdx[thisPlayerIdx] = playedCard;
            mainGameState.cardsPlayed.add(playedCard);
            //removes the card from the player's hand (regardles of CPU or human)
            mainGameState.playerHands[thisPlayerIdx].remove(playedCard);
            //lastly, set the turn
            incrementTurn();
            //after reaching 52 cards played, create a new round
            if(mainGameState.cardsPlayed.getSize()==52){
                newRound = true;
            }
            //after 4 moves, and not at the start of the round, set new trick to true
            else if (mainGameState.cardsPlayed.getSize()%4==0
                    &&mainGameState.cardsPlayed.getSize()!=0){
                newTrick = true;
            }
            sendAllUpdatedState();
            return true;
        }
        /////////////////////////////END PLAYCARD ACTIONS/////////////////////////
        return false;
    }

    /**
     *This method is called for each player to send them a new state
     */
    @Override
    protected void sendAllUpdatedState() {
        synchronized (mainGameState) {
            for (int i = 0; i < players.length; i++) {
                sendUpdatedStateTo(players[i]);
            }
        }
        Log.i("SentALLUpdatedStates",""+mainGameState.cardsInPlay.getSize());
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
    }
    /**
     * This method needs to null out all information that isn't supposed to be known
     * by certain players - TESTED--SUCCESS
     * @param p
     */
    @Override
    protected void sendUpdatedStateTo(GamePlayer p){
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
        if(mainGameState.grandingPhase) {
            for(int i = 0; i < censoredState.cardsInPlay.getSize(); i++) censoredState.cardsInPlay.stack.set(i,null);
        }
        //Log.i("mainState","Cards: "+mainGameState.cardsInPlay.getSize());
        Log.i("censoredState","Cards: "+censoredState.cardsInPlay.getSize());
        p.sendInfo(censoredState);
    }

    /**
     * This method scores the round
     */
    public void beginNewRound(){
        WhistMainActivity.mySoundpool.play(WhistMainActivity.soundId[3], 1, 1, 1, 0, 1.0f);
        //reset the new round boolean
        newRound = false;
        ///runs one last scoreTrick on the last trick in play
        scoreTrick();

        ///////handling points///////////
        //adding points to the team that won the most tricks in the round
        //if team 1 had more tricks, they get points
        if(mainGameState.team1WonTricks>mainGameState.team2WonTricks){
            //score is doubled for a team that wins without granding
            if(mainGameState.team2Granded){
                mainGameState.team1Points+=((mainGameState.team1WonTricks-6)*2);
            }
            else if(mainGameState.team1Granded){
                mainGameState.team1Points+=((mainGameState.team1WonTricks-6));
            }
            else{//it was a lowround
                mainGameState.team2Points+=((mainGameState.team1WonTricks-6));
            }
        }
        //else team 2 had more tricks, so they get points
        else{
            //score is doubled if team 2 wins when team 1 granded
            if(mainGameState.team1Granded){
                mainGameState.team2Points+=((mainGameState.team2WonTricks-6)*2);
            }
            else if(mainGameState.team2Granded){
                mainGameState.team2Points+=((mainGameState.team2WonTricks-6));
            }
            else{//it was a lowround
                mainGameState.team1Points+=((mainGameState.team2WonTricks-6));
            }
        }
        /////////////////////////////////Points Handled/////////////////////////////
        sendAllUpdatedState();
        //sleep for a little....shhhhh see the scored round
        try{
            Thread.sleep(2500);
        } catch (InterruptedException e){}

        //get a new deck and deal new hands
        mainGameState.mainDeck = new Deck();
        for(Hand h: mainGameState.playerHands){
            h.removeAll();
            for(int i = 0; i<13;i++){
                h.add(mainGameState.mainDeck.dealRandomCard());
            }
        }
        //sets back to granding phase
        mainGameState.grandingPhase = true;
        //clears out the cards played and cards in play
        mainGameState.cardsPlayed.removeAll();
        mainGameState.cardsInPlay.removeAll();
        for(int i = 0; i < 4; i++) mainGameState.cardsByPlayerIdx[i] = null;
        //resets the turn to zero
        mainGameState.turn = 0;
        //resets the grands to false
        mainGameState.team1Granded = false;
        mainGameState.team2Granded = false;
        //resets the tricks back to zero
        mainGameState.team1WonTricks = 0;
        mainGameState.team2WonTricks = 0;
        //reset the leadPlayer to zero (the "dealer)
        mainGameState.leadPlayer = 0;
        //reset the lead suit to null
        mainGameState.leadSuit = null;

        sendAllUpdatedState();

    }

    /**
     * This method is called anytime the turn reaches a multiple of 4
     */
    public void scoreTrick(){
        //sets new trick to false
        newTrick = false;
        //determine which card and player won the trick
        //establish the starting card to increment up from
        Card winningCard = Card.fromString("2C");
        Integer winningPlayerIdx = 0;
        //increment through the cards on the table and find the winning card and player number
        synchronized (winningPlayerIdx) {
            for (int i = 0; i < 4; i++) {
                if ((winningCard.getRank().value(14)<=
                        mainGameState.cardsByPlayerIdx[i].getRank().value(14))&&
                        (mainGameState.cardsByPlayerIdx[i].getSuit().equals(mainGameState.leadSuit))) {
                    winningCard = mainGameState.cardsByPlayerIdx[i];
                    winningPlayerIdx = i;
                }
            }
            //if the winning player was on team 2 (meaning it was either player 2 or 4)
            //add to their wonTricks
            Log.i("winningCard",""+winningCard.toString());
            if (winningPlayerIdx % 2 == 1) {
                mainGameState.team2WonTricks++;
            }
            //add to other team's wonTricks
            else {
                mainGameState.team1WonTricks++;
            }
        }

        sendAllUpdatedState();
        //sleep to let the player see the completed trick
        try{
            Thread.sleep(3000);
        }catch(InterruptedException e){}
        //clears the cards in play
        mainGameState.cardsInPlay.removeAll();
        //sets the turn to establish who  is leading the next trick
        mainGameState.turn = winningPlayerIdx;
        //sets the leadPlayer to the winningPlayerIdx
        mainGameState.leadPlayer = winningPlayerIdx;
        sendAllUpdatedState();

    }

    /**
     * This method does a special combination
     * to avoid keeping the turn 0-3
     */
    public void incrementTurn(){
        //increment turn
        mainGameState.turn ++;
        //use modular arithmetic to keep value 0-3
        mainGameState.turn%=4;
    }

}

