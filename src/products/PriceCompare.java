package products;

import java.util.Comparator;

//Price Compare for Sorting the Product List in ascending Retail Price
public class PriceCompare implements Comparator<Product> {
	public int compare(Product p1, Product p2) {
		if (p1.retailCost() < p2.retailCost()) return -1;
		if (p1.retailCost() > p2.retailCost()) return 1;
		else return 0;
	}
}
