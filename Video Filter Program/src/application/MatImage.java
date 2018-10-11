
package application;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;

public class MatImage {
	private RGBPixel[][] image;
	private BufferedImage total;
	private Color pencilColor = Color.BLACK;
	private int tolerance = 5;
	private Color backColor = Color.WHITE;
	private boolean flippedMirror = false;
	private int radius;

	public enum Filter {
		pencilDrawing, mirrorHorizontal, mirrorVertical, negate, keepRed, keepGreen, keepBlue, zeroRed, zeroGreen, zeroBlue, grayscale, sepia, switchBRG, switchGBR, switchBGR, pixelate, blur;
	}

	private ArrayList<Filter> filters;

	public MatImage(Mat image) {
		double[] pixel = new double[3];
		this.image = new RGBPixel[image.width()][image.height()];
		for (int i = 0; i < image.height(); i++) {
			for (int j = 1; j <= image.width(); j++) {
				pixel = image.get(i, image.width() - j);
				this.image[j - 1][i] = new RGBPixel((int) pixel[2], (int) pixel[1], (int) pixel[0]);
			}
		}
		radius = this.image.length / 2;
		filters = new ArrayList<>();
	}

	public void flipMirror() {
		flippedMirror = !flippedMirror;
	}

	public void setPencil(int tolerance, Color pencilColor, Color backColor) {
		this.tolerance = tolerance;
		this.pencilColor = pencilColor;
		this.backColor = backColor;
	}

	public void addFilters(Filter e) {
		filters.add(e);
	}

	public void reset() {
		filters.clear();
		radius = this.image.length / 2;
	}

	public void removeFilter(Filter e) {
		filters.remove(e);
	}

	public void updateMat(Mat image) {
		double[] pixel = new double[3];
		this.image = new RGBPixel[image.width()][image.height()];
		for (int i = 0; i < image.height(); i++) {
			for (int j = 1; j <= image.width(); j++) {
				pixel = image.get(i, image.width() - j);
				this.image[j - 1][i] = new RGBPixel((int) pixel[2], (int) pixel[1], (int) pixel[0]);
			}
		}
	}

	/*
	 * Applies Selected Filters and returns a BufferedImage of the filtered
	 * Image
	 */
	public BufferedImage getBufferedImage() {
		try {
			for (Filter f : filters) {
				if (f.equals(Filter.pencilDrawing))
					pencilDrawing(tolerance, pencilColor, backColor);
				if (f.equals(Filter.mirrorHorizontal))
					mirrorHorizontal(flippedMirror);
				if (f.equals(Filter.grayscale))
					grayscale();
				if (f.equals(Filter.sepia))
					sepia();
				if (f.equals(Filter.mirrorVertical))
					mirrorVertical(flippedMirror);
				if (f.equals(Filter.negate))
					negate();
				if (f.equals(Filter.keepBlue))
					keepBlue();
				if (f.equals(Filter.keepRed))
					keepRed();
				if (f.equals(Filter.keepGreen))
					keepGreen();
				if (f.equals(Filter.zeroRed))
					zeroRed();
				if (f.equals(Filter.zeroBlue))
					zeroBlue();
				if (f.equals(Filter.zeroGreen))
					zeroGreen();
				if (f.equals(Filter.switchBGR))
					switchBlueGreenRed();
				if (f.equals(Filter.switchBRG))
					switchBlueRedGreen();
				if (f.equals(Filter.switchGBR))
					switchGreenBlueRed();
				if (f.equals(Filter.pixelate))
					pixelate(300);
				if (f.equals(Filter.blur))
					weird();
			}
		} catch (ConcurrentModificationException e) {
		}
		total = new BufferedImage(image.length, image[0].length, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < total.getHeight(); i++) {
			for (int j = 0; j < total.getWidth(); j++) {
				total.setRGB(j, i, image[j][i].getRGB());
			}
		}
		return total;
	}
	
