import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
	private List regionsList, globalLineageList, UKLineageList;
	private int pixel = 6;
	private int index = 1;
	// public static int[] sampleIndex;
	public static int[] posIndex;
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
		modelObject = model;
		gui = new HeatmapGUI(this, modelObject);
		clusteringObject = new HeatmapClustering();
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
			gui.getContentPane().remove(gui.getWelcomePanel());
			String file = modelObject.selectFile(gui); // get the file with mutation data
			modelObject.readFile(file);
			heatmapPanel = modelObject.drawData(modelObject.getTable(), pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor); // draw the heatmap with obtained data
			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + 170,
					modelObject.customTable.size() * (pixel + 1) + 500));

			/*
			 * add the heatmap panel to a scrollPane and add the scrollPane to the JFrame
			 */
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);

			// add the list of the regions' name to JCheckBoxes

			JLabel regionLabel = new JLabel("Select region(s):");
			regionsList = new List(10, true);

			for (String s : modelObject.countries) {
				regionsList.add(s);
			}
			// create a JComboBox with checkboxes
			regionsList.addItemListener(this);
			gui.getRightPanel().add(regionLabel);
			gui.getRightPanel().add(regionsList);

			JLabel globalLineageLabel = new JLabel("Select global Lineage(s):");
			globalLineageList = new List(10, true);

			for (String s : modelObject.globalLineage) {
				globalLineageList.add(s);
			}

			globalLineageList.addItemListener(this);
			gui.getRightPanel().add(globalLineageLabel);
			gui.getRightPanel().add(globalLineageList);

			JLabel UKLineageLabel = new JLabel("Select UK Lineage(s):");
			UKLineageList = new List(10, true);

			for (String s : modelObject.UKLineage) {
				UKLineageList.add(s);
			}

			UKLineageList.addItemListener(this);
			gui.getRightPanel().add(UKLineageLabel);
			gui.getRightPanel().add(UKLineageList);

			// --------------------set date limits and add the date pickers to corresponding
			// panel ----------------------------------------

			JLabel date = new JLabel("Select date range:");
			gui.getDateSettings1().setDateRangeLimits(modelObject.getMinPastDate(), modelObject.getMaxDate());
			gui.getDateSettings2().setDateRangeLimits(modelObject.getMinPastDate(), modelObject.getMaxDate());
			gui.getDatePicker1().setDate(modelObject.getMinPastDate());
			gui.getDatePicker2().setDate(modelObject.getMaxDate());
			gui.getDatePicker1().addDateChangeListener(this);
			gui.getDatePicker2().addDateChangeListener(this);
			gui.getDatePanel().add(gui.getDatePicker1());
			gui.getDatePanel().add(gui.getDatesTo());
			gui.getDatePanel().add(gui.getDatePicker2());

			gui.setSize(1500, 800);
			gui.getRightPanel().add(date);
			gui.getRightPanel().add(gui.getDatePanel());
			gui.getRightPanel().add(gui.getResetPanel());

			gui.getRightPanel().setVisible(true); // set the panel with filters visible

			gui.revalidate();

		} else if (e.getSource() == gui.savePng) {
			modelObject.saveImage(heatmapPanel); // save the heatmap Panel as png or jpeg

		} else if (e.getSource() == gui.savePDF) {
			modelObject.saveImagePDF(heatmapPanel); // save the heatmap Panel as PDF

		} else if (e.getSource() == gui.synColor) {
			synColor = JColorChooser.showDialog(cc, "Color Chooser", gui.getBackground()); // save the color for the
																							// synonymous
																							// mutation,selected by the
																							// user
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			/*
			 * remove the old heatmap Panel and recreate the new one
			 */
			gui.remove(scrollPane);
			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + 170,
					modelObject.customTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		} else if (e.getSource() == gui.nonSynColor) {
			nonSynColor = JColorChooser.showDialog(cc, "Color Chooser", gui.getBackground()); // save the color for the
																								// non-synonymous
																								// mutation selected by
																								// the user
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			gui.remove(scrollPane);
			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + 170,
					modelObject.customTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);

			gui.revalidate();

		} else if (e.getSource() == gui.noMutColor) {
			noMutationColor = JColorChooser.showDialog(cc, "Color Chooser", gui.getBackground()); // save the color for
																									// the
																									// non-synonymous
																									// mutation selected
																									// by the user
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			gui.remove(scrollPane);
			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + 170,
					modelObject.customTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);

			gui.revalidate();

		} else if (e.getSource() == gui.delColor) {
			delColor = JColorChooser.showDialog(cc, "Color Chooser", gui.getBackground()); // save the color for the
																							// non-synonymous mutation
																							// selected by the user
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			gui.remove(scrollPane);
			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + 170,
					modelObject.customTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);

			gui.revalidate();

		} else if (e.getSource() == gui.insColor) {
			insColor = JColorChooser.showDialog(cc, "Color Chooser", gui.getBackground()); // save the color for the
																							// non-synonymous mutation
																							// selected by the user
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			gui.remove(scrollPane);
			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + 170,
					modelObject.customTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);

			gui.revalidate();

		} else if (e.getSource() == gui.noCColor) {
			noCodingColor = JColorChooser.showDialog(cc, "Color Chooser", gui.getBackground()); // save the color for
																								// the non-synonymous
																								// mutation selected by
																								// the user
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			gui.remove(scrollPane);
			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + 170,
					modelObject.customTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);

			gui.revalidate();

		} else if (e.getSource() == gui.addButton) {
			String sample = gui.searchSample.getText();
			searchUsed = true;
			gui.getSearchedSamples().add(sample);
			;
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}

			modelObject.newSearch(gui.getSearchedSamples(), newfilteredTable);

			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			gui.remove(scrollPane);
			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + 170,
					modelObject.customTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);

			gui.revalidate();
		} else if (e.getSource() == gui.clearButton) {
			String[] nameToRemove = gui.getSearchedSamples().getSelectedItems(); // store the name of the selected
																					// region
			for (String i : nameToRemove) {
				gui.getSearchedSamples().remove(i);
			}

			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}

			modelObject.newSearch(gui.getSearchedSamples(), newfilteredTable);

			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			gui.remove(scrollPane);
			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + 170,
					modelObject.customTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);

			gui.revalidate();
		} else if (e.getSource() == gui.addPButton) {

			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}

			String position = gui.searchPosition.getText();
			searchPUsed = true;
			gui.getSearchedPositions().add(position);
			String[] posNames = gui.getSearchedPositions().getItems();
			int count = 0;
			posIndex = new int[posNames.length];
			for (String j : posNames) {
				for (int i = 1; i < newfilteredTable.size(); i++) {

					if (j.equals(newfilteredTable.get(i).get(10))) {
						posIndex[count] = i - 1;
					}

				}
				count++;
			}

			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			gui.remove(scrollPane);
			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + 170,
					modelObject.customTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);

			gui.revalidate();
		} else if (e.getSource() == gui.clearPButton) {
			String[] nameToRemove = gui.getSearchedPositions().getSelectedItems(); // store the name of the selected
																					// region
			for (String i : nameToRemove) {
				gui.getSearchedPositions().remove(i);
			}
			String[] posNames = gui.getSearchedPositions().getItems();
			if (posNames.length == 0) {
				searchPUsed = false;
			}
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
				}
			}
			int count = 0;
			posIndex = new int[posNames.length];
			for (String j : posNames) {
				for (int i = 1; i < newfilteredTable.size(); i++) {

					if (j.equals(newfilteredTable.get(i).get(10))) {
						posIndex[count] = i - 1;
					}

				}
				count++;
			}

			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			gui.remove(scrollPane);
			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + 170,
					modelObject.customTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);

			gui.revalidate();
		} else if (e.getSource() == gui.getHelp()) {

			JOptionPane.showMessageDialog(null, gui.getScrollPaneHelp(), "Help", JOptionPane.PLAIN_MESSAGE);
		} else if (e.getSource() == gui.mutNumber) {
			isSelected5 = true;
			int minNumber = Integer.parseInt(gui.mutNumber.getText());
			int[] countMutations = new int[modelObject.getTable().get(0).size()];
			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToAddMutN = new ArrayList<Integer>();
			Arrays.fill(countMutations, 0);
			for (int j = 1; j < modelObject.getTable().size(); j++) {

				for (int i = 11; i < modelObject.getTable().get(0).size(); i++) {
					if (!(modelObject.getTable().get(j).get(i).equals("."))) {
						countMutations[i] += 1;
					}
				}
			}
			for (int i = 11; i < countMutations.length; i++) {
				if (countMutations[i] >= minNumber) {
					if (!(indexToAddMutN.contains(i))) {
						indexToAddMutN.add(i);
					}
				}
			}

			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);
			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);
			modelObject.removeHighlight(gui.searchedSamples, newfilteredTable);
			modelObject.newSearch(gui.searchedSamples, newfilteredTable);
			/*
			 * remove the old heatmap Panel and recreate the new one
			 */

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			heatmapPanel.setPreferredSize(new Dimension(newfilteredTable.get(0).size() * (pixel + 1) + 170,
					newfilteredTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		} else if (e.getSource() == gui.mutPerPos) {
			isSelected9 = true;
			int count = 0;
			int minNumber = Integer.parseInt(gui.mutPerPos.getText());
			int[] countMutations = new int[modelObject.getTable().size()];
			newfilteredTable = new ArrayList<ArrayList<String>>();
			rowsToRemove = new ArrayList<Integer>();
			Arrays.fill(countMutations, 0);
			for (int j = 1; j < modelObject.getTable().size(); j++) {

				for (int i = 11; i < modelObject.getTable().get(0).size(); i++) {
					if (!(modelObject.getTable().get(j).get(i).equals("."))) {
						count += 1;
					}
				}

				countMutations[j] = count;
				count = 0;
			}

			for (int i = 1; i < countMutations.length; i++) {
				if (countMutations[i] < minNumber) {
					if (!(rowsToRemove.contains(i))) {
						rowsToRemove.add(i);
					}
				}
			}

			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);
			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);

			String[] posNames = gui.getSearchedPositions().getItems();

			for (String k : posNames) {
				boolean isFound = false;
				for (int j = 1; j < newfilteredTable.size(); j++) {
					if (newfilteredTable.get(j).get(10).contains(k)) {
						isFound = true;
						break;
					}

				}
				if (!(isFound)) {
					gui.getSearchedPositions().remove(k);
				}
			}

			String[] newPosNames = gui.getSearchedPositions().getItems();
			int countLabel = 0;
			posIndex = new int[newPosNames.length];

			for (String j : newPosNames) {
				for (int i = 1; i < newfilteredTable.size(); i++) {

					if (j.equals(newfilteredTable.get(i).get(10))) {
						posIndex[countLabel] = i - 1;
					}
				}
				countLabel++;
			}
			/*
			 * remove the old heatmap Panel and recreate the new one
			 */

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			heatmapPanel.setPreferredSize(new Dimension(newfilteredTable.get(0).size() * (pixel + 1) + 170,
					newfilteredTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		} else if (e.getSource() == gui.samplesNumber) {
			isSelected8 = true;
			int minSampNumber = Integer.parseInt(gui.samplesNumber.getText());
			int[] countSamples = new int[modelObject.getTable().get(0).size()];
			Hashtable<String, Integer> differentMutations = new Hashtable<String, Integer>();
			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToAddSampleN = new ArrayList<Integer>();
			Arrays.fill(countSamples, 0);
			for (int j = 1; j < modelObject.getTable().size(); j++) {
				for (int i = 11; i < modelObject.getTable().get(0).size(); i++) {
					if (!(modelObject.getTable().get(j).get(i).equals("."))) {
						if (!(differentMutations.containsKey(modelObject.getTable().get(j).get(i)))) {
							differentMutations.put(modelObject.getTable().get(j).get(i), 1);
						} else {
							differentMutations.put(modelObject.getTable().get(j).get(i),
									differentMutations.get(modelObject.getTable().get(j).get(i)) + 1);
						}
					}
				}
			}
			for (int i = 11; i < modelObject.getTable().get(0).size(); i++) {
				for (int j = 1; j < modelObject.getTable().size(); j++) {
					if (differentMutations.containsKey(modelObject.getTable().get(j).get(i))
							&& countSamples[i] < differentMutations.get(modelObject.getTable().get(j).get(i))) {
						countSamples[i] = differentMutations.get(modelObject.getTable().get(j).get(i));
					}

				}
			}
			for (int i = 11; i < countSamples.length; i++) {
				if (countSamples[i] >= minSampNumber) {
					if (!(indexToAddSampleN.contains(i))) {
						indexToAddSampleN.add(i);
					}
				}
			}
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);
			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);
			modelObject.removeHighlight(gui.searchedSamples, newfilteredTable);
			modelObject.newSearch(gui.searchedSamples, newfilteredTable);
			/*
			 * remove the old heatmap Panel and recreate the new one
			 */

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			heatmapPanel.setPreferredSize(new Dimension(newfilteredTable.get(0).size() * (pixel + 1) + 170,
					newfilteredTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		} else if (e.getSource() == gui.hierarchicalClustered) {
			index = 2;
			clusteredTable = clusteringObject.clusterData(modelObject.getCustomTable());

			modelObject.newSearch(gui.getSearchedSamples(), clusteredTable);

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(clusteredTable, pixel, noMutationColor, synColor, nonSynColor, insColor,
					delColor, noCodingColor);

			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + 170,
					modelObject.customTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		} else if (e.getSource() == gui.dateClustered) {
			index = 3;
			sortedTable = clusteringObject.sortDateData(modelObject.getCustomTable());

			modelObject.newSearch(gui.getSearchedSamples(), sortedTable);

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(sortedTable, pixel, noMutationColor, synColor, nonSynColor, insColor,
					delColor, noCodingColor);

			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + 170,
					modelObject.customTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		} else if (e.getSource() == gui.nonClustered) {
			index = 1;
			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);

			modelObject.newSearch(gui.getSearchedSamples(), newfilteredTable);

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + 170,
					modelObject.customTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		} else if (e.getSource() == gui.resetButton) {
			noMutationColor = Color.LIGHT_GRAY;
			synColor = Color.GREEN;
			nonSynColor = Color.MAGENTA;
			insColor = Color.YELLOW;
			delColor = Color.BLACK;
			noCodingColor = Color.ORANGE;
			pixel = 6;
			index = 1;
			modelObject.setCustomTable(modelObject.getTable());

			gui.getMutNumber().setText("");
			gui.getSamplesNumber().setText("");
			gui.getWarning1().setText("");
			gui.getWarning2().setText("");
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
			gui.getCommonMutationSites().setSelected(false);
			gui.nonClustered.setSelected(true);
			gui.dateClustered.setSelected(false);
			gui.hierarchicalClustered.setSelected(false);
			gui.getPixelSize().setValue(6);
			heatmapPanel = modelObject.drawData(modelObject.getTable(), pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			/*
			 * remove the old heatmap Panel and recreate the new one
			 */
			gui.remove(scrollPane);
			heatmapPanel.setPreferredSize(new Dimension(modelObject.getTable().get(0).size() * (pixel + 1) + 170,
					modelObject.getTable().size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getDatePicker1().removeDateChangeListener(this);
			gui.getDatePicker2().removeDateChangeListener(this);
			gui.getDatePicker1().setDate(modelObject.getMinPastDate());
			gui.getDatePicker2().setDate(modelObject.getMaxDate());
			gui.getDatePicker1().addDateChangeListener(this);
			gui.getDatePicker2().addDateChangeListener(this);
			gui.getMutTypeList().removeAll();
			for (String s : gui.getMutations()) {
				gui.getMutTypeList().add(s);
			}
			globalLineageList.removeAll();
			for (String s : modelObject.globalLineage) {
				globalLineageList.add(s);
			}
			regionsList.removeAll();
			for (String s : modelObject.countries) {
				regionsList.add(s);
			}
			UKLineageList.removeAll();
			for (String s : modelObject.UKLineage) {
				UKLineageList.add(s);
			}
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) { // handle the actions when the JSlider is used
		JSlider source = (JSlider) e.getSource();
		if (!source.getValueIsAdjusting()) {
			pixel = (int) source.getValue(); // store the new pixel value
			// create the heatmap Panel
			gui.remove(scrollPane);
			// newTable = new ArrayList<ArrayList<String>>();
			if (modelObject.getCustomTable().get(0).size() > 11) {
				if (index == 2) {

					newfilteredTable = clusteringObject.clusterData(modelObject.getCustomTable());
					modelObject.setCustomTable(newfilteredTable);
				} else if (index == 3) {

					newfilteredTable = clusteringObject.sortDateData(modelObject.getCustomTable());
					modelObject.setCustomTable(newfilteredTable);
				}
			}

			heatmapPanel = modelObject.drawData(modelObject.getCustomTable(), pixel, noMutationColor, synColor,
					nonSynColor, insColor, delColor, noCodingColor);

			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + 170,
					modelObject.customTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		}
	}

	@Override
	public void dateChanged(DateChangeEvent e) {
		if (e.getSource() == gui.getDatePicker1()) {
			isSelected6 = true;
			fromDate = gui.getDatePicker1().getDate();
			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToAddDateF = new ArrayList<Integer>();
			for (int i = 10; i < modelObject.getTable().get(0).size(); i++) {
				if (i != 10) {
					String[] sampleDate = modelObject.getTable().get(0).get(i).split("\\|");
					if (sampleDate[2].length() == 10) {
						LocalDate convertedDate = LocalDate.parse(sampleDate[2],
								DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
						if (toDate == null) {
							if (!(convertedDate.isBefore(fromDate))) {
								if (!(indexToAddDateF.contains(i))) {
									indexToAddDateF.add(i);
								}
							}
						} else {

							if ((!(convertedDate.isBefore(fromDate))) && (!(convertedDate.isAfter(toDate)))) {

								if (!(indexToAddDateF.contains(i))) {
									indexToAddDateF.add(i);
								}
							}
						}
					}
				}
			}
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);

			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);
			modelObject.removeHighlight(gui.searchedSamples, newfilteredTable);
			modelObject.newSearch(gui.searchedSamples, newfilteredTable);
			/*
			 * remove the old heatmap Panel and recreate the new one
			 */

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			heatmapPanel.setPreferredSize(new Dimension(newfilteredTable.get(0).size() * (pixel + 1) + 170,
					newfilteredTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		}
		if (e.getSource() == gui.getDatePicker2()) {
			isSelected7 = true;
			toDate = gui.getDatePicker2().getDate();
			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToAddDateT = new ArrayList<Integer>();
			for (int i = 10; i < modelObject.getTable().get(0).size(); i++) {
				if (i != 10) {
					String[] sampleDate = modelObject.getTable().get(0).get(i).split("\\|");
					if (sampleDate[2].length() == 10) {
						LocalDate convertedDate = LocalDate.parse(sampleDate[2],
								DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
						if (fromDate == null) {
							if (!(convertedDate.isAfter(toDate))) {
								if (!(indexToAddDateT.contains(i))) {
									indexToAddDateT.add(i);
								}
							}
						} else {
							if ((!(convertedDate.isBefore(fromDate))) && (!(convertedDate.isAfter(toDate)))) {
								if (!(indexToAddDateT.contains(i))) {
									indexToAddDateT.add(i);
								}
							}
						}
					}
				}
			}

			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);
			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);
			modelObject.removeHighlight(gui.searchedSamples, newfilteredTable);
			modelObject.newSearch(gui.searchedSamples, newfilteredTable);
			/*
			 * remove the old heatmap Panel and recreate the new one
			 */

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			heatmapPanel.setPreferredSize(new Dimension(newfilteredTable.get(0).size() * (pixel + 1) + 170,
					newfilteredTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == gui.getMutTypeList()) {
			isSelected1 = true;
			String[] selected = gui.getMutTypeList().getSelectedItems();
			if (selected.length == 0) {
				isSelected1 = false;
			}
			String[] mutType = new String[selected.length];
			for (int i = 0; i < selected.length; i++) {
				mutType[i] = gui.getMutationsHash().get(selected[i]);
			}

			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToAddMutT = new ArrayList<Integer>();
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
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);

			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);
			modelObject.removeHighlight(gui.searchedSamples, newfilteredTable);
			modelObject.newSearch(gui.searchedSamples, newfilteredTable);
			/*
			 * remove the old heatmap Panel and recreate the new one
			 */

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			heatmapPanel.setPreferredSize(new Dimension(newfilteredTable.get(0).size() * (pixel + 1) + 170,
					newfilteredTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		} else if (e.getSource() == globalLineageList) { // handles the global lineage filter
			isSelected2 = true;
			String[] globalLineage = globalLineageList.getSelectedItems();
			if (globalLineage.length == 0) {
				isSelected2 = false;
			}
			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToAddGL = new ArrayList<Integer>();
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
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);
			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);
			modelObject.removeHighlight(gui.searchedSamples, newfilteredTable);
			modelObject.newSearch(gui.searchedSamples, newfilteredTable);
			/*
			 * remove the old heatmap Panel and recreate the new one
			 */

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			heatmapPanel.setPreferredSize(new Dimension(newfilteredTable.get(0).size() * (pixel + 1) + 170,
					newfilteredTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();
		} else if (e.getSource() == UKLineageList) { // handles the uk lineage filter
			isSelected3 = true;

			String[] ukLineage = UKLineageList.getSelectedItems();
			if (ukLineage.length == 0) {
				isSelected3 = false;
			}

			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToAddUKL = new ArrayList<Integer>();
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
			newfilteredTable = modelObject.columnsToKeep(indexToAddMutT, isSelected1, indexToAddGL, isSelected2,
					indexToAddUKL, isSelected3, indexToAddRegions, isSelected4, indexToAddMutN, isSelected5,
					indexToAddDateF, isSelected6, indexToAddDateT, isSelected7, indexToAddSampleN, isSelected8,
					rowsToRemove, isSelected9);
			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);
			modelObject.removeHighlight(gui.searchedSamples, newfilteredTable);
			modelObject.newSearch(gui.searchedSamples, newfilteredTable);

			/*
			 * remove the old heatmap Panel and recreate the new one
			 */

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			heatmapPanel.setPreferredSize(new Dimension(newfilteredTable.get(0).size() * (pixel + 1) + 170,
					newfilteredTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		} else if (e.getSource() == regionsList) { // handle action when a region in the JCombobox is selected
			isSelected4 = true;
			String[] regionName = regionsList.getSelectedItems(); // store the name of the selected region
			if (regionName.length == 0) {
				isSelected4 = false;
			}
			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToAddRegions = new ArrayList<Integer>();
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

			if (newfilteredTable.get(0).size() > 11) {
				if (index == 2) {
					newfilteredTable = clusteringObject.clusterData(newfilteredTable);
				} else if (index == 3) {
					newfilteredTable = clusteringObject.sortDateData(newfilteredTable);
				}
			}
			modelObject.setCustomTable(newfilteredTable);
			modelObject.removeHighlight(gui.searchedSamples, newfilteredTable);
			modelObject.newSearch(gui.searchedSamples, newfilteredTable);
			/*
			 * remove the old heatmap Panel and recreate the new one
			 */

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			heatmapPanel.setPreferredSize(new Dimension(newfilteredTable.get(0).size() * (pixel + 1) + 170,
					newfilteredTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		} else if (e.getSource() == gui.getCommonMutationSites()) {

			newfilteredTable = new ArrayList<ArrayList<String>>();
			newfilteredTable = modelObject.customTable;
			if (gui.getCommonMutationSites().isSelected()) {
				isUsed = true;
			} else {
				isUsed = false;
			}

			coordinates = new Hashtable<String, ArrayList<Integer>>();

			int consecutiveMut = 10;
			String mutToCheck = null;

			int count = 0;
			if (index == 2) {
				newfilteredTable = clusteringObject.clusterData(modelObject.customTable);
			} else if (index == 3) {
				newfilteredTable = clusteringObject.sortDateData(modelObject.customTable);
			}
			for (int j = 1; j < newfilteredTable.size(); j++) {
				for (int i = 11; i < newfilteredTable.get(0).size(); i++) {

					String tempMut = modelObject.getTable().get(j).get(i);
					if (!(tempMut.equals("."))) {
						if (mutToCheck == null) {
							mutToCheck = tempMut;
							count = 1;
						} else if (!(mutToCheck.equals(tempMut))) {
							mutToCheck = tempMut;
							count = 1;

						} else {
							++count;
						}
						if (count >= consecutiveMut && !(coordinates.containsKey(tempMut))) {
							ArrayList<Integer> XYCoord = new ArrayList<Integer>();
							System.out.println(i - count);
							XYCoord.add(i - count);
							XYCoord.add(j);
							coordinates.put(tempMut, XYCoord);

						}
					}

				}

			}
			gui.remove(scrollPane);

			heatmapPanel = modelObject.drawData(newfilteredTable, pixel, noMutationColor, synColor, nonSynColor,
					insColor, delColor, noCodingColor);

			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1) + 170,
					modelObject.customTable.size() * (pixel + 1) + 500));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		}
	}

}
