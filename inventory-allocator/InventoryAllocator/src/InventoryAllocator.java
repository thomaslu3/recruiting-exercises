import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryAllocator {
	private HashMap<String, Integer> order = new HashMap<String, Integer>();
	private List<Warehouse> warehouses = new ArrayList<Warehouse>();
	
	public InventoryAllocator(HashMap<String, Integer> order, List<Warehouse> warehouses) {
		this.order = order;
		this.warehouses.addAll(warehouses);
	}
	
	// Assumptions:
	// Order items are all lowercase and normalized (no mistakes in order item names)
	// All order item amounts and warehouse inventory amounts >= 0
	// O(W*I) time complexity where W is the number of warehouses and I is the number of types of items in the order
	public List<Shipment> getCheapestShipments() {
		// initialize cheapestShipments list
		List<Shipment> cheapestShipments = new ArrayList<>();
		
		// iterate through warehouses
		for(int i = 0; i < warehouses.size(); i++) {
			// if the order isn't empty and you haven't gone through all warehouses, check warehouse i for items
			if(!order.isEmpty()) {		
				// create a new Shipment for warehouse i
				Shipment s = new Shipment(warehouses.get(i).getName(), new HashMap<String, Integer>());
				List<String> toRemove = new ArrayList<String>();
				for(String item : order.keySet()) {
					// if the warehouse has item, add available inventory up to order amount to shipment
					// and remove shipment item amount from order
					if(warehouses.get(i).getInventory().containsKey(item)) {
						int orderCount = order.get(item);
						int warehouseCount = warehouses.get(i).getInventory().get(item);
						
						if(warehouseCount >= orderCount) {
							s.addOrder(item, orderCount);
							toRemove.add(item);
						} else if(warehouseCount != 0){
							s.addOrder(item, warehouseCount);
							order.put(item, orderCount - warehouseCount);
						}
					}
				}
				order.remove(toRemove);
				// only add shipment if there are items in the shipment
				if(!s.getOrder().isEmpty()) {
					cheapestShipments.add(s);
				}
			} else {
				return cheapestShipments;
			}
		}
		return cheapestShipments;
	}
	
}
