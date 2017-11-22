package edu.up.cs301.Whist;

import android.app.ActionBar;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import edu.up.cs301.animation.AnimationSurface;
import edu.up.cs301.animation.Animator;
import edu.up.cs301.card.Card;
import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.util.ArrayList;


/**
 * Created by PatrickMaloney on 11/7/17.
 */

public class WhistHumanPlayer extends GameHumanPlayer implements Animator, OnClickListener, OnSeekBarChangeListener {
    //the main activity for the player
    private GameMainActivity myActivity;
    //the unused background color
    private int backgroundColor = Color.BLACK;
    //the hand of the player with all cards
    private Hand myHand = new Hand();
    //the selected card of the player
    public Card selectedCard;
    //the saved state to be accessed by the player
    private WhistGameState savedState;
    //the animation surface for the user interface
    private AnimationSurface Tablesurface;
    //the widgets to the gui
    private SeekBar handSeekBar;
    private Button playCardButton;

    private RectF[] handSpots = new RectF[14];
    private RectF[] tableSpots = new RectF[4];




    public WhistHumanPlayer(String name){
        super(name);

    }

    public void setAsGui(GameMainActivity activity){

        // remember the activity
        this.myActivity = activity;

        // Load the layout resource for our GUI
        activity.setContentView(R.layout.whist_layout);

        // link the animator (this object) to the animation surface
        Tablesurface = (AnimationSurface) myActivity
                .findViewById(R.id.animationSurface);
        Tablesurface.setAnimator(this);

        handSeekBar = (SeekBar) myActivity.findViewById(R.id.hand_seek_bar);
        handSeekBar.setOnSeekBarChangeListener(this);
        handSeekBar.setProgress(50);

        playCardButton = (Button) myActivity.findViewById(R.id.play_card_button);
        playCardButton.setOnClickListener(this);

        // read in the card images
        Card.initImages(activity);


        // if the state is not null, simulate having just received the state so that
        // any state-related processing is done
        if (savedState != null) {
            receiveInfo(savedState);
        }

    }

    /**
     * when the game calls sendInfoTo...this method is called
     *
     * @param info
     */
    @Override
    public void receiveInfo(GameInfo info){
        //check if the info is the correct type
        if(!(info instanceof WhistGameState)){
            return;
        }
        //check if state is null
        if(info==null){
            return;
        }
        //updates the player's gamestate
        savedState = (WhistGameState) info;
        //updates the player hand from the new gamestate
        myHand = savedState.getHand();
        myHand.organizeBySuit();
        if(myHand.getSize()==13){
            selectedCard = myHand.getCardByIndex(myHand.getSize()/2);
        }



    }

    @Override
    public View getTopView(){

        //return activity.findViewById(R.id.top_gui_layout);
        return null;
    }
    //returns the player hand
    public Hand getMyHand(){ return myHand;}

    /**
     * This method checks to see if this player is on the same
     * team as the other player fed into the method
     * @param otherplayer -- the other player to compare this player to
     * @return -- returns true if the players are on the same team
     */
    public boolean sameTeam(GamePlayer otherplayer){

         return false;
    }


