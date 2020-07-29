import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

public class HeatmapModel {
	private FileReader fr;
	private int sampleNumber, positionNumber;
	private LocalDate minPastDate, maxDate;
	ArrayList<String> genomePosition = new ArrayList<String>();
	ArrayList<String> tableLine = new ArrayList<String>();
	ArrayList<String> samples = new ArrayList<String>();
	ArrayList<String> countries = new ArrayList<String>();
	ArrayList<String> dates = new ArrayList<String>();
	ArrayList<String> globalLineage = new ArrayList<String>();
	ArrayList<String> UKLineage = new ArrayList<String>();
	ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> customTable = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> filteredTable;

	// method that creates the file chooser frame and returns the path of the
	// selected file
	public String selectFile(HeatmapGUI gui) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("C:\\Users\\ges_k\\OneDrive\\Desktop\\summer project\\VirusHeatmap-master\\"));
		int result = fileChooser.showOpenDialog(gui);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selection = fileChooser.getSelectedFile();
			return selection.getAbsolutePath();
		}
		return "";
	}

	// method that read the file and store data in ArrayLists
	public void readFile(String path) {
		try {

			fr = new FileReader(path);
			Scanner s = new Scanner(fr);
			while (s.hasNextLine()) {
				String line = s.nextLine();
				tableLine = new ArrayList<>(Arrays.asList(line.split("\t")));

				sampleNumber = tableLine.size() - 10; // store the number of tableLine

				genomePosition.add(tableLine.get(10)); // store the first column of the file with genome positions

				table.add(tableLine);
				customTable.add(tableLine);
			}
			genomePosition.remove(0);

			// store samples'name in ArrayList
			for (int i = 10; i < table.get(0).size(); i++) {
				samples.add(table.get(0).get(i));
			}
			samples.remove(0);

			// create an ArrayList that stores all the names of the regions by which the
			// samples come from
			for (int i = 0; i < samples.size(); i++) {
				String[] sampleRegion = samples.get(i).split("/");
				String[] sampleDate = samples.get(i).split("\\|");
				if (countries.size() == 0) {
					countries.add(sampleRegion[1]);

				} else if (!(countries.contains(sampleRegion[1]))) {
					countries.add(sampleRegion[1]);
				}

				if (dates.size() == 0) {
					dates.add(sampleDate[2]);

				} else if (!(dates.contains(sampleDate[2]))) {
					dates.add(sampleDate[2]);
				}
				if (globalLineage.size() == 0) {
					globalLineage.add(sampleDate[3]);

				} else if (!(globalLineage.contains(sampleDate[3]))) {
					globalLineage.add(sampleDate[3]);
				}
				if (UKLineage.size() == 0) {
					UKLineage.add(sampleDate[4]);

				} else if (!(UKLineage.contains(sampleDate[4]))) {
					UKLineage.add(sampleDate[4]);
				}
			}

			String min = Collections.min(dates);
			DateTimeFormatter minFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
			minPastDate = LocalDate.parse(min, minFormatter);

			String max = Collections.max(dates);
			DateTimeFormatter maxFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
			maxDate = LocalDate.parse(max, maxFormatter);

			positionNumber = table.size() - 1; // store the number of genome positions

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} finally {
			try {
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// method that draws the heatmap with the default colors
	public JPanel drawData(ArrayList<ArrayList<String>> table, int pixel, Color NoM,Color Syn, Color Non, Color Ins, Color Del, Color NoC) {
		JPanel heatmapPanel = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				// start X,Y coordinates for drawing data
				int pointX = 0;
				int pointY = 0;

				/*
				 * draw a rectangle for each position of the table and color it with the default
				 * colors
				 */
				Font boldf =new Font("default", Font.BOLD, 22);
				Font defaultf=new Font("default",Font.PLAIN,12);
				g2.setFont(boldf);
				g2.drawString("Viral Samples", table.get(0).size()*pixel/2,17);
				g2.setFont(defaultf);
				for (int y = 0; y < table.size() - 1; y++) {
					for (int x = 10; x < table.get(0).size() - 1; x++) {
						assignColors(g2, x + 1, y + 1, table, NoM,Syn,Non,Ins,Del,NoC);
						g2.fillRect(x + pointX - 10, y + pointY+20, pixel, pixel);
						pointX = pointX + pixel;
					}

					// generate genome position labels
					g2.setPaint(Color.BLACK);
					g2.drawString(genomePosition.get(y), table.get(0).size() - 1 + pointX-10, y + pointY + pixel+20);
					pointY = pointY + pixel;
					pointX = 0;

				}
				g2.setFont(boldf);
				
				g2.translate((HEIGHT - WIDTH) / 2, (HEIGHT - WIDTH) / 2);
				g2.rotate(Math.PI / 2, HEIGHT / 2, WIDTH / 2);
				g2.drawString("Nucleotide position- ORF name",table.size()*(pixel+1)/2,-((table.get(0).size() - 1)*(pixel+1)+150));
				g2.setFont(defaultf);
				// generate the samples' name

//				g2.translate((HEIGHT - WIDTH) / 2, (HEIGHT - WIDTH) / 2);
//				g2.rotate(Math.PI / 2, HEIGHT / 2, WIDTH / 2);

				for (int i = 10; i < table.get(0).size() - 1; i++) {
					g2.drawString(table.get(0).get(i + 1), table.size() - 1 + pointY + pixel+20, -(i + pointX - 10));
					pointX = pointX + pixel;
				}

			}
		};

		heatmapPanel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				int pointX = 0;
				int pointY = 0;

				for (int y = 0; y < table.size() - 1; y++) {
					for (int x = 10; x < table.get(0).size() - 1; x++) {
						Rectangle rect = new Rectangle();
						rect.setBounds(x + pointX - 10, y + pointY+20, pixel, pixel);
						if (rect.contains(e.getPoint())) {
							if (!(table.get(y + 1).get(x + 1).equals("."))) {
								heatmapPanel.setToolTipText("<html>" + "Sample:" + "<br>" + "<b>"
										+ table.get(0).get(x + 1) + "</b>" + "<br>" + "Genome Position:" + "<br>"
										+ "<b>" + table.get(y + 1).get(10) + "</b>" + "<br>" + "Base substitution:"
										+ "<br>" + "<b>" + table.get(y + 1).get(x + 1).substring(27, 34) + "</b>"
										+ "<br>" + "Amino acid substitution:" + "<br>" + "<b>"
										+ table.get(y + 1).get(x + 1).substring(47, 50) + "</b>" + "</html>");
							} else if (table.get(y + 1).get(x + 1).equals(".")) {
								heatmapPanel.setToolTipText("<html>" + "Sample:" + "<br>" + "<b>"
										+ table.get(0).get(x + 1) + "</b>" + "<br>" + "Genome Position:" + "<br>"
										+ "<b>" + table.get(y + 1).get(10) + "</b>" + "<br>" + "<b>" + "No mutation"
										+ "</html>");
							}
						}
						pointX += pixel;
					}
					pointX = 0;
					pointY += pixel;
					ToolTipManager.sharedInstance().mouseMoved(e);
				}

			}

			@Override
			public void mouseDragged(MouseEvent e) {
			}

		});

		return heatmapPanel;
	}

	
	// gets the position in the map, searches for the number that correspond to the
	// position and assign the appropriate color to the graphic
	public void assignColors(Graphics2D g, int x, int y, ArrayList<ArrayList<String>> table, Color NoM,Color Syn, Color Non, Color Ins, Color Del, Color NoC) {

		String mutation = table.get(y).get(x);

		if (mutation.equals(".")) { // no mutation
			g.setColor(NoM);
		} else {
			String[] mutDescription = mutation.split(":");
			if (mutDescription[0].equals("Syn")) { // synonymous
				g.setColor(Syn);
			} else if (mutDescription[0].equals("Non")) { // non-synonymous
				g.setColor(Non);
			} else if (mutDescription[0].equals("Ins")) { // insertions
				g.setColor(Ins);
			} else if (mutDescription[0].equals("Del")) { // deletions
				g.setColor(Del);
			}else if (mutDescription[0].equals("NoC")) { // deletions
				g.setColor(NoC);
			}
		}

	}

	// method that take the given panel and generates a BufferedImage that is saved
	// as png or jpeg file
	public void saveImage(JPanel panel) {
		BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		panel.paint(g2d);
		String extension = null;
		try {
			JFileChooser jfc = new JFileChooser();
			jfc.setAcceptAllFileFilterUsed(false); // disable "all files" extension from the dropdown menu
			/*
			 * add the two possible file extensions
			 */
			jfc.addChoosableFileFilter(new FileNameExtensionFilter("JPEG file", "jpg", ".jpeg"));
			jfc.addChoosableFileFilter(new FileNameExtensionFilter("PNG file", "png"));
			int option = jfc.showSaveDialog(null);
			if (option == JFileChooser.APPROVE_OPTION) {
				File f = jfc.getSelectedFile();
				if (jfc.getFileFilter() instanceof FileNameExtensionFilter) {
					String[] exts = ((FileNameExtensionFilter) jfc.getFileFilter()).getExtensions();
					f = new File(f.toString() + '.' + exts[0]);
					extension = exts[0]; // store the chosen extension
				}
				ImageIO.write(image, extension, f); // generate the image file
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// method that take the given panel and generates a BufferedImage that is saved
	// as PDF file
	public void saveImagePDF(JPanel panel) {
		BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		panel.paint(image.getGraphics());
		Document document = new Document();
		PdfWriter writer = null;
		try {
			JFileChooser pdfFileChooser = new JFileChooser(); // create a filechooser so the user can chooser the
																// directory where the file will be saved
			pdfFileChooser.setAcceptAllFileFilterUsed(false); // disable "all files" extension from the dropdown menu
			pdfFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PDF file", "pdf")); // add the possible
																									// file extension
			int chooserResult = pdfFileChooser.showSaveDialog(null);
			if (chooserResult == JFileChooser.APPROVE_OPTION) {
				String filePath = pdfFileChooser.getSelectedFile().getPath() + ".pdf";
				writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
			}
			document.open();

			Image iTextImage = Image.getInstance(writer, image, 1);
			iTextImage.setAbsolutePosition(25, 250);

			iTextImage.scaleToFit(500, 590); // scale the image to fit in the pdf file

			document.add(iTextImage);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (document.isOpen()) {
				document.close();
			}
		}
	}
	public void saveFromConsole(Component panel){
		JPanel heatmap = new JPanel();
	    heatmap.setPreferredSize(new Dimension(getTable().get(0).size() * (8+ 1)+170,
				getTable().size() * (8 + 1)+500));
	    heatmap.add(panel);
	    BufferedImage bImage = ScreenImage.createImage(heatmap);
	    try {
	    	ScreenImage.writeImage(bImage, "image.png");
	    }catch (IOException ex) {
	    	ex.printStackTrace();
	    }
    }
	public ArrayList<ArrayList<String>> columnsToKeep(ArrayList<Integer> indexToAddMutT,boolean isSelected1,ArrayList<Integer> indexToAddGL,boolean isSelected2,ArrayList<Integer> indexToAddUKL,boolean isSelected3,
			ArrayList<Integer> indexToAddRegions,boolean isSelected4,ArrayList<Integer> indexToAddMutN,boolean isSelected5, ArrayList<Integer> indexToAddDateF, boolean isSelected6,
			ArrayList<Integer> indexToAddDateT,boolean isSelected7,ArrayList<Integer> indexToAddSampleN,boolean isSelected8 ) {
		ArrayList<Integer> originalInd=new ArrayList<Integer>();
		filteredTable=new ArrayList<ArrayList<String>>();
		for(int i =11;i<getTable().get(0).size();i++) {
			originalInd.add(i);
		}
		if(isSelected1) {
			originalInd.retainAll(indexToAddMutT);
		}
		if(isSelected2) {
			originalInd.retainAll(indexToAddGL);
		}
		if(isSelected3) {
			originalInd.retainAll(indexToAddUKL);
		}
		if(isSelected4) {
			originalInd.retainAll(indexToAddRegions);
		}
		if(isSelected5) {
			originalInd.retainAll(indexToAddMutN);
		}
		if(isSelected6) {
			originalInd.retainAll(indexToAddDateF);
		}
		if(isSelected7) {
			originalInd.retainAll(indexToAddDateT);
		}
		if(isSelected8) {
			originalInd.retainAll(indexToAddSampleN);
		}
		for (int j = 0; j < getTable().size(); j++) {
			ArrayList<String> tempRow = new ArrayList<String>();
			for (int k = 0; k < 11; k++) {
				tempRow.add(getTable().get(j).get(k));

			}
			for (int i : originalInd) {
				tempRow.add(getTable().get(j).get(i));
			}
			filteredTable.add(tempRow);

		}
		return filteredTable;
	}

	// getters
	public ArrayList<String> getCountries() {
		return countries;
	}

	public int getPositionNumber() {
		return positionNumber;
	}

	public int getSampleNumber() {
		return sampleNumber;
	}

	public ArrayList<ArrayList<String>> getTable() {
		return table;
	}

	public LocalDate getMinPastDate() {
		return minPastDate;
	}

	public LocalDate getMaxDate() {
		return maxDate;
	}

	public ArrayList<ArrayList<String>> getCustomTable() {
		return customTable;
	}

	public void setCustomTable(ArrayList<ArrayList<String>> customTable) {
		this.customTable = customTable;
	}

}

