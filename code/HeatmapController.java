import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.border.LineBorder;

public class HeatmapController implements ActionListener {
	private HeatmapGUI gui;
	private HeatmapModel modelObject;
	private JColorChooser cc;
	private Color newColor;

	
	public HeatmapController(HeatmapModel model) {
		modelObject=model;
		gui= new HeatmapGUI(this);
	}
	
	//manage the actions after the press of a button
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == gui.importFile) { //handle the actions when the import file button is pressed
			String file =modelObject.selectFile(gui);
			modelObject.readFile(file);
			/*
			set a grid layout in the heatmapPanel with number of columns the sampleNumber 
			and number of rows the number of genomePositions
			*/
			gui.getHeatmapPanel().setLayout(new GridLayout(modelObject.getPositionNumber(),modelObject.getSampleNumber()));
			//generate a JPanel for each position on the map
			 for (int i = 1; i <=modelObject.getPositionNumber(); i++) {
		         for (int j = 1; j <= modelObject.getSampleNumber(); j++) {
		        	 	gui.getHeatmapPanel().add(modelObject.gridHeatmap(i,j));
		         }
		        }
			 
		
		}else if (e.getSource() == gui.savePng) { //handle the actions when save as Png button is pressed
		
			
		}else if (e.getSource() == gui.synColor) {
			newColor = JColorChooser.showDialog( cc, "Color Chooser",gui.getBackground() ); //create a color palette frame and store on a variable the selected color
			
			
		}else if (e.getSource() == gui.nonSynColor) {
			 newColor = JColorChooser.showDialog( cc, "Color Chooser",gui.getBackground() ); //create a color palette frame and store on a variable the selected color
			
		}
	
	}

}
