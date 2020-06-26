import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;

public class HeatmapController implements ActionListener, ChangeListener, DateChangeListener {
	private HeatmapGUI gui;
	private HeatmapModel modelObject;
	private RegionsComboBox regionsBox;
	private JColorChooser cc;
	private Color newSynColor, newNonSynColor;
	private JPanel heatmapPanel;
	private JScrollPane scrollPane;
	private LocalDate toDate=null;
	private LocalDate fromDate=null;
	ArrayList<ArrayList<String>> filteredTable;
	ArrayList<ArrayList<String>> newfilteredTable;
	ArrayList<Integer> indexToRemove;

	public HeatmapController(HeatmapModel model) {
		modelObject = model;
		gui = new HeatmapGUI(this);
	}

	// manage the actions after the press of a button
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == gui.importFile) { // handle the actions when the import file button is pressed
			String file = modelObject.selectFile(gui); // get the file with mutation data
			modelObject.readFile(file);
			heatmapPanel = modelObject.drawData(modelObject.getTable(), 8); // draw the heatmap with obtained data
			heatmapPanel.setPreferredSize(new Dimension(5000, 6000));
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
			regionsBox = new RegionsComboBox(v); // create a JComboBox with checkboxes
			regionsBox.addActionListener(this);
			gui.getFilterPanel().add(regionsBox);

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
				heatmapPanel = modelObject.customDrawData(newSynColor, Color.MAGENTA, "1", "-1",
						modelObject.getCustomTable(),8);
			} else {
				heatmapPanel = modelObject.customDrawData(newSynColor, newNonSynColor, "1", "-1",
						modelObject.getCustomTable(),8);
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
				heatmapPanel = modelObject.customDrawData(Color.GREEN, newNonSynColor, "1", "-1",
						modelObject.getCustomTable(),8);
			} else {
				heatmapPanel = modelObject.customDrawData(newSynColor, newNonSynColor, "1", "-1",
						modelObject.getCustomTable(),8);
			}
			gui.remove(scrollPane);
			heatmapPanel.setPreferredSize(new Dimension(5000, 5000));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);

			gui.revalidate();

		} else if (e.getSource() == (RegionsComboBox) e.getSource()) { // handle action when a region in the JCombobox
																		// is selected

			String regionName = regionsBox.checkedBox(); // store the name of the selected region
			filteredTable = new ArrayList<ArrayList<String>>();
			filteredTable = modelObject.getTable();
			/*
			 * find the columns(samples) that don't contain at their details the selected
			 * region and remove those columns
			 */

			for (int j = 0; j < modelObject.getTable().size(); j++) {
				for (int i = 0; i < modelObject.getTable().get(0).size(); i++) {
					if (!(modelObject.getTable().get(0).get(i).contains(regionName)) && i != 0) {
						filteredTable.get(j).remove(i);
					}
				}
			}
			/*
			 * remove the old heatmap Panel and recreate the new one
			 */

			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(filteredTable, 8);

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
			int pixel = (int) source.getValue(); // store the new pixel value
			// create the heatmap Panel
			gui.remove(scrollPane);
			heatmapPanel = modelObject.drawData(modelObject.getTable(), pixel);

			heatmapPanel.setPreferredSize(new Dimension(600*pixel, 600*pixel));
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
							}else {
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
		if (e.getSource() == gui.getDatePicker2()){
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
						}else {
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
