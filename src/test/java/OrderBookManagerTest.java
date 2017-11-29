import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by ioluwayo. email: ioluwayo@gmail.com
 */
public class OrderBookManagerTest {
    private OrderBookManager orderBookManager;

    @Test
    public void addOrder() throws Exception {
        orderBookManager = new OrderBookManager();

        //create orders
        Order buyOrder1 = new Order("buyOrder1", "XBT", Side.buy, 100, 10);
        Order buyOrder2 = new Order("buyOrder2", "XBT", Side.buy, 100, 25);

        Order sellOrder1 = new Order("sellOrder1", "XRP", Side.sell, 100, 10);
        Order sellOrder2 = new Order("sellOrder2", "XBT", Side.sell, 100, 25);

        // add orders to book
        orderBookManager.addOrder(buyOrder1);
        orderBookManager.addOrder(buyOrder2);
        orderBookManager.addOrder(sellOrder1);
        orderBookManager.addOrder(sellOrder2);

        //confirm that these orders have been added to the orderId->instrument map
        Map<String, String> expectedInstrumentByOrderIdMap = new HashMap<>();

        expectedInstrumentByOrderIdMap.put(buyOrder1.getOrderId(), buyOrder1.getInstrument());
        expectedInstrumentByOrderIdMap.put(buyOrder2.getOrderId(), buyOrder2.getInstrument());
        expectedInstrumentByOrderIdMap.put(sellOrder1.getOrderId(), sellOrder1.getInstrument());
        expectedInstrumentByOrderIdMap.put(sellOrder2.getOrderId(), sellOrder2.getInstrument());

        Map<String, String> actuallInstrumentByOrderIdMap = orderBookManager.getInstrumentByOrderIdMap();
        assertEquals(expectedInstrumentByOrderIdMap, actuallInstrumentByOrderIdMap);//confirm they have the same contents

        // check that orders are kept in the order that they are added
        List<Order> expectedList = Arrays.asList(buyOrder1, buyOrder2);
        assertEquals(expectedList, orderBookManager.getOrdersAtLevel("XBT", Side.buy, 100));

        //confirm that orderBooks were created for each unique instrument. 2 orderBooks should be created in this case
        Map<String, OrderBook> expectedOrderBookByInstrumentMap = new HashMap<>();
        OrderBook xbtOrderBook = new OrderBook(); //create OrderBook for xbt orders
        OrderBook xrpOrderBook = new OrderBook(); //create OrderBook for xrp orders

        //add orders in the same order as they were added earlier
        xbtOrderBook.addOrder(buyOrder1);
        xbtOrderBook.addOrder(buyOrder2);
        xrpOrderBook.addOrder(sellOrder1);
        xbtOrderBook.addOrder(sellOrder2);
        expectedOrderBookByInstrumentMap.put("XBT", xbtOrderBook);
        expectedOrderBookByInstrumentMap.put("XRP", xrpOrderBook);

        //confirm that the maps contain the same values
        Map<String, OrderBook> actualOrderBookByInstrumentMap = orderBookManager.getOrderBookByInstrumentMap();
        assertEquals(expectedOrderBookByInstrumentMap, actualOrderBookByInstrumentMap);
    }

    @Test
    public void modifyOrder() throws Exception {
        orderBookManager = new OrderBookManager();
        //create orders
        Order buyOrder1 = new Order("buyOrder1", "XBT", Side.buy, 100, 10);
        Order buyOrder2 = new Order("buyOrder2", "XRP", Side.buy, 100, 25);
        Order buyOrder3 = new Order("buyOrder3", "XBT", Side.buy, 100, 10);

        //add orders book
        orderBookManager.addOrder(buyOrder1);
        orderBookManager.addOrder(buyOrder2);
        orderBookManager.addOrder(buyOrder3);

        //modify buyOrder1
        orderBookManager.modifyOrder("buyOrder1", 20);//increased, so should be moved to the back

        //check that it was modified correctly
        assertEquals(20, buyOrder1.getQuantity());

        //confirm that only buyOrder1 was modified
        assertEquals(25, buyOrder2.getQuantity());

        //check that buyOrder1 was move to the back. as its quantity increased
        List<Order> expectedArray = Arrays.asList(buyOrder3, buyOrder1); // expected order
        assertEquals(expectedArray, orderBookManager.getOrdersAtLevel("XBT", Side.buy, 100));

        //decrease the quantity of buyOrder1 and check that its position is maintained
        orderBookManager.modifyOrder("buyOrder1", 5);
        assertEquals(expectedArray, orderBookManager.getOrdersAtLevel("XBT", Side.buy, 100));
    }

