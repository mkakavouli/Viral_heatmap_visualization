import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class HeatmapController implements ActionListener, ChangeListener {
	private HeatmapGUI gui;
	private HeatmapModel modelObject;
	private RegionsComboBox regionsBox;
	private JColorChooser cc;
	private Color newSynColor, newNonSynColor;
	private JPanel heatmapPanel;
	private JScrollPane scrollPane;
	ArrayList<ArrayList<String>> filteredTable;

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
						modelObject.getTable());
			} else {
				heatmapPanel = modelObject.customDrawData(newSynColor, newNonSynColor, "1", "-1",
						modelObject.getTable());
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
						modelObject.getTable());
			} else {
				heatmapPanel = modelObject.customDrawData(newSynColor, newNonSynColor, "1", "-1",
						modelObject.getTable());
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

			heatmapPanel.setPreferredSize(new Dimension(500 * pixel, 500 * pixel));
			scrollPane = new JScrollPane(heatmapPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			gui.getContentPane().add(scrollPane, BorderLayout.CENTER);
			gui.revalidate();

		}
	}
}
