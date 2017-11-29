import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ioluwayo. email: ioluwayo@gmail.com
 * Implementation of an OrderBookManager which manages order books for different instruments.
 */
public class OrderBookManager {
    /**
     * Maintains instrument->orderBook pairs. Every instrument has a unique OrderBook. O(1) worst case for lookups
     */
    private Map<String, OrderBook> orderBookByInstrumentMap;

    /**
     * Maintains orderId -> instrument pairs. Facilitates retrieving an OrderBook when only the order id is known.
     */
    private Map<String, String> instrumentByOrderIdMap;

    public OrderBookManager() {
        orderBookByInstrumentMap = new HashMap<>();
        instrumentByOrderIdMap = new HashMap<>();
    }

    public Map<String, OrderBook> getOrderBookByInstrumentMap() {
        return orderBookByInstrumentMap;
    }

    public Map<String, String> getInstrumentByOrderIdMap() {
        return instrumentByOrderIdMap;
    }

    private void setOrderBookByInstrumentMap(Map<String, OrderBook> orderBookByInstrumentMap) {
        this.orderBookByInstrumentMap = orderBookByInstrumentMap;
    }

    private void setInstrumentByOrderIdMap(Map<String, String> instrumentByOrderIdMap) {
        this.instrumentByOrderIdMap = instrumentByOrderIdMap;
    }

    /**
     * Adds an order to its orderBook
     *
     * @param order new order to add
     */
    public void addOrder(Order order) {
        String instrument = order.getInstrument();
        String orderId = order.getOrderId();

        //get orderBook for this instrument or create new order book if non exists
        OrderBook orderBook = orderBookByInstrumentMap.getOrDefault(instrument, new OrderBook());
        orderBook.addOrder(order);
        orderBookByInstrumentMap.put(instrument, orderBook); // make sure it is in the map
        instrumentByOrderIdMap.put(orderId, instrument); // register the instrument of for this order id
    }

    /**
     * Changes the quantity of an existing order. Does nothing if the order does not exist
     *
     * @param orderId     unique identifier of existing order to modify
     * @param newQuantity new quantity for the order, NOT a delta from previous quantity, always positive
     */
    public void modifyOrder(String orderId, long newQuantity) {
        String instrument = instrumentByOrderIdMap.get(orderId);
        // do nothing if this order id does not match any existing instrument
        if (instrument == null) {
            return;
        }

        //get the orderBook for this instrument. An OrderBook is guaranteed to exist at this point.
        OrderBook orderBook = orderBookByInstrumentMap.get(instrument);
        orderBook.modifyOrder(orderId, newQuantity);
    }

    /**
     * Permanently deletes an existing order. Does nothing if the order does not exist
     *
     * @param orderId unique identifier of existing order
     */
    public void deleteOrder(String orderId) {
        String instrument = instrumentByOrderIdMap.get(orderId);

        // do nothing if there is no matching instrument for the given order id
        if (instrument == null) {
            return;
        }
        OrderBook orderBook = orderBookByInstrumentMap.get(instrument);
        orderBook.deleteOrder(orderId);
        instrumentByOrderIdMap.remove(orderId);// remove mapping from the instrumentByOrderIdMap

        // check if this orderBook has now become empty and stop keeping track of it
        if (orderBook.isEmpty()) {
            orderBookByInstrumentMap.remove(instrument);
        }
    }

    /**
     * @param instrument identifier of an instrument
     * @param side       either buy or sell
     * @return best bid/ask price for an instrument. -1 if no order for the given instrument exists on the given side
     */
    public long getBestPrice(String instrument, Side side) {
        OrderBook orderBook = orderBookByInstrumentMap.get(instrument);

        //return -1 if there is no OrderBook for the given instrument
        if (orderBook == null) {
            return -1;
        }
        return orderBook.getBestPrice(side); // returns -1 if there're no orders on the given side
    }

    /**
     * @param instrument identifier of an instrument
     * @param side       either buy or sell
     * @param price      requested price level
     * @return number of orders on a level and side. -1 if there are no orders for the given instrument, side or level
     */
    public long getOrderNumAtLevel(String instrument, Side side, long price) {
        OrderBook orderBook = orderBookByInstrumentMap.get(instrument);

        // return -1 if there is no OrderBook for the given instrument
        if (orderBook == null) {
            return -1;
        }
        return orderBook.getOrderNumAtLevel(side, price); // returns -1 if there're no orders for the side or level
    }

    /**
     * @param instrument identifier of an instrument
     * @param side       either buy or sell
     * @param price      requested price level
     * @return tradeable quantity for a side and level. -1 if there're no orders for the given instrument,side or level
     */
    public long getTotalQuantityAtLevel(String instrument, Side side, long price) {
        OrderBook orderBook = orderBookByInstrumentMap.get(instrument);

        //return -1 if no OrderBook exists for this instrument
        if (orderBook == null) {
            return -1;
        }
        return orderBook.getTotalQuantityAtLevel(side, price);// returns -1 if there're no orders for the side or level
    }

    /**
     * @param instrument identifier of an instrument
     * @param side       either buy or sell
     * @param price      requested price level
     * @return tradeable volume for a side and level. -1 if there're no orders for the given instrument,side or level
     */
    public long getTotalVolumeAtLevel(String instrument, Side side, long price) {
        OrderBook orderBook = orderBookByInstrumentMap.get(instrument);

        // return -1 if no OrderBook exists for this instrument
        if (orderBook == null) {
            return -1;
        }
        return orderBook.getTotalVolumeAtLevel(side, price);// returns -1 if there're no orders for the side or level
    }

    /**
     * @param instrument identifier of an instrument
     * @param side       either buy or sell
     * @param price      requested price level
     * @return a list of orders in the correct order for the given parameters. an empty list if there're no orders for
     * the instrument, side or level.
     */
    public List<Order> getOrdersAtLevel(String instrument, Side side, long price) {
        OrderBook orderBook = orderBookByInstrumentMap.get(instrument);
        if (orderBook == null) {
            return Collections.emptyList(); // maybe returning null might be more representative
        }
        return orderBook.getOrdersAtLevel(side, price);// returns an empty list if there're no orders for the side/level
    }

}
