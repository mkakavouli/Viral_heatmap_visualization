import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Locale;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;

public class HeatmapController implements ActionListener, ChangeListener, DateChangeListener, ItemListener {
	private HeatmapGUI gui;
	private HeatmapModel modelObject;
	private HeatmapClustering clusteringObject;
	private JColorChooser cc;
	private Color noMutationColor, synColor, nonSynColor, insColor, delColor, noCodingColor;
	private JPanel heatmapPanel;
	private JScrollPane scrollPane;
	private LocalDate toDate = null;
	private LocalDate fromDate = null;
	private int pixel = 6;
	private int index = 1;
	private int sampleLabelSize=620;
	private int positionLabelSize=170;
	public static boolean isUsed = false;
	public static boolean searchUsed = false;
	public static boolean searchPUsed = false;
	public static Hashtable<String, ArrayList<Integer>> coordinates;
	ArrayList<ArrayList<String>> filteredTable, newfilteredTable, clusteredTable, sortedTable;

	ArrayList<Integer> indexToAddMutT, indexToAddGL, indexToAddUKL, indexToAddRegions, indexToAddMutN, indexToAddDateF,
			indexToAddDateT, indexToAddSampleN, rowsToRemove;
	private boolean isSelected1, isSelected2, isSelected3, isSelected4, isSelected5, isSelected6, isSelected7,
			isSelected8, isSelected9;

	public HeatmapController(HeatmapModel model) {
		//intiallize HeamapGUI,HeatmapClustering and HeatmapModel Objects
		
		modelObject = model;
		gui = new HeatmapGUI(this, modelObject);
		clusteringObject = new HeatmapClustering();
		
		//set initial colors of the heatmap
		
		noMutationColor = Color.LIGHT_GRAY;
		synColor = Color.GREEN;
		nonSynColor = Color.MAGENTA;
		insColor = Color.YELLOW;
		delColor = Color.BLACK;
		noCodingColor = Color.ORANGE;
	}

