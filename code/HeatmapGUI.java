import java.awt.*;
import javax.swing.*;

public class HeatmapGUI extends JFrame {
	private JPanel menuPanel,rightPanel,customSizePanel, filterPanel;
	private JMenuBar fileMenuBar;
	private JMenu fileMenu, save,colors;
	public JMenuItem importFile,savePDF,savePng,nonSynColor,synColor;
	private HeatmapController localController;


	
	//create the JFrame
	public HeatmapGUI(HeatmapController controller) {
		localController=controller;
		setLayout(new BorderLayout());
		setTitle("COVID-19 Nucleotide Mutations");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setup();
		this.pack();
		this.setVisible(true);
		this.setSize(1500, 800);
	}
	
	//setup method generates the main Panels and the buttons
	private void setup() {
		// create panels
		menuPanel = new JPanel();
		menuPanel.setLayout(new BorderLayout());
		rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(0,1));
		customSizePanel=new JPanel();
		filterPanel=new JPanel();
	
		//menuPanel
		
		//generate the menuBar,the menus and sub-menus
		fileMenuBar=new JMenuBar();
		fileMenu = new JMenu("File");
		save=new JMenu("Save image as");
		colors=new JMenu("Personalise");
		
		//generate the menu items
		importFile=new JMenuItem("Import .txt file");
		savePDF=new JMenuItem("PDF");
		savePng=new JMenuItem("png/jpeg");
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
		
		//add menus to the menubar
		fileMenuBar.add(fileMenu);
		fileMenuBar.add(colors);
		menuPanel.add(fileMenuBar,BorderLayout.WEST);

		//right panel
		
		final int pixel_MIN = 1;	//minimum square size
		final int pixel_MAX = 30;	//maximum square size
		final int pixel_INIT = 8;    //initial square size
		
		//generate JSlider to customize square size
		JSlider pixelSize = new JSlider(JSlider.HORIZONTAL,pixel_MIN, pixel_MAX, pixel_INIT);
		pixelSize.addChangeListener(localController);

		//Turn on labels at major tick marks.
		pixelSize.setMajorTickSpacing(9);
		pixelSize.setMinorTickSpacing(1);
		pixelSize.setPaintTicks(true);
		pixelSize.setPaintLabels(true);
		
		//create labels for the filters
		JLabel pixels=new JLabel("Square size");
		JLabel filters=new JLabel("Filters");
		
		//add the JComponents to the corresponding JPanels
		customSizePanel.add(pixels);
		customSizePanel.add(pixelSize);
		rightPanel.add(customSizePanel);
		filterPanel.add(filters);
		rightPanel.add(filterPanel);
		
		rightPanel.setVisible(false); //set rightPanel not visible 
		
		//add the panels to JFrame
		this.add(menuPanel,BorderLayout.NORTH);
		this.add(rightPanel,BorderLayout.EAST);
		
	}
	
	//panels' getters to be used in the other classes
	public JPanel getFilterPanel() {
		return filterPanel;
	}

	public JPanel getRightPanel() {
		return rightPanel;
	}

}