    @Test
    public void deleteOrder() throws Exception {
        orderBookManager = new OrderBookManager();

        //create orders
        Order buyOrder1 = new Order("buyOrder1", "XBT", Side.buy, 100, 10);
        Order buyOrder2 = new Order("buyOrder2", "XBT", Side.buy, 100, 25);

        Order sellOrder1 = new Order("sellOrder1", "XRP", Side.sell, 200, 10);
        Order sellOrder2 = new Order("sellOrder2", "XBT", Side.sell, 100, 25);

        // add orders to book
        orderBookManager.addOrder(buyOrder1);
        orderBookManager.addOrder(buyOrder2);
        orderBookManager.addOrder(sellOrder1);
        orderBookManager.addOrder(sellOrder2);

        //delete orders
        orderBookManager.deleteOrder("buyOrder1"); //located in price level 100 of XBT bids
        orderBookManager.deleteOrder("sellOrder1"); //located in price level 200 of xrp asks

        //confirm that it has been removed from the instrumentByOrderIdMap
        Map<String, String> actualInstrumentbyOrderIdMap = orderBookManager.getInstrumentByOrderIdMap();
        assertFalse(actualInstrumentbyOrderIdMap.containsValue(buyOrder1));
        assertFalse(actualInstrumentbyOrderIdMap.containsValue(sellOrder1));

        //confirm that the orderBook no longer has this order
        long level = 100; // level for buyOrder1
        // get orderBook for the particular instrument
        OrderBook actualOrderBook = orderBookManager.getOrderBookByInstrumentMap().get("XBT");
        OrderList actualOrderlist = actualOrderBook.getBids().get(level); //orderList for level 100
        assertFalse(actualOrderlist.getOrders().contains(buyOrder1));
        actualOrderBook = orderBookManager.getOrderBookByInstrumentMap().get("XRP");
        assertNull(actualOrderBook); // there should be no orders left for XRP after deleting sellOrder1
    }

    @Test
    public void getBestPrice() throws Exception {
        orderBookManager = new OrderBookManager();

        //create orders
        Order buyOrder1 = new Order("buyOrder1", "XBT", Side.buy, 100, 10);
        Order buyOrder2 = new Order("buyOrder2", "XBT", Side.buy, 100, 25);
        Order buyOrder3 = new Order("buyOrder3", "XBT", Side.buy, 500, 10);
        Order buyOrder4 = new Order("buyOrder4", "XBT", Side.buy, 100, 25);

        Order sellOrder1 = new Order("sellOrder1", "XBT", Side.sell, 400, 10);
        Order sellOrder2 = new Order("sellOrder2", "XBT", Side.sell, 200, 25);
        Order sellOrder3 = new Order("sellOrder3", "XBT", Side.sell, 200, 10);
        Order sellOrder4 = new Order("sellOrder4", "XBT", Side.sell, 50, 25);
        // add orders to book
        orderBookManager.addOrder(buyOrder1);
        orderBookManager.addOrder(buyOrder2);
        orderBookManager.addOrder(buyOrder3);
        orderBookManager.addOrder(buyOrder4);
        orderBookManager.addOrder(sellOrder1);
        orderBookManager.addOrder(sellOrder2);
        orderBookManager.addOrder(sellOrder3);
        orderBookManager.addOrder(sellOrder4);

        //confirm that the returned best price is accurate
        long expectedBestBidPrice = 500; // highest price
        long expectedBestAskPrice = 50; //lowest prices
        assertEquals(expectedBestBidPrice, orderBookManager.getBestPrice("XBT", Side.buy));
        assertEquals(expectedBestAskPrice, orderBookManager.getBestPrice("XBT", Side.sell));
    }

