package edu.up.cs301.Whist;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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

/**
 * Created by PatrickMaloney on 11/7/17.
 */

public class WhistHumanPlayer extends GameHumanPlayer implements Animator{
    private GameMainActivity myActivity;
    private int backgroundColor = Color.BLACK;
    private Hand myHand = new Hand();
    private WhistGameState savedState;
    private AnimationSurface Tablesurface;

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



        // read in the card images
        Card.initImages(activity);

        // if the state is not null, simulate having just received the state so that
        // any state-related processing is done
        if (savedState != null) {
            receiveInfo(savedState);
        }
    }

    @Override
    public void receiveInfo(GameInfo info){
        info = (WhistGameState) info;

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
        //if team 1 contains our player check to see if it also contains otherplayer
        if(savedState.teams[0].hasPlayer(this)){
            if(savedState.teams[0].hasPlayer(otherplayer)){
                return true;
            }
            else return false;
        }
        //if team 2 contains our player check to see if it also contains otherplayer
        else if (savedState.teams[1].hasPlayer(this)){
            if(savedState.teams[1].hasPlayer(otherplayer)){
                return true;
            }
            else return false;
        }
        else return false;
    }

    public void tick(Canvas g){
        // ignore if we have not yet received the game state
        if (savedState == null) return;

        // get the height and width of the animation surface
        int height = Tablesurface.getHeight();
        int width = Tablesurface.getWidth();
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
        /*
        RectF myTopCardLoc = thisPlayerTopCardLocation();

        RectF middleTopCardLoc = middlePileTopCardLocation();
        if (myTopCardLoc.contains(x, y)) {
            // it's on my pile: we're playing a card: send action to
            // the game
            game.sendAction(new SJPlayAction(this));
        }
        else if (middleTopCardLoc.contains(x, y)) {
            // it's on the middlel pile: we're slapping a card: send
            // action to the game
            game.sendAction(new SJSlapAction(this));
        }
        else {
            // illegal touch-location: flash for 1/20 second
            Tablesurface.flash(Color.RED, 50);
        }
        */
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
}
