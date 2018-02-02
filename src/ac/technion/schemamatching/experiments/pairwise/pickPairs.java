package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class pickPairs {
	    public static void main(String[] args) {
	        int size = 151;

	        ArrayList<Integer> list = new ArrayList<Integer>(size);
	        for(int i = 1; i <= size; i++) {
	            list.add(i);
	        }
//	        list.remove((Integer) 407);
//	        list.remove((Integer) 420);
	        list.remove((Integer) 71);
	        list.remove((Integer) 102);
	        list.remove((Integer) 101);
	        list.remove((Integer) 85);
	        list.remove((Integer) 135);
	        list.removeAll(new ArrayList<>(Arrays.asList()));
	        Random rand = new Random();
	        int numWantedPairs = 151-1-5;
	        int initSize = list.size();
	        System.out.println(list.size());
	        while(list.size() > (initSize-numWantedPairs)) {
	            int index = rand.nextInt(list.size());
	            System.out.print(list.remove(index) + ",");
	        }
	    }
	}
