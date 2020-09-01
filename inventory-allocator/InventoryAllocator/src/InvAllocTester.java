import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

class InvAllocTester {
	
	public static String getShipments(List<Shipment> shipments) {
		String result = "";
		result += "[";
		for(int i = 0; i < shipments.size(); i++) {
			result += "{ ";
			
			result += shipments.get(i).getWarehouse() + ": ";
			
			HashMap<String, Integer> order = shipments.get(i).getOrder();
			int counter = 0;
			
			for(String item : order.keySet()) {
				counter ++;
				result += "{ " + item + ": " + order.get(item) + " }";
				if(counter <= order.size() - 1) {
					result += ", ";
				}
			}
			
			result += " }";
			if(i < shipments.size() - 1) {
				result += ", ";
			}
			
		}
		result += "]";
		return result;
	}
	
	/** Test A */
	//	Exact inventory match
	//	Input: { apple: 1 }, [{ name: owd, inventory: { apple: 1 } }]
	//	Output: [{ owd: { apple: 1 } }]
	@Test
	void testA() {

		HashMap<String, Integer> orderA = new HashMap<>();
		orderA.put("apple", 1);
		
		List<Warehouse> warehousesA = new ArrayList<Warehouse>();
		Warehouse wA1 = new Warehouse("owd", new HashMap<String, Integer>());
		wA1.addInventory("apple", 1);
		warehousesA.add(wA1);
		
		InventoryAllocator testA = new InventoryAllocator(orderA, warehousesA);
		
		String result = getShipments(testA.getCheapestShipments());
		String expected = "[{ owd: { apple: 1 } }]";
		assertEquals(result, expected);
	}
	
	/** Test B */
	//	Not enough allocations
	//	Input: { apple: 1 }, [{ name: owd, inventory: { apple: 0 } }]
	//	Output: []
	@Test
	void testB() {
		HashMap<String, Integer> orderB = new HashMap<>();
		orderB.put("apple", 1);
		
		List<Warehouse> warehousesB = new ArrayList<Warehouse>();
		Warehouse wB1 = new Warehouse("owd", new HashMap<String, Integer>());
		wB1.addInventory("apple", 0);
		warehousesB.add(wB1);
		
		InventoryAllocator testB = new InventoryAllocator(orderB, warehousesB);
		String result = getShipments(testB.getCheapestShipments());
		String expected = "[]";
		assertEquals(result, expected);
	}
	
	/** Test C */
	//	Should split an item across warehouses if that is the only way to completely ship an item:
	//	Input: { apple: 10 }, [{ name: owd, inventory: { apple: 5 } }, { name: dm, inventory: { apple: 5 }}]
	//	Output: [{ dm: { apple: 5 }}, { owd: { apple: 5 } }]
	@Test
	void testC() {
		HashMap<String, Integer> orderC = new HashMap<>();
		orderC.put("apple", 10);
		
		List<Warehouse> warehousesC = new ArrayList<Warehouse>();
		Warehouse wC1 = new Warehouse("owd", new HashMap<String, Integer>());
		wC1.addInventory("apple", 5);
		warehousesC.add(wC1);
		
		Warehouse wC2 = new Warehouse("dm", new HashMap<String, Integer>());
		wC2.addInventory("apple", 5);
		warehousesC.add(wC2);
		
		InventoryAllocator testC = new InventoryAllocator(orderC, warehousesC);
		String result = getShipments(testC.getCheapestShipments());
		String expected = "[{ owd: { apple: 5 } }, { dm: { apple: 5 } }]";
		assertEquals(result, expected);
	}
	
	/** Test D */
	//	Order more than one item from the same warehouse:
	//	Input: { apple: 5, orange: 5 }, [{ name: owd, inventory: { apple: 5, orange: 5 } }]
	//	Output: [{ owd: { apple: 5, orange: 5 }}]
	@Test
	void testD() {
		HashMap<String, Integer> orderD = new HashMap<>();
		orderD.put("apple", 5);
		orderD.put("orange", 5);
		
		List<Warehouse> warehousesD = new ArrayList<Warehouse>();
		Warehouse wD1 = new Warehouse("owd", new HashMap<String, Integer>());
		wD1.addInventory("apple", 5);
		wD1.addInventory("orange", 5);
		warehousesD.add(wD1);
		
		InventoryAllocator testD = new InventoryAllocator(orderD, warehousesD);
		String result = getShipments(testD.getCheapestShipments());
		String expected = "[{ owd: { orange: 5 }, { apple: 5 } }]";
		assertEquals(result, expected);
	}
	
	/** Test E */
	//	Order two items from two different warehouses:
	//	Input: { apple: 5, orange: 5 }, [{ name: owd, inventory: { apple: 5, orange: 0 } }, { name: dm, inventory: { orange: 5 } }]
	//	Output: [{ owd: { apple: 5 } }, { dm: { orange: 5 } }]
	@Test
	void testE() {
		HashMap<String, Integer> orderE = new HashMap<>();
		orderE.put("apple", 5);
		orderE.put("orange", 5);
		
		List<Warehouse> warehousesE = new ArrayList<Warehouse>();
		Warehouse wE1 = new Warehouse("owd", new HashMap<String, Integer>());
		wE1.addInventory("apple", 5);
		wE1.addInventory("orange", 0);
		warehousesE.add(wE1);
		
		Warehouse wE2 = new Warehouse("dm", new HashMap<String, Integer>());
		wE2.addInventory("orange", 5);
		warehousesE.add(wE2);
		
		InventoryAllocator testE = new InventoryAllocator(orderE, warehousesE);
		String result = getShipments(testE.getCheapestShipments());
		String expected = "[{ owd: { apple: 5 } }, { dm: { orange: 5 } }]";
		assertEquals(result, expected);
		
	}
	
