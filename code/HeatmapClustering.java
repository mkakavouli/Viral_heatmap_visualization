import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;

public class HeatmapClustering {
	private Hashtable<String, Integer> mutationHash;
	private String[] samplesNames;
	private String mutationType;
	private ArrayList<String> clusteredLine = new ArrayList<String>();
	private ArrayList<ArrayList<String>> clusteredTable ;
	private ArrayList<String> sortedLine = new ArrayList<String>();
	private ArrayList<ArrayList<String>> sortedTable ;
	
	//performs hierarchical clustering on mutation data
	public ArrayList<ArrayList<String>> clusterData(ArrayList<ArrayList<String>> mutationData) {
		int samples = mutationData.get(0).size() - 11;
		int positions = mutationData.size() - 1;
		int data[][] = new int[positions][samples];
		double dist[][] = new double[samples][samples];
		samplesNames = new String[samples];
		clusteredTable = new ArrayList<ArrayList<String>>();
		
		//create a hashtable to connect the types of mutation with their corresponding values
		mutationHash = new Hashtable<String, Integer>();
		mutationHash.put("Non", -1);
		mutationHash.put("NCo", -2);
		mutationHash.put("Syn", 1);
		mutationHash.put("Ins", 2);
		mutationHash.put("Del", 3);
		mutationHash.put(".", 0);

		//create a 2d array with the values for all the mutation of each sample
		for (int i = 0; i < positions; i++) {
			for (int j = 0; j < samples; j++) {
				samplesNames[j] = mutationData.get(0).get(j + 11);
				
				//extracts from samples their mutation type
				if (mutationData.get(i + 1).get(j + 11).equals(".")) {
					mutationType = mutationData.get(i + 1).get(j + 11).substring(0, 1);

				} else {
					mutationType = mutationData.get(i + 1).get(j + 11).substring(0, 3);
				}

				data[i][j] = mutationHash.get(mutationType); //populates the array with the appropriate number based on the type of mutation

			}
		}

		
		for (int i = 0; i < samples; i++) {
			for (int j = 0; j < samples; j++) {
				// Calculate distance between i and j summing up distance at all genome positions
				for (int k = 0; k < positions; k++) {
					// as can have -ve andd +ve numbers, square then square root
					dist[i][j] += Math.sqrt(Math.pow(data[k][i] - data[k][j], 2));
				}
			}
		}
		
		String clusters[] = new String[samples];
		for (int i = 0; i < clusters.length; i++) {
			// each cluster starts off with just one sample
			clusters[i] = "" + i;
		}
		
		String finalCluster = "";

		// an array of booleans is created to store if a sample has been clustered or not. 
		//If a sample has been clustered it'll be ignored in future clustering 
		
		boolean clustered[] = new boolean[samples];
		for (int i = 0; i < clustered.length; i++) {
			clustered[i] = false;
		}

		// loop to tell when clustering is done
		// clustering is done when only 1 sample remains unclustered (clustered=false)
		boolean clustering = true;

		while (clustering) {

			// set minDist to some super high number
			double minDist = 10000000;
			int toClust1 = 0;
			int toClust2 = 0;

			// find the minimum distance in the entire matrix
			// if distance is tied, this will simply use the first one we come to
			for (int i = 0; i < dist.length; i++) {
				// only use i if not already been clustered
				if (!clustered[i]) {
					for (int j = 0; j < dist[i].length; j++) {
						// only use j if not already been clustered
						// also ignore if i==j as can't cluster with itself
						if (!clustered[j] & i != j) {
							if (dist[i][j] < minDist) {
								minDist = dist[i][j];
								toClust1 = i;
								toClust2 = j;
							}
						}
					}
				}
			}

			
			//the sample name(s) of toCLust2 is added to the names of toClust1 
			// and isClust2 set clustered
			clustered[toClust2] = true;
			clusters[toClust1] += " " + clusters[toClust2];


			// Now as we are clustering we then to adjust the distances
			// If 2 samples merge keep the minimum distance from either of these 2
			// samples to all the other samples loop through all the distances of toClust1 sample
			
			for (int i = 0; i < dist[toClust1].length; i++) {
				
				// if the distance between toClust2 & i is less than the distance between
				// toClust1 & i, use the lower distance, if equal then nothing needs to be done
				
				if (dist[toClust1][i] > dist[toClust2][i]) {
					dist[toClust1][i] = dist[toClust2][i];
					
					// as the distance matrix is symmetrical we also set the distance the opposite
					// distance between i and clust1 = distance between clust1 and i
					
					dist[i][toClust1] = dist[i][toClust2];
				}
			}

			//count the samples that haven't been clustered
			
			int stillToCluster = 0;
			for (int i = 0; i < clustered.length; i++) {
				if (!clustered[i]) {
					stillToCluster++;
					// when we exit the loop, the finalCluster we be set to the final cluster
					finalCluster = clusters[i];
				}
			}

			// if more than one sample to be clustered then keep on going
			clustering = false;
			if (stillToCluster > 1) {
				clustering = true;
			}

		}
	
		//split the final Cluster at every space
		String splits[] = finalCluster.split(" ");
		//create an int array to hold the number of the columns in the clustered order
		int newOrder[] = new int[splits.length];
		for (int i = 0; i < splits.length; i++) {
			if(!(splits[i].equals(""))){
			newOrder[i] = Integer.parseInt(splits[i]); //convert the number from a string format to an integer and store it in the array
			}
		}
		
		//recreate each line of the original data with columns having the clustered order
		for (int i = 0; i < positions + 1; i++) {
			clusteredLine = new ArrayList<String>();
			//the first 10 columns have infos about genome position and don't have to change their order
			for (int k = 0; k < 11; k++) {
				clusteredLine.add(mutationData.get(i).get(k));
			}
			//the rest of columns are inserted in each line based on the clustered order
			for (int j : newOrder) {

				clusteredLine.add(mutationData.get(i).get(j + 11));
			}
			clusteredTable.add(clusteredLine); //add each line in the arrayList 
		}

		return clusteredTable;

	}
	