	public RGBPixel[][] getImage() {
		try {
			for (Filter f : filters) {
				if (f.equals(Filter.pencilDrawing))
					pencilDrawing(tolerance, pencilColor, backColor);
				if (f.equals(Filter.mirrorHorizontal))
					mirrorHorizontal(flippedMirror);
				if (f.equals(Filter.grayscale))
					grayscale();
				if (f.equals(Filter.sepia))
					sepia();
				if (f.equals(Filter.mirrorVertical))
					mirrorVertical(flippedMirror);
				if (f.equals(Filter.negate))
					negate();
				if (f.equals(Filter.keepBlue))
					keepBlue();
				if (f.equals(Filter.keepRed))
					keepRed();
				if (f.equals(Filter.keepGreen))
					keepGreen();
				if (f.equals(Filter.zeroRed))
					zeroRed();
				if (f.equals(Filter.zeroBlue))
					zeroBlue();
				if (f.equals(Filter.zeroGreen))
					zeroGreen();
				if (f.equals(Filter.switchBGR))
					switchBlueGreenRed();
				if (f.equals(Filter.switchBRG))
					switchBlueRedGreen();
				if (f.equals(Filter.switchGBR))
					switchGreenBlueRed();
				if (f.equals(Filter.pixelate))
					pixelate(300);
				if (f.equals(Filter.blur))
					weird();
			}
		} catch (ConcurrentModificationException e) {
		}
		return image;
	}

	public void negate() {
		for (int row = 0; row < image.length; row++) {
			for (int col = 0; col < image[0].length; col++) {
				image[row][col].setRed(255 - image[row][col].getRed());
				image[row][col].setGreen(255 - image[row][col].getGreen());
				image[row][col].setBlue(255 - image[row][col].getBlue());
			}
		}
	}

	public void mirrorVertical(boolean negative) {
		if (!negative) {
			for (int row = 0; row < image.length; row++) {
				for (int col = 0; col < image[0].length / 2; col++) {
					image[row][image[0].length - col - 1].setRed(image[row][col].getRed());
					image[row][image[0].length - col - 1].setGreen(image[row][col].getGreen());
					image[row][image[0].length - col - 1].setBlue(image[row][col].getBlue());
				}
			}
		} else {
			for (int row = 0; row < image.length; row++) {
				for (int col = image[0].length - 1; col > image[0].length / 2; col--) {
					image[row][image[0].length - col].setRed(image[row][col].getRed());
					image[row][image[0].length - col].setGreen(image[row][col].getGreen());
					image[row][image[0].length - col].setBlue(image[row][col].getBlue());
				}
			}
		}
	}

	public void mirrorHorizontal(boolean negative) {
		if (!negative) {
			for (int row = 0; row < image.length / 2; row++) {
				for (int col = 0; col < image[0].length; col++) {
					image[image.length - 1 - row][col].setRed(image[row][col].getRed());
					image[image.length - 1 - row][col].setGreen(image[row][col].getGreen());
					image[image.length - 1 - row][col].setBlue(image[row][col].getBlue());
				}
			}
		} else {
			for (int row = image.length - 1; row > image.length / 2; row--) {
				for (int col = 0; col < image[0].length; col++) {
					image[image.length - row][col].setRed(image[row][col].getRed());
					image[image.length - row][col].setGreen(image[row][col].getGreen());
					image[image.length - row][col].setBlue(image[row][col].getBlue());
				}
			}
		}
	}

	public void pencilDrawing(int tolerance) {
		for (int row = 0; row < image.length - 1; row++) {
			for (int col = 0; col < image[0].length - 1; col++) {
				if (image[row][col].getRed() - image[row + 1][col].getRed() >= tolerance)
					image[row][col].setPixel(0, 0, 0);
				else if (image[row][col].getGreen() - image[row + 1][col].getGreen() >= tolerance)
					image[row][col].setPixel(0, 0, 0);
				else if (image[row][col].getBlue() - image[row + 1][col].getBlue() >= tolerance)
					image[row][col].setPixel(0, 0, 0);
				else if (image[row][col].getRed() - image[row][col + 1].getRed() >= tolerance)
					image[row][col].setPixel(0, 0, 0);
				else if (image[row][col].getGreen() - image[row][col + 1].getGreen() >= tolerance)
					image[row][col].setPixel(0, 0, 0);
				else if (image[row][col].getBlue() - image[row][col + 1].getBlue() >= tolerance)
					image[row][col].setPixel(0, 0, 0);
				else
					image[row][col].setPixel(255, 255, 255);
			}
		}
	}

