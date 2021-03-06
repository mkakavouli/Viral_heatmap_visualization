import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.demo.FullDemo;

public class HeatmapGUI extends JFrame {
	private JPanel menuPanel, rightPanel, datePanel, minNumbersPanel, minSamplesPanel, minSamplesPerPosPanel,
			orderingPanel, resetPanel, textfieldPanel, welcomePanel;

	private JMenuBar fileMenuBar;
	private JMenu fileMenu, save, colors, search, searchP;
	public JMenuItem importFile, savePDF, savePng, nonSynColor, synColor, noMutColor, insColor, delColor, noCColor,
			help;
	public JTextField searchSample, searchPosition;
	public JButton dateButton1, dateButton2, resetButton, addButton, clearButton, addPButton, clearPButton;
	public JRadioButton nonClustered, hierarchicalClustered, dateClustered;
	private HeatmapController localController;
	private HeatmapModel localModel;
	private DatePicker datePicker1, datePicker2;
	private DatePickerSettings dateSettings1, dateSettings2;
	private JLabel date,datesTo, warning1, warning2, warning3, mutTypes;
	public JTextField mutNumber, samplesNumber, mutPerPos;
	private JCheckBox commonMutationSites;
	public List mutTypeList, searchedSamples, searchedPositions;
	private List regionsList, globalLineageList, UKLineageList;
	private JSlider pixelSize;
	private String[] mutations;
	private JScrollPane scrollPaneHelp;

	private Hashtable<String, String> mutationsHash;

	// create the JFrame
	public HeatmapGUI(HeatmapController controller, HeatmapModel modelObject) {

		localController = controller;
		localModel = modelObject;
		setLayout(new BorderLayout());
		setTitle("Viral consensus sequences visualization");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setup();
		this.pack();
		this.setVisible(true);
		this.setSize(1300, 800);
	}

	// setup method generates the main Panels and the buttons
	
