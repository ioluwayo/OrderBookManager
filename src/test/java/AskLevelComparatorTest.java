import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ioluwayo. email: ioluwayo@gmail.com
 */
public class AskLevelComparatorTest {
    @Test
    public void compare() throws Exception {
        long price1 = 10;
        long price2 = 20;
        long price3 = 10;
        AskLevelComparator askLevelComparator = new AskLevelComparator();
        assertTrue(askLevelComparator.compare(price1, price2) < 0); //-ve, price1 is less than price2
        assertTrue(askLevelComparator.compare(price2, price1) > 0); //+ve, if price2 is greater than price1
        assertTrue(askLevelComparator.compare(price1, price3) == 0); // 0, price1 is equal to price3
    }

}