package edu.up.cs301.Whist;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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

    private RectF[] handSpots = new RectF[25];




    public WhistHumanPlayer(String name){

        super(name);
        //testHand
        myHand.add(Card.fromString("2C"));
        myHand.add(Card.fromString("3D"));
        myHand.add(Card.fromString("4D"));
        myHand.add(Card.fromString("2S"));
        myHand.add(Card.fromString("QC"));
        myHand.add(Card.fromString("KD"));
        myHand.add(Card.fromString("2C"));
        myHand.add(Card.fromString("6H"));
        myHand.add(Card.fromString("5C"));
        myHand.add(Card.fromString("2S"));
        myHand.add(Card.fromString("2C"));
        myHand.add(Card.fromString("2C"));
        myHand.add(Card.fromString("2H"));
        myHand.add(Card.fromString("AS"));
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
        myHand = savedState.getHand(playerNum);



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
        //set cards in hand spots
        setHandSpots();

        Paint tableIn = new Paint();
        Paint tableOut = new Paint();
        tableOut.setColor(Color.rgb(42,111,0));
        tableIn.setColor(Color.rgb(104,69,0));
        RectF rectIn = new RectF(180,70,1840,780);
        RectF rectOut = new RectF(200,90,1820,760);

        g.drawOval(rectIn,tableIn);
        g.drawOval(rectOut,tableOut);
        //TODO need to draw cards onto 4 different RectF's for all 4 Blayers
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        g.drawText("Overall Scores", 10, 40, paint);
        paint.setTextSize(35);
        g.drawText("Team 1:", 10, 75, paint);
        g.drawText("Team 2:", 10, 110, paint);

        paint.setTextSize(40);
        g.drawText("Current Tricks", 1750, 40, paint);
        paint.setTextSize(35);
        g.drawText("Team 1:", 1750, 75, paint);
        g.drawText("Team 2:", 1750, 110, paint);


        // get the height and width of the animation surface
        RectF myCardSpot = new RectF((Tablesurface.getWidth()/2)-100,(Tablesurface.getHeight()/2)-133,
                (Tablesurface.getWidth()/2)+100,(Tablesurface.getHeight()/2)+133);
        RectF playerTopSpot = new RectF((Tablesurface.getWidth()/2)-100,(Tablesurface.getHeight()/2)-133-330,
                (Tablesurface.getWidth()/2)+100,(Tablesurface.getHeight()/2)+133-330);
        RectF playerRightSpot = new RectF((Tablesurface.getWidth()/2)-100-500,(Tablesurface.getHeight()/2)-133-150,
                (Tablesurface.getWidth()/2)+100-500,(Tablesurface.getHeight()/2)+133-150);
        RectF playerLeftSpot = new RectF((Tablesurface.getWidth()/2)-100+500,(Tablesurface.getHeight()/2)-133-150,
                (Tablesurface.getWidth()/2)+100+500,(Tablesurface.getHeight()/2)+133-150);
        Card dcl = Card.fromString("2C");
        Card dl = Card.fromString("AH");
        Card dd = Card.fromString("QH");
        drawCard(g,myCardSpot,dcl);
        drawCard(g,playerTopSpot,dcl);
        drawCard(g,playerRightSpot,dl);
        drawCard(g,playerLeftSpot,dl);

        for(int i = 11; i>=0;i--){
            drawCard(g,handSpots[i],dcl);
        }
        drawCard(g,handSpots[12],selectedCard);
        for(int i = 24;i>=13;i--){
            drawCard(g,handSpots[i],dd);
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
        Tablesurface.flash(Color.WHITE, 50);


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
            white.setColor(Color.WHITE);
            Paint blue = new Paint();
            blue.setColor(Color.BLUE);
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

    private void setHandSpots(){
        int middle = Tablesurface.getWidth()/2;
        int top = (Tablesurface.getHeight()/2)-133+400;
        int bottom = (Tablesurface.getHeight()/2)+133+400;

        handSpots[12]  = new RectF(middle-100,top,middle+100,bottom);

        for(int i = 0; i<12;i++){
            handSpots[i] = new RectF(middle-100-(450+(i*30)),top,middle+100-(450+(i*30)),bottom);
        }
        for(int i = 0; i<12; i++){
            handSpots[i+13] = new RectF(middle-100+(450+(i*30)),top,middle+100+(450+(i*30)),bottom);

        }

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
            }
        }

    }

    public void onStartTrackingTouch(SeekBar sb){

    }

    public void onStopTrackingTouch(SeekBar sb){

    }

    public void onProgressChanged(SeekBar sb, int progress, boolean fromUser){
        if(myHand==null){flash(Color.RED,300);}
        else {
            float percent = progress / 100;
            float flIndex = myHand.getSize() * percent;
            //Log.i("Percent",""+percent);
            selectedCard = myHand.getCardByIndex((int) flIndex);
        }

    }
}
