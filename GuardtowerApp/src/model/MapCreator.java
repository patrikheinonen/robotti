package model;

import javafx.scene.shape.Line;


/**
 * 
 * @author Mattila This class creates a table of line elements and scales them
 *         properly , which are then to be used as a map in the GUI.
 *
 */

public class MapCreator {

	private Line[] lines;
	/**
	 * Maximum size of the square shaped map in pixels in the GUI. The inputed map is scaled to this size.
	 */
	public final static double SCALE = 800;
	/**
	 * Maximum width of the map used in EV3 as a Linemap. Linemap is not implemented
	 * in this proof of concept.
	 */
	final static double MAXWIDTH = 100;
	/**
	 * Maximum height of the map used in EV3 as a Linemap. Linemap is not
	 * implemented in this proof of concept.
	 */
	final static double MAXHEIGHT = 100;

	/**
	 * A constructor that creates the lines to be displayed on the GUI as a map and
	 * then scales it.
	 */
	public MapCreator() {
		setLines(new Line[] { new Line(0, 0, 0, MAXHEIGHT), new Line(0, MAXHEIGHT, MAXWIDTH, MAXHEIGHT),
				new Line(MAXWIDTH, MAXHEIGHT, MAXWIDTH, 0), new Line(MAXWIDTH, 0, 0, 0),
				new Line(MAXWIDTH / 2, 0, MAXWIDTH / 2, MAXHEIGHT), new Line(0, MAXHEIGHT / 2, MAXWIDTH, MAXHEIGHT / 2)

		});

		scaleupLines(lines);

	}

	/**
	 * @param lines is the list of lines used as a map 
	 * @return lines
	 * This method scales the lines
	 *              to fit the GUI correctly and returns them
	 */
	public Line[] scaleupLines(Line[] lines) {

		for (int i = 0; i < lines.length; i++) {

			lines[i] = new Line((lines[i].getStartX()) * SCALE / MAXWIDTH, (lines[i].getStartY()) * SCALE / MAXHEIGHT,
					(lines[i].getEndX()) * SCALE / MAXWIDTH, (lines[i].getEndY()) * SCALE / MAXHEIGHT);
		}

		return lines;
	}

	public Line[] getLines() {
		return lines;
	}

	public void setLines(Line[] lines) {
		this.lines = lines;
	}

}
