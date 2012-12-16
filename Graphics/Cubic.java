import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.applet.Applet;
import java.awt.geom.*;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;

public class Cubic extends JApplet {

	static protected JLabel label;
	CubicPanel cubicPanel;

	public void init() {
		// Initialize the layout.
		getContentPane().setLayout(new BorderLayout());
		cubicPanel = new CubicPanel();
		cubicPanel.setBackground(Color.white);
		getContentPane().add(cubicPanel);
		label = new JLabel("Drag the points to adjust the curve.");
		getContentPane().add("South", label);
	}

	public static void main(String s[]) {
		JFrame f = new JFrame("Cubic");
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		JApplet applet = new Cubic();
		f.setSize(new Dimension(500, 500));
		f.getContentPane().add(applet, BorderLayout.CENTER);

		applet.init();
		f.setVisible(true);
	}
}

class CubicPanel extends JPanel implements MouseListener, MouseMotionListener {

	BufferedImage bi;
	Graphics2D big;
	int x, y;
	Rectangle area, startpt, endpt, onept, twopt, rect, end2pt, one2pt, two2pt;
	CubicCurve2D.Double cubic = new CubicCurve2D.Double();
	Point2D.Double start, end, one, two, point, end2, one2, two2;
	boolean firstTime = true;
	boolean pressOut = false;

	public CubicPanel() {

		setBackground(Color.white);
		addMouseMotionListener(this);
		addMouseListener(this);

		start = new Point2D.Double();
		one = new Point2D.Double();
		two = new Point2D.Double();
		end = new Point2D.Double();
		one2 = new Point2D.Double();
		two2 = new Point2D.Double();
		end2 = new Point2D.Double();

		cubic.setCurve(start, one, two, end);
		startpt = new Rectangle(0, 0, 8, 8);
		endpt = new Rectangle(0, 0, 8, 8);
		onept = new Rectangle(0, 0, 8, 8);
		twopt = new Rectangle(0, 0, 8, 8);
		end2pt = new Rectangle(0, 0, 8, 8);
		one2pt = new Rectangle(0, 0, 8, 8);
		two2pt = new Rectangle(0, 0, 8, 8);
	}

