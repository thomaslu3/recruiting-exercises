import java.util.Collections;
import java.util.HashMap;

public class Warehouse {
		private String name;
		private HashMap<String, Integer> inventory = new HashMap<>();
		
		public Warehouse(String name, HashMap<String, Integer> inventory) {
			this.name = name;
			this.inventory.putAll(inventory);
		}
		
		public String getName() {
			return this.name;
		}
		
		public HashMap<String, Integer> getInventory() {
			HashMap<String, Integer> inventoryCopy = new HashMap<>();
			inventoryCopy.putAll(inventory);
			return inventoryCopy;
		}
		
		public void addInventory(String item, int amount) {
			inventory.put(item, amount);
		}
		
	}