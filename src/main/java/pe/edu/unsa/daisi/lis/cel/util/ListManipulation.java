package pe.edu.unsa.daisi.lis.cel.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ListManipulation { 

	/**
	 * Return true if list2 is a subset of list1 by comparing editions distances between them
	 **/
	public static boolean isSubset(List<String> list1, List<String> list2) { 
		if (list1 == null || list2 == null || list1.isEmpty() || list2.isEmpty())
			return false;
		DamerauLevenshteinAlgorithm distanceAlgorithm = new DamerauLevenshteinAlgorithm(1, 1, 1, 1);
		int m = list1.size(); 
		int n = list2.size();  
		int i = 0;
		int j = 0;
		for (i = 0; i < n; i++) { 
			for (j = 0; j < m; j++) { 
				int distance = distanceAlgorithm.execute(list2.get(i).toUpperCase(), list1.get(j).toUpperCase(), 2);
				if (distance < 2)
					break; 
			}
			/* If the above inner loop was not broken at all then list2 is not present in list1 */
			if (j == m) 
				return false; 
		} 

		/* If we reach here then all elements of subSet are present in list1 */
		return true; 
	} 

	/**
	 * Copy elements, not references
	 * @param <T>
	 * @param source
	 * @param target
	 */
	public static <T> List<T>  copyList(List<T> source){
		List<T> target = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
        return target;
	}
        
	// Driver code 
	public static void main(String args[]) 
	{ 
		List<String> mainSet = new ArrayList<String>(Arrays.asList("paso 1", "paso 2", "paso 3", "paso 4")); 
		List<String> subSet = new ArrayList<String>(Arrays.asList("paso 1s", "passo 2", "Pasos 3"));

		System.out.println(mainSet.toString());

		if(isSubset(mainSet, subSet)) 
			System.out.print("arr2[] is subset of arr1[] "); 
		else
			System.out.print("arr2[] is not a subset of arr1[]");  
	} 
}
