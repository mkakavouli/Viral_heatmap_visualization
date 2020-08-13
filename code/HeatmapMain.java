import java.awt.Color;
import java.awt.Dimension;
import java.util.Scanner;
import javax.swing.JPanel;

public class HeatmapMain {
	private static JPanel heatmapPanel;

	public static void main(String[] args) {
		HeatmapModel model = new HeatmapModel();
		
		//If the program is launched from the command line and the file name is given, a heatmap with the default parameters is saved in png
		if(args.length==1) {

			model.readFile(args[0]);
			heatmapPanel = model.drawData(model.getTable(), 8, Color.LIGHT_GRAY,Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.BLACK, Color.ORANGE); // draw the heatmap with obtained data
			heatmapPanel.setPreferredSize(new Dimension(model.getTable().get(0).size() * (8+ 1)+170,
					model.getTable().size() * (8 + 1)+620));

			String filePath=model.saveFromConsole(heatmapPanel);
			System.out.println("The heatmap plot is saved as: "+filePath);
		}
		// if the program is launched from console without a file name provided or from the jar file the GUI is displayed
		else {

			HeatmapController controller = new HeatmapController(model);
		}

	}

}
