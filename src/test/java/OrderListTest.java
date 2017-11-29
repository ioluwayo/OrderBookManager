import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by ioluwayo. email: ioluwayo@gmail.com
 */
public class OrderListTest {
    OrderList orderList;

    @Test
    public void getOrders() throws Exception {
        orderList = new OrderList();
        //create orders
        Order order1 = new Order("order1", "VOD.L", Side.buy, 200, 10);
        Order order2 = new Order("order2", "VOD.L", Side.buy, 100, 25);
        //add orders to list
        orderList.addOrder(order1);
        orderList.addOrder(order2);
        // create list of actual orders
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        // compare both Lists... This implicitly tests for proper ordering of orders as they are added.
        assertTrue(orders.equals(orderList.getOrders()));
    }
    @Test
    public void getTotalTradeableQuantity() throws Exception {
        orderList = new OrderList();
        //create orders and add them to orderList
        Order order1 = new Order("order1", "VOD.L", Side.buy, 200, 10);
        Order order2 = new Order("order2", "VOD.L", Side.buy, 100, 25);
        orderList.addOrder(order1);
        orderList.addOrder(order2);
        //compute actual value
        long actualTotalTradeableQuantity = order1.getQuantity() + order2.getQuantity();//25
        assertEquals(actualTotalTradeableQuantity, orderList.getTotalTradeableQuantity()); //(expected,actual)
    }

    @Test
    public void getTotalTradeableVolume() throws Exception {
        orderList = new OrderList();
        //create orders and add them to orderList
        Order order1 = new Order("order1", "VOD.L", Side.buy, 500, 1);
        Order order2 = new Order("order2", "VOD.L", Side.buy, 2, 5);
        orderList.addOrder(order1);
        orderList.addOrder(order2);
        //compute actual value
        long order1Volume = order1.getPrice() * order1.getQuantity();//500
        long order2Volume = order2.getPrice() * order2.getQuantity();//10
        long actualTotalTradeableVolume = order1Volume + order2Volume;//510
        assertEquals(actualTotalTradeableVolume, orderList.getTotalTradeableVolume()); //(expected,actual);

    }

    @Test
    public void addOrder() throws Exception {
        orderList = new OrderList();
        //create orders and add them to orderList
        Order order1 = new Order("order1", "VOD.L", Side.buy, 500, 1);
        Order order2 = new Order("order2", "VOD.L", Side.buy, 2, 5);
        Order order3 = new Order("order3", "VOD.L", Side.buy, 40, 12);
        orderList.addOrder(order1);
        orderList.addOrder(order2);
        orderList.addOrder(order3);
        //get order from orderList
        ArrayList<Order> orders = orderList.getOrders();
        //check that orders were appended to the end of the list as they arrive
        int indexOfLastOrder = orders.size() - 1;
        assertTrue(order3.equals(orders.get(indexOfLastOrder)));
        assertTrue(order2.equals(orders.get(indexOfLastOrder - 1))); //second to last order
        assertTrue(order1.equals(orders.get(indexOfLastOrder - 2))); //first item in the list

    }

    @Test
    public void deleteOrder() throws Exception {
        orderList = new OrderList();
        //create orders and add them to orderList
        Order order1 = new Order("order1", "VOD.L", Side.buy, 500, 11);
        Order order2 = new Order("order2", "VOD.L", Side.buy, 2, 5);
        orderList.addOrder(order1);
        orderList.addOrder(order2);
        long totalTradeableVolumeBeforeDeletion = orderList.getTotalTradeableVolume();
        long totalTradeableQuantityBeforeDeletion = orderList.getTotalTradeableQuantity();
        // check that the orderList does indeed contain the added order
        assertTrue(orderList.getOrders().contains(order1));

        //delete the added order
        orderList.deleteOrder(order1.getOrderId());

        //check that it is no longer in the list of orders
        assertFalse(orderList.getOrders().contains(order1));

        //check that the following fields were updated correctly
        long tradeableVolumeAfterDeletion = totalTradeableVolumeBeforeDeletion - order1.getQuantity() * order1.getPrice();
        long tradeableQuantityAfterDeletion = totalTradeableQuantityBeforeDeletion - order1.getQuantity();
        assertTrue(tradeableQuantityAfterDeletion == orderList.getTotalTradeableQuantity());
        assertTrue(tradeableVolumeAfterDeletion == orderList.getTotalTradeableVolume());

        //check that fields are zero for an empty OrderList
        orderList.deleteOrder(order2.getOrderId()); // empty now as only 2 orders were initially added
        assertTrue(0 == orderList.getTotalTradeableQuantity());
        assertTrue(0 == orderList.getTotalTradeableVolume());

    }

    @Test
    public void modifyOrder() throws Exception {
        orderList = new OrderList();

        //create orders and add them to orderList
        Order order1 = new Order("order1", "VOD.L", Side.buy, 500, 11);
        Order order2 = new Order("order2", "VOD.L", Side.buy, 2, 5);
        orderList.addOrder(order1);
        orderList.addOrder(order2);
        long volumeBeforeChange = order1.getPrice() * order1.getQuantity(); // volume of this order: 5500
        long quantityBeforeChange = order1.getQuantity(); //quantity of this order: 11
        long totalTradeableVolumeBeforeChange = orderList.getTotalTradeableVolume(); //total volume: 5510
        long totalTradeableQuantityBeforeChange = orderList.getTotalTradeableQuantity(); // total quantity: 16

        //modify order1
        long newQuantity = 10;
        orderList.modifyOrder(order1.getOrderId(), newQuantity);
        long order1VolumeAfterChange = newQuantity * order1.getPrice(); // the new volume of this order: 5000

        //check that the quantity of correct order was changed
        // order1 is the first order added so its at index 0
        assertEquals(newQuantity, orderList.getOrders().get(0).getQuantity()); //(expected,actual)

        //check that the quantity of order2 was not changed. only order one should be changed
        assertFalse(newQuantity == orderList.getOrders().get(1).getQuantity());

        //check that the totalTradeableQuantity of all orders is correctly updated
        long expectedTotalTradeableQuantityAfterChange =
                newQuantity + totalTradeableQuantityBeforeChange - quantityBeforeChange;// 15
        assertEquals(expectedTotalTradeableQuantityAfterChange, orderList.getTotalTradeableQuantity());

        // check that the totalTradeableVolume of all order is correctly updated
        long expectedTotalTradeableVolumeAfterChange =
                order1VolumeAfterChange + totalTradeableVolumeBeforeChange - volumeBeforeChange;// 5010
        assertEquals(expectedTotalTradeableVolumeAfterChange, orderList.getTotalTradeableVolume()); //(expected,actual)
    }

}