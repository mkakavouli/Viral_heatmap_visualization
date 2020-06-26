import java.awt.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.Properties;
import javax.swing.*;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DatePickerSettings.DateArea;
import com.github.lgooddatepicker.demo.FullDemo;



public class HeatmapGUI extends JFrame {
	private JPanel menuPanel,rightPanel,customSizePanel, filterPanel,datePanel;
	private JMenuBar fileMenuBar;
	private JMenu fileMenu, save,colors;
	public JMenuItem importFile,savePDF,savePng,nonSynColor,synColor;
	public JButton dateButton1,dateButton2;
	private HeatmapController localController;
	private DatePicker datePicker1, datePicker2;
	private DatePickerSettings dateSettings1, dateSettings2;
	private JLabel datesTo;
	

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
	/**
	 * 
	 */
	private void setup() {
		// create panels
		menuPanel = new JPanel();
		menuPanel.setLayout(new BorderLayout());
		rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(0,1));
		customSizePanel=new JPanel();
		filterPanel=new JPanel();
		datePanel=new JPanel();
		//datePanel.setLayout(new GridLayout(2,2));
		
	
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

        dateSettings1.setColorBackgroundWeekdayLabels(new Color(100,149,237), true);
        dateSettings1.setColorBackgroundWeekNumberLabels(new Color(100,149,237), true);
        

        dateSettings2.setColorBackgroundWeekdayLabels(new Color(100,149,237), true);
        dateSettings2.setColorBackgroundWeekNumberLabels(new Color(100,149,237), true);
		
		
		//create labels for the filters
		JLabel pixels=new JLabel("Square size");
		JLabel filters=new JLabel("Filters");
		datesTo=new JLabel("to");
		//add the JComponents to the corresponding JPanels
		customSizePanel.add(pixels);
		customSizePanel.add(pixelSize);
		
	
		
		rightPanel.add(customSizePanel);
		filterPanel.add(filters);
		rightPanel.add(filterPanel);
		rightPanel.add(datePanel);
		rightPanel.setVisible(false); //set rightPanel not visible 
		
		//add the panels to JFrame
		this.add(menuPanel,BorderLayout.NORTH);
		this.add(rightPanel,BorderLayout.EAST);
		
	}
	

	public JPanel getDatePanel() {
		return datePanel;
	}

	//getters
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

	//panels' getters to be used in the other classes
	public JPanel getFilterPanel() {
		return filterPanel;
	}

	public JPanel getRightPanel() {
		return rightPanel;
	}

}