    public void tick(Canvas g){
    //checks to make sure there is a state to pull information from
        if(savedState!=null) {
            //set rectangles for hand and table spots
            setHandSpots();
            setTableSpots(playerNum);
            //drawing the table on the GUI
            Paint tableIn = new Paint();
            Paint tableOut = new Paint();
            tableOut.setColor(Color.rgb(42, 111, 0));
            tableIn.setColor(Color.rgb(104, 69, 0));
            RectF rectIn = new RectF(40, 50, Tablesurface.getWidth() - 40, (int) (Tablesurface.getBottom() * 0.69));
            RectF rectOut = new RectF(70, 80, Tablesurface.getWidth() - 70, (int) (Tablesurface.getBottom() * 0.66));
            g.drawOval(rectIn, tableIn);
            g.drawOval(rectOut, tableOut);
            //drawing text on the GUI
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            g.drawText("Overall Scores", 10, 40, paint);
            paint.setTextSize(35);

            g.drawText("Team 1: " + savedState.team1Points, 10, 75, paint);
            g.drawText("Team 2: " + savedState.team2Points, 10, 110, paint);
            paint.setTextSize(40);
            g.drawText("Current Tricks", Tablesurface.getWidth() - 260, 40, paint);
            paint.setTextSize(35);
            g.drawText("Team 1: " + savedState.team1WonTricks, Tablesurface.getWidth() - 260, 75, paint);
            g.drawText("Team 2: " + savedState.team2WonTricks, Tablesurface.getWidth() - 260, 110, paint);

            //in order to make the GUI more user friendly, I addded a handler to make the playCard button
            //light up green when it is this player's turn to play

            Handler refresh = new Handler(Looper.getMainLooper());
            refresh.post(new Runnable() {
                public void run() {
                    //if the leadSuit has been established and we have the suit
                    if (savedState.cardsInPlay.getSize() != 0) {
                        if (savedState.getTurn() == playerNum && myHand.hasCardInSuit(savedState.leadSuit)) {
                            //button turn green to indicate legal card play
                            if (selectedCard != null) {
                                if (selectedCard.getSuit().equals(savedState.leadSuit)) {
                                    playCardButton.setBackgroundColor(Color.GREEN);
                                    playCardButton.setEnabled(true);
                                } else {
                                    playCardButton.setBackgroundColor(Color.DKGRAY);
                                    playCardButton.setEnabled(false);
                                }
                            } else {
                                playCardButton.setBackgroundColor(Color.GREEN);
                                playCardButton.setEnabled(false);
                            }

                        }
                        //if we do not have the suit of the leadSuit, set button to a darker, sadder green
                        else if (savedState.getTurn() % 4 == playerNum && !myHand.hasCardInSuit(savedState.leadSuit)) {
                            playCardButton.setBackgroundColor(Color.rgb(34, 139, 34));
                            playCardButton.setEnabled(true);
                        }
                        //for all other times, it is not our turn and it is not legal to play
                        else {
                            playCardButton.setBackgroundColor(Color.DKGRAY);
                            playCardButton.setEnabled(false);
                        }
                    }
                    //there are no cards in play, the hand has just started
                    else {
                        //if it is our turn, all cards will be good to lead
                        if (savedState.getTurn() % 4 == playerNum) {
                            playCardButton.setBackgroundColor(Color.GREEN);
                            playCardButton.setEnabled(true);
                        }
                        //it is not our turn, do not play
                        else {
                            playCardButton.setBackgroundColor(Color.DKGRAY);
                            playCardButton.setEnabled(false);
                        }
                    }

                }
            });

            //assigns and paints the cards in play that will appear on the table
            if (savedState.cardsInPlay != null) {
                synchronized (g) {
                    setTableDisplay(g);
                }
            }

            drawCard(g, handSpots[13], selectedCard);

            for (int i = 0; i < myHand.getSize(); i++) {
                drawCard(g, handSpots[12 - i], myHand.getCardByIndex(i));
            }
        }

    }
    /**
     * @return
     * 		the amimation interval, in milliseconds
     */
    public int interval() {
        // 1/20 of a second
        return 50;
    }

    /**
     * @return
     * 		the background color
     */
    public int backgroundColor() {
        return backgroundColor;
    }

    /**
     * @return
     * 		whether the animation should be paused
     */
    public boolean doPause() {
        return false;
    }

    /**
     * @return
     * 		whether the animation should be terminated
     */
    public boolean doQuit() {
        return false;
    }

    /**
     * callback method: we have received a touch on the animation surface
     *
     * @param event
     * 		the motion-event
     */
    public void onTouch(MotionEvent event) {

        // ignore everything except down-touch events
        if (event.getAction() != MotionEvent.ACTION_DOWN) return;

        // get the location of the touch on the surface
        int x = (int) event.getX();
        int y = (int) event.getY();

        // determine whether the touch occurred on the top-card of either
        // the player's pile or the middle pile

        //TODO this code is copied over from slapjack. it needs to be fixed to our game

        // illegal touch-location: flash for 1/20 second
        //Tablesurface.flash(Color.WHITE, 50);


    }

    /**
     * draws a card on the canvas; if the card is null, draw a card-back
     *
     * @param g
     * 		the canvas object
     * @param rect
     * 		a rectangle defining the location to draw the card
     * @param c
     * 		the card to draw; if null, a card-back is drawn
     */
    private static void drawCard(Canvas g, RectF rect, Card c) {
        if (c == null) {
            // null: draw a card-back, consisting of a blue card
            // with a white line near the border. We implement this
            // by drawing 3 concentric rectangles:
            // - blue, full-size
            // - white, slightly smaller
            // - blue, even slightly smaller
            Paint white = new Paint();
            white.setColor(Color.BLACK);
            Paint blue = new Paint();
            blue.setColor(Color.BLACK);
            RectF inner1 = scaledBy(rect, 0.96f); // scaled by 96%
            RectF inner2 = scaledBy(rect, 0.98f); // scaled by 98%
            g.drawRect(rect, blue); // outer rectangle: blue
            g.drawRect(inner2, white); // middle rectangle: white
            g.drawRect(inner1, blue); // inner rectangle: blue
        }
        else {
            // just draw the card
            c.drawOn(g, rect);
        }
    }