	private void setup() {
		// create panels
		
		menuPanel = new JPanel(new BorderLayout());

		welcomePanel = new JPanel();

		rightPanel = new JPanel(new GridLayout(0, 1));

		orderingPanel = new JPanel(new GridLayout(0, 1));

		textfieldPanel = new JPanel(new GridLayout(0, 1));

		minNumbersPanel = new JPanel(new BorderLayout());

		minSamplesPanel = new JPanel(new BorderLayout());

		minSamplesPerPosPanel = new JPanel(new BorderLayout());

		datePanel = new JPanel();

		resetPanel = new JPanel();

		// --------------------------------menuPanel------------------------------------------------//


		// generate the menuBar,the menus and sub-menus
		
		fileMenuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		save = new JMenu("Save image as");
		colors = new JMenu("Colors");
		search = new JMenu("Search sample(s)");
		searchP = new JMenu("Search nucleotide position(s)");

		// generate the menu items
		
		importFile = new JMenuItem("Import .txt file");
		savePDF = new JMenuItem("PDF");
		savePng = new JMenuItem("PNG/JPEG");
		synColor = new JMenuItem("Synonumous mutations color");
		nonSynColor = new JMenuItem("Non-synonumous mutations color");
		noMutColor = new JMenuItem("No mutation color");
		insColor = new JMenuItem("Insertions color");
		delColor = new JMenuItem("Deletions color");
		noCColor = new JMenuItem("Non-coding mutations color");
		help = new JMenuItem("Help");
		
		//generate the necessary textfields,buttons and list to search sample and position names
		
		searchSample = new JTextField(70);
		searchSample.setText("");
		addButton = new JButton("Add for searching");
		clearButton = new JButton("Clear selected");
		searchedSamples = new List(10, true); 
		
		searchPosition = new JTextField(40);
		searchPosition.setText("");
		addPButton = new JButton("Add for searching");
		clearPButton = new JButton("Clear selected");
		searchedPositions = new List(10, true); 

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
		addButton.addActionListener(localController);
		clearButton.addActionListener(localController);
		addPButton.addActionListener(localController);
		clearPButton.addActionListener(localController);
		help.addActionListener(localController);

		// Add keyListeners to implement auto-complete on textfields where the names are typed 
		
		searchSample.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				
				/*
				 * the autocomplete works if a key is released which is not 
				 * 'back_space' or 'delete' or 'left arrow' or 'right arrow'
				 * (those keys are usually pressed when a user want to change the typed text)
				 */
				
				if (!(e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() ==KeyEvent.VK_LEFT|| e.getKeyCode() ==KeyEvent.VK_RIGHT)) {

					String tempText = searchSample.getText(); //the string typed at each moment someone release a key in searchSample textfield
					int length = tempText.length();
					
					//check the names of the samples displayed at the moment on heatmap plot

					for (int j = 11; j < localModel.customTable.get(0).size(); j++) {
						
						/* For each column, create a String by adding to it the characters from the String that corresponds to the sample name,
						 * the number of characters added should be the length of the text that has been typed
						 */

						String dataText = "";
						for (int i = 0; i < length; i++) {
							if (length <= localModel.customTable.get(0).get(j).length()) {
								dataText = dataText + localModel.customTable.get(0).get(j).charAt(i);
							}
						}
						
						//if the created String equals to the one typed in the textfield the the text is set with the name of the sample stored in that column of the 2D Arraylist 
						if (dataText.equals(tempText)) {
							searchSample.setText(localModel.customTable.get(0).get(j));
							searchSample.setSelectionStart(length); 
							break; //when the name is found the loop stops
						}

					}
				}
			}
		});

		searchPosition.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				
				/*
				 * the autocomplete works if a key is released which is not 
				 * 'back_space' or 'delete' or 'left arrow' or 'right arrow'
				 * (those keys are usually pressed when a user want to change the typed text)
				 */
				
				if (!(e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() ==KeyEvent.VK_LEFT|| e.getKeyCode() ==KeyEvent.VK_RIGHT)) {

					String tempText = searchPosition.getText(); //the string typed at each moment someone release a key
					int length = tempText.length(); 
					
					//check the names of the genomic positions displayed at the moment on heatmap plot
					
					for (int j = 1; j < localModel.customTable.size(); j++) {
						
						/* Create a String by adding to it the characters from the String that corresponds to the position name
						 * the number of characters added should be the length of the text that has been typed
						 */
						
						String dataText = "";
						for (int i = 0; i < length; i++) {
							if (length <= localModel.customTable.get(j).get(10).length()) {
								dataText = dataText + localModel.customTable.get(j).get(10).charAt(i); 
							}
						}
						
						//if the created String equals to the one typed in the textfield the the text is set with the name of the ORF stored in that row of the 2D Arraylist 
						if (dataText.equals(tempText)) {

							searchPosition.setText(localModel.customTable.get(j).get(10));
							searchPosition.setSelectionStart(length);
							break; //when the name is found the loop stops
						}

					}
				}
			}
		});
		
		// create a non editable textpane added in a scrollPane to display instructions and infos for the user
		
		JTextPane helpArea = new JTextPane();
		helpArea.setPreferredSize(new Dimension(700, 650));
		helpArea.setBackground(new Color(221, 239, 240));
		helpArea.setContentType("text/html");
		helpArea.setText("<html>" + "<p style=\"font-size:110%\"><b>Customization options:</b></>" + "<body>" + "<br>"
				+ "<p style=\"font-size:100%\"> <u>To change the color of mutations:</u> <br> Click on the menu button <b>'Colors'</b> and then click on the button with tha name of the mutation you wish to change. <br>"
				+ "Default colors: <b>No mutation:</b> light gray/<b>Non-synonymous:</b>Magenta/<b>Synonymous:</b>Green/<b>Deletion:</b>Black/<b>Insertion:</b>Yellow/<b>Non-coding:</b>Orange."
				+ "<br> <br> <u>To zoom in or out:</u> <br> Use the slide bar, located on the right of the plot. <br> <br>"
				+ "<u>To change the order of samples being displayed:</u> <br> Click on of the available data ordering options: <b>no ordering,hierchical ordering, date ordering(ascending)</b>. <br> <br>"
				+ "<u>To display labels with nucleotide position of the most common mutation:</u><br> Check the box <b>'Common mutation sites labels' </b>on the right of the plot."
				+ "<br><br>" + "<p style=\"font-size:110%\"><b>Search options:</b><br><br></p>"
				+ "<p style=\"font-size:100%\"><u>To search one or more samples:</u><br> Click on the menu button <b>'Search sample(s)'</b>, type the name of the sample of interest in the textfield and press the button <b>'add for searching'</b>.<br>"
				+ "To remove a searched sample click on the menu button <b>'Search sample(s)'</b> select the one you wish to remove from the list and press <b>'clear selected'</b>."
				+ "<br><br><u>To search one or more nucleotide positions:</u><br> Click on the menu button <b>'Search nucleotide position(s)'</b>, type the name of the position of interest in the textfield and press the button <b>'add for searching'</b>.<br>"
				+ "To remove a searched n.position click on the menu button <b>'Search nucleotide positions(s)'</b> select the one you wish to remove from the list and press <b>'clear selected'</b>.</p>"
				+ "<br><br></b><p style=\"font-size:110%\"><b>Filter options:</b><br><br></p>"
				+ "<p style=\"font-size:100%\"><u>To filter out the samples with  less mutation than the selected minimum: </u><br>Type a number to the <b>'minimum number of mutations/sample' </b>box at the right of the window and press <b>Enter</b> .<br> "
				+ "<br><u>To filter out the samples havinng mutations which are present in less than selected minimum number of samples: </u><br>Type a number to the <b>'minimum number of samples/mutation'</b> box at the right of the window and press <b>Enter </b>.<br>"
				+ "<br><u>To filter out the nucleotide positions with less than selected minimum number of mutations: </u><br>Type a number to the <b>'Minimum number of mutations/genomic position'</b> box at the right of the window and press <b>Enter</b> .<br> "
				+ "<br><u>To display samples having specific mutation types/region or country/global lineage/Uk lineage </u><br> Click on the desired filters from the corresponding lists.<br>To <b>deselect</b> one of the selected filters, click again on the selected filter you wish to remove."
				+ "<br><br><u>To display samples collected in specific date range</u><br> Click on the <b>calendars buttons</b> at the right bottom of the window and click on the available dates .</p>"
				+ "<br><br></b><p style=\"font-size:110%\"><b>Save options:</b><br><br></p>"
				+ "<p style=\"font-size:100%\">Click on the menu button <b>'File'</b> and  then on <b>'save image as'</b> select one of the option <b>'Pdf'</b> or <b>'jpeg/png'</b> .</p>"
				+ "</body>" +

				"</html>");
		helpArea.setEditable(false);

		scrollPaneHelp = new JScrollPane(helpArea);

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
		search.add(searchSample);
		search.add(addButton);
		search.add(searchedSamples);
		search.add(clearButton);
		searchP.add(searchPosition);
		searchP.add(addPButton);
		searchP.add(searchedPositions);
		searchP.add(clearPButton);
		
		//set some of the menus inactive until a file is imported
		
		colors.setEnabled(false);
		search.setEnabled(false);
		searchP.setEnabled(false);
		save.setEnabled(false);

		// add menus to the menubar
		fileMenuBar.add(fileMenu);
		fileMenuBar.add(colors);
		fileMenuBar.add(search);
		fileMenuBar.add(searchP);
		fileMenuBar.add(help);
		menuPanel.add(fileMenuBar, BorderLayout.WEST);

		// ----------------------------------------------------WelcomePanel----------------------------------------------------//
		
		// create a non editable text pane to welcome the user and inform him/her about the input file
		
		JTextPane welcomeText = new JTextPane();
		welcomeText.setPreferredSize(new Dimension(1295, 800));
		welcomeText.setBackground(new Color(221, 239, 240));
		welcomeText.setContentType("text/html");
		welcomeText.setText("<html>"
				+ "<div style='text-align: center;'> <img src=\"https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/SARS-CoV-2_without_background.png/239px-SARS-CoV-2_without_background.png\"> <h1 style=\"font-size:120%\">Welcome to viral consensus sequences visualization!</h1>"
				+ "<body>" + "<br>" +

				"<p style=\"font-size:110%\"> <u>To start:</u> <br><br> Click on the menu button <b>'File'</b> and then click on the button <b>'import .txt file'</b>. "
				+ "<br> <br> For more instructions and information about the program click on <b>'help'</b> button. <br> <br>"
				+ " <u>File format:</u> <br> <br> A tab delimited txt file produced by " +"<a href=\"https://github.com/rjorton/VAlign\">‘valign_mutations_dnds.py’</a>"+ " script, following the structure:<br>"
				+ "Position/ORF/ORFPosition/SequencePosition/ReferenceCodonPosition/CodonPosition/ReferenceCodon/ReferenceAA/AAposition/AlignPosition/SampleName_1/.../SampleName_n"
				+ "<br> <br><b> Samples' name format: </b> >VirusName/RegionName/SampleName/Year|GISAID_ID|GlobalLineage|UK Lineage <br> <br>"
				+ "<b> Mutation format: </b> MutationType: base[ref base/mut base]: pos[codon position]: codon[ref codon/mut codon]: dist[N]: aa[ref aa/mut aa]: ORFposition"
				+ "<br><b>Absence of mutation: .</b>" + "</p>" + "<br>" + "</body>" + "</html>");
		
		// add a HyperlinkListener to activate the url
		welcomeText.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (HyperlinkEvent.EventType.ACTIVATED == e.getEventType()) {
                    	try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException | URISyntaxException e1) {
							
							e1.printStackTrace();
						}
                }
            }
        });
		welcomeText.setEditable(false);
		welcomePanel.add(welcomeText);
		this.add(welcomePanel, BorderLayout.CENTER);

		// ----------------------------------------------------RightPanel------------------------------------------------------//


		// ----------------------------------sizePanel--------------------------------//

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

		// add the components to the size Panel
		rightPanel.add(pixels);
		rightPanel.add(pixelSize);

		// ------------------------------orderingPanel--------------------------------//

		// generate the buttonGroup and the 3 radio buttons
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

		// add the components to the ordering panel
		rightPanel.add(ordering);
		orderingPanel.add(nonClustered);
		orderingPanel.add(hierarchicalClustered);
		orderingPanel.add(dateClustered);
		rightPanel.add(orderingPanel);

		// -----------------------------mutationSites--------------------------------//

		// create a checkbox to display or not the labels of the common mutation sites
		commonMutationSites = new JCheckBox("Common mutation sites labels", false);
		commonMutationSites.addItemListener(localController);

		// add the componens to the ordering panel
		rightPanel.add(commonMutationSites, BorderLayout.WEST);

		// ---------------------------------minNumbersPanel---------------------------------//

		JLabel minMutation = new JLabel("Minimum number of mutations/sample:");

		warning1 = new JLabel("");
		mutNumber = new JTextField(4);

		/*
		 * add a keyListener to the JTextField to display a warning if the user doesn't
		 * type a numeric value
		 */
		mutNumber.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					// when a key which is not 'Enter' is pressed the text in the textfield is checked 
					// if it is an integer by trying converting it into Integer
					
					if (!(e.getKeyCode() == KeyEvent.VK_ENTER)) {
						if (!(mutNumber.getText().isEmpty())) {
							warning1.setText("");
						} else {
							int temp = Integer.parseInt(mutNumber.getText() + e.getKeyChar());
							warning1.setText("");
						}
					}
				} catch (NumberFormatException ex) {
					// display a warning message in red color
					warning1.setForeground(Color.red);
					warning1.setText("Invalid format.Please type a numeric value");
					return;
				}
			}
		});

		mutNumber.addActionListener(localController);

		minNumbersPanel.add(minMutation, BorderLayout.WEST);
		minNumbersPanel.add(mutNumber, BorderLayout.CENTER);
		minNumbersPanel.add(warning1, BorderLayout.EAST);
		textfieldPanel.add(minNumbersPanel);

		// ----------------------------minSamplesPanel---------------------------------//

		JLabel minSamples = new JLabel("Minimum number of samples/mutation:");
		samplesNumber = new JTextField(4);
		warning2 = new JLabel("");

		/*
		 * add a keyListener to the JTextField to display a warning if the user doesn't
		 * type a numeric value
		 */
		samplesNumber.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				try {
					if (!(e.getKeyCode() == KeyEvent.VK_ENTER)) {
						if (!(samplesNumber.getText().isEmpty())) {
							warning2.setText("");
						} else {
							int temp = Integer.parseInt(samplesNumber.getText() + e.getKeyChar());
							warning2.setText("");
						}
					}
				} catch (NumberFormatException ex) {
					warning2.setForeground(Color.red);
					warning2.setText("Invalid format.Please type a numeric value");
					return;
				}
			}
		});

		samplesNumber.addActionListener(localController);
		minSamplesPanel.add(minSamples, BorderLayout.WEST);
		minSamplesPanel.add(samplesNumber, BorderLayout.CENTER);
		minSamplesPanel.add(warning2, BorderLayout.EAST);
		textfieldPanel.add(minSamplesPanel);

		// ----------------------------------------minMutationPerPosPanel---------------------------------------//

		JLabel minMutationPerPos = new JLabel("Minimum number of mutations/genomic position:");

		warning3 = new JLabel("");
		mutPerPos = new JTextField(4);

		/*
		 * add a keyListener to the JTextField to display a warning if the user doesn't
		 * type a numeric value
		 */
		mutPerPos.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					if (!(e.getKeyCode() == KeyEvent.VK_ENTER && !(mutPerPos.getText().equals("")))) {
						if (!(mutPerPos.getText().isEmpty())) {
							warning3.setText("");
						} else {
							int temp = Integer.parseInt(mutPerPos.getText() + e.getKeyChar());
							warning3.setText("");
						}
					}
				} catch (NumberFormatException ex) {
					warning3.setForeground(Color.red);
					warning3.setText("Invalid format.Please type a numeric value");
					return;
				}
			}
		});

		mutPerPos.addActionListener(localController);

		minSamplesPerPosPanel.add(minMutationPerPos, BorderLayout.WEST);
		minSamplesPerPosPanel.add(mutPerPos, BorderLayout.CENTER);
		minSamplesPerPosPanel.add(warning3, BorderLayout.EAST);
		textfieldPanel.add(minSamplesPerPosPanel);
		rightPanel.add(textfieldPanel);

		// ------------------------------------------mutationTypPanel----------------------------------------------//

		mutTypes = new JLabel("Select mutation type(s):");

		mutations = new String[] { "Non synonymous", "Non coding", "Synonymous", "Insertions", "Deletions" }; //array with the available mutation types
		mutTypeList = new List(10, true);
		
		// the hashmap connect the text of the JList to the data
		mutationsHash = new Hashtable<String, String>();
		mutationsHash.put("Non synonymous", "Non");
		mutationsHash.put("Non coding", "NCo");
		mutationsHash.put("Synonymous", "Syn");
		mutationsHash.put("Insertions", "Ins");
		mutationsHash.put("Deletions", "Del");
		mutationsHash.put("No mutation", ".");

		// populate the list with the the names from mutations array
		for (String s : mutations) {
			mutTypeList.add(s);
		}

		mutTypeList.addItemListener(localController);

		rightPanel.add(mutTypes);
		rightPanel.add(mutTypeList);
		//--------------------------------------------------------------------------------------------//
		
		// create empty lists with multiselection options activated
		// they will be populated after the importation of a file
		
		JLabel regionLabel = new JLabel("Select region(s):");
		regionsList = new List(10, true);
		regionsList.addItemListener(localController);
		rightPanel.add(regionLabel);
		rightPanel.add(regionsList);
		JLabel globalLineageLabel = new JLabel("Select global Lineage(s):");
		globalLineageList = new List(10, true);
		globalLineageList.addItemListener(localController);
		rightPanel.add(globalLineageLabel);
		rightPanel.add(globalLineageList);

		JLabel UKLineageLabel = new JLabel("Select UK Lineage(s):");
		UKLineageList = new List(10, true);
		UKLineageList.addItemListener(localController);
		rightPanel.add(UKLineageLabel);
		rightPanel.add(UKLineageList);


		// -------------------------------------------------datePanel--------------------------------------------------//

		// download the image of a calendar
		URL dateImageURL = FullDemo.class.getResource("/images/datepickerbutton1.png");
		Image dateExampleImage = Toolkit.getDefaultToolkit().getImage(dateImageURL);
		ImageIcon dateExampleIcon = new ImageIcon(dateExampleImage);

		// create the datePickers and their settings
		dateSettings1 = new DatePickerSettings();
		dateSettings2 = new DatePickerSettings();

		datePicker1 = new DatePicker(dateSettings1);
		datePicker2 = new DatePicker(dateSettings2);

		// create the dateButtons and add the image of a calendar to the buttons
		dateButton1 = datePicker1.getComponentToggleCalendarButton();
		dateButton1.setText("");
		dateButton1.setIcon(dateExampleIcon);
		dateButton2 = datePicker2.getComponentToggleCalendarButton();
		dateButton2.setText("");
		dateButton2.setIcon(dateExampleIcon);

		// set the background colors of the calendar
		dateSettings1.setColorBackgroundWeekdayLabels(new Color(100, 149, 237), true);
		dateSettings1.setColorBackgroundWeekNumberLabels(new Color(100, 149, 237), true);

		dateSettings2.setColorBackgroundWeekdayLabels(new Color(100, 149, 237), true);
		dateSettings2.setColorBackgroundWeekNumberLabels(new Color(100, 149, 237), true);
		
		
		date = new JLabel("Select date range:");
		datesTo = new JLabel("to");
		
		//add the datePicker to the datePanel
		datePanel.add(datePicker1);
		datePanel.add(datesTo);
		datePanel.add(datePicker2);
		
		//add the label and the datePanel to the rightPanel
		rightPanel.add(date);
		rightPanel.add(datePanel);
		

		// -------------------------------------resetPanel--------------------------------------------------------------//
		
		//create the reset button and add an ActionListener
		resetButton = new JButton("reset filters/customization");
		resetButton.addActionListener(localController);
		
		// border to put the resetButton in the right position of the resetPanel
		Border button = BorderFactory.createEmptyBorder(0, 10, 0, 10); 													
		resetPanel.setBorder(button);
		
		/*
		 * create an innerPanel in which reset button is added
		 * to decrease its size
		 */
		
		JPanel innerPanel = new JPanel(); 
		innerPanel.setLayout(new GridLayout(1, 0));
		innerPanel.add(resetButton);
		
		//add the innerPanel to the resetPanel
		resetPanel.add(innerPanel);
		
		rightPanel.add(resetPanel);
		
		//---------------------------------------------------------------------------------------------------------------//
		rightPanel.setVisible(false); // set rightPanel not visible

		// add the panels to JFrame
		this.add(menuPanel, BorderLayout.NORTH);
		this.add(rightPanel, BorderLayout.EAST);
	}
	
	//getters
	public JMenuItem getHelp() {
		return help;
	}

	public List getSearchedSamples() {
		return searchedSamples;
	}

	public List getSearchedPositions() {
		return searchedPositions;
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

	public JMenu getSearch() {
		return search;
	}

	public void setSearch(JMenu search) {
		this.search = search;
	}

	public JMenu getColors() {
		return colors;
	}

	public JMenu getSearchP() {
		return searchP;
	}

	public List getRegionsList() {
		return regionsList;
	}

	public List getGlobalLineageList() {
		return globalLineageList;
	}

	public List getUKLineageList() {
		return UKLineageList;
	}

	public JLabel getWarning3() {
		return warning3;
	}

	public JTextField getMutPerPos() {
		return mutPerPos;
	}

	public JMenu getSave() {
		return save;
	}

	public JScrollPane getScrollPaneHelp() {
		return scrollPaneHelp;
	}

	public JPanel getWelcomePanel() {
		return welcomePanel;
	}

	public String[] getMutations() {
		return mutations;
	}

}
