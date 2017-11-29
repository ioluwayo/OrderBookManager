import java.util.ArrayList;

/**
 * Created by ioluwayo. email: ioluwayo@gmail.com
 * <p>
 * Represents a list of orders.
 */
public class OrderList {
    private ArrayList<Order> orders;

    /**
     * Fields to provide constant lookup time.
     * Updated after every modify/delete/add operation on the list.
     * So they wouldn't need to be computed when required.
     */
    private long totalTradeableQuantity; //sum of the quantity of orders in an orderList
    private long totalTradeableVolume; // sum of the volume of orders in an orderList. order volume = price*quantity

    /**
     * Default constructor
     * Orders should be added using the addOrder method.
     */
    public OrderList() {
        this.orders = new ArrayList<>();
        this.totalTradeableQuantity = 0;
        this.totalTradeableVolume = 0;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public long getNumberOfOrders() {
        return orders.size();
    }

    public long getTotalTradeableQuantity() {
        return totalTradeableQuantity;
    }


    public long getTotalTradeableVolume() {
        return totalTradeableVolume;
    }

    private void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    private void setTotalTradeableQuantity(long totalTradeableQuantity) {
        this.totalTradeableQuantity = totalTradeableQuantity;
    }
    private void setTotalTradeableVolume(long totalTradeableVolume) {
        this.totalTradeableVolume = totalTradeableVolume;
    }

    /**
     * Adds an order to the list of orders and updates the totalTradeableQuantity and totalTradeableVolume fields
     *
     * @param order the order to be added to list.
     */
    public void addOrder(Order order) {
        orders.add(order); // appends to end of list

        // update the fields to reflect the addition
        totalTradeableQuantity += order.getQuantity();
        totalTradeableVolume += order.getPrice() * order.getQuantity();
    }

    /**
     * Permanently deletes the order corresponding to the Id provided from the list of orders.
     * If no order matches the provided Id it does nothing.
     * updates the totalTradeableQuantity and totalTradeableVolume fields to reflect the change.
     *
     * @param orderId the id of the order to be deleted.
     */
    public void deleteOrder(String orderId) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.getOrderId() == orderId) {
                orders.remove(i); // removes and shifts subsequent elements to the left(subtracts 1 from their indices
                totalTradeableQuantity -= order.getQuantity(); //update the fields to reflect change
                totalTradeableVolume -= order.getPrice() * order.getQuantity();
                break; // absolutely no need to go further. only 1 order can match this id.
            }
        }
    }

    /**
     * Changes the quantity of the order in the list of orders corresponding to the Id provided.
     * If no order matches the provided id, it does nothing.
     * Updates the totalTradeableQuantity and totalTradeableVolume fields to reflect the change.
     * Moves the order to the end of the list if the quantity increases.
     *
     * @param orderId     the id of the order to be modified. only the quantity of an order can be changed.
     * @param newQuantity new quantity for the order.
     */
    public void modifyOrder(String orderId, long newQuantity) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.getOrderId() == orderId) {
                long currentQuantity = order.getQuantity();

                //The total totalTradeable fields are going to change in both cases.
                totalTradeableQuantity -= order.getQuantity();
                totalTradeableVolume -= order.getPrice() * order.getQuantity();

                if (newQuantity > currentQuantity) {
                    orders.remove(order); // order has to be placed at the end of the list, so remove it.
                    order.setQuantity(newQuantity);
                    addOrder(order);// add to end of the list. method also handles updating the relevant fields.
                } else {
                    order.setQuantity(newQuantity);// just change the quantity and leave the order in current position

                    //update the totalTradeable fields to reflect changes
                    totalTradeableQuantity += order.getQuantity();
                    totalTradeableVolume += order.getPrice() * order.getQuantity();
                }
                return; // absolutely no need to go further. only 1 order can match this id.
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderList orderList = (OrderList) o;

        if (totalTradeableQuantity != orderList.totalTradeableQuantity) return false;
        if (totalTradeableVolume != orderList.totalTradeableVolume) return false;
        return orders != null ? orders.equals(orderList.orders) : orderList.orders == null;
    }

    @Override
    public int hashCode() {
        int result = orders != null ? orders.hashCode() : 0;
        result = 31 * result + (int) (totalTradeableQuantity ^ (totalTradeableQuantity >>> 32));
        result = 31 * result + (int) (totalTradeableVolume ^ (totalTradeableVolume >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "OrderList{" +
                "orders=" + orders +
                ", totalTradeableQuantity=" + totalTradeableQuantity +
                ", totalTradeableVolume=" + totalTradeableVolume +
                '}';
    }
}
