import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class HeatmapModel {
	private FileReader fr;
	private int sampleNumber, positionNumber;
	ArrayList<String> genomePosition = new ArrayList<String>();
	ArrayList<String> columns = new ArrayList<String>();
	ArrayList<ArrayList<String>> table =new ArrayList<ArrayList<String>>();
	
	
	//method that creates the file chooser frame and returns the path of the selected file
	public String selectFile(HeatmapGUI gui) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("C:\\Users\\ges_k\\OneDrive\\Desktop\\summer project\\"));
		int result = fileChooser.showOpenDialog(gui);
		if (result == JFileChooser.APPROVE_OPTION) {
            File selection = fileChooser.getSelectedFile();
            return selection.getAbsolutePath(); 
		}
		return "";
	}
	
	//method that take as an argument the path of a file and reads the file
	public void readFile(String path) {
		try {

			fr=new FileReader(path);
			Scanner s =new Scanner(fr);
			while(s.hasNextLine()) {
				String line=s.nextLine();
				columns = new ArrayList<>(Arrays.asList(line.split("\t"))); //split each line and store it in an ArrayList
				sampleNumber= columns.size()-1; //store the number of samples(number of columns)
				genomePosition.add(columns.get(0)); //store the first column of the file with genome positions
				table.add(columns); //create an ArrayList with the content of each line(ArrayList of ArrayLists)

			}
			
			positionNumber=genomePosition.size()-1; //store the number of rows
		 
			
		}catch(FileNotFoundException e) {
            e.printStackTrace();
			
		}
		finally {
			try {
				fr.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	public int getPositionNumber() {
		return positionNumber;
	}

	public int getSampleNumber() {
		return sampleNumber;
	}
	
	//gets the position in the map  creates a Panel for that position and search for the number that correspond to the position 
	public JPanel gridHeatmap(int i, int j) {
		JPanel square=new JPanel();
		String number =table.get(i).get(j);
		if (number.equals("0")) {				//no mutation
			square.setBackground(Color.GRAY);
		}else if(number.equals("1")) {			//synonymoys
			square.setBackground(Color.GREEN);
		}else if(number.equals("-1")) {			//non-synonymous
			square.setBackground(Color.PINK);
		}else if(number.equals("2")){			//insertions
			square.setBackground(Color.YELLOW);
		}else if(number.equals("3")){			//deletions
			square.setBackground(Color.RED);
		}

         square.setBorder(BorderFactory.createLineBorder(Color.WHITE)); //set white border around the panel

	return square;
	}

}