	/** Test F */
	//	Should split items across warehouses if that is the only way to completely ship multiple items:
	//	Input: { apple: 10, orange: 10 }, [{ name: owd, inventory: { apple: 5, orange: 0 } }, 
	//	{ name: dm, inventory: { orange: 5 } }, 
	//	{ name: xyz, inventory: { apple: 5, orange: 5 } }]
	//	Output: [{ owd: { apple: 5 } }, { dm: { orange: 5 } }, { xyz: { apple: 5, orange: 5 } }]
	@Test
	void testF() {
		HashMap<String, Integer> orderF = new HashMap<>();
		orderF.put("apple", 10);
		orderF.put("orange", 10);
		
		List<Warehouse> warehousesF = new ArrayList<Warehouse>();
		Warehouse wF1 = new Warehouse("owd", new HashMap<String, Integer>());
		wF1.addInventory("apple", 5);
		wF1.addInventory("orange", 0);
		warehousesF.add(wF1);
		
		Warehouse wF2 = new Warehouse("dm", new HashMap<String, Integer>());
		wF2.addInventory("orange", 5);
		warehousesF.add(wF2);
		
		Warehouse wF3 = new Warehouse("xyz", new HashMap<String, Integer>());
		wF3.addInventory("apple", 5);
		wF3.addInventory("orange", 5);
		warehousesF.add(wF3);
		
		InventoryAllocator testF = new InventoryAllocator(orderF, warehousesF);
		String result = getShipments(testF.getCheapestShipments());
		String expected = "[{ owd: { apple: 5 } }, { dm: { orange: 5 } }, { xyz: { orange: 5 }, { apple: 5 } }]";
		assertEquals(result, expected);
	}
	
	/** Test G */
	//	Should have items not in the first warehouse
	//	Input: { apple: 5 }, [{ name: owd, inventory: { } }, 
	//	{ name: dm, inventory: { apple: 5 } }]
	//	Output: [{ dm: { apple: 5 } }]
	@Test
	void testG() {
		HashMap<String, Integer> orderG = new HashMap<>();
		orderG.put("apple", 5);
		
		List<Warehouse> warehousesG = new ArrayList<Warehouse>();
		Warehouse wG1 = new Warehouse("owd", new HashMap<String, Integer>());
		warehousesG.add(wG1);
		
		Warehouse wG2 = new Warehouse("dm", new HashMap<String, Integer>());
		wG2.addInventory("apple", 5);
		warehousesG.add(wG2);
		
		InventoryAllocator testG = new InventoryAllocator(orderG, warehousesG);
		String result = getShipments(testG.getCheapestShipments());
		String expected = "[{ dm: { apple: 5 } }]";
		assertEquals(result, expected);
	}
	
	/** Test H */
	//	Empty order
	//	Input: {}, [{ name: owd, inventory: { apple: 5 } }]
	//	Output: []
	@Test
	void testH() {
		HashMap<String, Integer> orderH = new HashMap<>();
		
		List<Warehouse> warehousesH = new ArrayList<Warehouse>();
		Warehouse wH1 = new Warehouse("owd", new HashMap<String, Integer>());
		wH1.addInventory("apple", 5);
		warehousesH.add(wH1);
		
		InventoryAllocator testH = new InventoryAllocator(orderH, warehousesH);
		String result = getShipments(testH.getCheapestShipments());
		String expected = "[]";
		assertEquals(result, expected);
	}
	
	/** Test I */
	//	Enough for one item but not for the other
	//	Input: { apple: 5, orange: 5 }, [{ name: owd, inventory: { orange: 3 } }, 
	//	{ name: dm, inventory: { apple: 5 } }]
	//	Output: [{ owd: { orange: 3 } }, { dm: { apple: 5 } }]
	@Test
	void testI() {
		HashMap<String, Integer> orderI = new HashMap<>();
		orderI.put("apple", 5);
		orderI.put("orange", 5);
		
		List<Warehouse> warehousesI = new ArrayList<Warehouse>();
		Warehouse wI1 = new Warehouse("owd", new HashMap<String, Integer>());
		wI1.addInventory("orange", 3);
		warehousesI.add(wI1);
		
		Warehouse wI2 = new Warehouse("dm", new HashMap<String, Integer>());
		wI2.addInventory("apple", 5);
		warehousesI.add(wI2);
		
		InventoryAllocator testI = new InventoryAllocator(orderI, warehousesI);
		String result = getShipments(testI.getCheapestShipments());
		String expected = "[{ owd: { orange: 3 } }, { dm: { apple: 5 } }]";
		assertEquals(result, expected);
	}
	
	/** Test J */
	//	Different item in warehouse
	//	Input: { apple: 5 }, [{ name: owd, inventory: { orange: 3 } }]
	//	Output: []
	@Test
	void testJ() {
		HashMap<String, Integer> orderJ = new HashMap<>();
		orderJ.put("apple", 5);
		
		List<Warehouse> warehousesJ = new ArrayList<Warehouse>();
		Warehouse wJ1 = new Warehouse("owd", new HashMap<String, Integer>());
		wJ1.addInventory("orange", 3);
		warehousesJ.add(wJ1);
		
		InventoryAllocator testJ = new InventoryAllocator(orderJ, warehousesJ);
		String result = getShipments(testJ.getCheapestShipments());
		String expected = "[]";
		assertEquals(result, expected);
	}

}