    @Test
    public void getOrderNumAtLevel() throws Exception {
        orderBookManager = new OrderBookManager();

        //create orders
        Order buyOrder1 = new Order("buyOrder1", "XBT", Side.buy, 100, 10);
        Order buyOrder2 = new Order("buyOrder2", "XBT", Side.buy, 100, 25);
        Order buyOrder3 = new Order("buyOrder3", "XBT", Side.buy, 500, 10);
        Order buyOrder4 = new Order("buyOrder4", "XBT", Side.buy, 100, 25);

        Order sellOrder1 = new Order("sellOrder1", "XBT", Side.sell, 200, 10);
        Order sellOrder2 = new Order("sellOrder2", "XBT", Side.sell, 200, 25);
        Order sellOrder3 = new Order("sellOrder3", "XBT", Side.sell, 200, 10);
        Order sellOrder4 = new Order("sellOrder4", "XBT", Side.sell, 50, 25);
        // add orders to book
        orderBookManager.addOrder(buyOrder1);
        orderBookManager.addOrder(buyOrder2);
        orderBookManager.addOrder(buyOrder3);
        orderBookManager.addOrder(buyOrder4);
        orderBookManager.addOrder(sellOrder1);
        orderBookManager.addOrder(sellOrder2);
        orderBookManager.addOrder(sellOrder3);
        orderBookManager.addOrder(sellOrder4);

        //confirm that number for each level and side is correct
        long expectedNumber = 3; //buyOrder1,buyOrder2,buyOrder4
        assertEquals(expectedNumber, orderBookManager.getOrderNumAtLevel("XBT", Side.buy, 100));
        expectedNumber = 1; //buyOrder3
        assertEquals(expectedNumber, orderBookManager.getOrderNumAtLevel("XBT", Side.buy, 500));
        expectedNumber = 3; //sellOder1,sellOrder2,sellOrder3
        assertEquals(expectedNumber, orderBookManager.getOrderNumAtLevel("XBT", Side.sell, 200));
        expectedNumber = 1; //sellOrder4
        assertEquals(expectedNumber, orderBookManager.getOrderNumAtLevel("XBT", Side.sell, 50));
    }

    @Test
    public void getTotalQuantityAtLevel() throws Exception {
        orderBookManager = new OrderBookManager();

        //create orders
        Order buyOrder1 = new Order("buyOrder1", "XBT", Side.buy, 100, 10);
        Order buyOrder2 = new Order("buyOrder2", "XBT", Side.buy, 100, 25);
        Order buyOrder3 = new Order("buyOrder3", "XBT", Side.buy, 500, 10);
        Order buyOrder4 = new Order("buyOrder4", "XBT", Side.buy, 100, 25);

        Order sellOrder1 = new Order("sellOrder1", "XRP", Side.sell, 400, 10);
        Order sellOrder2 = new Order("sellOrder2", "XRP", Side.sell, 400, 20);
        Order sellOrder3 = new Order("sellOrder3", "XRP", Side.sell, 400, 30);
        Order sellOrder4 = new Order("sellOrder4", "XRP", Side.sell, 400, 40);

        // add orders to book
        orderBookManager.addOrder(buyOrder1);
        orderBookManager.addOrder(buyOrder2);
        orderBookManager.addOrder(buyOrder3);
        orderBookManager.addOrder(buyOrder4);
        orderBookManager.addOrder(sellOrder1);
        orderBookManager.addOrder(sellOrder2);
        orderBookManager.addOrder(sellOrder3);
        orderBookManager.addOrder(sellOrder4);

        //confirm that the total quantity for each level, side and instrument is accurate
        long expectedQuantity = 10; // quantity for level 500 on the buy side of XBT
        assertEquals(expectedQuantity, orderBookManager.getTotalQuantityAtLevel("XBT", Side.buy, 500));
        expectedQuantity = 60; //quantity for level 100 on the buy side of "XBT"
        assertEquals(expectedQuantity, orderBookManager.getTotalQuantityAtLevel("XBT", Side.buy, 100));
        expectedQuantity = 100; //quantity for level 400 on the sell side of "XRP"
        assertEquals(expectedQuantity, orderBookManager.getTotalQuantityAtLevel("XRP", Side.sell, 400));

        //check that -1 is returned when there are no orders for an instrument, side or level
        assertEquals(-1, orderBookManager.getTotalQuantityAtLevel("XRP", Side.buy, 100));
        assertEquals(-1, orderBookManager.getTotalQuantityAtLevel("XRP", Side.sell, 100));
        assertEquals(-1, orderBookManager.getTotalQuantityAtLevel("XBT", Side.buy, 80));
    }