	public void pencilDrawing(int tolerance, Color pencil, Color background) {
		for (int row = 0; row < image.length - 1; row++) {
			for (int col = 0; col < image[0].length - 1; col++) {
				if (image[row][col].getRed() - image[row + 1][col].getRed() >= tolerance)
					image[row][col].setPixel(pencil);
				else if (image[row][col].getGreen() - image[row + 1][col].getGreen() >= tolerance)
					image[row][col].setPixel(pencil);
				else if (image[row][col].getBlue() - image[row + 1][col].getBlue() >= tolerance)
					image[row][col].setPixel(pencil);
				else if (image[row][col].getRed() - image[row][col + 1].getRed() >= tolerance)
					image[row][col].setPixel(pencil);
				else if (image[row][col].getGreen() - image[row][col + 1].getGreen() >= tolerance)
					image[row][col].setPixel(pencil);
				else if (image[row][col].getBlue() - image[row][col + 1].getBlue() >= tolerance)
					image[row][col].setPixel(pencil);
				else
					image[row][col].setPixel(background);
			}
		}
	}

	public void increseEdgeSharpness(int tolerance) {
		for (int row = 0; row < image.length - 1; row++) {
			for (int col = 0; col < image[0].length - 1; col++) {
				if (image[row][col].getRed() - image[row + 1][col].getRed() >= tolerance)
					image[row][col].setPixel(0, 0, 0);
				else if (image[row][col].getGreen() - image[row + 1][col].getGreen() >= tolerance)
					image[row][col].setPixel(0, 0, 0);
				else if (image[row][col].getBlue() - image[row + 1][col].getBlue() >= tolerance)
					image[row][col].setPixel(0, 0, 0);
				else if (image[row][col].getRed() - image[row][col + 1].getRed() >= tolerance)
					image[row][col].setPixel(0, 0, 0);
				else if (image[row][col].getGreen() - image[row][col + 1].getGreen() >= tolerance)
					image[row][col].setPixel(0, 0, 0);
				else if (image[row][col].getBlue() - image[row][col + 1].getBlue() >= tolerance)
					image[row][col].setPixel(0, 0, 0);
				// else image[row][col].setPixel(255, 255, 255);
			}
		}
	}

	public void keepRed() {
		for (int row = 0; row < image.length - 1; row++) {
			for (int col = 0; col < image[0].length; col++) {
				image[row][col].setPixel(image[row][col].getRed(), 0, 0);
			}
		}
	}

	public void keepBlue() {
		for (int row = 0; row < image.length - 1; row++) {
			for (int col = 0; col < image[0].length; col++) {
				image[row][col].setPixel(0, 0, image[row][col].getBlue());
			}
		}
	}

	public void keepGreen() {
		for (int row = 0; row < image.length - 1; row++) {
			for (int col = 0; col < image[0].length; col++) {
				image[row][col].setPixel(0, image[row][col].getGreen(), 0);
			}
		}
	}

	public void grayscale() {
		int avg = 0;
		for (int row = 0; row < image.length - 1; row++) {
			for (int col = 0; col < image[0].length; col++) {
				avg = (image[row][col].getGreen() + image[row][col].getRed() + image[row][col].getBlue()) / 3;
				image[row][col].setPixel(avg, avg, avg);
			}
		}
	}

	public void zeroGreen() {
		for (int row = 0; row < image.length - 1; row++) {
			for (int col = 0; col < image[0].length; col++) {
				image[row][col].setPixel(image[row][col].getRed(), 0, image[row][col].getBlue());
			}
		}
	}

	public void zeroRed() {
		for (int row = 0; row < image.length - 1; row++) {
			for (int col = 0; col < image[0].length; col++) {
				image[row][col].setPixel(0, image[row][col].getGreen(), image[row][col].getBlue());
			}
		}
	}

