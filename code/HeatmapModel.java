import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

public class HeatmapModel {
	private FileReader fr;
	private LocalDate minPastDate, maxDate;
	private int[] sampleIndex,posIndex;
	private int startColumn=10;
	
	ArrayList<String> tableLine = new ArrayList<String>();
	ArrayList<String> samples = new ArrayList<String>();
	ArrayList<String> countries = new ArrayList<String>();
	ArrayList<String> dates = new ArrayList<String>();
	ArrayList<String> globalLineage = new ArrayList<String>();
	ArrayList<String> UKLineage = new ArrayList<String>();
	ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> customTable = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> filteredTable;
	

	// method that creates the file chooser frame and returns the path of the
	// selected file to import
	public String selectFile(HeatmapGUI gui) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(
				new File("C:\\Users\\ges_k\\OneDrive\\Desktop\\summer project\\VirusHeatmap-master\\Case_studies\\"));
		int result = fileChooser.showOpenDialog(gui);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selection = fileChooser.getSelectedFile();
			return selection.getAbsolutePath();
		}
		return "";
	}

	// method that reads the file and store data in ArrayLists
	public void readFile(String path) {
		try {
			
			//read file
			fr = new FileReader(path);
			Scanner s = new Scanner(fr);
			
			//loop continues as long as there is a next line
			while (s.hasNextLine()) {
				String line = s.nextLine();
				
				// the imported files are tab delimited so the line is split in tabs 
				// and every value in the line is stored in a arraylist
				
				tableLine = new ArrayList<>(Arrays.asList(line.split("\t")));
				
				//the ArrayList of each line is stored in two other Arraylists creating a two 2D Arraylists
				//the one ArrayList will remain unchanged(table) 
				//and the other(customTable) will change according to user actions
				
				table.add(tableLine);
				customTable.add(tableLine);
			}
		

			//loop around table to store the sample names
			
			for (int i = startColumn+1; i < table.get(0).size(); i++) {
				String[] sampleDetails = table.get(0).get(i).split("\\|");
				
				//if the lineage codes are unknown they are set to null to the sample name
				//and the changed sample name is stored again in the two 2D arrayLists
				
				if (sampleDetails.length < 4) {
					String temp = table.get(0).get(i) + "|NULL|NULL";
					table.get(0).set(i, temp);
					customTable.get(0).set(i, temp);
				}
				
				//save the sample name in ArrayList
				samples.add(table.get(0).get(i));
			}
			
			
			for (int i = 0; i < samples.size(); i++) {
				//split the names by '/' to extract region names
				//split the names by '|' to extract dates and lineage codes (Global and UK)
				
				String[] sampleRegion = samples.get(i).split("/");
				String[] sampleDate = samples.get(i).split("\\|");
				
				if (countries.size() == 0) { //if the ArrayList is empty add the region name
					countries.add(sampleRegion[1]);

				} else if (!(countries.contains(sampleRegion[1]))) { //if the region is not already in the ArrayList add it
					countries.add(sampleRegion[1]);
				}

				if (dates.size() == 0) { //if the ArrayList is empty add the date
					// if date is in YY/M/D format convert it to YY/MM/DD
					if (sampleDate[2].length()==9) {
						sampleDate[2]=sampleDate[2].substring(0,5)+"0"+sampleDate[2].substring(5, 9);
					}else if (sampleDate[2].length()==8) {
						sampleDate[2]=sampleDate[2].substring(0,5)+"0"+sampleDate[2].substring(5, 7)+"0"+sampleDate[2].substring(7, 8);
					}
					//add the date
					dates.add(sampleDate[2]);

				} else if (!(dates.contains(sampleDate[2]))) { //if the date is not already in the ArrayList add it
					// if date is in YY/M/D format convert it to YY/MM/DD
					if (sampleDate[2].length()==9) {
						sampleDate[2]=sampleDate[2].substring(0,5)+"0"+sampleDate[2].substring(5, 9);
					}else if (sampleDate[2].length()==8) {
						sampleDate[2]=sampleDate[2].substring(0,5)+"0"+sampleDate[2].substring(5, 7)+"0"+sampleDate[2].substring(7, 8);
					}
					dates.add(sampleDate[2]);
				}
				if (globalLineage.size() == 0) { //if the ArrayList is empty add the global lineage code
					globalLineage.add(sampleDate[3]);

				} else if (!(globalLineage.contains(sampleDate[3]))) { //if the global Lineage is not already in the ArrayList add it
					globalLineage.add(sampleDate[3]);
				}
				if (UKLineage.size() == 0) { //if the ArrayList is empty add the UK lineage code
					UKLineage.add(sampleDate[4]);

				} else if (!(UKLineage.contains(sampleDate[4]))) { //if the UK lineage code is not already in the ArrayList add it
					UKLineage.add(sampleDate[4]);
				}
			}
			
			//get the min and max value of the date Arraylist convert it to LocalDate object and save it
			String min = Collections.min(dates);
			DateTimeFormatter minFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
			minPastDate = LocalDate.parse(min, minFormatter);

			String max = Collections.max(dates);
			DateTimeFormatter maxFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
			maxDate = LocalDate.parse(max, maxFormatter);


		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} finally {
			try {
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// method that draws the heatmap with the provided colors
	public JPanel drawData(ArrayList<ArrayList<String>> table, int pixel, Color NoM, Color Syn, Color Non, Color Ins,
			Color Del, Color NoC) {
		
		//the whole heatmap is drawn in a JPanel
		
		JPanel heatmapPanel = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				// start X,Y coordinates for drawing data
				int pointX = 0;
				int pointY = 0;
				int in = 0;

				//create the fonts that will be used in the heatmap plot
				
				Font boldf = new Font("default", Font.BOLD, 30);
				Font boldSmallSamplef = new Font("default", Font.BOLD, 16);
				Font boldSmallf = new Font("default", Font.BOLD, 12);
				Font defaultf = new Font("default", Font.PLAIN, 12);
				
				//if the dataset is smaller use a smaller font for the labels
				
				if (table.get(0).size() < 100 || table.size() < 100) {
					g2.setFont(boldSmallSamplef);
				} else {
					g2.setFont(boldf);
				}
				g2.drawString("Viral Samples", (table.get(0).size() - 10) * (pixel + 1) / 2 - 50, 25);
				g2.setFont(defaultf);
				
				/*
				 * draw a rectangle for each position of the table and color it with the given
				 * colors
				 */
				for (int y = 0; y < table.size() - 1; y++) {
					for (int x = startColumn; x < table.get(0).size() - 1; x++) {
						
						// based on mutation type in each position color the rectangles
						assignColors(g2, x + 1, y + 1, table, NoM, Syn, Non, Ins, Del, NoC);
						g2.fillRect(x + pointX - 10, y + pointY + 50, pixel, pixel);
						pointX = pointX + pixel;
						
						//if the user wants to display the labels with the common mutation sites
						if (HeatmapController.isUsed) {
							
							//if a mutation at specific row and column is stored coordinate hashTable
							// draw a string at the coordinates x and y given by the table
							
							if (HeatmapController.coordinates.containsKey(table.get(y + 1).get(x + 1)) && in == 0) {

								g2.setPaint(Color.BLACK);
								g2.setFont(boldSmallf);	
								g2.drawString(table.get(y + 1).get(10),
										HeatmapController.coordinates.get(table.get(y + 1).get(x + 1)).get(0)*(pixel+1)
												- 100
												,
										HeatmapController.coordinates.get(table.get(y + 1).get(x + 1)).get(1) + pointY
												+ 50 - pixel / 2);

								g2.setFont(defaultf);
								in = 1;
							}
						}
					}

					// generate genome position labels
					g2.setPaint(Color.BLACK);
					g2.drawString(table.get(y + 1).get(10), table.get(0).size() - 1 + pointX - 10,
							y + pointY + pixel + 50);
					pointY = pointY + pixel;
					pointX = 0;
					in = 0;

				}

				g2.setFont(boldf);
				Stroke oldStroke = g2.getStroke();
				
				//if the user has used the sample search option red rectangle is drawn
				//around the index(es)  provided by newSearch method 
				
				if (HeatmapController.searchUsed) {

					g2.setStroke(new BasicStroke(2)); //change the line thichness
					g2.setColor(Color.RED);

					for (int i = 0; i < sampleIndex.length; i++) {
						g2.drawRect(sampleIndex[i] * (pixel + 1), 50, pixel + 1, table.size() - 1 + pointY + pixel - 8);
						
					}
					g2.setStroke(oldStroke);
				}
				
				//if the user has used the position search option red rectangle is drawn
				//around the index(es)  provided by newSearchRow method 
				
				if (HeatmapController.searchPUsed) {
					g2.setStroke(new BasicStroke(2));
					g2.setColor(Color.RED);

					for (int i = 0; i < posIndex.length; i++) {
						g2.drawRect(0, posIndex[i] * (pixel + 1) + 50,
								(table.get(0).size() - 11) * (pixel + 1), pixel + 1);
					}
					g2.setStroke(oldStroke);
				}
				
				// set the color black and rotate 90 degrees the strings corresponding to y-axis label and sample names
				
				g2.setColor(Color.BLACK);
				g2.translate((HEIGHT - WIDTH) / 2, (HEIGHT - WIDTH) / 2);
				g2.rotate(Math.PI / 2, HEIGHT / 2, WIDTH / 2);
				
				//if the dataset is small use a smaller font
				if (table.get(0).size() < 100 || table.size() < 100) {
					g2.setFont(boldSmallSamplef);
				} else {
					g2.setFont(boldf);
				}
				
				g2.drawString("Nucleotide position- ORF name", (table.size() - 1) * (pixel + 1) / 2 - 120,
						-((table.get(0).size() - 1) * (pixel + 1) + 130));
				g2.setFont(defaultf);

				// draw the samples' name

				for (int i = startColumn; i < table.get(0).size() - 1; i++) {
				
					g2.drawString(table.get(0).get(i + 1), table.size() - 1 + pointY + pixel + 50, -(i + pointX - 10));
					pointX = pointX + pixel;
				}

			}
		};
		
		//add ActionListener to display tooltips
		heatmapPanel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				int pointX = 0;
				int pointY = 0;
				
				//loop around the 2D ArrayList
				for (int y = 0; y < table.size() - 1; y++) {
					for (int x = startColumn; x < table.get(0).size() - 1; x++) {
						
						//draw a rectangle based on x and y from the loop with the same dimension the rectangles on heatmap have
						Rectangle rect = new Rectangle();
						rect.setBounds(x + pointX - 10, y + pointY + 50, pixel, pixel);
						
						// if the coordinates provided by mouseListeners are in the new rect 
						// the tooltip display a text about the mutation stored in the specific x and y in the ArrayList
						
						if (rect.contains(e.getPoint())) {

							if (!(table.get(y + 1).get(x + 1).equals("."))) {

								heatmapPanel.setToolTipText("<html><p width=\"350px\">" + "Sample:" + "<br>" + "<b>"
										+ table.get(0).get(x + 1) + "</b>" + "<br>" + "Genome Position:" + "<br>"
										+ "<b>" + table.get(y + 1).get(10) + "</b>" + "<br>" + "Base substitution:"
										+ "<br>" + "<b>" + table.get(y + 1).get(x + 1).substring(27, 34) + "</b>"
										+ "<br>" + "Amino acid substitution:" + "<br>" + "<b>"
										+ table.get(y + 1).get(x + 1).substring(47, 50) + "</b>" + "</p></html>");
							} else if (table.get(y + 1).get(x + 1).equals(".")) {
								heatmapPanel.setToolTipText("<html><p width=\"350px\">" + "Sample:" + "<br>" + "<b>"
										+ table.get(0).get(x + 1) + "</b>" + "<br>" + "Genome Position:" + "<br>"
										+ "<b>" + table.get(y + 1).get(10) + "</b>" + "<br>" + "<b>" + "No mutation"
										+ "</p></html>");
							}
						}

						pointX += pixel;
					}
					pointX = 0;
					pointY += pixel;
					ToolTipManager.sharedInstance().mouseMoved(e);
				}

			}

			@Override
			public void mouseDragged(MouseEvent e) {
			}

		});

		return heatmapPanel;
	}

	// gets the positions of the ArrayList and finds the mutation type of the
	// position and assign the appropriate color to the graphic
	public void assignColors(Graphics2D g, int x, int y, ArrayList<ArrayList<String>> table, Color NoM, Color Syn,
			Color Non, Color Ins, Color Del, Color NoC) {

		String mutation = table.get(y).get(x);

		if (mutation.equals(".")) { // no mutation
			g.setColor(NoM);
		} else {
			String[] mutDescription = mutation.split(":");
			if (mutDescription[0].equals("Syn")) { // synonymous
				g.setColor(Syn);
			} else if (mutDescription[0].equals("Non")) { // non-synonymous
				g.setColor(Non);
			} else if (mutDescription[0].equals("Ins")) { // insertions
				g.setColor(Ins);
			} else if (mutDescription[0].equals("Del")) { // deletions
				g.setColor(Del);
			} else if (mutDescription[0].equals("NoC")) { // deletions
				g.setColor(NoC);
			}
		}

	}

	// method that take the given panel and generates a BufferedImage that is saved
	// as a png or jpeg file
	public void saveImage(JPanel panel) {
		BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		panel.paint(g2d); //draw the given Panel to the graphic
		String extension = null;
		try {
			JFileChooser jfc = new JFileChooser();// create a filechooser so the user can chooser the directory where the file will be saved
			jfc.setAcceptAllFileFilterUsed(false); // disable "all files" extension from the dropdown menu
			/*
			 * add the two possible file extensions
			 */
			jfc.addChoosableFileFilter(new FileNameExtensionFilter("JPEG file", "jpg", ".jpeg"));
			jfc.addChoosableFileFilter(new FileNameExtensionFilter("PNG file", "png"));
			int option = jfc.showSaveDialog(null);
			if (option == JFileChooser.APPROVE_OPTION) {
				File f = jfc.getSelectedFile();
				if (jfc.getFileFilter() instanceof FileNameExtensionFilter) {
					String[] exts = ((FileNameExtensionFilter) jfc.getFileFilter()).getExtensions();
					f = new File(f.toString() + '.' + exts[0]);
					extension = exts[0]; // store the chosen extension
				}
				ImageIO.write(image, extension, f); // generate the image file
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// method that take the given panel and generates a BufferedImage that is saved
	// as PDF file
	public void saveImagePDF(JPanel panel) {
		BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);

		panel.paint(image.getGraphics());
		Document document = new Document();
		PdfWriter writer = null;
		try {
			JFileChooser pdfFileChooser = new JFileChooser(); // create a filechooser so the user can chooser the
																// directory where the file will be saved
			pdfFileChooser.setAcceptAllFileFilterUsed(false); // disable "all files" extension from the dropdown menu
			pdfFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PDF file", "pdf")); // add the possible file extension
			
			int chooserResult = pdfFileChooser.showSaveDialog(null);
			if (chooserResult == JFileChooser.APPROVE_OPTION) {
				String filePath = pdfFileChooser.getSelectedFile().getPath() + ".pdf"; //get the path provided by the user and add pdf suffix
				writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
			}
			document.open();

			Image iTextImage = Image.getInstance(writer, image, 1);
			iTextImage.setAbsolutePosition(25, 250);

			iTextImage.scaleToFit(500, 590); // scale the image to fit in the pdf file

			document.add(iTextImage);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (document.isOpen()) {
				document.close();
			}
		}
	}
	
	//method that saves an image of the Panel with methods from ScreenImage(source code: https://tips4java.wordpress.com/2008/10/13/screen-image/)
	public String saveFromConsole(Component panel, int pixel) {
		String newFilePath="";
		int sampleLabelSize=620;
		int positionLabelSize=190;
		JPanel heatmap = new JPanel();
		
		//set the size of the Panel based on the size of the dataset
		
		heatmap.setPreferredSize(
				new Dimension(getTable().get(0).size() * (pixel + 1) + positionLabelSize, getTable().size() * (pixel + 1) + sampleLabelSize));
		heatmap.add(panel);
		
		//store the bufferedImage returned by createImage method
		BufferedImage bImage = ScreenImage.createImage(heatmap);
		try {
			newFilePath=ScreenImage.writeImage(bImage, "heatmap.png"); // save the bufferedImage in a PNG file
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return newFilePath;
	}
	
	//The method gets all the indexes of the columns that fulfill the requirements from the several filters and finds their intersection
	public ArrayList<ArrayList<String>> columnsToKeep(ArrayList<Integer> indexToAddMutT, boolean isSelected1,
			ArrayList<Integer> indexToAddGL, boolean isSelected2, ArrayList<Integer> indexToAddUKL, boolean isSelected3,
			ArrayList<Integer> indexToAddRegions, boolean isSelected4, ArrayList<Integer> indexToAddMutN,
			boolean isSelected5, ArrayList<Integer> indexToAddDateF, boolean isSelected6,
			ArrayList<Integer> indexToAddDateT, boolean isSelected7, ArrayList<Integer> indexToAddSampleN,
			boolean isSelected8, ArrayList<Integer> rowsToRemove, boolean isSelected9) {
		
		ArrayList<Integer> originalInd = new ArrayList<Integer>();
		filteredTable = new ArrayList<ArrayList<String>>();
		
		//create a ArrayList containing all the indexes of the original Table 
		for (int i = 11; i < getTable().get(0).size(); i++) {
			originalInd.add(i);
		}
		
		//find the intersection of the indexes of the original Table and the indexed of the filters
		
		if (isSelected1) {
			originalInd.retainAll(indexToAddMutT);
		}
		if (isSelected2) {
			originalInd.retainAll(indexToAddGL);
		}
		if (isSelected3) {
			originalInd.retainAll(indexToAddUKL);
		}
		if (isSelected4) {
			originalInd.retainAll(indexToAddRegions);
		}
		if (isSelected5) {
			originalInd.retainAll(indexToAddMutN);
		}
		if (isSelected6) {
			originalInd.retainAll(indexToAddDateF);
		}
		if (isSelected7) {
			originalInd.retainAll(indexToAddDateT);
		}
		if (isSelected8) {
			originalInd.retainAll(indexToAddSampleN);
		}
		
		//create a filteredTable
		for (int j = 0; j < getTable().size(); j++) {
			ArrayList<String> tempRow = new ArrayList<String>();
			
			//the first 11 columns remains the same
			for (int k = 0; k < 11; k++) {
				tempRow.add(getTable().get(j).get(k));

			}
			
			//only the columns with indexes of the intersection are added
			for (int i : originalInd) {
				tempRow.add(getTable().get(j).get(i));
			}
			filteredTable.add(tempRow);

		}
		
		//if minimum number per genomic position has been used remove the rows of the filteredTable that doesn't pass the filtering
		
		if (isSelected9) {
			int countRemove = 0;
			for (int i : rowsToRemove) {
				filteredTable.remove(i - countRemove);
				countRemove += 1;
			}
		}
		return filteredTable;
	}

	//method that searches for sample name(s) searched by user
	public void newSearch(List search, ArrayList<ArrayList<String>> table) {
		String[] sampNames = search.getItems(); //get the name(s) written by the user in the textfield
		int count = 0;
		sampleIndex = new int[sampNames.length];
		//for each name typed in the textfield the current table gets searched to find the index of the column in which the name exist
		for (String j : sampNames) {
			for (int i = 11; i < table.get(0).size(); i++) {

				if (j.equals(table.get(0).get(i))) {
					sampleIndex[count] = i - 11;
				}

			}
			count++;
		}

	}
	//method that searches for position name(s) searched by user
	public void newSearchRow(List search, ArrayList<ArrayList<String>> table) {
		String[] newPosNames = search.getItems(); //get the name(s) written by the user in the textfield
		int countLabel = 0;
		posIndex = new int[newPosNames.length];
		
		//for each name typed in the textfield the current table gets searched to find the index of the row in which the name exist
		for (String j : newPosNames) {
			for (int i = 1; i <table.size(); i++) {

				if (j.equals(table.get(i).get(10))) {
					posIndex[countLabel] = i - 1;
				}
			}
			countLabel++;
		}

	}
	
	//remove a sample name from the search list if the name is not exist in the current table
	public void removeHighlightColumn(List search, ArrayList<ArrayList<String>> table) {
		String[] sampNames = search.getItems();
		
		//for each name typed in the textfield search if the name still exists in the current table
		for (String k : sampNames) {
			boolean isFound = false;
			for (int j = 11; j < table.get(0).size(); j++) {
				if (table.get(0).get(j).contains(k)) {
					isFound = true;
					break;
				}

			}
			if (!(isFound)) { //if the name is not found, remove it from the search list
				search.remove(k);
			}
		}
	}
	
	//remove a position name from the search list if the name is not exist in the current table
	public void removeHighlightRow(List search, ArrayList<ArrayList<String>> table) {
		String[] posNames = search.getItems();
		
		//for each name typed in the textfield search if the name still exists in the current table
		for (String k : posNames) {
			boolean isFound = false;
			for (int j = 1; j < table.size(); j++) {
				if (table.get(j).get(10).contains(k)) {
					isFound = true;
					break;
				}

			}
			if (!(isFound)) { //if the name is not found, remove it from the search list
				search.remove(k);
			}
		}
	}
	
	//count how many mutations exist per column
	public int[] countMutationsPerColumn(ArrayList<ArrayList<String>> table, int[] countMutations) {
		//fill the array corresponds to the count of mutations
		//in each column with zero
		
		Arrays.fill(countMutations, 0);
		
		for (int j = 1; j < table.size(); j++) {
			for (int i = 11; i < table.get(0).size(); i++) {
				
				//if the position of the table is not '.' (no mutation) 
				//increase count by 1
				if (!(table.get(j).get(i).equals("."))) {
					countMutations[i] += 1;
				}
			}
		}
		return countMutations;
	}
	
	//count how many mutations exist per row
	public int[] countMutationsPerRow(ArrayList<ArrayList<String>> table, int[] countMutations) {
		int count = 0;
		//fill the array corresponds to the count of mutations
		//in each row with zero
		Arrays.fill(countMutations, 0);
		for (int j = 1; j < table.size(); j++) {

			for (int i = 11; i <  table.get(0).size(); i++) {
				//if the position of the table is not '.' (no mutation) 
				//increase count by 1
				if (!( table.get(j).get(i).equals("."))) {
					count += 1;
				}
			}

			countMutations[j] = count;
			count = 0;
		}
		return countMutations;
	}
	

	// getters
	public ArrayList<String> getCountries() {
		return countries;
	}

	public ArrayList<ArrayList<String>> getTable() {
		return table;
	}

	public LocalDate getMinPastDate() {
		return minPastDate;
	}

	public LocalDate getMaxDate() {
		return maxDate;
	}

	public ArrayList<ArrayList<String>> getCustomTable() {
		return customTable;
	}

	public void setCustomTable(ArrayList<ArrayList<String>> customTable) {
		this.customTable = customTable;
	}

}
