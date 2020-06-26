import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
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
	ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> customTable = new ArrayList<ArrayList<String>>();

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

				sampleNumber = tableLine.size() - 1; // store the number of tableLine

				genomePosition.add(tableLine.get(0)); // store the first column of the file with genome positions

				table.add(tableLine);
				customTable.add(tableLine);
			}
			genomePosition.remove(0);

			// store samples'name in ArrayList
			for (int i = 0; i < table.get(0).size(); i++) {
				samples.add(table.get(0).get(i));
			}
			samples.remove(0);

			// create an ArrayList that stores all the names of the regions by which the
			// samples come from
			for (int i = 0; i < samples.size(); i++) {
				String[] sampleRegion = samples.get(i).split("/");
				String[] sampleDate =samples.get(i).split("\\|");
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
			}
			String min =Collections.min(dates);
			DateTimeFormatter minFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
			minPastDate = LocalDate.parse(min, minFormatter);
			
			String max =Collections.max(dates);
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
	public JPanel drawData(ArrayList<ArrayList<String>> table, int pixel) {
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
				for (int y = 0; y < table.size() - 1; y++) {
					for (int x = 0; x < table.get(0).size() - 1; x++) {
						assignColors(g2, x + 1, y + 1, table);
						g2.fillRect(x + pointX, y + pointY, pixel, pixel);
						pointX = pointX + pixel;
					}

					// generate genome position labels

					g2.drawString(genomePosition.get(y), table.get(0).size() - 1 + pointX, y + pointY + pixel);
					pointY = pointY + pixel;
					pointX = 0;

				}

				// generate the samples' name

				 g2.translate((HEIGHT - WIDTH) / 2, (HEIGHT - WIDTH) / 2);
				 g2.rotate(Math.PI / 2, HEIGHT / 2, WIDTH / 2);
				 
				for (int i = 0; i < table.get(0).size() - 1; i++) {
					g2.drawString(table.get(0).get(i + 1), table.size() - 1 + pointY + pixel,-(i + pointX ));
					pointX = pointX + pixel;
				}

			}
		};

		return heatmapPanel;
	}

	// method that draws the heatmap with colors chosen by the user
	public JPanel customDrawData(Color color1, Color color2, String numberSyn, String numberNonSyn,
			ArrayList<ArrayList<String>> table, int pixel) {
		JPanel heatmapPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				// start X,Y coordinates for drawing data
				int pointX = 0;
				int pointY = 0;
				/*
				 * draw a rectangle for each position of the table and color it with the chosen
				 * colors
				 */
				for (int y = 0; y < table.size() - 1; y++) {
					for (int x = 0; x < table.get(0).size() - 1; x++) {
						assignColors(g2, x + 1, y + 1, table);
						if (table.get(y + 1).get(x + 1).equals(numberSyn)) {
							g2.setColor(color1);
						}
						if (table.get(y + 1).get(x + 1).equals(numberNonSyn)) {
							g2.setColor(color2);
						}

						g2.fillRect(x + pointX, y + pointY, pixel, pixel);
						pointX = pointX + pixel;
					}

					// generate genome position labels

					g2.drawString(genomePosition.get(y), getSampleNumber() + pointX, y + pointY + pixel);
					pointY = pointY + pixel;
					pointX = 0;

				}
				
				// generate the samples' name
				
				//rotate the sample names 90 degrees
				
				 g2.translate((HEIGHT - WIDTH) / 2, (HEIGHT - WIDTH) / 2);
				 g2.rotate(Math.PI / 2, HEIGHT / 2, WIDTH / 2);
				 
				for (int i = 0; i < table.get(0).size() - 1; i++) {

					g2.drawString(table.get(0).get(i + 1),table.size() - 1  + pointY + pixel,-( i + pointX));
					pointX = pointX + pixel;
				}

			}
		};

		return heatmapPanel;
	}

	// gets the position in the map, searches for the number that correspond to the
	// position and assign the appropriate color to the graphic
	public void assignColors(Graphics2D g, int x, int y, ArrayList<ArrayList<String>> table) {

		String number = table.get(y).get(x);

		if (number.equals("0")) { // no mutation
			g.setColor(Color.GRAY);
		} else if (number.equals("1")) { // synonymous
			g.setColor(Color.GREEN);
		} else if (number.equals("-1")) { // non-synonymous
			g.setColor(Color.MAGENTA);
		} else if (number.equals("2")) { // insertions
			g.setColor(Color.YELLOW);
		} else if (number.equals("3")) { // deletions
			g.setColor(Color.RED);
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