	public void zeroBlue() {
		for (int row = 0; row < image.length - 1; row++) {
			for (int col = 0; col < image[0].length; col++) {
				image[row][col].setPixel(image[row][col].getRed(), image[row][col].getGreen(), 0);
			}
		}
	}

	public void switchBlueRedGreen() {
		for (int row = 0; row < image.length - 1; row++) {
			for (int col = 0; col < image[0].length; col++) {
				image[row][col].setPixel(image[row][col].getBlue(), image[row][col].getRed(),
						image[row][col].getGreen());
			}
		}
	}

	public void switchGreenBlueRed() {
		for (int row = 0; row < image.length - 1; row++) {
			for (int col = 0; col < image[0].length; col++) {
				image[row][col].setPixel(image[row][col].getGreen(), image[row][col].getBlue(),
						image[row][col].getRed());
			}
		}
	}

	public void switchBlueGreenRed() {
		for (int row = 0; row < image.length - 1; row++) {
			for (int col = 0; col < image[0].length; col++) {
				image[row][col].setPixel(image[row][col].getBlue(), image[row][col].getGreen(),
						image[row][col].getRed());
			}
		}
	}

	public void sepia() {
		for (int row = 0; row < image.length - 1; row++) {
			for (int col = 0; col < image[0].length; col++) {
				double red = (image[row][col].getPixel()[0] * .393) + (image[row][col].getPixel()[1] * .769)
						+ (image[row][col].getPixel()[2] * .189);
				double green = (image[row][col].getPixel()[0] * .349) + (image[row][col].getPixel()[1] * .686)
						+ (image[row][col].getPixel()[2] * .168);
				double blue = (image[row][col].getPixel()[0] * .272) + (image[row][col].getPixel()[1] * .534)
						+ (image[row][col].getPixel()[2] * .132);
				if (red > 255)
					red = 255;
				if (green > 255)
					green = 255;
				if (blue > 255)
					blue = 255;
				image[row][col].setPixel((int) red, (int) green, (int) blue);
			}
		}
	}

	public void pixelate(int pixelSize) {
		for (int row = 0; row < image.length; row += pixelSize) {
			for (int col = 0; col < image[0].length; col += pixelSize) {
				int div = 0;
				int r = 0;
				int g = 0;
				int b = 0;
				for (int i = row; i < row + pixelSize && i < image.length; i++) {
					for (int j = col; j < col + pixelSize && j < image[0].length; j++) {
						div++;
						r += image[i][j].getRed();
						g += image[i][j].getGreen();
						b += image[i][j].getBlue();
					}
				}
				r /= div;
				g /= div;
				b /= div;
				for (int i = row; i < row + pixelSize && i < image.length; i++) {
					for (int j = col; j < col + pixelSize && j < image[0].length; j++) {
						image[i][j].setPixel(r, g, b);
					}
				}
			}
		}
	}

	public void blur(int intensity) {
		double div = 1 + intensity / 10.0;
		for (int row = 1; row < image.length; row++) {
			for (int col = 1; col < image[0].length; col++) {
				int redDiff = image[row][col].getRed() - image[row][col - 1].getRed();
				int greenDiff = image[row][col].getGreen() - image[row][col - 1].getGreen();
				int blueDiff = image[row][col].getBlue() - image[row][col - 1].getBlue();
				image[row][col].setPixel((int) (image[row][col].getRed() - redDiff / div),
						(int) (image[row][col].getGreen() - greenDiff / div),
						(int) (image[row][col].getBlue() - blueDiff / div));

				redDiff = image[row][col].getRed() - image[row - 1][col].getRed();
				greenDiff = image[row][col].getGreen() - image[row - 1][col].getGreen();
				blueDiff = image[row][col].getBlue() - image[row - 1][col].getBlue();
				image[row][col].setPixel((int) (image[row][col].getRed() - redDiff / div),
						(int) (image[row][col].getGreen() - greenDiff / div),
						(int) (image[row][col].getBlue() - blueDiff / div));
			}
		}
	}

