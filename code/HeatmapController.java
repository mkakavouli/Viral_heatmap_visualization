public class HeatmapController implements ActionListener, ChangeListener, DateChangeListener {
	private HeatmapGUI gui;
	private HeatmapModel modelObject;
	private HeatmapClustering clusteringObject;
	private ComboBoxes regionsBox, globalLineageBox, UKLineageBox;
	private JColorChooser cc;
	private Color newSynColor, newNonSynColor;
	private JPanel heatmapPanel;
	private JScrollPane scrollPane;
	private LocalDate toDate = null;
	private LocalDate fromDate = null;
	private int pixel = 6;
	ArrayList<ArrayList<String>> filteredTable;
	ArrayList<ArrayList<String>> newfilteredTable;
	ArrayList<ArrayList<String>> clusteredTable;
	ArrayList<ArrayList<String>> sortedTable;
	ArrayList<Integer> indexToRemove;

	public HeatmapController(HeatmapModel model) {
		modelObject = model;
		gui = new HeatmapGUI(this);
		clusteringObject = new HeatmapClustering();
	}

	// manage the actions after the press of a button
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == gui.importFile) { // handle the actions when the import file button is pressed
			String file = modelObject.selectFile(gui); // get the file with mutation data
			modelObject.readFile(file);
			heatmapPanel = modelObject.drawData(modelObject.getTable(), pixel); // draw the heatmap with obtained data
			heatmapPanel.setPreferredSize(new Dimension(pixel * 600, pixel * 600));

			/*
			 * add the heatmap panel to a scrollPane and add the scrollPane to the JFrame
			 */
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);

			// add the list of the regions' name to JCheckBoxes

			Vector v = new Vector();
			v.add("Countries/Regions");
			for (String s : modelObject.countries) {
				v.add(new JCheckBox(s, false));

			}
			regionsBox = new ComboBoxes(v); // create a JComboBox with checkboxes
			regionsBox.addActionListener(this);
			gui.getFilterPanel().add(regionsBox);

			Vector vGlobalLineage = new Vector();
			vGlobalLineage.add("Global Lineage");
			for (String s : modelObject.globalLineage) {
				vGlobalLineage.add(new JCheckBox(s, false));

			}
			globalLineageBox = new ComboBoxes(vGlobalLineage); // create a JComboBox with checkboxes
			globalLineageBox.addActionListener(this);
			gui.getFilterPanel().add(globalLineageBox);

			Vector vUKLineage = new Vector();
			vUKLineage.add("UK Lineage");
			for (String s : modelObject.UKLineage) {
				vUKLineage.add(new JCheckBox(s, false));

			}
			UKLineageBox = new ComboBoxes(vUKLineage); // create a JComboBox with checkboxes
			UKLineageBox.addActionListener(this);
			gui.getFilterPanel().add(UKLineageBox);

			// --------------------set date limits and add the date pickers to corresponding
			// panel ----------------------------------------

			gui.getDateSettings1().setDateRangeLimits(modelObject.getMinPastDate(), modelObject.getMaxDate());
			gui.getDateSettings2().setDateRangeLimits(modelObject.getMinPastDate(), modelObject.getMaxDate());
			gui.getDatePicker1().setDate(modelObject.getMinPastDate());
			gui.getDatePicker2().setDate(modelObject.getMaxDate());
			gui.getDatePicker1().addDateChangeListener(this);
			gui.getDatePicker2().addDateChangeListener(this);
			gui.getDatePanel().add(gui.getDatePicker1());
			gui.getDatePanel().add(gui.getDatesTo());
			gui.getDatePanel().add(gui.getDatePicker2());

			gui.getRightPanel().setVisible(true); // set the panel with filters visible

			gui.revalidate();

		} else if (e.getSource() == gui.savePng) {
			modelObject.saveImage(heatmapPanel); // save the heatmap Panel as png or jpeg

		} else if (e.getSource() == gui.savePDF) {
			modelObject.saveImagePDF(heatmapPanel); // save the heatmap Panel as PDF

		} else if (e.getSource() == gui.synColor) {

			newSynColor = JColorChooser.showDialog(cc, "Color Chooser", gui.getBackground()); // save the color for the
																								// synonymous mutation
																								// selected by the user
			if (newNonSynColor == null) { // check if the user has already change the non-synonymous color
				heatmapPanel = modelObject.customDrawData(newSynColor, Color.MAGENTA, "Syn", "Non",
						modelObject.getCustomTable(), pixel);
			} else {
				heatmapPanel = modelObject.customDrawData(newSynColor, newNonSynColor, "Syn", "Non",
						modelObject.getCustomTable(), pixel);
			}
			/*
			 * remove the old heatmap Panel and recreate the new one
			 */

			gui.remove(scrollPane);
			heatmapPanel.setPreferredSize(new Dimension(5000, 5000));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		} else if (e.getSource() == gui.nonSynColor) {
			newNonSynColor = JColorChooser.showDialog(cc, "Color Chooser", gui.getBackground()); // save the color for
																									// the
																									// non-synonymous
																									// mutation selected
																									// by the user

			if (newSynColor == null) { // check if the user has already change the synonymous color
				heatmapPanel = modelObject.customDrawData(Color.GREEN, newNonSynColor, "Syn", "Non",
						modelObject.getCustomTable(), pixel);
			} else {
				heatmapPanel = modelObject.customDrawData(newSynColor, newNonSynColor, "Syn", "Non",
						modelObject.getCustomTable(), pixel);
			}
			gui.remove(scrollPane);
			heatmapPanel.setPreferredSize(new Dimension(5000, 5000));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);

			gui.revalidate();

		} else if (e.getSource() == gui.getMutationBox()) {
			String mutType = (String) gui.getMutationBox().checkedBox();
			;
			String mutNumber = gui.getMutationsHash().get(mutType);
			System.out.println(mutNumber);

		} else if (e.getSource() == gui.hierarchicalClustered) {
			clusteredTable = clusteringObject.clusterData(modelObject.getTable());
			modelObject.getCustomTable().clear();
			modelObject.setCustomTable(clusteredTable);

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(clusteredTable, 8);

			heatmapPanel.setPreferredSize(new Dimension(10000, 10000));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		} else if (e.getSource() == gui.dateClustered) {
			sortedTable = clusteringObject.sortDateData(modelObject.getTable());
			modelObject.getCustomTable().clear();
			modelObject.setCustomTable(sortedTable);

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(sortedTable, 8);

			heatmapPanel.setPreferredSize(new Dimension(10000, 10000));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		} else if (e.getSource() == gui.nonClustered) {

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(modelObject.getTable(), 8);

			heatmapPanel.setPreferredSize(new Dimension(10000, 10000));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		} else if (e.getSource() == (ComboBoxes) e.getSource()) { // handle action when a region in the JCombobox
																	// is selected
			ArrayList<String> tempRow = null;
			String regionName = regionsBox.checkedBox(); // store the name of the selected region
			newfilteredTable = new ArrayList<ArrayList<String>>();
			// filteredTable = modelObject.getTable();
			indexToRemove = new ArrayList<Integer>();
			for (int i = 1; i < modelObject.getCustomTable().get(0).size(); i++) {
				String[] sampleRegion = modelObject.getCustomTable().get(0).get(i).split("/");
				if (!(regionName.contentEquals(sampleRegion[1]))) {
					indexToRemove.add(i);
				}
			}
			/*
			 * find the columns(samples) that don't contain at their details the selected
			 * region and remove those columns
			 */

			for (int j = 0; j < modelObject.getCustomTable().size(); j++) {
				int countRemove = 0;
				for (int i : indexToRemove) {
					tempRow = modelObject.getCustomTable().get(j);
					tempRow.remove(i - countRemove);
					countRemove += 1;
				}
				newfilteredTable.add(tempRow);

			}

			modelObject.getCustomTable().clear();
			modelObject.setCustomTable(newfilteredTable);
			/*
			 * /* remove the old heatmap Panel and recreate the new one
			 */

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(newfilteredTable, 8);

			heatmapPanel.setPreferredSize(new Dimension(5000, 5000));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
			heatmapPanel = modelObject.drawData(modelObject.getCustomTable(), pixel);

			heatmapPanel.setPreferredSize(new Dimension(modelObject.customTable.get(0).size() * (pixel + 1),
					modelObject.customTable.get(0).size() * (pixel + 1)));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		}
	}

	@Override
	public void dateChanged(DateChangeEvent e) {
		ArrayList<String> tempRow = null;
		if (e.getSource() == gui.getDatePicker1()) {
			LocalDate fromDate = gui.getDatePicker1().getDate();

			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToRemove = new ArrayList<Integer>();
			for (int i = 0; i < modelObject.getCustomTable().get(0).size(); i++) {
				if (i != 0) {
					String[] sampleDate = modelObject.getCustomTable().get(0).get(i).split("\\|");
					if (sampleDate[2].length() == 10) {
						LocalDate convertedDate = LocalDate.parse(sampleDate[2],
								DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
						if (toDate == null) {
							if (convertedDate.isBefore(fromDate)) {
								indexToRemove.add(i);
							}
						} else {
							if (convertedDate.isBefore(fromDate) || convertedDate.isAfter(toDate)) {
								indexToRemove.add(i);
							}
						}
					}
				}
			}

			for (int j = 0; j < modelObject.getCustomTable().size(); j++) {
				int countRemove = 0;
				for (int i : indexToRemove) {
					tempRow = modelObject.getCustomTable().get(j);
					tempRow.remove(i - countRemove);
					countRemove += 1;
				}
				newfilteredTable.add(tempRow);

			}

			modelObject.getCustomTable().clear();
			modelObject.setCustomTable(newfilteredTable);
			/*
			 * remove the old heatmap Panel and recreate the new one
			 */

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(newfilteredTable, 8);

			heatmapPanel.setPreferredSize(new Dimension(5000, 5000));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		}
		if (e.getSource() == gui.getDatePicker2()) {
			toDate = gui.getDatePicker2().getDate();
			newfilteredTable = new ArrayList<ArrayList<String>>();
			indexToRemove = new ArrayList<Integer>();
			for (int i = 0; i < modelObject.getCustomTable().get(0).size(); i++) {
				if (i != 0) {
					String[] sampleDate = modelObject.getCustomTable().get(0).get(i).split("\\|");
					if (sampleDate[2].length() == 10) {
						LocalDate convertedDate = LocalDate.parse(sampleDate[2],
								DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
						if (fromDate == null) {
							if (convertedDate.isAfter(toDate)) {
								indexToRemove.add(i);
							}
						} else {
							if (convertedDate.isBefore(fromDate) || convertedDate.isAfter(toDate)) {
								indexToRemove.add(i);
							}
						}
					}
				}
			}

			for (int j = 0; j < modelObject.getCustomTable().size(); j++) {
				int countRemove = 0;
				for (int i : indexToRemove) {
					tempRow = modelObject.getCustomTable().get(j);
					tempRow.remove(i - countRemove);
					countRemove += 1;
				}
				newfilteredTable.add(tempRow);

			}

			modelObject.getCustomTable().clear();
			modelObject.setCustomTable(newfilteredTable);
			/*
			 * remove the old heatmap Panel and recreate the new one
			 */

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(modelObject.getCustomTable(), 8);

			heatmapPanel.setPreferredSize(new Dimension(5000, 5000));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();
		}
	}
}
