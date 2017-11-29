import java.util.Comparator;

/**
 * Created by ioluwayo. email: ioluwayo@gmail.com
 * Implements a comparator for sorting bid levels by price in descending order.
 * Orders with higher prices come first. [9,8,7,4....]
 */
public class BidLevelComparator implements Comparator<Long> {
    public int compare(Long price1, Long price2) {
        return price2.compareTo(price1);
    }
}
