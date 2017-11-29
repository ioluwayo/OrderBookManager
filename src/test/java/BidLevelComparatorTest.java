import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by ioluwayo. email: ioluwayo@gmail.com
 */
public class BidLevelComparatorTest {
    @Test
    public void compare() throws Exception {
        long price1 = 10;
        long price2 = 20;
        long price3 = 10;
        BidLevelComparator bidLevelComparator = new BidLevelComparator();

        //check that higher prices will come before lower prices
        assertTrue(bidLevelComparator.compare(price1, price2) > 0);//-ve, price1 is less than price2
        assertTrue(bidLevelComparator.compare(price2, price1) < 0);//+ve, price2 is greater than price 1
        assertTrue(bidLevelComparator.compare(price1, price3) == 0);//0, price1 is equal to price3
    }

}