import java.awt.*;
import javax.swing.*;

public class HeatmapGUI extends JFrame {
	private JPanel topPanel,rightPanel,heatmapPanel;
	private JMenuBar fileMenuBar;
	private JMenu fileMenu, save,colors;
	public JMenuItem importFile,savePDF,savePng,nonSynColor,synColor;
	private HeatmapController localController;

	
	//create the the JFrame
	public HeatmapGUI(HeatmapController controller) {
		localController=controller;
		setLayout(new BorderLayout());
		setTitle("COVID-19 Nucleotide Mutations");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		this.setSize(1500, 800);
		setup();
	
	}
	
	//setup method generates the main Panels and the buttons
	private void setup() {
		//create three main panels
		topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		rightPanel = new JPanel();
		heatmapPanel = new JPanel();
	
		//topPanel

		//generate the menuBar and the menus and sub-menus
		fileMenuBar=new JMenuBar();
		fileMenu = new JMenu("File");
		save=new JMenu("Save image as");
		colors=new JMenu("Personalise");
		
		//generate the menu items
		importFile=new JMenuItem("Import .txt file");
		savePDF=new JMenuItem("PDF");
		savePng=new JMenuItem("png");
		synColor=new JMenuItem("Synonumous mutation");
		nonSynColor=new JMenuItem("Non-synonumous mutation");
		
		//add actionListener in all the menu-items
		importFile.addActionListener(localController);;
		savePDF.addActionListener(localController);
		savePng.addActionListener(localController);
		synColor.addActionListener(localController);
		nonSynColor.addActionListener(localController);
		
		//add the menu-items to the menus
		save.add(savePDF);
		save.add(savePng);
		fileMenu.add(importFile);
		fileMenu.add(save);
		colors.add(nonSynColor);
		colors.add(synColor);
		
		//add menus to the menu bat
		fileMenuBar.add(fileMenu);
		fileMenuBar.add(colors);
		topPanel.add(fileMenuBar,BorderLayout.WEST); //add the menu bar to the topPanel

		//HeatmapPanel

		JScrollPane scrollPane = new JScrollPane(heatmapPanel); //add scrollPanel to the HeatmapPanel
		
		rightPanel.setBackground(Color.yellow);

		//add the main Panels to the JFrame
		add(topPanel,BorderLayout.NORTH);
		add(rightPanel,BorderLayout.EAST);
		add(scrollPane,BorderLayout.CENTER);
		
	}

	//getter for the heatmapPanel to be used in the other classes
	public JPanel getHeatmapPanel() {
		return heatmapPanel;
	}
}