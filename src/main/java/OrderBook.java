import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by ioluwayo. email: ioluwayo@gmail.com
 *
 * An OrderBook consists of 2 sides. bids and asks
 * bids is the set of orders from buyers
 * asks is the set of orders from sellers
 * <p>
 * Each side is an ordered Map where the price is used as key and the value is an OrderList
 * of orders that have the same price. Price -> OrderList This makes up a level.
 * <p>
 * Both Maps are ordered using the price/level as key. Comparators for both bids and asks have been implemented based
 * on the requirements. bids are sorted in descending order of price level and asks are sorted in ascending order.
 * <p>
 * The OrderList class encapsulates the list of orders into an object that provides efficient
 * lookup for total volume and quantity of orders in the list.
 */
public class OrderBook {

    /**
     * Ordered map of price -> OrderList. Guarantees O(log(n)) worst case time for all operations.
     * Entries are sorted in descending order of price.
     */
    private TreeMap<Long, OrderList> bids;

    /**
     * Ordered map of price -> OrderList. Guarantees O(log(n)) worst case time for all operations.
     * Entries are sorted in ascending order of price.
     */
    private TreeMap<Long, OrderList> asks;

    /**
     * Quick lookup map for the price/level of an order given its Id
     */
    private HashMap<String, Long> priceByOrderIdMap;

    /**
     * Quick lookup of the side of an order given its Id.
     */
    private HashMap<String, Side> sideByOrderIdMap;

    /**
     * Preferred constructor.
     * Ideally, to instantiate an OrderBook an instrument must be specified.
     */
    public OrderBook() {
        bids = new TreeMap<>(new BidLevelComparator()); // sort Map in descending order of Key<price>
        asks = new TreeMap<>(new AskLevelComparator()); // sort Map in ascending order of Key<price>
        priceByOrderIdMap = new HashMap<>();
        sideByOrderIdMap = new HashMap<>();
    }

    public TreeMap<Long, OrderList> getBids() {
        return bids;
    }

    public TreeMap<Long, OrderList> getAsks() {
        return asks;
    }

    /**
     * @return map for retrieving the price/level for an order using the order id as key
     */
    public HashMap<String, Long> getPriceByOrderIdMap() {
        return priceByOrderIdMap;
    }

    /**
     * @return map for retrieving the side of an order using the order id as key
     */
    public HashMap<String, Side> getSideByOrderIdMap() {
        return sideByOrderIdMap;
    }

    /* To prevent errors, fields should not be explicitly set */
    private void setBids(TreeMap<Long, OrderList> bids) {
        this.bids = bids;
    }

    private void setAsks(TreeMap<Long, OrderList> asks) {
        this.asks = asks;
    }

    private void setPriceByOrderIdMap(HashMap<String, Long> priceByOrderIdMap) {
        this.priceByOrderIdMap = priceByOrderIdMap;
    }

    private void setSideByOrderIdMap(HashMap<String, Side> sideByOrderIdMap) {
        this.sideByOrderIdMap = sideByOrderIdMap;
    }

    /**
     * Adds the given order to the either the bids or asks TreeMap based on the side of the order.
     * Registers the id of this order in the priceByOrderId and sideByOrderId maps.
     *
     * @param order new order to add
     */
    public void addOrder(Order order) {
        long price = order.getPrice(); // get the price/level of the order with this id
        Side side = order.getSide(); // get the side of the order with this id

        OrderList orderList;
        switch (side) {
            case buy:
                orderList = bids.getOrDefault(price, new OrderList());// new List if mapping doesn't exist
                orderList.addOrder(order);// add order to the end of the list
                bids.put(price, orderList);//update TreeMap
                break;
            case sell:
                orderList = asks.getOrDefault(price, new OrderList());// new List if mapping doesn't exist
                orderList.addOrder(order); //add order to the end of the list
                asks.put(price, orderList); // update TreeMap
                break; // no need for default case. Side Enum must be 1 of the 2 values.
        }
        // register this price and side in the priceByOrderId and sideByOrderId maps
        priceByOrderIdMap.put(order.getOrderId(), order.getPrice());
        sideByOrderIdMap.put(order.getOrderId(), order.getSide());
    }

    /**
     * Given the ID of an existing order, modifies its quantity.
     * It is assumed that an order with the given id is present.
     *
     * @param orderId
     * @param newQuantity
     */
    public void modifyOrder(String orderId, long newQuantity) {
        long price = priceByOrderIdMap.get(orderId);// get the price/level for the order with this id
        Side side = sideByOrderIdMap.get(orderId);// get the side of the order with this id
        OrderList orderList;
        switch (side) {
            case buy:
                orderList = bids.get(price);
                orderList.modifyOrder(orderId, newQuantity);
                break;
            case sell:
                orderList = asks.get(price);
                orderList.modifyOrder(orderId, newQuantity);
                break; // no need for default case. Side Enum must be 1 of the 2 values.
        }
    }

    /**
     * Permanently removes the order with the given id from the list.
     * assumes that an order with given id is present
     *
     * @param orderId
     */
    public void deleteOrder(String orderId) {
        long price = priceByOrderIdMap.get(orderId);// get the price/level for the order with this id
        Side side = sideByOrderIdMap.get(orderId); // get the side of the order with this id
        OrderList orderList;
        switch (side) {
            case buy:
                orderList = bids.get(price);
                orderList.deleteOrder(orderId);
                if (orderList.getNumberOfOrders() == 0)
                    bids.remove(price);// no more orders in this side for this level, so remove it from map
                break;
            case sell:
                orderList = asks.get(price);
                orderList.deleteOrder(orderId);
                if (orderList.getNumberOfOrders() == 0)
                    asks.remove(price);// no more orders in this side for this level, so remove it from map
                break; // no need for default case. Side Enum must be 1 of the 2 values.
        }
        //update priceByOrderId and sideByOrderId
        priceByOrderIdMap.remove(orderId);
        sideByOrderIdMap.remove(orderId);
    }