	//create a new ArrayList in which the samples are displaying in ascending date order
	public ArrayList<ArrayList<String>> sortDateData(ArrayList<ArrayList<String>> mutationData) {
		int samples = mutationData.get(0).size();
		int positions = mutationData.size() - 1;
		sortedTable = new ArrayList<ArrayList<String>>();
		ArrayList<Dates> sampleDate = new ArrayList<Dates>();
		ArrayList<Dates> sortedDates = new ArrayList<Dates>();
		/*
		 * extracts the dates from the sample name, converts them in LocalDate object, creates Dates Object 
		 * and populates an ArrayList with those Dates objects  
		 */
		for (int i = 11; i < samples; i++) {
			String[] dataDetails = mutationData.get(0).get(i).split("\\|");
			String dateString = dataDetails[2];
			//if only year or year and month is avalaible complete the missing info with "-01"
			if(dateString.length()==4) {
				dateString+="-01-01";
			}else if(dateString.length()==7) {
				dateString+="-01";
			}
			//if the date is not in "yyyy-mm-dd" format convert it
			else if (dateString.length()==9) {
				dateString=dateString.substring(0,5)+"0"+dateString.substring(5, 9);
			}else if (dateString.length()==8) {
				dateString=dateString.substring(0,5)+"0"+dateString.substring(5, 7)+"0"+dateString.substring(7, 8);
			}
			
			//convert that String containing the date to a date Object
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
			LocalDate date = LocalDate.parse(dateString, dateFormatter);
		
			Dates dateObject= new Dates(date,i);	
			sampleDate.add(dateObject);
		}
		sortedDates=sort(sampleDate);  //call the sort method to sort the sampleDate ArrayList
		
		//recreate the original data and change the order of the samples based on the newOrder given from the sort method
		for (int i = 0; i < positions + 1; i++) {
			sortedLine = new ArrayList<String>();
			
			//the first 10 columns have informations about the genome positions and they have to remain as they are
			for (int k = 0; k < 11; k++) {
				sortedLine.add(mutationData.get(i).get(k));
			}
			
			//for the remain columns,in each line, the data are inserted based on the date order
			for(int j=0; j<sortedDates.size();j++) {
				sortedLine.add(mutationData.get(i).get(sortedDates.get(j).getIndex()));
			}
			sortedTable.add(sortedLine); //each line is inserted in the sorted table
		}

		return sortedTable;
		
	}
	
	//sort ascending order the dates
	public ArrayList<Dates> sort(ArrayList<Dates> d) {
		boolean change;
		do {
			change = false;
			for (int i = 0; i < d.size() - 1; i++) {
				//check if date is after the date of the next position and if it is, we change the positions of the two dates
				if (d.get(i).getDate().isAfter(d.get(i+1).getDate())) {
					change = true;
					Dates tempDate = d.get(i);
					d.set(i,d.get(i+1))  ;
					d.set(i+1,tempDate);
				}
			}
		} while (change); //when no position is changed the loop stops
		return d;
	}
	

}

//create dates object with the date and the index in the unsorted table
class Dates {
	private LocalDate date;
	private int index;

	public Dates(LocalDate d, int i) {
		date = d;
		index = i;
	}

	public LocalDate getDate() {
		return date;
	}
	public int getIndex() {
		return index;
	}
}
