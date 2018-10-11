package application;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import application.MatImage.Filter;
import javafx.application.Platform;
/*import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.image.BufferedImage;
//import javax.swing.text.html.ImageView;
*/
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainControllerClass implements Initializable {
	@FXML
	private ImageView cameraView;
	@FXML
	private ColorPicker pencilColor;
	@FXML
	private ColorPicker backColor;
	@FXML
	private Label pencilColorLabel;
	@FXML
	private Label backColorLabel;
	@FXML
	private Label pencilLabel;
	@FXML
	private Label mirrorOptions;
	@FXML
	private Button flipMirror;
	@FXML
	private Label framerateLabel;
	@FXML
	private Label cameraLabel;
	@FXML
	private Label toleranceLabel;
	@FXML
	private TextField toleranceInput;
	@FXML
	private CheckMenuItem mirrorHItem;
	@FXML
	private CheckMenuItem pencilItem;
	@FXML
	private CheckMenuItem mirrorVItem;
	@FXML
	private CheckMenuItem sepiaItem;
	@FXML
	private CheckMenuItem grayscaleItem;
	@FXML
	private CheckMenuItem zeroRedItem;
	@FXML
	private CheckMenuItem zeroBlueItem;
	@FXML
	private CheckMenuItem zeroGreenItem;
	@FXML
	private CheckMenuItem keepRedItem;
	@FXML
	private CheckMenuItem keepGreenItem;
	@FXML
	private CheckMenuItem keepBlueItem;
	@FXML
	private CheckMenuItem negateItem;
	@FXML
	private CheckMenuItem switchBRGItem;
	@FXML
	private CheckMenuItem switchGBRItem;
	@FXML
	private CheckMenuItem switchBGRItem;
	@FXML
	private CheckMenuItem pixelate;
	@FXML
	private CheckMenuItem blur;
	private static boolean terminate = false;
	private volatile long startTime = System.currentTimeMillis();
	private volatile MatImage image;
	private volatile boolean stop = false;
	
	public static void terminate() {
		terminate = true;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		pencilLabel.setDisable(true);
		toleranceLabel.setDisable(true);
		toleranceInput.setText("5");
		toleranceInput.setDisable(true);
		pencilColor.setValue(toJavaFxColor(Color.BLACK));
		/*
		 * pencilColor.showingProperty().addListener((obs,b,b1)->{ if(b1){
		 * PopupWindow popupWindow = getPopupWindow(); Node popup =
		 * popupWindow.getScene().getRoot().getChildrenUnmodifiable().get(0);
		 * popup.lookupAll(".color-rect").stream() .forEach(rect->{ Color c =
		 * (Color)((Rectangle)rect).getFill(); // Replace with your custom color
		 * ((Rectangle)rect).setFill(c.brighter()); }); } });
		 */
		pencilColor.setDisable(true);
		backColor.setDisable(true);
		pencilColorLabel.setDisable(true);
		backColorLabel.setDisable(true);
		mirrorOptions.setDisable(true);
		flipMirror.setDisable(true);
		String opencvpath = System.getProperty("user.dir") + "\\src\\application\\";
		System.load(opencvpath + Core.NATIVE_LIBRARY_NAME + ".dll");
		System.load(opencvpath + "opencv_ffmpeg2413_64.dll");
		VideoCapture camera = new VideoCapture(0);
		// VideoCapture camera = new
		// VideoCapture("http://169.254.148.78/mjpg/video.mjpg");
		// camera.open("http://169.254.148.78/mjpg/video.mjpg");
		Mat mat = new Mat();
		camera.read(mat);
		image = new MatImage(mat);
		new Thread(() -> {
			//BufferedImage bf = null;
			RGBPixel[][] RGBimage;
			while (true) {
				if (stop)
					continue;
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						try {
							framerateLabel.setText("Framerate: "
									+ Math.round((1000 / (System.currentTimeMillis() - startTime))) + " fps");
						} catch (ArithmeticException ee) {
						}
						startTime = System.currentTimeMillis();
					}
				});
				camera.read(mat);
				image.updateMat(mat);
				//bf = (image.getBufferedImage());
				RGBimage = image.getImage();
				WritableImage wr = null;
				if (RGBimage != null) {
					wr = new WritableImage(RGBimage.length, RGBimage[0].length);
					PixelWriter pw = wr.getPixelWriter();
					for (int x = 0; x < RGBimage.length; x++) {
						for (int y = 0; y < RGBimage[0].length; y++) {
							pw.setArgb(x, y, RGBimage[x][y].getRGB());
						}
					}
				}
				cameraView.setImage(wr);

			}

		}).start();

	}

	public void close() {
		System.exit(1);
	}

	public void about() throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("About.fxml"));
		Scene scene = new Scene(root, 400, 300);
		Stage stage = new Stage();
		// stage.setOnCloseRequest(e -> {System.exit(1);});
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
	}

	public void revert() {
		image.reset();
		pencilItem.setSelected(false);
		mirrorHItem.setSelected(false);
		pencilLabel.setDisable(true);
		toleranceLabel.setDisable(true);
		toleranceInput.setDisable(true);
		pencilColor.setDisable(true);
		backColor.setDisable(true);
		pencilColorLabel.setDisable(true);
		backColorLabel.setDisable(true);
		mirrorOptions.setDisable(true);
		flipMirror.setDisable(true);
		mirrorVItem.setSelected(false);
		sepiaItem.setSelected(false);
		grayscaleItem.setSelected(false);
		zeroRedItem.setSelected(false);
		zeroBlueItem.setSelected(false);
		zeroGreenItem.setSelected(false);
		keepRedItem.setSelected(false);
		keepGreenItem.setSelected(false);
		keepBlueItem.setSelected(false);
		negateItem.setSelected(false);
		switchBRGItem.setSelected(false);
		switchGBRItem.setSelected(false);
		switchBGRItem.setSelected(false);
		pixelate.setSelected(false);
		blur.setSelected(false);
	}

	public void flipMirror() {
		image.flipMirror();
	}

	public void pencil() {
		if (pencilItem.isSelected()) {
			image.addFilters(Filter.pencilDrawing);
			pencilLabel.setDisable(false);
			toleranceLabel.setDisable(false);
			toleranceInput.setDisable(false);
			pencilColor.setDisable(false);
			backColor.setDisable(false);
			pencilColorLabel.setDisable(false);
			backColorLabel.setDisable(false);
		} else {
			image.removeFilter(Filter.pencilDrawing);
			pencilLabel.setDisable(true);
			toleranceLabel.setDisable(true);
			toleranceInput.setDisable(true);
			pencilColor.setDisable(true);
			backColor.setDisable(true);
			pencilColorLabel.setDisable(true);
			backColorLabel.setDisable(true);
		}
	}

	public void mirrorHorizontal() {
		if (mirrorHItem.isSelected()) {
			image.addFilters(Filter.mirrorHorizontal);
			mirrorOptions.setDisable(false);
			flipMirror.setDisable(false);
		} else {
			image.removeFilter(Filter.mirrorHorizontal);
			mirrorOptions.setDisable(true);
			flipMirror.setDisable(true);
		}
	}

	public void mirrorVertical() {
		if (mirrorVItem.isSelected()) {
			image.addFilters(Filter.mirrorVertical);
			mirrorOptions.setDisable(false);
			flipMirror.setDisable(false);
		} else {
			image.removeFilter(Filter.mirrorVertical);
			mirrorOptions.setDisable(true);
			flipMirror.setDisable(true);
		}
	}

	public void sepia() {
		if (sepiaItem.isSelected())
			image.addFilters(Filter.sepia);
		else
			image.removeFilter(Filter.sepia);
	}

	public void grayscale() {
		if (grayscaleItem.isSelected())
			image.addFilters(Filter.grayscale);
		else
			image.removeFilter(Filter.grayscale);
	}

	public void negate() {
		if (negateItem.isSelected())
			image.addFilters(Filter.negate);
		else
			image.removeFilter(Filter.negate);
	}

	public void keepRed() {
		if (keepRedItem.isSelected())
			image.addFilters(Filter.keepRed);
		else
			image.removeFilter(Filter.keepRed);
	}

	public void keepBlue() {
		if (keepBlueItem.isSelected())
			image.addFilters(Filter.keepBlue);
		else
			image.removeFilter(Filter.keepBlue);
	}

	public void keepGreen() {
		if (keepGreenItem.isSelected())
			image.addFilters(Filter.keepGreen);
		else
			image.removeFilter(Filter.keepGreen);
	}

	public void zeroRed() {
		if (zeroRedItem.isSelected())
			image.addFilters(Filter.zeroRed);
		else
			image.removeFilter(Filter.zeroRed);
	}

	public void zeroBlue() {
		if (zeroBlueItem.isSelected())
			image.addFilters(Filter.zeroBlue);
		else
			image.removeFilter(Filter.zeroBlue);
	}

	public void zeroGreen() {
		if (zeroGreenItem.isSelected())
			image.addFilters(Filter.zeroGreen);
		else
			image.removeFilter(Filter.zeroGreen);
	}

	public void switchBRG() {
		if (switchBRGItem.isSelected())
			image.addFilters(Filter.switchBRG);
		else
			image.removeFilter(Filter.switchBRG);
	}

	public void switchGBR() {
		if (switchGBRItem.isSelected())
			image.addFilters(Filter.switchGBR);
		else
			image.removeFilter(Filter.switchGBR);
	}

	public void switchBGR() {
		if (switchBGRItem.isSelected())
			image.addFilters(Filter.switchBGR);
		else
			image.removeFilter(Filter.switchBGR);
	}

	public void pixelate() {
		if (pixelate.isSelected())
			image.addFilters(Filter.pixelate);
		else
			image.removeFilter(Filter.pixelate);
	}

	public void blur() {
		if (blur.isSelected())
			image.addFilters(Filter.blur);
		else
			image.removeFilter(Filter.blur);
	}

	public void capture() {
		stop = true;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("PNG Files", "*.png"),
				new ExtensionFilter("JPG Files", "*.jpg", "*.jpeg"));
		File selectedFile = fileChooser.showSaveDialog(null);
		try {
			if (selectedFile.getName().endsWith("jpg"))
				ImageIO.write(image.getBufferedImage(), "jpg", selectedFile);
			else
				ImageIO.write(image.getBufferedImage(), "png", selectedFile);
		} catch (IOException e) {
		} finally {
			stop = false;
		}
	}

	private javafx.scene.paint.Color toJavaFxColor(java.awt.Color color) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		double a = color.getAlpha() / 255.0;
		return new javafx.scene.paint.Color(r, g, b, a);
	}

	private java.awt.Color toAwtColor(javafx.scene.paint.Color color) {
		int r = (int) (color.getRed() * 255);
		int g = (int) (color.getGreen() * 255);
		int b = (int) (color.getBlue() * 255);
		return new Color(r, g, b);
	}

	public void updatePencil() {
		try {
			image.setPencil(Integer.parseInt(toleranceInput.getText()), toAwtColor(pencilColor.getValue()),
					toAwtColor(backColor.getValue()));
		} catch (Exception e) {}
	}

	public void toggle() {
		stop = !stop;
	}
}
