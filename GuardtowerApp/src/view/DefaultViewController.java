package view;

import java.io.IOException;
import model.DataThread;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import model.DataInterface;
import model.MapCreator;

/**
 * The class is used for handling the GUI events and the drawing of GUI elements
 * .
 *
 * @author Mattila, Laine, Heinonen, Taivalmaa
 * 
 */
public class DefaultViewController {

	MapCreator linemap = new MapCreator();
	Line[] lines = linemap.getLines();

	/**
	 * This is x and y coordinates used in drawing ovals.
	 */
	private int x;
	private int y;

	private static int robotAngle;
	private static int ielevAngle;

	private GraphicsContext gc;

	@FXML
	DataInterface inter = new DataInterface();
	DataThread data;

	@FXML
	private Canvas canvasMap = new Canvas(MapCreator.SCALE, MapCreator.SCALE);
	@FXML
	private Canvas canvasMark = new Canvas(MapCreator.SCALE, MapCreator.SCALE);
	@FXML
	private Button stopButton;
	@FXML
	private Button usermodeButton;
	@FXML
	private Button fireButton;
	@FXML
	private Button burstfireButton;
	@FXML
	private Button scannermodeButton;
	@FXML
	private Button calibrateButton;
	@FXML
	private TextField angleField;
	@FXML
	private Button angleturnButton;
	@FXML
	private Button sndmodeButton;
	@FXML
	private Text robotangleText;
	@FXML
	private Text robotelevText;

	public DefaultViewController() {

	}

	/**
	 *
	 * @param inter is DataInterface class parameter, which is used to give access
	 *              to send or receive commands between this controller and robot
	 *              This is a constructor, it forms a connection with the robot and
	 *              starts a datathread for data transferring.
	 */
	public DefaultViewController(DataInterface inter) {
		this.inter = inter;
		inter.createConnection();
		data = new DataThread(inter);
		data.start();
	}

	public static int getRobotAngle() {
		return robotAngle;
	}

	public static void setRobotAngle(int robotANGLE) {
		robotAngle = robotANGLE;
	}

	/**
	 * This method prints robot's current robotAngle to robotangleText text element
	 * in GUI. It is first formatted as "" and then as the desired value.
	 */
	@FXML
	public void handleRobotAngleText() {
		robotangleText.setText("");
		robotangleText.setText(Integer.toString(robotAngle));

	}

	/**
	 * This method prints robot's current robotelevAngle to robotelevText text
	 * element in GUI. It is first formatted as "" and then as the desired value.
	 */
	@FXML
	public void handleRobotElevText() {
		robotelevText.setText("");
		robotelevText.setText(Integer.toString(ielevAngle));

	}

