import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.Border;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DatePickerSettings.DateArea;
import com.github.lgooddatepicker.demo.FullDemo;

public class HeatmapGUI extends JFrame {
	private JPanel menuPanel, rightPanel,datePanel,minNumbersPanel,minSamplesPanel,orderingPanel,resetPanel;
	//, sizePanel, ,mutationSites,,,mutationTypPanel,;
	private JMenuBar fileMenuBar;
	private JMenu fileMenu, save, colors;
	public JMenuItem importFile, savePDF, savePng, nonSynColor, synColor,noMutColor,insColor,delColor,noCColor;
	public JButton dateButton1, dateButton2,resetButton;
	public JRadioButton nonClustered, hierarchicalClustered, dateClustered;
	private HeatmapController localController;
	private DatePicker datePicker1, datePicker2;
	private DatePickerSettings dateSettings1, dateSettings2;
	private JLabel datesTo,warning1,warning2,mutTypes;
	public JTextField mutNumber,samplesNumber;
	private JCheckBox commonMutationSites;
	public List mutTypeList; 
	private JSlider pixelSize;
	private String[] mutations;
	

	
	private Hashtable<String, String> mutationsHash;

	// create the JFrame
	public HeatmapGUI(HeatmapController controller) {
		localController = controller;
		setLayout(new BorderLayout());
		setTitle("COVID-19 Nucleotide Mutations");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setup();
		this.pack();
		this.setVisible(true);
		this.setSize(1500, 800);
	}

