import java.awt.Dimension;
import java.util.Scanner;
import javax.swing.JPanel;

public class HeatmapMain {
	private static JPanel heatmapPanel;

	public static void main(String[] args) {
		HeatmapModel model = new HeatmapModel();
		
		//If the program is launched from the command line heatmap with the default parameters is saved in png
		if (System.console() != null) {
			
			System.out.println("Please provide the file path");
			Scanner s = new Scanner(System.in);
			String filePath = s.next();
			model.readFile(filePath);
			heatmapPanel = model.drawData(model.getTable(), 8); // draw the heatmap with obtained data
			heatmapPanel.setPreferredSize(new Dimension(model.getTable().get(0).size() * (8+ 1)+170,
					model.getTable().size() * (8 + 1)+500));

			model.saveFromConsole(heatmapPanel);
		}
		// if the program is launched from jar file the GUI is displayed
		else {

			HeatmapController controller = new HeatmapController(model);
		}

	}

}
