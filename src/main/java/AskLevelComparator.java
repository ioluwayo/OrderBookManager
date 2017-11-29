import java.util.Comparator;

/**
 * Created by ioluwayo. email: ioluwayo@gmail.com
 *
 * Implements a comparator for sorting ask levels by price in ascending order.
 * [1,2,3,4....]
 * <p>
 * Note:    Though this comparator follows the natural ordering of prices,
 * for maintainability, I think it is necessary to explicitly specify a comparator.
 */
public class AskLevelComparator implements Comparator<Long> {
    public int compare(Long price1, Long price2) {
        return price1.compareTo(price2);
    }
}