	// setup method generates the main Panels and the buttons
	/**
	 * 
	 */
	private void setup() {
		// create panels
		menuPanel = new JPanel(new BorderLayout());
		
		rightPanel = new JPanel(new GridLayout(0, 1));

		orderingPanel = new JPanel(new GridLayout(0, 1));

		minNumbersPanel=new JPanel(new BorderLayout());
	
		minSamplesPanel=new JPanel(new BorderLayout());
	
		datePanel = new JPanel();
		
		resetPanel = new JPanel();

		//-------------------------------- menuPanel------------------------------------------------//

		// generate the menuBar,the menus and sub-menus
		fileMenuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		save = new JMenu("Save image as");
		colors = new JMenu("Personalise");

		// generate the menu items
		importFile = new JMenuItem("Import .txt file");
		savePDF = new JMenuItem("PDF");
		savePng = new JMenuItem("png/jpeg");
		synColor = new JMenuItem("Synonumous mutations color");
		nonSynColor = new JMenuItem("Non-synonumous mutations color");
		noMutColor = new JMenuItem("No mutation color");
		insColor= new JMenuItem("Insertions color");
		delColor= new JMenuItem("Deletions color");
		noCColor= new JMenuItem("Non-coding mutations color");;

		// add actionListener in all the menu-items
		importFile.addActionListener(localController);
		savePDF.addActionListener(localController);
		savePng.addActionListener(localController);
		synColor.addActionListener(localController);
		nonSynColor.addActionListener(localController);
		noMutColor.addActionListener(localController);
		insColor.addActionListener(localController);
		delColor.addActionListener(localController);
		noCColor.addActionListener(localController);

		// add the menu-items to the menus
		save.add(savePDF);
		save.add(savePng);
		fileMenu.add(importFile);
		fileMenu.add(save);
		colors.add(nonSynColor);
		colors.add(synColor);
		colors.add(noMutColor);
		colors.add(insColor);
		colors.add(delColor);
		colors.add(noCColor);
		
		// add menus to the menubar
		fileMenuBar.add(fileMenu);
		fileMenuBar.add(colors);
		menuPanel.add(fileMenuBar, BorderLayout.WEST);

		//----------------------------------------------------Right Panel------------------------------------------------------//
		
		
		//----------------------------------sizePanel--------------------------------//
		
		JLabel pixels = new JLabel("Square size:");
		final int pixel_MIN = 1; // minimum square size
		final int pixel_MAX = 30; // maximum square size
		final int pixel_INIT = 6; // initial square size

		// generate JSlider to customize square size
		pixelSize = new JSlider(JSlider.HORIZONTAL, pixel_MIN, pixel_MAX, pixel_INIT);
		pixelSize.addChangeListener(localController);

		// Turn on labels at major tick marks.
		pixelSize.setMajorTickSpacing(9);
		pixelSize.setMinorTickSpacing(1);
		pixelSize.setPaintTicks(true);
		pixelSize.setPaintLabels(true);

		
		//add the components to the size Panel
		rightPanel.add(pixels);
		rightPanel.add(pixelSize);
		
		
		//------------------------------ordering Panel--------------------------------// 
		
		//generate the buttonGroup and the 3 radio buttons
		JLabel ordering = new JLabel("Data ordering options:");
		ButtonGroup group = new ButtonGroup();
		nonClustered = new JRadioButton("No ordering", true);
		nonClustered.addActionListener(localController);
		hierarchicalClustered = new JRadioButton("Hierarchical ordering");
		hierarchicalClustered.addActionListener(localController);
		dateClustered = new JRadioButton("Date ordering");
		dateClustered.addActionListener(localController);
		
		// add the radioButtons to the buttonGroup
		group.add(nonClustered);
		group.add(hierarchicalClustered);
		group.add(dateClustered);
		
		//add the components to the ordering panel
		rightPanel.add(ordering);
		orderingPanel.add(nonClustered);
		orderingPanel.add(hierarchicalClustered);
		orderingPanel.add(dateClustered);
		rightPanel.add(orderingPanel);
		
		//-----------------------------mutationSites--------------------------------//
		
		//create a checkbox to display or not the labels of the common mutation sites
		commonMutationSites = new JCheckBox("Common mutation sites labels", false);
		commonMutationSites.addActionListener(localController);
		
		//add the componens to the ordering panel
		rightPanel.add(commonMutationSites,BorderLayout.WEST);
		
		
		//---------------------------------minNumbersPanel---------------------------------//
		
		JLabel minMutation= new JLabel("Minimum number of mutations/sample:");
		
		warning1=new JLabel("");		
		mutNumber=new JTextField(5);
		
		/*add a keyListener to the JTextField to 
		 * display a warning if the user doesn't type a numeric value
		 */
		mutNumber.addKeyListener(new KeyAdapter(){
			@Override
			public void keyTyped(KeyEvent e) {
				try {
					if( !(e.getKeyCode() == KeyEvent.VK_ENTER)){
						int temp=Integer.parseInt(mutNumber.getText()+e.getKeyChar());;
						warning1.setText("");
					}
				}catch(NumberFormatException ex) {
					warning1.setForeground (Color.red);
					warning1.setText("Invalid format.Please type a numeric value");
					return;
				}
			}
		});

		mutNumber.addActionListener(localController);
		
		rightPanel.add(minMutation);
		minNumbersPanel.add(mutNumber,BorderLayout.WEST);
		minNumbersPanel.add(warning1,BorderLayout.EAST);
		rightPanel.add(minNumbersPanel);
		
		//----------------------------minSamplesPanel---------------------------------//
		
		JLabel minSamples= new JLabel("Minimum number of samples/mutation:");	
		samplesNumber=new JTextField(5);
		warning2=new JLabel("");
		
		/*add a keyListener to the JTextField
		 *  to display a warning if the user doesn't type a numeric value
		 */
		samplesNumber.addKeyListener(new KeyAdapter(){
			@Override
			public void keyTyped(KeyEvent e) {
				try {
					if( !(e.getKeyCode() == KeyEvent.VK_ENTER)){
						int temp=Integer.parseInt(samplesNumber.getText()+e.getKeyChar());;
						warning2.setText("");
					}
				}catch(NumberFormatException ex) {
					warning2.setForeground (Color.red);
					warning2.setText("Invalid format.Please type a numeric value");
					return;
				}
			}
		});

		samplesNumber.addActionListener(localController);
		rightPanel.add(minSamples);
		minSamplesPanel.add(samplesNumber,BorderLayout.WEST);
		minSamplesPanel.add(warning2,BorderLayout.EAST);
		rightPanel.add(minSamplesPanel);
		
		//------------------------------------------mutationTypPanel----------------------------------------------//
		
		mutTypes=new JLabel("Select mutation type(s):");
		
		mutations = new String[]{ "Non synonymous", "Non coding", "Synonymous", "Insertions", "Deletions" };
		mutTypeList=new List(10,true);
		//the hashmap connect the text of the JList to the data
		mutationsHash = new Hashtable<String, String>();
		mutationsHash.put("Non synonymous", "Non");
		mutationsHash.put("Non coding", "NCo");
		mutationsHash.put("Synonymous", "Syn");
		mutationsHash.put("Insertions", "Ins");
		mutationsHash.put("Deletions", "Del");
		mutationsHash.put("No mutation", ".");
		
		
		//populate the list with the the names from mutations array
		for (String s : mutations) {
			mutTypeList.add(s);
		}
		
		mutTypeList.addItemListener(localController);
		
		rightPanel.add(mutTypes);
		rightPanel.add(mutTypeList);
		
		//-------------------------------------------------datePanel---------------------------------//
		
		//download the image of a calendar 
		URL dateImageURL = FullDemo.class.getResource("/images/datepickerbutton1.png");
		Image dateExampleImage = Toolkit.getDefaultToolkit().getImage(dateImageURL);
		ImageIcon dateExampleIcon = new ImageIcon(dateExampleImage);
		
		//create the datePickers and their settings
		dateSettings1 = new DatePickerSettings();
		dateSettings2 = new DatePickerSettings();

		datePicker1 = new DatePicker(dateSettings1);
		datePicker2 = new DatePicker(dateSettings2);
		
		//create the dateButtons and add the image of a calendar to the buttons
		dateButton1 = datePicker1.getComponentToggleCalendarButton();
		dateButton1.setText("");
		dateButton1.setIcon(dateExampleIcon);
		dateButton2 = datePicker2.getComponentToggleCalendarButton();
		dateButton2.setText("");
		dateButton2.setIcon(dateExampleIcon);
		
		//set the background colors of the calendar
		dateSettings1.setColorBackgroundWeekdayLabels(new Color(100, 149, 237), true);
		dateSettings1.setColorBackgroundWeekNumberLabels(new Color(100, 149, 237), true);

		dateSettings2.setColorBackgroundWeekdayLabels(new Color(100, 149, 237), true);
		dateSettings2.setColorBackgroundWeekNumberLabels(new Color(100, 149, 237), true);

		datesTo = new JLabel("to");
	
		//-------------------------------------resetPanel--------------------------------------------------------------//
		
		resetButton=new JButton("reset filters/customization");
		resetButton.addActionListener(localController);
		Border button  =  BorderFactory.createEmptyBorder(0,10,0,10); // the border is needed to put the panel of buttons in the right position of the BottomRight panel
		resetPanel.setBorder(button);
		
		JPanel innerPanel =  new JPanel(); //create an innerPanel
		innerPanel.setLayout(new GridLayout(1,0));
		innerPanel.add(resetButton);
		resetPanel.add(innerPanel);
		
		rightPanel.setVisible(false); // set rightPanel not visible

		// add the panels to JFrame
		this.add(menuPanel, BorderLayout.NORTH);
		this.add(rightPanel, BorderLayout.EAST);
	}


	public JLabel getWarning1() {
		return warning1;
	}

	public JLabel getWarning2() {
		return warning2;
	}

	public JCheckBox getCommonMutationSites() {
		return commonMutationSites;
	}

	public JPanel getResetPanel() {
		return resetPanel;
	}

	public List getMutTypeList() {
		return mutTypeList;
	}

	public Hashtable<String, String> getMutationsHash() {
		return mutationsHash;
	}

	public JPanel getDatePanel() {
		return datePanel;
	}

	// getters
	public JLabel getDatesTo() {
		return datesTo;
	}

	public DatePicker getDatePicker1() {
		return datePicker1;
	}

	public DatePicker getDatePicker2() {
		return datePicker2;
	}

	public DatePickerSettings getDateSettings1() {
		return dateSettings1;
	}

	public DatePickerSettings getDateSettings2() {
		return dateSettings2;
	}

	public JPanel getRightPanel() {
		return rightPanel;
	}

	public JTextField getMutNumber() {
		return mutNumber;
	}

	public JTextField getSamplesNumber() {
		return samplesNumber;
	}
	public JSlider getPixelSize() {
		return pixelSize;
	}

	public String[] getMutations() {
		return mutations;
	}

}

