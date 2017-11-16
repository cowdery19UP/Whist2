package edu.up.cs301.Whist;

import android.util.Log;

import org.junit.Test;

import edu.up.cs301.card.Card;

import static org.junit.Assert.*;

/**
 * Created by PatrickMaloney on 11/15/17.
 */
public class WhistHumanPlayerTest {
    @Test
    public void testProgressPercent(){
        WhistHumanPlayer p = new WhistHumanPlayer("name");
        p.getMyHand().add(Card.fromString("2c"));
        p.getMyHand().add(Card.fromString("3c"));
        p.getMyHand().add(Card.fromString("4c"));
        p.getMyHand().add(Card.fromString("5c"));
        p.getMyHand().add(Card.fromString("6c"));

        p.onProgressChanged(null,60,true );

        assertTrue("wrong card selected"+p.selectedCard.shortName(), p.selectedCard.equals(Card.fromString("5c")));
    }
}