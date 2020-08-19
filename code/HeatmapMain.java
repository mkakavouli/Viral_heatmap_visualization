import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;

public class HeatmapMain {
	private static JPanel heatmapPanel;

	public static void main(String[] args) {
		HeatmapModel model = new HeatmapModel();
		
		//If the program is launched from the command line and the file name is given, a heatmap with the default parameters is saved in png
		if(args.length==1) {
			
			int pixel = 8;
			int sampleLabelSize=620;
			int orfLabelSize=190;
			
			//read the file provided as an argument by the user
			
			model.readFile(args[0]);
			
			//if the dataset is too small or too big make squares bigger or smaller respectively
			
			if(model.getTable().size()<=100||model.getTable().get(0).size()<=100) {
				pixel=12;
			}else if(model.getTable().size()>=2000||model.getTable().get(0).size()>=2000){
				pixel =1;
			}
			
			// draw the heatmap plot with default colors
			
			heatmapPanel = model.drawData(model.getTable(), pixel, Color.LIGHT_GRAY,Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.BLACK, Color.ORANGE);
			
			//set the size of the Panel contains the heatmap plot
			
			heatmapPanel.setPreferredSize(new Dimension(model.getTable().get(0).size() * (pixel+ 1)+orfLabelSize,
					model.getTable().size() * (pixel + 1)+sampleLabelSize));

			String filePath=model.saveFromConsole(heatmapPanel,pixel); //save the panel and store the name of the created PNG file
			System.out.println("The heatmap plot is saved as: "+filePath);
		}
		
		// if the program is launched from console without a file name provided or from the jar file the GUI is displayed
		
		else {

			HeatmapController controller = new HeatmapController(model);
		}

	}

}