    private void setTableDisplay(Canvas g){
           int Startspot = savedState.leadPlayer;
           ArrayList<Card> stackCopy = (ArrayList<Card>)savedState.cardsInPlay.stack.clone();
           for (Card c : stackCopy) {
               drawCard(g, tableSpots[Startspot % 4], c);
               Startspot++;
           }
    }

    private void setHandSpots(){
        int middle = Tablesurface.getWidth()/2;
        int top = (Tablesurface.getHeight()/2)-133+450;
        int bottom = (Tablesurface.getHeight()/2)+133+450;

        handSpots[13] = new RectF(middle+700, top, middle + 900, bottom);
        for(int i = 0; i<=12;i++){
            handSpots[i] = new RectF(middle-100-(-350+(i*100)),top,middle+100-(-350+(i*100)),bottom);
        }
        /*
        for(int i = 0; i<12; i++){
            handSpots[i+13] = new RectF(middle-100+(450+(i*30)),top,middle+100+(450+(i*30)),bottom);

        }
*/
    }
    private void setTableSpots(int mySpot){
        //the spot in front of the human player
        tableSpots[mySpot] = new RectF((Tablesurface.getWidth()/2)-100,(Tablesurface.getHeight()/2)-133,
                (Tablesurface.getWidth()/2)+100,(Tablesurface.getHeight()/2)+133);
        //the spot across from the humanPlayer
        tableSpots[(mySpot+2)%4] = new RectF((Tablesurface.getWidth()/2)-100,(Tablesurface.getHeight()/2)-133-330,
                (Tablesurface.getWidth()/2)+100,(Tablesurface.getHeight()/2)+133-330);
        //the spot to the left of the human player
        tableSpots[(mySpot+3)%4] = new RectF((Tablesurface.getWidth()/2)-100-500,(Tablesurface.getHeight()/2)-133-150,
                (Tablesurface.getWidth()/2)+100-500,(Tablesurface.getHeight()/2)+133-150);
        //the spot to the right of the human player
        tableSpots[(mySpot+1)%4] = new RectF((Tablesurface.getWidth()/2)-100+500,(Tablesurface.getHeight()/2)-133-150,
                (Tablesurface.getWidth()/2)+100+500,(Tablesurface.getHeight()/2)+133-150);
    }

    /**
     * scales a rectangle, moving all edges with respect to its center
     *
     * @param rect
     * 		the original rectangle
     * @param factor
     * 		the scaling factor
     * @return
     * 		the scaled rectangle
     */
    private static RectF scaledBy(RectF rect, float factor) {
        // compute the edge locations of the original rectangle, but with
        // the middle of the rectangle moved to the origin
        float midX = (rect.left+rect.right)/2;
        float midY = (rect.top+rect.bottom)/2;
        float left = rect.left-midX;
        float right = rect.right-midX;
        float top = rect.top-midY;
        float bottom = rect.bottom-midY;

        // scale each side; move back so that center is in original location
        left = left*factor + midX;
        right = right*factor + midX;
        top = top*factor + midY;
        bottom = bottom*factor + midY;

        // create/return the new rectangle
        return new RectF(left, top, right, bottom);

    }

    public void onClick(View v){
        if(v instanceof Button) {
            Button b = (Button) v;
            if (selectedCard != null) {
                game.sendAction(new PlayCardAction(this, selectedCard));
                b.setBackgroundColor(Color.DKGRAY);
                selectedCard = null;
            }
            else flash(Color.RED,1000);
        }

    }

    public void onStartTrackingTouch(SeekBar sb){
        Log.i("trackingtouch","money");
    }

    public void onStopTrackingTouch(SeekBar sb){

    }

    public void onProgressChanged(SeekBar sb, int progress, boolean fromUser){
        Log.i("progress int:",""+progress);
        if(myHand.getSize()==0){
            flash(Color.RED,3000);

        }
        else {
            float percent = (float)(progress) / 100;
            float flIndex = (myHand.getSize()-1) * percent;
            selectedCard = myHand.getCardByIndex((int) flIndex);
        }

    }
}
