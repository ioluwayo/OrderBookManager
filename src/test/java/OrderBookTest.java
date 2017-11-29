import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by ioluwayo. email: ioluwayo@gmail.com
 */
public class OrderBookTest {
    private OrderBook orderBook;

    @Test
    public void addOrder() throws Exception {
        orderBook = new OrderBook();
        Order buyOrder = new Order("order1", "VOD.L", Side.buy, 200, 10);
        Order sellOrder = new Order("order2", "VOD.L", Side.sell, 50, 10);

        //confirm the OrderBook has no orders before adding orders
        assertTrue(orderBook.getBids().isEmpty()); // should not contain any pairings
        assertTrue(orderBook.getAsks().isEmpty()); // should not contain any pairings

        // add orders to OrderBook
        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        //check that it now contains pairings
        assertFalse(orderBook.getBids().isEmpty());
        assertFalse(orderBook.getAsks().isEmpty());

        //check that it was registered in the priceByIdMap and sideByIdMap
        assertTrue(orderBook.getPriceByOrderIdMap().get(buyOrder.getOrderId()) == buyOrder.getPrice());
        assertTrue(orderBook.getSideByOrderIdMap().get(buyOrder.getOrderId()) == buyOrder.getSide());
        assertTrue(orderBook.getPriceByOrderIdMap().get(sellOrder.getOrderId()) == sellOrder.getPrice());
        assertTrue(orderBook.getSideByOrderIdMap().get(sellOrder.getOrderId()) == sellOrder.getSide());

        // check that orders were added to the correct TreeMap
        assertFalse(orderBook.getBids().containsKey(sellOrder.getPrice())); // bids map should not contain this key
        assertFalse(orderBook.getAsks().containsKey(buyOrder.getPrice())); // asks map should not contain this key
        assertTrue(orderBook.getBids().containsKey(buyOrder.getPrice()));
        assertTrue(orderBook.getAsks().containsKey(sellOrder.getPrice()));

        // check that orders were added to the correct OrderList and level
        OrderList bidsOrderList = orderBook.getBids().get(buyOrder.getPrice());
        assertTrue(bidsOrderList.getOrders().contains(buyOrder));
        assertFalse(bidsOrderList.getOrders().contains(sellOrder)); // sell order should not be in the bids map

        OrderList asksOrderList = orderBook.getAsks().get(sellOrder.getPrice());
        assertTrue(asksOrderList.getOrders().contains(sellOrder));
        assertFalse(asksOrderList.getOrders().contains(buyOrder));// buy order should not be in the asks map
    }

    @Test
    public void modifyOrder() throws Exception {
        orderBook = new OrderBook();
        // create orders
        Order buyOrder1 = new Order("buyOrder1", "VOD.L", Side.buy, 100, 10);
        Order buyOrder2 = new Order("buyOrder2", "VOD.L", Side.buy, 100, 25);
        Order buyOrder3 = new Order("buyOrder3", "VOD.L", Side.buy, 100, 50);

        Order sellOrder1 = new Order("sellOrder1", "VOD.L", Side.sell, 100, 10);
        Order sellOrder2 = new Order("sellOrder2", "VOD.L", Side.sell, 100, 25);
        Order sellOrder3 = new Order("sellOrder3", "VOD.L", Side.sell, 100, 50);

        //add orders to orderBook. They should all belong to the same level
        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(buyOrder3);

        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(sellOrder3);

        //modify the orders
        orderBook.modifyOrder("buyOrder1", 20); //increased should be moved to back of list
        orderBook.modifyOrder("buyOrder2", 15); //decreased, should maintain position
        orderBook.modifyOrder("buyOrder3", 45); //decreased, should maintain position

        orderBook.modifyOrder("sellOrder1", 5); // decreased, should maintain position
        orderBook.modifyOrder("sellOrder2", 30); // increased, should be moved to back of the list
        orderBook.modifyOrder("sellOrder3", 45); // decreased, should maintain position

        // get list of orders for the price level on both sides
        List<Order> buyOrders = orderBook.getOrdersAtLevel(Side.buy, 100);
        List<Order> sellOrders = orderBook.getOrdersAtLevel(Side.sell, 100);

        //list should contain the orders in the expected order after modifying
        assertEquals(20, buyOrders.get(2).getQuantity()); // moved to back of the list from index 0 to 2
        assertEquals(15, buyOrders.get(0).getQuantity()); // moved an index up from 1 to 0
        assertEquals(45, buyOrders.get(1).getQuantity()); // moved an index up from 2 to 1

        assertEquals(5, sellOrders.get(0).getQuantity()); // did not move
        assertEquals(30, sellOrders.get(2).getQuantity()); // moved to the back of the list from index 1 to 2
        assertEquals(45, sellOrders.get(1).getQuantity()); // moved an index up from 2 to 1

    }

