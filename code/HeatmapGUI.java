import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DatePickerSettings.DateArea;
import com.github.lgooddatepicker.demo.FullDemo;

public class HeatmapGUI extends JFrame {
	private JPanel menuPanel, rightPanel, sizePanel, filterPanel, datePanel, customizePanel, orderingPanel,minNumbersPanel;
	private JMenuBar fileMenuBar;
	private JMenu fileMenu, save, colors;
	public JMenuItem importFile, savePDF, savePng, nonSynColor, synColor;
	public JButton dateButton1, dateButton2;
	public JRadioButton nonClustered, hierarchicalClustered, dateClustered;
	private HeatmapController localController;
	private DatePicker datePicker1, datePicker2;
	private DatePickerSettings dateSettings1, dateSettings2;
	private JLabel datesTo,warning;
	public JTextField mutNumber;
	public ComboBoxes mutationBox;
	

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
		menuPanel = new JPanel();
		menuPanel.setLayout(new BorderLayout());
		customizePanel = new JPanel();
		customizePanel.setLayout(new GridLayout(0, 1, 0, 10));
		orderingPanel = new JPanel();
		orderingPanel.setLayout(new GridLayout(0, 1, 0, 7));
		sizePanel = new JPanel();
		sizePanel.setLayout(new GridLayout(0, 1));
		rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(0, 1));

		filterPanel = new JPanel();
		filterPanel.setLayout(new GridLayout(0, 1));
		minNumbersPanel=new JPanel(new GridLayout(0, 1));
		datePanel = new JPanel();
		// datePanel.setLayout(new GridLayout(2,2));

		// menuPanel

		// generate the menuBar,the menus and sub-menus
		fileMenuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		save = new JMenu("Save image as");
		colors = new JMenu("Personalise");

		// generate the menu items
		importFile = new JMenuItem("Import .txt file");
		savePDF = new JMenuItem("PDF");
		savePng = new JMenuItem("png/jpeg");
		synColor = new JMenuItem("Synonumous mutation");
		nonSynColor = new JMenuItem("Non-synonumous mutation");

		// add actionListener in all the menu-items
		importFile.addActionListener(localController);
		
		savePDF.addActionListener(localController);
		savePng.addActionListener(localController);
		synColor.addActionListener(localController);
		nonSynColor.addActionListener(localController);

		// add the menu-items to the menus
		save.add(savePDF);
		save.add(savePng);
		fileMenu.add(importFile);
		fileMenu.add(save);
		colors.add(nonSynColor);
		colors.add(synColor);

		// add menus to the menubar
		fileMenuBar.add(fileMenu);
		fileMenuBar.add(colors);
		menuPanel.add(fileMenuBar, BorderLayout.WEST);

		// right panel

		final int pixel_MIN = 1; // minimum square size
		final int pixel_MAX = 30; // maximum square size
		final int pixel_INIT = 8; // initial square size

		// generate JSlider to customize square size
		JSlider pixelSize = new JSlider(JSlider.HORIZONTAL, pixel_MIN, pixel_MAX, pixel_INIT);
		pixelSize.addChangeListener(localController);

		// Turn on labels at major tick marks.
		pixelSize.setMajorTickSpacing(9);
		pixelSize.setMinorTickSpacing(1);
		pixelSize.setPaintTicks(true);
		pixelSize.setPaintLabels(true);

		// Mutation types JComboBox
		String[] mutations = { "Non synonymous", "Non coding", "Synonymous", "Insertions", "Deletions" };

		mutationsHash = new Hashtable<String, String>();
		mutationsHash.put("Non synonymous", "Non");
		mutationsHash.put("Non coding", "NCo");
		mutationsHash.put("Synonymous", "Syn");
		mutationsHash.put("Insertions", "Ins");
		mutationsHash.put("Deletions", "Del");
		mutationsHash.put("No mutation", ".");
		Vector vMutations = new Vector();
		vMutations.add("Types of Mutation");
		vMutations.add(new JCheckBox("all types", true));
		for (String s : mutations) {
			vMutations.add(new JCheckBox(s, false));

		}
		mutationBox = new ComboBoxes(vMutations); // create a JComboBox with checkboxes
		mutationBox.addActionListener(localController);

		URL dateImageURL = FullDemo.class.getResource("/images/datepickerbutton1.png");
		Image dateExampleImage = Toolkit.getDefaultToolkit().getImage(dateImageURL);
		ImageIcon dateExampleIcon = new ImageIcon(dateExampleImage);
		dateSettings1 = new DatePickerSettings();
		dateSettings2 = new DatePickerSettings();

		datePicker1 = new DatePicker(dateSettings1);
		datePicker2 = new DatePicker(dateSettings2);

		dateButton1 = datePicker1.getComponentToggleCalendarButton();
		dateButton1.setText("");
		dateButton1.setIcon(dateExampleIcon);
		dateButton2 = datePicker2.getComponentToggleCalendarButton();
		dateButton2.setText("");
		dateButton2.setIcon(dateExampleIcon);

		dateSettings1.setColorBackgroundWeekdayLabels(new Color(100, 149, 237), true);
		dateSettings1.setColorBackgroundWeekNumberLabels(new Color(100, 149, 237), true);

		dateSettings2.setColorBackgroundWeekdayLabels(new Color(100, 149, 237), true);
		dateSettings2.setColorBackgroundWeekNumberLabels(new Color(100, 149, 237), true);

		ButtonGroup group = new ButtonGroup();
		nonClustered = new JRadioButton("No ordering", true);
		nonClustered.addActionListener(localController);
		hierarchicalClustered = new JRadioButton("Hierarchical ordering");
		hierarchicalClustered.addActionListener(localController);
		dateClustered = new JRadioButton("Date ordering");
		dateClustered.addActionListener(localController);
		group.add(nonClustered);
		group.add(hierarchicalClustered);
		group.add(dateClustered);

		// create labels for the filters
		JLabel customize = new JLabel("Customize Heatmap");
		JLabel pixels = new JLabel("Square size");

		// add the JComponents to the corresponding JPanels
		sizePanel.add(pixels);
		sizePanel.add(pixelSize);

		JLabel filters = new JLabel("Filters");
		datesTo = new JLabel("to");
		JLabel ordering = new JLabel("Data ordering options");
		
		
		////////////
		JLabel minMutation= new JLabel("Minimum number of mutations/sample");
		warning=new JLabel("");		
		mutNumber=new JTextField("");
		mutNumber.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					int temp=Integer.parseInt(mutNumber.getText());
					warning.setText("");
				}catch(NumberFormatException ex) {
					warning.setForeground (Color.red);
					warning.setText("Invalid format.Please type a numeric value");
				}
			}
		});

		mutNumber.addActionListener(localController);
		////////////
		
		orderingPanel.add(ordering);
		orderingPanel.add(nonClustered);
		orderingPanel.add(hierarchicalClustered);
		orderingPanel.add(dateClustered);

		customizePanel.add(customize);
		customizePanel.add(sizePanel);
		customizePanel.add(orderingPanel);
		
		minNumbersPanel.add(minMutation);
		minNumbersPanel.add(mutNumber);
		minNumbersPanel.add(warning);
		

		rightPanel.add(customizePanel);
		filterPanel.add(filters);
		filterPanel.add(minNumbersPanel);
		filterPanel.add(mutationBox);

		rightPanel.add(filterPanel);
		rightPanel.add(datePanel);
		rightPanel.setVisible(false); // set rightPanel not visible

		// add the panels to JFrame
		this.add(menuPanel, BorderLayout.NORTH);
		this.add(rightPanel, BorderLayout.EAST);

	}


	public Hashtable<String, String> getMutationsHash() {
		return mutationsHash;
	}

	public JPanel getDatePanel() {
		return datePanel;
	}
	public ComboBoxes getMutationBox() {
		return mutationBox;
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

	// panels' getters to be used in the other classes
	public JPanel getFilterPanel() {
		return filterPanel;
	}

	public JPanel getRightPanel() {
		return rightPanel;
	}

}