	public void oldTV() {
		int count = 0;
		boolean black = false;
		for (int row = 0; row < image.length; row++) {
			for (int col = 0; col < image[0].length; col++) {
				if (Math.random() < .00005) {
					black = true;
					count = 0;
				}
				if (black) {
					image[row][col].setPixel(0, 0, 0);
					count++;
				}
				if (black && count > 200)
					black = false;

			}
		}
	}

	public void brighten(int strength) {
		for (int row = 0; row < image.length; row++) {
			for (int col = 0; col < image[0].length; col++) {
				Color c = image[row][col].getColor();
				int red = c.getRed() + strength > 255 ? 255 : c.getRed() + strength;
				int blue = c.getBlue() + strength > 255 ? 255 : c.getBlue() + strength;
				int green = c.getGreen() + strength > 255 ? 255 : c.getGreen() + strength;
				// int red = c.getRed() + strength;
				// int green = c.getGreen() + strength;
				// int blue = c.getBlue() + strength;
				image[row][col].setPixel(red, green, blue);
			}
		}
	}
	
	public void darken(int strength) {
		for (int row = 0; row < image.length; row++) {
			for (int col = 0; col < image[0].length; col++) {
				Color c = image[row][col].getColor();
				int red = c.getRed() - strength < 0? 0 : c.getRed() - strength;
				int blue = c.getBlue() - strength < 0 ? 0 : c.getBlue() - strength;
				int green = c.getGreen() - strength < 0 ? 0 : c.getGreen() - strength;
				// int red = c.getRed() + strength;
				// int green = c.getGreen() + strength;
				// int blue = c.getBlue() + strength;
				image[row][col].setPixel(red, green, blue);
			}
		}
	}

	public void hyperbola(int step){
		int midY = image.length / 2;
		int midX = image[0].length / 2;
		for (int row = 0; row < image.length; row++) {
			for (int col = 0; col < image[0].length; col++) {
				if(Math.sqrt(Math.pow(midX - col, 2) - Math.pow(midY - row, 2)) > radius) image[row][col].setPixel(0, 0, 0);
			}
		}
		if(radius > -5){
		radius -= step;
	//	if(radius < -5) radius = image.length / 2;
		}
	}
	
	public void circle(int radius){
		int midY = image.length / 2;
		int midX = image[0].length / 2;
		for (int row = 0; row < image.length; row++) {
			for (int col = 0; col < image[0].length; col++) {
				if(Math.sqrt(Math.pow(midX - col, 2) + Math.pow(midY - row, 2)) > radius) image[row][col].setPixel(0, 0, 0);
			}
		}
	}

	public void zoomCircle(int step){
		int midY = image.length / 2;
		int midX = image[0].length / 2;
		for (int row = 0; row < image.length; row++) {
			for (int col = 0; col < image[0].length; col++) {
				if(Math.sqrt(Math.pow(midX - col, 2) + Math.pow(midY - row, 2)) > radius) image[row][col].setPixel(0, 0, 0);
			}
		}
		radius -= step;
		if(radius <= 0) radius = image.length / 2;
	}
	
	public void bubbleFilter(){
		BufferedImage bf = null;
		try {
			bf = ImageIO.read(new File("C:\\Users\\Theo Kachelski\\Pictures\\bubbles.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int row = 0; row < image.length; row++) {
			for (int col = 0; col < image[0].length; col++) {
				Color c = new Color(bf.getRGB(row, col));
				int red = (c.getRed() + image[row][col].getRed() * 2) / 3;
				int green = (c.getGreen() + image[row][col].getGreen() * 2) / 3;
				int blue = (c.getBlue() + image[row][col].getBlue() * 2) / 3;
				image[row][col].setPixel(red, green, blue);
			}
		}
	}
	
	public void weird(){
		for (int row = 0; row < image.length; row++) {
			for (int col = 0; col < image[0].length; col++) {
				int color = (int) Math.sqrt(Math.pow(image[row][col].getRed(),2) + Math.pow(image[row][col].getGreen(),2));
				image[row][col].setPixel(color, color, color);
			}
		}
	}
}