	/**
	 * This method creates a connection between this application and the robot when
	 * the Calibrate button is pressed.
	 */
	@FXML
	public void handleCalibrateButton() {
		inter.createConnection();
		data = new DataThread(inter);
		data.start();
		try {
			inter.sendUTF("calibrate");
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * This method sends a command in string format to the robot to switch to
	 * SearchAndDestroy mode.
	 */
	@FXML
	public void handleSndmodeButton() {
		try {
			inter.sendUTF("sndmode");
		} catch (IOException e) {

			e.printStackTrace();
		}
		;
	}

	/**
	 * This method sends a command in string format for the robot to switch to
	 * Scanning mode.
	 */
	@FXML
	public void handleScannermodeButton() {
		try {
			inter.sendUTF("scannermode");
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * This method sends a command in string format for the robot to execute a
	 * burstfire. It will not send the command if the robot is not in Usermode.
	 */
	@FXML
	public void handleButtonBurstfire() {
		if (data.getUsermodeOn() == true) {
			try {
				inter.sendUTF("burstfire");

			} catch (IOException e) {

				e.printStackTrace();
			}
		} else {
			showUsermodeAlert();

		}

	}

	/**
	 * This method commands the robot to execute a single fire. It will not send the
	 * command if the robot is not in Usermode.
	 */
	@FXML
	public void handleButtonFire() {

		if (data.getUsermodeOn() == true) {
			try {
				inter.sendUTF("fire");

			} catch (IOException e) {

				e.printStackTrace();
			}
		} else {
			showUsermodeAlert();

		}

	}

	/**
	 * 
	 * @param e This method handles the mouse click event on the map. It calculates
	 *          the shortest direction to rotate towards the clicked coordinates and
	 *          sends them as commands for the robot as a string message.
	 */
	@FXML
	public void handleMouseXY(MouseEvent e) {
		double finalAngleA;
		double finalAngleB;
		gc = canvasMark.getGraphicsContext2D();
		gc.clearRect(800, 800, 10, 10);
		newLocation((int) e.getX(), (int) e.getY());
		double roboX = 400, roboY = 400, X, Y, angle, result;
		X = e.getX() - roboX;
		Y = e.getY() - roboY;
		angle = Math.atan(Y / X);
		angle = Math.toDegrees(angle);

		if (X > 0 && Y > 0) {
			angle = angle;
		} else if (X < 0 && Y > 0) {
			angle = 180 + angle;
		} else if (X < 0 && Y < 0) {
			angle = 180 - angle;
		} else {
			angle = 360 + angle;
		}

		if (angle > robotAngle) {
			finalAngleA = angle - robotAngle;
			finalAngleB = 360 - angle + robotAngle;
		} else {
			finalAngleA = 360 - robotAngle + angle;
			finalAngleB = robotAngle - angle;
		}

		if (finalAngleA <= finalAngleB) {
			result = finalAngleA;

			result = result * 11;
			int intResult = (int) result;
			String message = Integer.toString(intResult);
			try {
				inter.sendUTF(message);
			} catch (IOException i) {
				i.printStackTrace();
			}
		}

		else {
			result = -1 * finalAngleB;

			result = result * 11;
			int intResult = (int) result;
			String message = Integer.toString(intResult);
			try {
				inter.sendUTF(message);
			} catch (IOException i) {
				i.printStackTrace();
			}
		}
		double elevAngle = Math.sqrt(X * X + Y * Y);
		elevAngle = elevAngle / 2;
		if (elevAngle > 199) {
			elevAngle = 199;
		}
		ielevAngle = (int) elevAngle;
		try {
			inter.sendUTF("e" + Integer.toString(ielevAngle));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		handleRobotAngleText();
		handleRobotElevText();

	}

	/**
	 * This method sends a command for the robot switch to Usermode.
	 */
	@FXML
	public void handleButtonUsermode() {
		try {
			inter.sendUTF("usermode");
			System.out.println("Usermode on!");
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * This method sends a command for the robot to shut down and exit the
	 * application.
	 */
	@FXML
	public void handleButtonStop() {
		try {
			inter.sendUTF("stop");
		} catch (IOException e) {

			e.printStackTrace();
		}
		data.stop();
		System.exit(0);

	}

	/**
	 * This method sends a command for the robot to turn clockwise or
	 * counterclockwise by a integer specified by the user in the angleField
	 * textfield.
	 */

	@FXML
	public void handleButtonTurn() {

		try {
			// Kulma kerrotaan oikeaan suhteeseen
			int turnAngle = Integer.parseInt(angleField.getText()) * 11;
			inter.sendUTF(Integer.toString(turnAngle));
			System.out.println(angleField.getText());

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * This method draws a pre-generated linemap (list of line coordinates) on a
	 * canvas that is displayed for the user.
	 */

	public void drawMap() {

		gc = canvasMap.getGraphicsContext2D();
		gc.setLineWidth(1.0);
		for (int i = 0; i < lines.length; i++) {
			gc.strokeLine(lines[i].getStartX(), lines[i].getStartY(), lines[i].getEndX(), lines[i].getEndY());
		}
	}

	/**
	 * This method compares the input coordinates to the canvas area limits and if
	 * true, it sets the coordinates and executes drawOval method.
	 * 
	 * @param x is the x input coordinate.
	 * @param y is the y input coordinate.
	 */
	public void newLocation(int x, int y) {

		if (x >= 0 && x <= canvasMark.getWidth() && y >= 0 && y <= canvasMark.getHeight()) {
			this.x = x;
			this.y = y;
			drawOval();

		}
	}

	/**
	 * This method draws a green oval.
	 */

	public void drawOval() {

		clearScreen();
		gc.setFill(Color.GREEN);
		gc.fillOval(x - 5, y - 5, 10, 10);
	}

	/**
	 * This method clears the canvas where the oval marks are drawn.
	 */
	public void clearScreen() {

		gc.clearRect(0, 0, canvasMark.getWidth(), canvasMark.getHeight());

	}

	/**
	 * This method prints out an alert about user mode required.
	 */
	public void showUsermodeAlert() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Alert!");

		alert.setHeaderText(null);
		alert.setContentText("Usermode required!");

		alert.showAndWait();

	}

}