    @Test
    public void deleteOrder() throws Exception {
        orderBook = new OrderBook();
        // create orders
        Order buyOrder1 = new Order("buyOrder1", "VOD.L", Side.buy, 100, 10);
        Order buyOrder2 = new Order("buyOrder2", "VOD.L", Side.buy, 100, 25);

        //add order to orderBook
        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);

        //delete buyOrder2
        orderBook.deleteOrder("buyOrder2");

        //check that it is no longer in the priceByIdMap and sellByIdMap
        assertNull(orderBook.getPriceByOrderIdMap().get(buyOrder2.getOrderId())); // no value for key
        assertNull(orderBook.getSideByOrderIdMap().get(buyOrder2.getOrderId()));

        //check it is no longer in orderList for this price level
        assertFalse(orderBook.getBids().get(buyOrder2.getPrice()).getOrders().contains(buyOrder2));

        //delete buyOrder1
        orderBook.deleteOrder("buyOrder1");

        //check that there are no longer any orders on the buy side for this price Level
        assertNull(orderBook.getBids().get(buyOrder1.getPrice())); // map returns null since no orders exist

    }

    @Test
    public void getBestPrice() throws Exception {
        orderBook = new OrderBook();

        //check that the best price is -1 when there are no orders in the orderBook
        assertTrue(orderBook.getBestPrice(Side.buy) == -1);
        assertTrue(orderBook.getBestPrice(Side.sell) == -1);

        //create 2 price levels
        long expectedHigherPrice = 1000000;
        long expectedLowerPrice = 50;

        //create 2 orders for each side
        Order buyOrder1 = new Order("buyOrder1", "VOD.L", Side.buy, expectedHigherPrice, 10);
        Order buyOrder2 = new Order("buyOrder2", "VOD.L", Side.buy, expectedLowerPrice, 25);
        Order sellOrder1 = new Order("sellOrder1", "VOD.L", Side.sell, expectedHigherPrice, 10);
        Order sellOrder2 = new Order("sellOrder2", "VOD.L", Side.sell, expectedLowerPrice, 25);

        //add orders to orderBook
        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);

        //check that best price on the bids side is the higherPrice
        assertEquals(expectedHigherPrice, orderBook.getBestPrice(Side.buy));

        //check that best price on the asks side is the lowerPrice
        assertEquals(expectedLowerPrice, orderBook.getBestPrice(Side.sell));
    }

    @Test
    public void getOrderNumAtLevel() throws Exception {
        orderBook = new OrderBook();

        // create orders
        Order buyOrder1 = new Order("buyOrder1", "VOD.L", Side.buy, 100, 10);
        Order buyOrder2 = new Order("buyOrder2", "VOD.L", Side.buy, 100, 50);
        Order buyOrder3 = new Order("buyOrder3", "VOD.L", Side.buy, 200, 50);
        Order buyOrder4 = new Order("buyOrder4", "VOD.L", Side.buy, 400, 30);

        Order sellOrder1 = new Order("sellOrder1", "VOD.L", Side.sell, 200, 10);
        Order sellOrder2 = new Order("sellOrder2", "VOD.L", Side.sell, 200, 40);
        Order sellOrder3 = new Order("sellOrder3", "VOD.L", Side.sell, 200, 30);
        Order sellOrder4 = new Order("sellOrder4", "VOD.L", Side.sell, 300, 50);

        //confirm that -1 is returned when there are no orders for  a price on each side
        assertEquals(-1, orderBook.getOrderNumAtLevel(Side.buy, 100));
        assertEquals(-1, orderBook.getOrderNumAtLevel(Side.sell, 400));

        //add orders to orderBook.
        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(buyOrder3);
        orderBook.addOrder(buyOrder4);

        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(sellOrder3);
        orderBook.addOrder(sellOrder4);

        //confirm that the num of orders for each level is correct
        assertEquals(1, orderBook.getOrderNumAtLevel(Side.buy, 400));
        assertEquals(1, orderBook.getOrderNumAtLevel(Side.buy, 200));
        assertEquals(2, orderBook.getOrderNumAtLevel(Side.buy, 100));
        assertEquals(3, orderBook.getOrderNumAtLevel(Side.sell, 200));
        assertEquals(1, orderBook.getOrderNumAtLevel(Side.sell, 300));

    }

    @Test
    public void getTotalQuantityAtLevel() throws Exception {
        orderBook = new OrderBook();

        // create orders
        Order buyOrder1 = new Order("buyOrder1", "VOD.L", Side.buy, 100, 10);
        Order buyOrder2 = new Order("buyOrder2", "VOD.L", Side.buy, 100, 50);
        Order buyOrder3 = new Order("buyOrder3", "VOD.L", Side.buy, 200, 50);

        Order sellOrder1 = new Order("sellOrder1", "VOD.L", Side.sell, 200, 10);
        Order sellOrder2 = new Order("sellOrder2", "VOD.L", Side.sell, 200, 40);
        Order sellOrder3 = new Order("sellOrder3", "VOD.L", Side.sell, 200, 30);

        //confirm that -1 is returned if there no order for a given price and side
        assertEquals(-1, orderBook.getTotalQuantityAtLevel(Side.buy, 100));
        assertEquals(-1, orderBook.getTotalQuantityAtLevel(Side.sell, 300));

        //add orders orderBook
        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(buyOrder3);

        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(sellOrder3);


        //check total quantity
        assertEquals(60, orderBook.getTotalQuantityAtLevel(Side.buy, 100));//10+50
        assertEquals(50, orderBook.getTotalQuantityAtLevel(Side.buy, 200));//50
        assertEquals(80, orderBook.getTotalQuantityAtLevel(Side.sell, 200));//10+40+30

        //check that total quantity is correct after deleting an order
        orderBook.deleteOrder(buyOrder2.getOrderId());
        assertEquals(10, orderBook.getTotalQuantityAtLevel(Side.buy, 100));
        orderBook.deleteOrder(sellOrder1.getOrderId());
        assertEquals(70, orderBook.getTotalQuantityAtLevel(Side.sell, 200));

        // add order after deletion and check that the total quantity is accurate
        orderBook.addOrder(buyOrder2);
        assertEquals(60, orderBook.getTotalQuantityAtLevel(Side.buy, 100));
        orderBook.addOrder(sellOrder1);
        assertEquals(80, orderBook.getTotalQuantityAtLevel(Side.sell, 200));

        //modify order and check that total quantity is accurate
        orderBook.modifyOrder(sellOrder1.getOrderId(), 20);
        assertEquals(90, orderBook.getTotalQuantityAtLevel(Side.sell, 200)); //20+40+30
        orderBook.modifyOrder(buyOrder2.getOrderId(), 20);
        assertEquals(30, orderBook.getTotalQuantityAtLevel(Side.buy, 100));//10+20

    }

    @Test
    public void getTotalVolumeAtLevel() throws Exception {
        orderBook = new OrderBook();

        // create orders
        Order buyOrder1 = new Order("buyOrder1", "VOD.L", Side.buy, 100, 10);
        Order buyOrder2 = new Order("buyOrder2", "VOD.L", Side.buy, 100, 50);
        Order buyOrder3 = new Order("buyOrder3", "VOD.L", Side.buy, 200, 50);

        Order sellOrder1 = new Order("sellOrder1", "VOD.L", Side.sell, 200, 10);
        Order sellOrder2 = new Order("sellOrder2", "VOD.L", Side.sell, 200, 40);
        Order sellOrder3 = new Order("sellOrder3", "VOD.L", Side.sell, 200, 30);

        //confirm that -1 is returned if there are no orders for a given price and side
        assertEquals(-1, orderBook.getTotalVolumeAtLevel(Side.buy, 100));
        assertEquals(-1, orderBook.getTotalQuantityAtLevel(Side.sell, 300));

        //add orders orderBook
        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(buyOrder3);

        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(sellOrder3);

        //check that total volume at level is accurate
        assertEquals(10000, orderBook.getTotalVolumeAtLevel(Side.buy, 200));// (200*50)
        assertEquals(6000, orderBook.getTotalVolumeAtLevel(Side.buy, 100));// (50*10)+(60*100)
        assertEquals(16000, orderBook.getTotalVolumeAtLevel(Side.sell, 200));// (10*200)+(40*200)+(30*200)

        // check that total quantity after deleting, adding and modifying orders is accurate
        orderBook.deleteOrder(sellOrder1.getOrderId());
        orderBook.deleteOrder(buyOrder3.getOrderId()); //
        assertEquals(14000, orderBook.getTotalVolumeAtLevel(Side.sell, 200));// (40*200)+(30*200)
        assertEquals(-1, orderBook.getTotalVolumeAtLevel(Side.buy, 200));//no orders for this level & side

        orderBook.addOrder(buyOrder3);
        orderBook.addOrder(sellOrder1);
        assertEquals(10000, orderBook.getTotalVolumeAtLevel(Side.buy, 200));// (200*50)
        assertEquals(16000, orderBook.getTotalVolumeAtLevel(Side.sell, 200));// (10*200)+(40*200)+(30*200)

        orderBook.modifyOrder(buyOrder3.getOrderId(), 10);
        orderBook.modifyOrder(sellOrder1.getOrderId(), 100);
        assertEquals(2000, orderBook.getTotalVolumeAtLevel(Side.buy, 200));// (200*10)
        assertEquals(34000, orderBook.getTotalVolumeAtLevel(Side.sell, 200));//(100*200)+(40*200)+(30*200)
    }

    @Test
    public void getOrdersAtLevel() throws Exception {
        orderBook = new OrderBook();

        // create orders
        Order buyOrder1 = new Order("buyOrder1", "VOD.L", Side.buy, 100, 10);
        Order buyOrder2 = new Order("buyOrder2", "VOD.L", Side.buy, 200, 50);
        Order buyOrder3 = new Order("buyOrder3", "VOD.L", Side.buy, 200, 50);

        Order sellOrder1 = new Order("sellOrder1", "VOD.L", Side.sell, 100, 10);
        Order sellOrder2 = new Order("sellOrder2", "VOD.L", Side.sell, 200, 40);
        Order sellOrder3 = new Order("sellOrder3", "VOD.L", Side.sell, 200, 30);

        //group arrays into lists for each level as expected
        List<Order> expectedBidLevel1 = Arrays.asList(buyOrder2, buyOrder3);
        List<Order> expectedBidLevel2 = Arrays.asList(buyOrder1);
        List<Order> expectedAskLevel1 = Arrays.asList(sellOrder1);
        List<Order> expectedAskLevel2 = Arrays.asList(sellOrder2, sellOrder3);

        //add orders to orderBook
        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(buyOrder3);
        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(sellOrder3);

        // confirm that the returned Lists are as expected
        assertEquals(expectedBidLevel1, orderBook.getOrdersAtLevel(Side.buy, 200));
        assertEquals(expectedBidLevel2, orderBook.getOrdersAtLevel(Side.buy, 100));
        assertEquals(expectedAskLevel1, orderBook.getOrdersAtLevel(Side.sell, 100));
        assertEquals(expectedAskLevel2, orderBook.getOrdersAtLevel(Side.sell, 200));

        // check that an empty list is returned if there are no orders
        orderBook.deleteOrder(sellOrder2.getOrderId());
        orderBook.deleteOrder(sellOrder3.getOrderId());
        List<Order> emptyList = Arrays.asList();
        assertEquals(emptyList, orderBook.getOrdersAtLevel(Side.sell, 200));
    }

    @Test
    public void isEmpty() {
        orderBook = new OrderBook();

        //check that newly created OrderBook is empty
        assertTrue(orderBook.isEmpty());

        // check that OrderBook is empty after deleting all its orders
        Order buyOrder1 = new Order("buyOrder1", "VOD.L", Side.buy, 100, 10);
        Order buyOrder2 = new Order("buyOrder2", "VOD.L", Side.buy, 200, 50);
        Order buyOrder3 = new Order("buyOrder3", "VOD.L", Side.buy, 200, 50);

        Order sellOrder1 = new Order("sellOrder1", "VOD.L", Side.sell, 100, 10);
        Order sellOrder2 = new Order("sellOrder2", "VOD.L", Side.sell, 200, 40);
        Order sellOrder3 = new Order("sellOrder3", "VOD.L", Side.sell, 200, 30);

        //add orders to orderBook
        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(buyOrder3);
        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(sellOrder3);

        //confirm that it is not empty
        assertFalse(orderBook.isEmpty());

        //delete some orders
        orderBook.deleteOrder(buyOrder1.getOrderId());
        orderBook.deleteOrder(buyOrder2.getOrderId());
        orderBook.deleteOrder(sellOrder3.getOrderId());

        //check that it is not empty after deleting some but not all orders
        assertFalse(orderBook.isEmpty());

        //delete all order
        orderBook.deleteOrder(buyOrder3.getOrderId());
        orderBook.deleteOrder(sellOrder1.getOrderId());
        orderBook.deleteOrder(sellOrder2.getOrderId());

        //confirm that it is now empty
        assertTrue(orderBook.isEmpty());

    }

}