    /**
     * @param side
     * @return The best price for the given side in the OrderBook, -1 if there're no orders on the give side
     */
    public long getBestPrice(Side side) {
        long bestPrice = -1;
        // since the TreeMap maintains order, the best price is always the first key
        switch (side) {
            case buy:
                if (!bids.isEmpty())
                    bestPrice = bids.firstKey(); // the TreeMap enforces the required ordering for bids
                break;
            case sell:
                if (!asks.isEmpty())
                    bestPrice = asks.firstKey(); // the TreeMap enforces the required ordering for asks
                break; // no need for default case. Side Enum must be 1 of the 2 values.
        }
        return bestPrice; // would still be -1 if there're no orders on the given side
    }

    /**
     * @param side
     * @param price
     * @return the number of orders on the given side of the given level. -1 if there're no orders on the given side
     * with the given price.
     */
    public long getOrderNumAtLevel(Side side, long price) {
        long orderNumAtLevel = -1;
        OrderList orderList;
        switch (side) {
            case buy:
                orderList = bids.get(price); // returns null if there is no OrderList for this level
                if (orderList != null)
                    orderNumAtLevel = orderList.getNumberOfOrders();
                break;
            case sell:
                orderList = asks.get(price); // returns null if there is no OrderList for this level
                if (orderList != null)
                    orderNumAtLevel = orderList.getNumberOfOrders();
                break; // no need for default case. Side Enum must be 1 of the 2 values.
        }
        return orderNumAtLevel;// will still be -1 if there're no orders on the given side for the given price
    }

    /**
     * @param side
     * @param price
     * @return The cumulative quantity of orders on the given side for the given price. -1 if there are no orders on
     * the given side with the given price.
     */
    public long getTotalQuantityAtLevel(Side side, long price) {
        long totalQuantity = -1;
        OrderList orderList;
        switch (side) {
            case buy:
                orderList = bids.get(price); // returns null if there is no OrderList for this level
                if (orderList != null)
                    totalQuantity = orderList.getTotalTradeableQuantity();
                break;
            case sell:
                orderList = asks.get(price);// returns null if there is no OrderList for this level
                if (orderList != null)
                    totalQuantity = orderList.getTotalTradeableQuantity();
                break; // no need for default case. Side Enum must be 1 of the 2 values.
        }
        return totalQuantity; // would still be -1 if there're no orders on the given side for the given price
    }

    /**
     * @param side
     * @param price
     * @return The cumulative volume of orders on the given side for the given price. -1 if there are no orders on
     * the given side with the given price.
     */
    public long getTotalVolumeAtLevel(Side side, long price) {
        long totalVolume = -1;
        OrderList orderList;
        switch (side) {
            case buy:
                orderList = bids.get(price); // returns null if there is no OrderList for this level
                if (orderList != null)
                    totalVolume = orderList.getTotalTradeableVolume();
                break;
            case sell:
                orderList = asks.get(price);// returns null if there is no OrderList for this level
                if (orderList != null)
                    totalVolume = orderList.getTotalTradeableVolume();
                break; // no need for default case. Side Enum must be 1 of the 2 values.
        }
        return totalVolume; // would still be -1 if there're no orders on the given side for the given price
    }

    /**
     * @param side
     * @param price
     * @return All orders in the order they arrive for the given side and price. An empty List if there are no
     * orders on the given side for the given price
     */
    public List<Order> getOrdersAtLevel(Side side, long price) {
        List<Order> orders = new ArrayList<>(); // empty list
        OrderList orderList;
        switch (side) {
            case buy:
                orderList = bids.get(price); // returns null if there is no OrderList for this level
                if (orderList != null)
                    orders = orderList.getOrders();
                break;
            case sell:
                orderList = asks.get(price);// returns null if there is no OrderList for this level
                if (orderList != null)
                    orders = orderList.getOrders();
                break; // no need for default case. Side Enum must be 1 of the 2 values.
        }
        return orders; // would still be an empty list if there're no orders on the given side for the given price
    }

    /**
     * @return true if an orderBook contains no orders. False if it does. Useful to know when to stop keeping track of
     * OrderBooks. Especially after deleting orders.
     */
    public boolean isEmpty() {
        return bids.isEmpty() && asks.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderBook orderBook = (OrderBook) o;

        if (bids != null ? !bids.equals(orderBook.bids) : orderBook.bids != null) return false;
        if (asks != null ? !asks.equals(orderBook.asks) : orderBook.asks != null) return false;
        if (priceByOrderIdMap != null ? !priceByOrderIdMap.equals(orderBook.priceByOrderIdMap) : orderBook.priceByOrderIdMap != null)
            return false;
        return sideByOrderIdMap != null ? sideByOrderIdMap.equals(orderBook.sideByOrderIdMap) : orderBook.sideByOrderIdMap == null;
    }

    @Override
    public int hashCode() {
        int result = bids != null ? bids.hashCode() : 0;
        result = 31 * result + (asks != null ? asks.hashCode() : 0);
        result = 31 * result + (priceByOrderIdMap != null ? priceByOrderIdMap.hashCode() : 0);
        result = 31 * result + (sideByOrderIdMap != null ? sideByOrderIdMap.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrderBook{" +
                "bids=" + bids +
                ", asks=" + asks +
                ", priceByOrderIdMap=" + priceByOrderIdMap +
                ", sideByOrderIdMap=" + sideByOrderIdMap +
                '}';
    }
}
