# OrderBookManager

Exchanges use order books to manage/keep track of buy and sell order of financial instruments.
orders for different instruments are kept separate and buy and sell order for the same instrument are matched
using the order book.

##### This project is an implementation of an orderbook manager that has the following features.
* Order books have 2 sides. buy and sell.
* The sides are sorted in order or price levels. descending for buy orders and ascending for sell orders.
* Orders of the price are sorted in the order they arrive.
##### Possible operations include
1. Adding an order for instrument.
    * Given the instrument and the side, and order is added to the appropriate book.
    * Each new order is assigned a unique id.
2. Deleting a specific order.
    * Given the unique id of an existing order, it is removed from its order book completely.
3. Modifying the quantity of an existing order.
    * Given the unique id, and quantity, the quantity of an existing order can be modified.
    * If the quantity increases, the order is placed at the end of its price level.
    * If the quantity decreases, the order remains in it current position within its price level.
4. Getting best buy and sell prices.
    * Since buy/sell orders are maintained in a very specific order, best buy price (highest) is always the first order and the best
    sell price (lowest) is always the first order. 
5. Getting the number of orders on a level and side of a book.
6. Getting the total tradeable quantity of orders for an instrument, level and side of a book.
7. Getting the total tradeable volume (quantity * price) for an instrument, level and side of a book.
8. Getting a list of all orders on a level and side of a book in correct order.

###### Unit tests have been implemented to validate the above mentioned features.

###### Possible additions
* Order matching.
* REST endpoints.
* User interface/Web client.