    @Test
    public void getTotalVolumeAtLevel() throws Exception {
        orderBookManager = new OrderBookManager();

        //create orders
        Order buyOrder1 = new Order("buyOrder1", "XBT", Side.buy, 100, 10);
        Order buyOrder2 = new Order("buyOrder2", "XBT", Side.buy, 100, 25);
        Order buyOrder3 = new Order("buyOrder3", "XBT", Side.buy, 500, 10);
        Order buyOrder4 = new Order("buyOrder4", "XBT", Side.buy, 100, 25);

        Order sellOrder1 = new Order("sellOrder1", "XRP", Side.sell, 400, 10);
        Order sellOrder2 = new Order("sellOrder2", "XRP", Side.sell, 400, 20);
        Order sellOrder3 = new Order("sellOrder3", "XRP", Side.sell, 400, 30);
        Order sellOrder4 = new Order("sellOrder4", "XRP", Side.sell, 400, 40);

        // add orders to book
        orderBookManager.addOrder(buyOrder1);
        orderBookManager.addOrder(buyOrder2);
        orderBookManager.addOrder(buyOrder3);
        orderBookManager.addOrder(buyOrder4);
        orderBookManager.addOrder(sellOrder1);
        orderBookManager.addOrder(sellOrder2);
        orderBookManager.addOrder(sellOrder3);
        orderBookManager.addOrder(sellOrder4);

        //confirm that the total volume for each level, side and instrument is accurate
        long expectedVolume = 6000;
        assertEquals(expectedVolume, orderBookManager.getTotalVolumeAtLevel("XBT", Side.buy, 100));
        expectedVolume = 5000;
        assertEquals(expectedVolume, orderBookManager.getTotalVolumeAtLevel("XBT", Side.buy, 500));
        expectedVolume = 40000;
        assertEquals(expectedVolume, orderBookManager.getTotalVolumeAtLevel("XRP", Side.sell, 400));

        //check that -1 is returned when there are no orders for an instrument, side or level
        assertEquals(-1, orderBookManager.getTotalVolumeAtLevel("XRP", Side.buy, 100));
        assertEquals(-1, orderBookManager.getTotalVolumeAtLevel("XRP", Side.sell, 100));
        assertEquals(-1, orderBookManager.getTotalVolumeAtLevel("XBT", Side.buy, 80));
    }

    @Test
    public void getOrdersAtLevel() throws Exception {
        orderBookManager = new OrderBookManager();

        //create orders
        Order buyOrder1 = new Order("buyOrder1", "XBT", Side.buy, 100, 10);
        Order buyOrder2 = new Order("buyOrder2", "XBT", Side.buy, 100, 25);
        Order buyOrder3 = new Order("buyOrder3", "XBT", Side.buy, 500, 10);
        Order buyOrder4 = new Order("buyOrder4", "XBT", Side.buy, 100, 25);

        Order sellOrder1 = new Order("sellOrder1", "XRP", Side.sell, 400, 10);
        Order sellOrder2 = new Order("sellOrder2", "XRP", Side.sell, 400, 20);
        Order sellOrder3 = new Order("sellOrder3", "XRP", Side.sell, 400, 30);
        Order sellOrder4 = new Order("sellOrder4", "XRP", Side.sell, 400, 40);

        // add orders to book
        orderBookManager.addOrder(buyOrder1);
        orderBookManager.addOrder(buyOrder2);
        orderBookManager.addOrder(buyOrder3);
        orderBookManager.addOrder(buyOrder4);
        orderBookManager.addOrder(sellOrder1);
        orderBookManager.addOrder(sellOrder2);
        orderBookManager.addOrder(sellOrder3);
        orderBookManager.addOrder(sellOrder4);

        //group arrays into lists for each level as expected
        List<Order> expectedBidLevel1 = Arrays.asList(buyOrder3);
        List<Order> expectedBidLevel2 = Arrays.asList(buyOrder1, buyOrder2, buyOrder4);
        List<Order> expectedAskLevel1 = Arrays.asList(sellOrder1, sellOrder2, sellOrder3, sellOrder4);
        List<Order> emptyList = Collections.emptyList();

        assertEquals(expectedBidLevel1, orderBookManager.getOrdersAtLevel("XBT", Side.buy, 500));
        assertEquals(expectedBidLevel2, orderBookManager.getOrdersAtLevel("XBT", Side.buy, 100));
        assertEquals(expectedAskLevel1, orderBookManager.getOrdersAtLevel("XRP", Side.sell, 400));

        //confirm that an empty list is returned when there no orders for a side, level or instrument
        assertEquals(emptyList, orderBookManager.getOrdersAtLevel("VOD.L", Side.buy, 500));
        assertEquals(emptyList, orderBookManager.getOrdersAtLevel("XRP", Side.buy, 500));
        assertEquals(emptyList, orderBookManager.getOrdersAtLevel("XRP", Side.sell, 200));
    }
}