import java.util.Collections;
import java.util.HashMap;

public class Shipment {
		private String warehouse;
		private HashMap<String, Integer> order = new HashMap<>();
		
		public Shipment(String warehouse, HashMap<String, Integer> order) {
			this.warehouse = warehouse;
			this.order.putAll(order);
		}
		
		public String getWarehouse() {
			return this.warehouse;
		}
		
		public HashMap<String, Integer> getOrder() {
			HashMap<String, Integer> orderCopy = new HashMap<>();
			orderCopy.putAll(order);
			return orderCopy;
		}
		
		public void addOrder(String item, int amount) {
			order.put(item, amount);
		}
		
	}