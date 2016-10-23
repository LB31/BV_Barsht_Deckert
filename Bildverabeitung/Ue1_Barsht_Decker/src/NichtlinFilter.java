
// BV Ue1 WS2016/17 Vorgabe
//
// Copyright (C) 2015 by Klaus Jung

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class NichtlinFilter extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final String author = "<Your Name>"; // TODO: type in your
														// name here
	private static final String initialFilename = "lena_klein.png";
	private static final File openPath = new File(".");
	private static final int borderWidth = 5;
	private static final int maxWidth = 446;
	private static final int maxHeight = maxWidth;
	private static final int maxNoise = 30; // in per cent
	private int pixelsCopy[] = new int[maxWidth * maxWidth];
	private static JFrame frame;

	private ImageView srcView; // source image view
	private ImageView dstView; // filtered image view

	private int[] origPixels = null;

	private JLabel statusLine = new JLabel("     "); // to print some status
														// text

	private JComboBox<String> noiseType;
	private JLabel noiseLabel;
	private JSlider noiseSlider;
	private JLabel noiseAmountLabel;
	private boolean addNoise = false;
	private double noiseFraction = 0.01; // fraction for number of pixels to be
											// modified by noise

	private JComboBox<String> filterType;

	public NichtlinFilter() {
		super(new BorderLayout(borderWidth, borderWidth));

		setBorder(BorderFactory.createEmptyBorder(borderWidth, borderWidth, borderWidth, borderWidth));

		// load the default image
		File input = new File(initialFilename);

		if (!input.canRead())
			input = openFile(); // file not found, choose another image

		srcView = new ImageView(input);
		srcView.setMaxSize(new Dimension(maxWidth, maxHeight));

		// convert to grayscale

		makeGray(srcView);

		// keep a copy of the grayscaled original image pixels
		origPixels = srcView.getPixels().clone();

		// create empty destination image of same size
		dstView = new ImageView(srcView.getImgWidth(), srcView.getImgHeight());
		dstView.setMaxSize(new Dimension(maxWidth, maxHeight));

		// control panel
		JPanel controls = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, borderWidth, 0, 0);

		// load image button
		JButton load = new JButton("Open Image");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadFile(openFile());
				// convert to grayscale
				makeGray(srcView);
				// keep a copy of the grayscaled original image pixels
				origPixels = srcView.getPixels().clone();
				calculate(true);
			}
		});

		// selector for the noise method
		String[] noiseNames = { "No Noise ", "Salt & Pepper " };

		noiseType = new JComboBox<String>(noiseNames);
		noiseType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addNoise = noiseType.getSelectedIndex() > 0;
				noiseLabel.setEnabled(addNoise);
				noiseSlider.setEnabled(addNoise);
				noiseAmountLabel.setEnabled(addNoise);
				calculate(true);
			}
		});

		// amount of noise
		noiseLabel = new JLabel("Noise:");
		noiseAmountLabel = new JLabel("" + Math.round(noiseFraction * 100.0) + " %");
		noiseSlider = new JSlider(JSlider.HORIZONTAL, 0, maxNoise, (int) Math.round(noiseFraction * 100.0));
		noiseSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				noiseFraction = noiseSlider.getValue() / 100.0;
				noiseAmountLabel.setText("" + Math.round(noiseFraction * 100.0) + " %");
				calculate(true);
			}
		});
		noiseLabel.setEnabled(addNoise);
		noiseSlider.setEnabled(addNoise);
		noiseAmountLabel.setEnabled(addNoise);

		// selector for filter
		String[] filterNames = { "No Filter", "Min Filter", "Max Filter", "Box Filter", "Median Filter" };
		filterType = new JComboBox<String>(filterNames);
		filterType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				calculate(false);
			}
		});

		controls.add(load, c);
		controls.add(noiseType, c);
		controls.add(noiseLabel, c);
		controls.add(noiseSlider, c);
		controls.add(noiseAmountLabel, c);
		controls.add(filterType, c);

		// images panel
		JPanel images = new JPanel(new GridLayout(1, 2));
		images.add(srcView);
		images.add(dstView);

		// status panel
		JPanel status = new JPanel(new GridBagLayout());

		status.add(statusLine, c);

		add(controls, BorderLayout.NORTH);
		add(images, BorderLayout.CENTER);
		add(status, BorderLayout.SOUTH);

		calculate(true);

	}

	private File openFile() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Images (*.jpg, *.png, *.gif)", "jpg", "png",
				"gif");
		chooser.setFileFilter(filter);
		chooser.setCurrentDirectory(openPath);
		int ret = chooser.showOpenDialog(this);
		if (ret == JFileChooser.APPROVE_OPTION)
			return chooser.getSelectedFile();
		return null;
	}

	private void loadFile(File file) {
		if (file != null) {
			srcView.loadImage(file);
			srcView.setMaxSize(new Dimension(maxWidth, maxHeight));
			// create empty destination image of same size
			dstView.resetToSize(srcView.getImgWidth(), srcView.getImgHeight());
			frame.pack();
		}

	}

	private static void createAndShowGUI() {
		// create and setup the window
		frame = new JFrame("Nonlinear Filters - " + author);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JComponent newContentPane = new NichtlinFilter();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// display the window.
		frame.pack();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private void calculate(boolean createNoise) {
		long startTime = System.currentTimeMillis();

		if (createNoise) {
			// start with original image pixels
			srcView.setPixels(origPixels);
			// add noise
			if (addNoise)
				makeNoise(srcView);
			// make changes visible
			srcView.applyChanges();
		}

		// apply filter
		filter();

		// make changes visible
		dstView.applyChanges();

		long time = System.currentTimeMillis() - startTime;
		statusLine.setText("Processing Time = " + time + " ms");
	}

	private void makeGray(ImageView imgView) {
		int pixels[] = imgView.getPixels();
		System.arraycopy(pixels, 0, pixelsCopy, 0, pixels.length);
		// TODO: convert pixels to grayscale
		int r, g, b, average;
		int argb;

		// loop over all pixels
		for (int i = 0; i < pixels.length; i++) {
			argb = pixels[i];
			r = (argb >> 16) & 0xff;
			g = (argb >> 8) & 0xff;
			b = argb & 0xff;
			average = (r + g + b) / 3;
			pixels[i] = (0xFF << 24) | (average << 16) | (average << 8) | average;

		}
		imgView.applyChanges();
	}

	private void makeNoise(ImageView imgView) {
		int pixels[] = imgView.getPixels();
		System.arraycopy(pixelsCopy, 0, pixels, 0, pixels.length);

		// TODO: add noise to pixels
		int zufallszahl;
		int r, g, b, average;
		int argb;
		int width = imgView.getImgWidth();
		int height = imgView.getImgHeight();
		int newColor;

		// System.out.println(zufallszahl);
		// System.out.println(noiseFraction);

		for (int i = 0; i < pixels.length * noiseFraction; i++) {

			zufallszahl = (int) (Math.random() * width * height);

			if (i < (pixels.length * noiseFraction) / 2)
				newColor = 255;
			else
				newColor = 0;

			pixels[zufallszahl] = (0xFF << 24) | (newColor << 16) | (newColor << 8) | newColor;

		}
		imgView.applyChanges();

	}

	private void filter() {
		int src[] = srcView.getPixels();
		int dst[] = dstView.getPixels();
		int width = srcView.getImgWidth();
		int height = srcView.getImgHeight();
		int filterIndex = filterType.getSelectedIndex();

		int kernel[] = new int[9];

		int rAdd = 0;
		int gAdd = 0;
		int bAdd = 0;

		int r, g, b, argb;

		// TODO: implement filters

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				kernel = getKernelPixels(src, width, height, x, y);
				Arrays.sort(kernel);

				int pos = y * width + x;

				if (filterIndex == 1) {
					dst[pos] = kernel[0];
				} else if (filterIndex == 2) {
					dst[pos] = kernel[kernel.length - 1];
				} else if (filterIndex == 3) {
					for (int i = 0; i < kernel.length; i++) {

						argb = kernel[i];
						r = (argb >> 16) & 0xff;
						g = (argb >> 8) & 0xff;
						b = argb & 0xff;

						rAdd += r;
						gAdd += g;
						bAdd += b;

						// boxAddition += kernel[i];
					}
					dst[pos] = (0xFF << 24) | (rAdd / 9 << 16) | (gAdd / 9 << 8) | bAdd / 9;

					
					rAdd = 0;
					gAdd = 0;
					bAdd = 0;
				} else if (filterIndex == 4) {

					// if(minimum.length % 2 == 0){
					// dst[pos] = ((minimum.length / 2) + (minimum.length / 2 +
					// 1)) / 2;
					// }else{
					dst[pos] = kernel[Math.round(kernel.length / 2)];
					// }

				}

			}
		}

	}

	private int[] getKernelPixels(int[] imagePixels, int imageWidth, int imageHeight, int currentX, int currentY) {
		// array with 9 copies of pixels from original picture
		int[] kernelPixels = new int[9];
		int arrayPos = 0;

		for (int kernelY = -1; kernelY <= 1; kernelY++) {
			for (int kernelX = -1; kernelX <= 1; kernelX++) {

				int pixelX = currentX + kernelX;
				int pixelY = currentY + kernelY;

				if (pixelX < 0) {
					pixelX = 0;
				} else if (pixelX > imageWidth - 1) {
					pixelX = imageWidth - 1;
				}
				if (pixelY < 0) {
					pixelY = 0;
				} else if (pixelY > imageHeight - 1) {
					pixelY = imageHeight - 1;
				}
				kernelPixels[arrayPos] = imagePixels[pixelY * imageWidth + pixelX];
				arrayPos++;
			}
		}
		return kernelPixels;
	}
}