	// manage the actions after the press of a button
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == gui.importFile) { // handle the actions when the import file button is pressed
			
			gui.getContentPane().remove(gui.getWelcomePanel()); // remove the welcome page panel
			
			String file = modelObject.selectFile(gui); // get the file with mutation data
			modelObject.readFile(file);
			
			if(modelObject.getTable().size()<=100 && modelObject.getTable().get(0).size()<100) { //if the dataset is too small change rectangle size
				pixel =15;
				//remove the lister so it won't get triggered if slidder change
				gui.getPixelSize().removeChangeListener(this);
				gui.getPixelSize().setValue(15);
				//add it again
				gui.getPixelSize().addChangeListener(this);
			}
			
			heatmapPanel = modelObject.drawData(modelObject.getTable(), pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor); // draw the heatmap with obtained data
			
			//fill the selection list with data extracted from the imported file
			for (String s : modelObject.countries) {
				gui.getRegionsList().add(s);
			}
	
			for (String s : modelObject.globalLineage) {
				gui.getGlobalLineageList().add(s);
			}

			for (String s : modelObject.UKLineage) {
				gui.getUKLineageList().add(s);
			}

			// set date limits and add the 
			//dateChangeListener to datepickers
			
			gui.getDateSettings1().setDateRangeLimits(modelObject.getMinPastDate(), modelObject.getMaxDate());
			gui.getDateSettings2().setDateRangeLimits(modelObject.getMinPastDate(), modelObject.getMaxDate());
			gui.getDatePicker1().setDate(modelObject.getMinPastDate());
			gui.getDatePicker2().setDate(modelObject.getMaxDate());
			gui.getDatePicker1().addDateChangeListener(this);
			gui.getDatePicker2().addDateChangeListener(this);
			

			gui.setSize(1500, 800); //increase the size of the frame
			
			//enable several jmenu buttons
			gui.getColors().setEnabled(true);
			gui.getSearch().setEnabled(true);
			gui.getSearchP().setEnabled(true);
			gui.getSave().setEnabled(true);

			gui.getRightPanel().setVisible(true); // set the panel with filters visible
			
			
			//set the size of the heatmapPanel based on the size of the dataset
			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + positionLabelSize,
					modelObject.customTable.size() * (pixel + 1) + sampleLabelSize));
			
			//add the Panel to scrollPane
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER); //add the scrollPane to the frame
			gui.revalidate();

		} else if (e.getSource() == gui.savePng) { //activated when save as png/jpeg is pressed
			
			modelObject.saveImage(heatmapPanel); // save the heatmap Panel as png or jpeg

		} else if (e.getSource() == gui.savePDF) { // activated when save as pdf is pressed
			
			modelObject.saveImagePDF(heatmapPanel); // save the heatmap Panel as PDF

		} else if (e.getSource() == gui.synColor) { // activate when synonymous color jmenuItems is pressed
			
			synColor = JColorChooser.showDialog(cc, "Color Chooser", gui.getBackground()); // save the color for the synonymous mutation,selected by the  user
																							
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered
			
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}
			
			//draw the new heatmap with the new color
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			//recreate the new heatmapPanel
			
			recreateGui();


		} else if (e.getSource() == gui.nonSynColor) { // activate when non-synonymous color jmenuItems is pressed
			nonSynColor = JColorChooser.showDialog(cc, "Color Chooser", gui.getBackground()); // save the color for the non-synonymous mutation selected by the user
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}
			//draw the new heatmap with the new color
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			
			//recreate the new heatmapPanel
			
			recreateGui();


		} else if (e.getSource() == gui.noMutColor) { // activate when no mutation color jmenuItems is pressed
			noMutationColor = JColorChooser.showDialog(cc, "Color Chooser", gui.getBackground()); // save the color for the no mutation selected by the user
			
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}
			
			//draw the new heatmap with the new color
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			
			//recreate the new heatmapPanel
			
			recreateGui();


		} else if (e.getSource() == gui.delColor) { // activate when deletion color jmenuItems is pressed
			delColor = JColorChooser.showDialog(cc, "Color Chooser", gui.getBackground()); // save the color for deletions selected by the user
			
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}
			
			//draw the new heatmap with the new color
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			
			recreateGui(); 	//recreate the new heatmapPanel


		} else if (e.getSource() == gui.insColor) { // activate when insertion color jmenuItems is pressed
			insColor = JColorChooser.showDialog(cc, "Color Chooser", gui.getBackground()); // save the color for the insertions selected by the user
			
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}
			
			//draw the new heatmap with the new color
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			recreateGui(); 	//recreate the new heatmapPanel


		} else if (e.getSource() == gui.noCColor) { // activate when non coding color jmenuItems is pressed
			
			noCodingColor = JColorChooser.showDialog(cc, "Color Chooser", gui.getBackground()); // save the color for non coding mutations selected by the user
			
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}
			
			//draw the new heatmap with the new color
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			
			//recreate the new heatmapPanel
			recreateGui();


		} else if (e.getSource() == gui.addButton) { //activated when addButton is search sample names is pressed
			
			String sample = gui.searchSample.getText(); //get the sample name(s) typed in the textfield 
			searchUsed = true; 
			gui.getSearchedSamples().add(sample); //add the name(s) to the searched list
			
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}
			
			//search for the names of the samples
			modelObject.newSearch(gui.getSearchedSamples(), newfilteredTable);
			
			//draw the new heatmap
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			
			//recreate the new heatmapPanel
			recreateGui();

		} else if (e.getSource() == gui.clearButton) { //activated when clearButton is search sample names is pressed
			
			String[] nameToRemove = gui.getSearchedSamples().getSelectedItems(); // store the name(s) of the selected sample(s) to remove
																					
			for (String i : nameToRemove) {
				gui.getSearchedSamples().remove(i); //remove the name(s) from the searched list
			}

			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}
			
			//search for the names of the samples in the searched list
			modelObject.newSearch(gui.getSearchedSamples(), newfilteredTable);

			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor); //draw the new heatmap

			recreateGui(); //recreate the new heatmapPanel

		} else if (e.getSource() == gui.addPButton) { //activated when addButton in search position(s) names is pressed

			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}

			String position = gui.searchPosition.getText(); // store the name(s) of the position(s) typed by user
			searchPUsed = true;
			gui.getSearchedPositions().add(position); //add the name of the position to the searched position selection list
			modelObject.newSearchRow(gui.getSearchedPositions(), newfilteredTable);
			
			//draw the new heatmap
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			recreateGui(); //recreate the new HeatmapPanel

		} else if (e.getSource() == gui.clearPButton) { //activated when clearButton in search position names is pressed
			
			String[] nameToRemove = gui.getSearchedPositions().getSelectedItems(); // store the name(s) of the selected position(s) to remove
			
			for (String i : nameToRemove) {
				gui.getSearchedPositions().remove(i); //for each name in the array remove the position name from the selection list
			}
			
			String[] posNames = gui.getSearchedPositions().getItems(); //get the items that are currently in the searchedPosition selection list
			
			if (posNames.length == 0) {
				searchPUsed = false; //if there is no name left turn search boolean false
			}
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}
			
			//searh for the position name(s) that are in the selection list
			modelObject.newSearchRow(gui.getSearchedPositions(), newfilteredTable);

			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor); //draw the new heatmap
			recreateGui(); //recreate the heatmapPanel

		} else if (e.getSource() == gui.getHelp()) { //gets activate when help menuItem is pressed
			//display a help message to the user
			
			JOptionPane.showMessageDialog(null, gui.getScrollPaneHelp(), "Help", JOptionPane.PLAIN_MESSAGE);
			
		} else if (e.getSource() == gui.mutNumber) { //gets activated when enter is pressed in minimum number of mutations per sample
			
			isSelected5 = true;
			int minNumber = Integer.parseInt(gui.mutNumber.getText()); //get the minimum number of mutations per sample typed by user
			int[] countMutations = new int[modelObject.getTable().get(0).size()]; //create an array to store counts of every column/sample
			
			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToAddMutN = new ArrayList<Integer>();
			
			//count the mutation/sample
			countMutations=modelObject.countMutationsPerColumn(modelObject.getTable(), countMutations);
			
			//if the count is equal or bigger to the one given by used the index of that column gets stored
			for (int i = 11; i < countMutations.length; i++) {
				if (countMutations[i] >= minNumber) {
					if (!(indexToAddMutN.contains(i))) {
						indexToAddMutN.add(i);
					}
				}
			}
			
			//create the filterTable consisting of indexes fulfull filtering criteria
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);
			
			//if filtering out rows is used the columns are checked again to confirm they still have count of mutation bigger or equal to the one set by user
			//if not the index of that sample is removed
			if(isSelected9) {
				int[] countMutationsSamples = new int[newfilteredTable.get(0).size()];
				countMutationsSamples=modelObject.countMutationsPerColumn(newfilteredTable,countMutationsSamples);
				for(int i=11;i<countMutationsSamples.length;i++) {
					if(countMutationsSamples[i]<minNumber) {
						String sampleName=newfilteredTable.get(0).get(i);
						for(int k=11;k<modelObject.getTable().get(0).size();k++) {
							if(sampleName.equals(modelObject.getTable().get(0).get(k))) {
								indexToAddMutN.remove(Integer.valueOf(k));
							}
						}
					}
				}
				
				//the filteredTable is created again with the new indexes
				newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
						indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
						indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
						rowsToRemove, isSelected9);
			}
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered
			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);
			//check if there is need to remove a searched name if the highlighted sample don't exist in the current Table
			modelObject.removeHighlightColumn(gui.searchedSamples, newfilteredTable);
			modelObject.newSearch(gui.searchedSamples, newfilteredTable); //search the remaining names of the samples
			
			
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor); //draw the new heatmap
			recreateGui(); //recreate the Frame


		} else if (e.getSource() == gui.mutPerPos) { //gets activated when enter is pressed when a minimum mutation per position is typed by user
			newfilteredTable = new ArrayList<ArrayList<String>>();
			
			if(rowsToRemove!=null) { //if the filtered has been used again the arrayList of indexes is full clear it
				rowsToRemove.clear();
			}
			
			//gets the table fullfill the other filter's criteria
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);
			isSelected9 = true;
		
			int minNumber = Integer.parseInt(gui.mutPerPos.getText()); //get the minimum number typed by the user
			int[] countMutations = new int[modelObject.getTable().size()]; //create an array to store the count of mutation of each row
			
			rowsToRemove = new ArrayList<Integer>();
			//count the mutations
			countMutations=modelObject.countMutationsPerRow(newfilteredTable, countMutations);
			
			//each row that has number of mutations less than the minimum set by user its index get stored so it can be removed
			for (int i = 1; i < countMutations.length; i++) {
				if (countMutations[i] < minNumber) {
					if (!(rowsToRemove.contains(i))) {
						rowsToRemove.add(i);
					}
				}
			}
			
			//gets the new filteredTable with the appropriate rows removed
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);
			
			//if minimum number of mutations per sample has been set the table with remaining rows will be checked again 
			// to confirm that sample still fullfill the criteria after the removing of the rows
			//if not those sample's indexes are removed from the ArrayList 
			if(isSelected5) {
				int[] countMutationsSamples = new int[newfilteredTable.get(0).size()];
				countMutationsSamples=modelObject.countMutationsPerColumn(newfilteredTable,countMutationsSamples);
				int minColumnNumber = Integer.parseInt(gui.mutNumber.getText());
				for(int i=11;i<countMutationsSamples.length;i++) {
					if(countMutationsSamples[i]<minColumnNumber) {
						String sampleName=newfilteredTable.get(0).get(i);
						for(int k=11;k<modelObject.getTable().get(0).size();k++) {
							if(sampleName.equals(modelObject.getTable().get(0).get(k))) {
								indexToAddMutN.remove(Integer.valueOf(k));
							}
						}
					}
				}
			}
			//if types of mutation displayed has been set, the table with remaining rows will be checked again 
			// to confirm that sample still fullfill the criteria after the removing of the rows
			//if not those sample's indexes are removed from the ArrayList 
			if(isSelected1) {
				String[] selected = gui.getMutTypeList().getSelectedItems();
				String[] mutType = new String[selected.length];
				for (int i = 0; i < selected.length; i++) {
					mutType[i] = gui.getMutationsHash().get(selected[i]);
				}
			
				for (int k = 0; k < mutType.length; k++) {
					for (int i = 11; i <newfilteredTable.get(0).size(); i++) {
						boolean isFound =false;
						for (int j = 1; j < newfilteredTable.size(); j++) {
							String[] sample = newfilteredTable.get(j).get(i).split(":");
							if (sample[0].equals(mutType[k])) {
								isFound=true;
							}
						}
						if(!isFound) {
							String sampleName=newfilteredTable.get(0).get(i);
							for(int l=11;l<modelObject.getTable().get(0).size();l++) {
								if(sampleName.equals(modelObject.getTable().get(0).get(l))) {
									indexToAddMutT.remove(Integer.valueOf(l));
								}
							}	
						}
					}
				}
			}
			
			//create the filtered table
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered
			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable); //set the customTable to the newfilteredTable
			//check if there is need to remove a searched name if the highlighted sample don't exist in the current Table
			modelObject.removeHighlightRow(gui.getSearchedPositions(),newfilteredTable);
			modelObject.newSearchRow(gui.getSearchedPositions(),newfilteredTable); //search the remaining names of the samples
		
			//create the new heatmap
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			recreateGui(); //recreate the Frame


		} else if (e.getSource() == gui.samplesNumber) { //gets activated when user presses enter in samples/mutation textfield
			isSelected8 = true;
			
			int minSampNumber = Integer.parseInt(gui.samplesNumber.getText()); //get the minimum number typed by user
			int[] countSamples = new int[modelObject.getTable().get(0).size()]; //create an array to count the samples that have the same mutation
			
			Hashtable<String, Integer> differentMutations = new Hashtable<String, Integer>();
			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToAddSampleN = new ArrayList<Integer>();
			Arrays.fill(countSamples, 0); //fill the count array to zeros
			for (int j = 1; j < modelObject.getTable().size(); j++) {
				for (int i = 11; i < modelObject.getTable().get(0).size(); i++) {
					if (!(modelObject.getTable().get(j).get(i).equals("."))) {
						// if a mutation exist store all the different mutation to hashTable and set their count to one
						if (!(differentMutations.containsKey(modelObject.getTable().get(j).get(i)))) {
							differentMutations.put(modelObject.getTable().get(j).get(i), 1);
						} else { //if a mutation is already stored to the HadhTable increase the count by one
							differentMutations.put(modelObject.getTable().get(j).get(i),
									differentMutations.get(modelObject.getTable().get(j).get(i)) + 1);
						}
					}
				}
			}
			//check the table column by column
			for (int i = 11; i < modelObject.getTable().get(0).size(); i++) {
				for (int j = 1; j < modelObject.getTable().size(); j++) {
					//store as count for each sample the count of the mutation existing in the samples and has appeared most times
					if (differentMutations.containsKey(modelObject.getTable().get(j).get(i))
							&& countSamples[i] < differentMutations.get(modelObject.getTable().get(j).get(i))) {
						countSamples[i] = differentMutations.get(modelObject.getTable().get(j).get(i));
					}

				}
			}
			
			//if the count of the sample is equal or bigger than the one set by user the sample index gets stored to be added to the filteredTable 
			for (int i = 11; i < countSamples.length; i++) {
				if (countSamples[i] >= minSampNumber) {
					if (!(indexToAddSampleN.contains(i))) {
						indexToAddSampleN.add(i);
					}
				}
			}
			
			//creates the filterTable fullfill the filtering criteria
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered
			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);//set the customTable to the newfilteredTable
			//check if there is need to remove a searched name if the highlighted sample don't exist in the current Table
			modelObject.removeHighlightColumn(gui.searchedSamples, newfilteredTable);
			modelObject.newSearch(gui.searchedSamples, newfilteredTable); //search the remaining names of the samples
			
			//draw the new heatmap
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			recreateGui(); //recreate the frame


		} else if (e.getSource() == gui.hierarchicalClustered) { //gets activated when hierarchical ordering radio button is pressed
			index = 2;
			clusteredTable = clusteringObject.clusterData(modelObject.getCustomTable()); //hierarchical order columns

			modelObject.newSearch(gui.getSearchedSamples(), clusteredTable); //search for samples hightlighted that may have change due to reording of columns

			
			heatmapPanel = modelObject.drawData(clusteredTable, pixel, noMutationColor, synColor, nonSynColor, insColor,
					delColor, noCodingColor); //draw the new heatmap
			recreateGui(); //recreate the Frame


		} else if (e.getSource() == gui.dateClustered) { //gets activated when date ordering radio button is pressed
			index = 3;
			sortedTable = clusteringObject.sortDateData(modelObject.getCustomTable()); //sort samples based on date

			modelObject.newSearch(gui.getSearchedSamples(), sortedTable); //search for samples hightlighted that may have change due to reording of columns

			
			heatmapPanel = modelObject.drawData(sortedTable, pixel, noMutationColor, synColor, nonSynColor, insColor,
					delColor, noCodingColor); //draw the new heatmap
			recreateGui(); //recreate the Frame


		} else if (e.getSource() == gui.nonClustered) { //gets activated when no clustering radio button is pressed
			index = 1;
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);

			modelObject.newSearch(gui.getSearchedSamples(), newfilteredTable); //search for samples hightlighted that may have change due to reording of columns

			
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor); //draw the new heatmap
			recreateGui(); //recreate Frame


		} else if (e.getSource() == gui.resetButton) { // gets activated when reset button is pressed
			//set the variables to the default values
			noMutationColor = Color.LIGHT_GRAY;
			synColor = Color.GREEN;
			nonSynColor = Color.MAGENTA;
			insColor = Color.YELLOW;
			delColor = Color.BLACK;
			noCodingColor = Color.ORANGE;
			pixel = 6;
			index = 1;
			modelObject.setCustomTable(modelObject.getTable()); //set the custom Table to the initial table
			
			//empty all the textfields
			gui.getMutNumber().setText("");
			gui.getSamplesNumber().setText("");
			gui.getMutPerPos().setText("");
			gui.getWarning1().setText("");
			gui.getWarning2().setText("");
			gui.getWarning3().setText("");
			//set all the booleans of the filters to false
			isSelected1 = false;
			isSelected2 = false;
			isSelected3 = false;
			isSelected4 = false;
			isSelected5 = false;
			isSelected6 = false;
			isSelected7 = false;
			isSelected8 = false;
			isSelected9 = false;
			isUsed = false;
			searchUsed = false;
			searchPUsed = false;
			//set the radiobuttons and checkboxes to false
			gui.getCommonMutationSites().setSelected(false);
			gui.nonClustered.setSelected(true);
			gui.dateClustered.setSelected(false);
			gui.hierarchicalClustered.setSelected(false);
			gui.getPixelSize().setValue(6);
			//draw the heatmap with default values
			heatmapPanel = modelObject.drawData(modelObject.getTable(), pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			
			//remove the dateChangeListener so they're not activated when the max and min date set to the initial values
			gui.getDatePicker1().removeDateChangeListener(this);
			gui.getDatePicker2().removeDateChangeListener(this);
			gui.getDatePicker1().setDate(modelObject.getMinPastDate());
			gui.getDatePicker2().setDate(modelObject.getMaxDate());
			//add the dateChangelisteners again
			gui.getDatePicker1().addDateChangeListener(this);
			gui.getDatePicker2().addDateChangeListener(this);
			
			//clear the coordinate and indexes ArrayLists if they have been used
			if(coordinates!=null) {
				coordinates.clear();
			}
			if(indexToAddMutT!=null) {
				indexToAddMutT.clear();
			}
			if(indexToAddMutN!=null) {
				indexToAddMutN.clear();
			}
			
			//remove the content of the selection list and repopulate them
			gui.getMutTypeList().removeAll();
			gui.getGlobalLineageList().removeAll();
			gui.getRegionsList().removeAll();
			gui.getUKLineageList().removeAll();
			for (String s : gui.getMutations()) {
				gui.getMutTypeList().add(s);
			}
			
			for (String s : modelObject.globalLineage) {
				gui.getGlobalLineageList().add(s);
			}
		
			for (String s : modelObject.countries) {
				gui.getRegionsList().add(s);
			}
			
			for (String s : modelObject.UKLineage) {
				gui.getUKLineageList().add(s);
			}
			recreateGui(); //recreate frame

		}

	}

	@Override
	public void stateChanged(ChangeEvent e) { // handle the actions when the JSlider is used
		JSlider source = (JSlider) e.getSource();
		if (!source.getValueIsAdjusting()) {
			pixel = (int) source.getValue(); // store the new pixel value
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {

					newfilteredTable = clusteringObject.clusterData(modelObject.getCustomTable());
					modelObject.setCustomTable(newfilteredTable);
				} else if (index == 3) {

					newfilteredTable = clusteringObject.sortDateData(modelObject.getCustomTable());
					modelObject.setCustomTable(newfilteredTable);
				}
			}
			
			//draw the new heatmap 
			heatmapPanel = modelObject.drawData(modelObject.getCustomTable(), pixel, noMutationColor, synColor,
					nonSynColor, insColor, delColor, noCodingColor);
			recreateGui(); //recreate the frame


		}
	}

	@Override
	public void dateChanged(DateChangeEvent e) { //gets activated when the date in the calendar has changed
		if (e.getSource() == gui.getDatePicker1()) { //activated when the first date limit has change
			isSelected6 = true;
			fromDate = gui.getDatePicker1().getDate(); //gets the new date set by user
			
			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToAddDateF = new ArrayList<Integer>();
			
			for (int i = 10; i < modelObject.getTable().get(0).size(); i++) {
				if (i != 10) {
					String[] sampleDate = modelObject.getTable().get(0).get(i).split("\\|"); //spit the name of each sample to extract the date
					
					//if the date is in 'YYYY-M-D' format change it to 'YYYY-MM-DD' format
					if (sampleDate[2].length()==9) {
						sampleDate[2]=sampleDate[2].substring(0,5)+"0"+sampleDate[2].substring(5, 9);
					}else if (sampleDate[2].length()==8) {
						sampleDate[2]=sampleDate[2].substring(0,5)+"0"+sampleDate[2].substring(5, 7)+"0"+sampleDate[2].substring(7, 8);
					}
					//if it's the correct format convert it to Local Date object
					if (sampleDate[2].length() == 10) {
						LocalDate convertedDate = LocalDate.parse(sampleDate[2],
								DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
						
						if (toDate == null) { //if the other date limit has not been used the date of the sample should be after the first date limit to get added in the filteredTable
							if (!(convertedDate.isBefore(fromDate))) {
								if (!(indexToAddDateF.contains(i))) {
									indexToAddDateF.add(i);
								}
							}
						} else { //else if both date limits have been used, the date of the sample should be in the date range to get added in the filteredTable

							if ((!(convertedDate.isBefore(fromDate))) && (!(convertedDate.isAfter(toDate)))) {

								if (!(indexToAddDateF.contains(i))) {
									indexToAddDateF.add(i);
								}
							}
						}
					}
				}
			}
			//create the filteredTable
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);

			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered

			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);//set the customTable to the newfilteredTable
			//check if there is need to remove a searched name if the highlighted sample don't exist in the current Table
			modelObject.removeHighlightColumn(gui.searchedSamples, newfilteredTable);
			modelObject.newSearch(gui.searchedSamples, newfilteredTable); //search the remaining names of the samples
			
			//draw the new heatmap
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			recreateGui(); //recreate the frame

		}
		if (e.getSource() == gui.getDatePicker2()) { //activated when the second date limit has change
			isSelected7 = true;
			toDate = gui.getDatePicker2().getDate(); //gets the new date set by user
			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToAddDateT = new ArrayList<Integer>();
			for (int i = 10; i < modelObject.getTable().get(0).size(); i++) {
				if (i != 10) {
					String[] sampleDate = modelObject.getTable().get(0).get(i).split("\\|"); //spit the name of each sample to extract the date
					
					//if the date is in 'YYYY-M-D' format change it to 'YYYY-MM-DD' format
					if (sampleDate[2].length()==9) {
						sampleDate[2]=sampleDate[2].substring(0,5)+"0"+sampleDate[2].substring(5, 9);
					}else if (sampleDate[2].length()==8) {
						sampleDate[2]=sampleDate[2].substring(0,5)+"0"+sampleDate[2].substring(5, 7)+"0"+sampleDate[2].substring(7, 8);
					}
					
					//if it's the correct format convert it to Local Date object
					if (sampleDate[2].length() == 10) {
						LocalDate convertedDate = LocalDate.parse(sampleDate[2],
								DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
						if (fromDate == null) { //if the first date limit has not been used the date of the sample should be before the second date limit to get added in the filteredTable
							if (!(convertedDate.isAfter(toDate))) {
								if (!(indexToAddDateT.contains(i))) {
									indexToAddDateT.add(i);
								}
							}
						} else { //else if both date limits have been used, the date of the sample should be in the date range to get added in the filteredTable
							if ((!(convertedDate.isBefore(fromDate))) && (!(convertedDate.isAfter(toDate)))) {
								if (!(indexToAddDateT.contains(i))) {
									indexToAddDateT.add(i);
								}
							}
						}
					}
				}
			}
			//create the filteredTable
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered

			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);//set the customTable to the newfilteredTable
			//check if there is need to remove a searched name if the highlighted sample don't exist in the current Table
			modelObject.removeHighlightColumn(gui.searchedSamples, newfilteredTable);
			modelObject.newSearch(gui.searchedSamples, newfilteredTable); //search the remaining names of the samples
			
			//draw the new heatmap
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			recreateGui(); //recreates the frame
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) { 
		if (e.getSource() == gui.getMutTypeList()) { //gets activate if a mutation type has been selected or deselected
			isSelected1 = true;
			String[] selected = gui.getMutTypeList().getSelectedItems(); //get the Strings of selected Items
			if (selected.length == 0) { //if nothing is selected set filter's boolean false
				isSelected1 = false;
			}
			String[] mutType = new String[selected.length];
			for (int i = 0; i < selected.length; i++) {
				mutType[i] = gui.getMutationsHash().get(selected[i]); //store in an array the types of mutations selected as they written in the dataset (e.g synonymous =Syn)
			}

			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToAddMutT = new ArrayList<Integer>();
			
			//for each mutation type check if a sample has that mutation
			// if it does store the index of the sample to be added in the new filteredTable
			for (int k = 0; k < mutType.length; k++) {
				for (int j = 1; j < modelObject.getTable().size(); j++) {
					for (int i = 11; i < modelObject.getTable().get(0).size(); i++) {
						String[] sample = modelObject.getTable().get(j).get(i).split(":");
						if (sample[0].equals(mutType[k])) {
							if (!(indexToAddMutT.contains(i))) {
								indexToAddMutT.add(i);

							}
						}
					}
				}
			}
			
			//create the new filteredTable
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);
			
			//if rows has been filtered out
			//check in the filtered table if the samples still fullfill the criteria of mutation type
			//if not remove the sample index from the Arraylist so it won't be added to the newfiltered table
			if(isSelected9) {
				for (int i = 11; i <newfilteredTable.get(0).size(); i++) {
					boolean isFound =false;
					for (int k = 0; k < mutType.length; k++) {
						for (int j = 1; j < newfilteredTable.size(); j++) {
							String[] sample = newfilteredTable.get(j).get(i).split(":");
							if (sample[0].equals(mutType[k])) {
								isFound=true;
							}
						}
					}	
					if(!isFound) {
						String sampleName=newfilteredTable.get(0).get(i);
						for(int l=11;l<modelObject.getTable().get(0).size();l++) {
							if(sampleName.equals(modelObject.getTable().get(0).get(l))) {
								indexToAddMutT.remove(Integer.valueOf(l));
								}
						}	
					}
				}
				
				//create again the newfilteredTable
				newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
						indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
						indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
						rowsToRemove, isSelected9);
				
			}
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered

			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);//set the customTable to the newfilteredTable
			//check if there is need to remove a searched name if the highlighted sample don't exist in the current Table
			modelObject.removeHighlightColumn(gui.searchedSamples, newfilteredTable);
			modelObject.newSearch(gui.searchedSamples, newfilteredTable);  //search the remaining names of the samples
		
			//draw the new heatmap
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			recreateGui(); //recreates the frame

		} else if (e.getSource() == gui.getGlobalLineageList()) { // gets activated when a global lineage is selected or deselected
			isSelected2 = true;
			String[] globalLineage = gui.getGlobalLineageList().getSelectedItems(); //gets the selected global lineages
			if (globalLineage.length == 0) { //if nothing is selected set the filter's boolean to false
				isSelected2 = false;
			}
			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToAddGL = new ArrayList<Integer>();
			
			//for each global lineage code check if a sample has that code on its name
			// if it does store the index of the sample to be added in the new filteredTable
			for (int k = 0; k < globalLineage.length; k++) {
				for (int i = 11; i < modelObject.getTable().get(0).size(); i++) {
					String[] sampleUkLineage = modelObject.getTable().get(0).get(i).split("\\|");
					if (sampleUkLineage[3].equals(globalLineage[k])) {
						if (!(indexToAddGL.contains(i))) {
							indexToAddGL.add(i);
						}
					}
				}
			}
			//create the new filteredTable
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered

			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);//set the customTable to the newfilteredTable
			//check if there is need to remove a searched name if the highlighted sample don't exist in the current Table
			modelObject.removeHighlightColumn(gui.searchedSamples, newfilteredTable);
			modelObject.newSearch(gui.searchedSamples, newfilteredTable);  //search the remaining names of the samples
		

			//draw the new heatmap
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			recreateGui(); //recreate the frame

		} else if (e.getSource() == gui.getUKLineageList()) { // handles the uk lineage filter
			isSelected3 = true;

			String[] ukLineage = gui.getUKLineageList().getSelectedItems(); //get the UK lineage code selected
			if (ukLineage.length == 0) {
				isSelected3 = false; //if nothing is selected set the filter's boolean to false
			}

			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToAddUKL = new ArrayList<Integer>();
			
			//for each Uk lineage code check if a sample has code on its name
			// if it does store the index of the sample to be added in the new filteredTable
			for (int k = 0; k < ukLineage.length; k++) {
				for (int i = 11; i < modelObject.getTable().get(0).size(); i++) {
					String[] sampleUkLineage = modelObject.getTable().get(0).get(i).split("\\|");
					if (sampleUkLineage[4].equals(ukLineage[k])) {
						if (!(indexToAddUKL.contains(i))) {
							indexToAddUKL.add(i);
						}
					}
				}
			}
			//create the newFilteredTable
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered

			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);//set the customTable to the newfilteredTable
			//check if there is need to remove a searched name if the highlighted sample don't exist in the current Table
			modelObject.removeHighlightColumn(gui.searchedSamples, newfilteredTable);
			modelObject.newSearch(gui.searchedSamples, newfilteredTable);  //search the remaining names of the samples

			//draw the new heatmap
		
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			recreateGui(); //recreates the frame


		} else if (e.getSource() == gui.getRegionsList()) { // handle action when a region in the JCombobox is selected
			isSelected4 = true;
			String[] regionName = gui.getRegionsList().getSelectedItems(); // store the name of the selected region
			if (regionName.length == 0) {
				isSelected4 = false; //if nothing is selected set the filter's boolean to false
			}
			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToAddRegions = new ArrayList<Integer>();
			
			//for each region check if a sample has that region on its name
			// if it does store the index of the sample to be added in the new filteredTable
			for (int k = 0; k < regionName.length; k++) {
				for (int i = 11; i < modelObject.getTable().get(0).size(); i++) {
					String[] sampleRegion = modelObject.getTable().get(0).get(i).split("/");
					if (regionName[k].equals(sampleRegion[1])) {
						if (!(indexToAddRegions.contains(i))) {
							indexToAddRegions.add(i);
						}
					}
				}
			}
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered

			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);//set the customTable to the newfilteredTable
			//check if there is need to remove a searched name if the highlighted sample don't exist in the current Table
			modelObject.removeHighlightColumn(gui.searchedSamples, newfilteredTable);
			modelObject.newSearch(gui.searchedSamples, newfilteredTable);  //search the remaining names of the samples
			
			//draw the new heatmap
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			recreateGui(); //recreates the frame


		} else if (e.getSource() == gui.getCommonMutationSites()) { //gets activated when the user checks the commonMutationSites checkbox

			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			
			//if the checkbox is checked set the parameter's boolean to true or else set to false
			if (gui.getCommonMutationSites().isSelected()) {
				isUsed = true;
			} else {
				isUsed = false;
			}

			coordinates = new Hashtable<String, ArrayList<Integer>>();

			int consecutiveMut = 10; //consecutive mutation threshold
			String mutToCheck = null;

			int count = 0;
			
			//if the customTable has at least a sample and if a clustering options has been selected the customTable gets clustered

			if (index == 2) {
				newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
			} else if (index == 3) {
				newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
			}
			
			//count the consecutive mutations per row
			for (int j = 1; j < newfilteredTable.size(); j++) {
				for (int i = 11; i < newfilteredTable.get(0).size(); i++) {

					String tempMut = newfilteredTable.get(j).get(i); //store the current mutation 
					if (!(tempMut.equals("."))) { //only count if there is a mutation
						if (mutToCheck == null||!(mutToCheck.equals(tempMut))) { 
							mutToCheck = tempMut;
							count = 1;
							
						} else { //if the mutation checked is equal to the current mutation count is increased by one
							++count;
							
						}
						
						//if there are more than 10 consecutive mutation store the mutation and 
						//the coordinates of the first mutation in that line of consecutive mut(column, row) if there are not already stored
						if (count >= consecutiveMut && !(coordinates.containsKey(tempMut))) {
							ArrayList<Integer> XYCoord = new ArrayList<Integer>();
							XYCoord.add(i-consecutiveMut);
							XYCoord.add(j);
							coordinates.put(tempMut, XYCoord);

						}
					}else { // if there is no mutation counter starts from 1 again
						count=1;
					}

				}
				

			}
			
			//draw the new heatmap
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);
			recreateGui(); //recreates the frame

		}
	}
	
	public void recreateGui() {
		gui.remove(scrollPane); //remove the old scrollpane
		heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + positionLabelSize,
				modelObject.customTable.size() * (pixel + 1) + sampleLabelSize)); //set the size of the heatmapPanel based on samples and position used 
		scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); //add the heatmap to the scrollpane
		gui.getContentPane().add(scrollPane, BorderLayout.CENTER); //add the scrollpane to the frame
		gui.revalidate();
	}

}