	public void mousePressed(MouseEvent e) {

		x = e.getX();
		y = e.getY();

		if (startpt.contains(x, y)) {
			rect = startpt;
			point = start;
			x = startpt.x - e.getX();
			y = startpt.y - e.getY();
			updateLocation(e);
		} else if (endpt.contains(x, y)) {
			rect = endpt;
			point = end;
			x = endpt.x - e.getX();
			y = endpt.y - e.getY();
			one2.setLocation(collinear(two, end));
			one2pt.setLocation((int) one2.x, (int) one2.y);
			updateLocation(e);
		} else if (onept.contains(x, y)) {
			rect = onept;
			point = one;
			x = onept.x - e.getX();
			y = onept.y - e.getY();
			updateLocation(e);
		} else if (twopt.contains(x, y)) {
			rect = twopt;
			point = two;
			x = twopt.x - e.getX();
			y = twopt.y - e.getY();
			one2.setLocation(collinear(two, end));
			one2pt.setLocation((int) one2.x, (int) one2.y);
			updateLocation(e);
		} else if (end2pt.contains(x, y)) {
			rect = end2pt;
			point = end2;
			x = end2pt.x - e.getX();
			y = end2pt.y - e.getY();
			updateLocation(e);
		} else if (one2pt.contains(x, y)) {
			rect = one2pt;
			point = one2;
			x = one2pt.x - e.getX();
			y = one2pt.y - e.getY();
			two.setLocation(collinear(one2, end));
			twopt.setLocation((int) two.x, (int) two.y);
			updateLocation(e);
		} else if (two2pt.contains(x, y)) {
			rect = two2pt;
			point = two2;
			x = two2pt.x - e.getX();
			y = two2pt.y - e.getY();
			updateLocation(e);
		} else {
			pressOut = true;
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (!pressOut) {
			if (twopt.contains(e.getX(), e.getY())) {
				one2.setLocation(collinear(two, end));
				one2pt.setLocation((int) one2.x, (int) one2.y);
			} else if (one2pt.contains(e.getX(), e.getY())) {
				two.setLocation(collinear(one2, end));
				twopt.setLocation((int) two.x, (int) two.y);
			} else if (endpt.contains(e.getX(), e.getY())) {
				one2.setLocation(collinear(two, end));
				one2pt.setLocation((int) one2.x, (int) one2.y);
			}
			updateLocation(e);
		}

	}

	public void mouseReleased(MouseEvent e) {
		if (startpt.contains(e.getX(), e.getY())) {
			rect = startpt;
			point = start;
			updateLocation(e);
		} else if (endpt.contains(e.getX(), e.getY())) {
			rect = endpt;
			point = end;
			one2.setLocation(collinear(two, end));
			one2pt.setLocation((int) one2.x, (int) one2.y);
			updateLocation(e);
		} else if (onept.contains(e.getX(), e.getY())) {
			rect = onept;
			point = one;
			updateLocation(e);
		} else if (twopt.contains(e.getX(), e.getY())) {
			rect = twopt;
			point = two;
			one2.setLocation(collinear(two, end));
			one2pt.setLocation((int) one2.x, (int) one2.y);
			updateLocation(e);
		} else if (end2pt.contains(e.getX(), e.getY())) {
			rect = end2pt;
			point = end2;
			updateLocation(e);
		} else if (one2pt.contains(e.getX(), e.getY())) {
			rect = one2pt;
			point = one2;
			two.setLocation(collinear(one2, end));
			twopt.setLocation((int) two.x, (int) two.y);
			updateLocation(e);
		} else if (two2pt.contains(e.getX(), e.getY())) {
			rect = two2pt;
			point = two2;
			updateLocation(e);
		} else {
			pressOut = false;
		}
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void updateLocation(MouseEvent e) {

		rect.setLocation((x + e.getX()), (y + e.getY()));
		point.setLocation(x + e.getX(), y + e.getY());

		checkPoint();

		// cubic.setCurve(start, one, two, end);

		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		update(g);
	}

	public void update(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		Dimension dim = getSize();
		int w = dim.width;
		int h = dim.height;

		if (firstTime) {

			// Create the offsecren graphics to render to
			bi = (BufferedImage) createImage(w, h);
			big = bi.createGraphics();

			// Get some initial positions for the control points

			start.setLocation(w / 2 - 50, h / 2);
			end.setLocation(w / 2 + 50, h / 2);
			one.setLocation((int) (start.x) + 25, (int) (start.y) - 25);

			two.setLocation((int) (end.x) - 25, (int) (end.y) + 25);
			end2.setLocation(w / 2 + 150, h / 2);
			// one2.setLocation((int)(end.x)+25, (int) (end.y) - 25);
			// two.setLocation(collinear(one2,end));
			one2 = collinear(two, end);
			two2.setLocation((int) (end2.x) - 25, (int) (end2.y) + 25);

			// Set the initial positions of the squares that are
			// drawn at the control points
			startpt.setLocation((int) ((start.x) - 4), (int) ((start.y) - 4));
			endpt.setLocation((int) ((end.x) - 4), (int) ((end.y) - 4));
			onept.setLocation((int) ((one.x) - 4), (int) ((one.y) - 4));
			twopt.setLocation((int) ((two.x) - 4), (int) ((two.y) - 4));
			end2pt.setLocation((int) ((end2.x) - 4), (int) ((end2.y) - 4));
			one2pt.setLocation((int) ((one2.x) - 4), (int) ((one2.y) - 4));
			two2pt.setLocation((int) ((two2.x) - 4), (int) ((two2.y) - 4));

			// Initialise the CubicCurve2D
			cubic.setCurve(start, one, two, end);

			// Set some defaults for Java2D
			big.setColor(Color.black);
			big.setStroke(new BasicStroke(5.0f));
			big.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			area = new Rectangle(dim);
			firstTime = false;
		}

		// Clears the rectangle that was previously drawn.

		big.setColor(Color.white);
		big.clearRect(0, 0, area.width, area.height);

		// Set the colour for the bezier
		big.setPaint(Color.black);

		// Replace the following line by your own function to
		// draw the bezier specified by start, one, two, end

		// big.draw(cubic);
		Point2D.Double[] curve1 = { start, one, two, end };
		Point2D.Double[] curve2 = { end, one2, two2, end2 };
		bezier(curve1);
		bezier(curve2);

		// Draw the control points

		big.setPaint(Color.red);
		big.fill(startpt);
		big.setPaint(Color.magenta);
		big.fill(endpt);
		big.setPaint(Color.blue);
		big.fill(onept);
		big.setPaint(new Color(0, 200, 0));
		big.fill(twopt);
		big.setPaint(Color.yellow);
		big.fill(end2pt);
		big.setPaint(Color.cyan);
		big.fill(one2pt);
		big.setPaint(Color.green);
		big.fill(two2pt);

		// Draws the buffered image to the screen.
		g2.drawImage(bi, 0, 0, this);

	}

	void bezier(Point2D.Double[] points) {
		Point2D.Double[] split;
		if (checkDistance(points)) {
			Line2D line = new Line2D.Double(points[0], points[3]);
			big.draw(line);
		} else {
			split = split(points);
			Point2D.Double[] left = new Point2D.Double[4];
			Point2D.Double[] right = new Point2D.Double[4];
			System.arraycopy(split, 0, left, 0, 4);
			System.arraycopy(split, 3, right, 0, 4);
			bezier(left);
			bezier(right);
		}
	}

	Point2D.Double[] split(Point2D.Double[] points) {
		Point2D.Double[] split = new Point2D.Double[7];
		Point2D.Double A = midpoint(points[0], points[1]);
		Point2D.Double B = midpoint(points[1], points[2]);
		Point2D.Double C = midpoint(points[2], points[3]);
		Point2D.Double D = midpoint(A, B);
		Point2D.Double E = midpoint(B, C);
		Point2D.Double F = midpoint(D, E);
		split[0] = points[0];
		split[1] = A;
		split[2] = D;
		split[3] = F;
		split[4] = E;
		split[5] = C;
		split[6] = points[3];
		return split;
	}

	Point2D.Double midpoint(Point2D p0, Point2D p1) {
		Point2D.Double point;
		double xpoint = (p0.getX() + p1.getX()) / 2;
		double ypoint = (p0.getY() + p1.getY()) / 2;
		point = new Point2D.Double(xpoint, ypoint);
		return point;
	}

	boolean checkDistance(Point2D[] points) {
		if (points[0].distance(points[1]) <= 1
				&& points[2].distance(points[3]) <= 1)
			return true;
		return false;
	}

	Point2D.Double collinear(Point2D.Double point1, Point2D.Double point2) {
		Point2D.Double point = new Point2D.Double();
		double x = (point2.x * 2 - point1.x);
		double y = (point2.y * 2 - point1.y);
		point.setLocation(x, y);
		return point;
	}

	/*
	 * Checks if the rectangle is contained within the applet window. If the
	 * rectangle is not contained within the applet window, it is redrawn so
	 * that it is adjacent to the edge of the window and just inside the window.
	 */

	void checkPoint() {

		if (area == null) {
			return;
		}

		if ((area.contains(rect)) && (area.contains(point))) {
			return;
		}
		int new_x = rect.x;
		int new_y = rect.y;

		double new_px = point.x;
		double new_py = point.y;

		if ((rect.x + rect.width) > area.getWidth()) {
			new_x = (int) area.getWidth() - (rect.width - 1);
		}
		if (point.x > area.getWidth()) {
			new_px = (int) area.getWidth() - 1;
		}
		if (rect.x < 0) {
			new_x = -1;
		}
		if (point.x < 0) {
			new_px = -1;
		}
		if ((rect.y + rect.width) > area.getHeight()) {
			new_y = (int) area.getHeight() - (rect.height - 1);
		}
		if (point.y > area.getHeight()) {
			new_py = (int) area.getHeight() - 1;
		}
		if (rect.y < 0) {
			new_y = -1;
		}
		if (point.y < 0) {
			new_py = -1;
		}
		rect.setLocation(new_x, new_y);
		point.setLocation(new_px, new_py);

	}
}
