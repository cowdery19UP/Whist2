package WhistTesting;

import org.junit.Test;

import edu.up.cs301.Whist.Deck;
import edu.up.cs301.card.Card;
import edu.up.cs301.card.Suit;

import static org.junit.Assert.assertTrue;

/**
 * Created by Samuel on 11/9/2017.
 */

public class Test1 {
    //TODO why does the tester report "Empty test suite" for this class??

    @Test
    public void testDeck() throws Exception{
        Deck myDeck = new Deck();
        Card aceClubs = Card.fromString("AC");
        Card highClub = myDeck.getHighestInSuit(Suit.Club);
        assertTrue("highClub!",aceClubs.equals(highClub));
    }
    @Test
    public void testGameState(){

    }
}
