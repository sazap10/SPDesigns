import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;

public class TransAnim extends JApplet {
	public static void main(String args[]) {
		JFrame f = new JFrame("TransAnim");
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		JApplet applet = new TransAnim();
		f.getContentPane().add(applet, BorderLayout.CENTER);
		applet.init();
		f.setBackground(Color.black);
		f.setSize(new Dimension(650, 500));
		f.setVisible(true);

	}

	public void init() {
		getContentPane().setLayout(new BorderLayout());
		TransAnimPanel animator = new TransAnimPanel();
		animator.setSize(650, 500);
		animator.setBackground(Color.black);
		getContentPane().add(animator);
		animator.setVisible(true);
		animator.startAnimation();
	}
}

class TransAnimPanel extends JPanel implements Runnable {
	Thread animatorThread;
	int delay;
	AffineTransform mytrans, moontrans, earthtrans, moonrotate;
	Shape sun, earth, moon;
	// Shape ray;
	GeneralPath triangle;
	double theta = 0.0;
	double moonorb = 0.0;
	double earthaxis = 0.0;
	float earthx, earthy, moonx, moony;
	double[] starx,stary,starradius;

	public TransAnimPanel() {
		mytrans = new AffineTransform();
		moontrans = new AffineTransform();
		earthtrans = new AffineTransform();
		moonrotate = new AffineTransform();
		sun = new Ellipse2D.Float(200, 200, 100, 100);
		earth = new Ellipse2D.Float(300, 100, 40, 30);
		moon = new Ellipse2D.Double(-10, -7.5, 20, 15);
		// triangles
		earthx = earth.getBounds().x + (earth.getBounds().width / 2);
		earthy = earth.getBounds().y + (earth.getBounds().height / 2);
		moonx = moon.getBounds().x + (moon.getBounds().width / 2);
		moony = moon.getBounds().y + (moon.getBounds().height / 2);
		triangle = new GeneralPath();
		triangle.moveTo(-10, 60);
		triangle.lineTo(10, 60);
		triangle.lineTo(0, 80);
		triangle.closePath();
		// ray = new Line2D.Float(0, 0, 80, 0);
		createStarArray();
		delay = 100;
	}

	// Draw the current frame of animation.
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// float earthx = earth.getBounds().x+(earth.getBounds().width/2);
		// float earthy = earth.getBounds().y+(earth.getBounds().height/2);
		Graphics2D g2 = (Graphics2D) g;
		drawStars(g2);
		if (moonorb == (Math.PI * 2)) {
			moonorb = 0;
		} else {
			moonorb += (Math.PI / 8);
		}
		if (earthaxis == (Math.PI * 2)) {
			earthaxis = 0;
		} else {
			earthaxis += (Math.PI / 4);
		}
		theta -= 0.05;
		// earth rotation around its axis
		mytrans.setToTranslation(0, 0);
		// mytrans.translate(-earthx, -earthy);
		mytrans.rotate(earthaxis, earthx, earthy);
		// earth rotating around the sun
		earthtrans.setToTranslation(0.0, 0.0); // Rough center of the screen
		earthtrans.rotate(theta, 250, 250); // Orbit angle
		Shape earth1 = mytrans.createTransformedShape(earth);
		Shape earth2 = earthtrans.createTransformedShape(earth1);
		// x and y of earth after rotation around its axis
		earthx = earth1.getBounds().x + (earth1.getBounds().width / 2);
		earthy = earth1.getBounds().y + (earth1.getBounds().height / 2);
		// x and y of earth after rotating around the sun
		double x = earth2.getBounds().x + (earth2.getBounds().width / 2);
		double y = earth2.getBounds().y + (earth2.getBounds().height / 2);
		// moon rotating around its axis
		//
		moonrotate.setToTranslation(x, y);
		moonrotate.rotate(-moonorb);
		// moon rotating around earth
		moonrotate.translate(25, 25);
		moonrotate.rotate(-earthaxis);

		//
		// Shape moon1 = moonrotate.createTransformedShape(moon);
		// moonx = moon1.getBounds().x + (moon1.getBounds().width / 2);
		// moony = moon1.getBounds().y + (moon1.getBounds().height / 2);
		// moon around the earth
		// moontrans.setToTranslation(x,y);
		// moontrans.translate(30, 27.5);
		// moontrans.rotate(earthaxis);
			

		g2.setColor(new Color(248, 255, 0));
		g2.fill(sun);
		drawRays(g2);
		g2.setColor(new Color(135, 206, 235));
		g2.fill(earthtrans.createTransformedShape(earth1));
		g2.setColor(new Color(146, 146, 146));
		g2.fill(moonrotate.createTransformedShape(moon));
		// g2.fill(moontrans.createTransformedShape(moon1));

	}

	private void drawStars(Graphics2D g2) {
		
		g2.setColor(Color.white);
		for (int i = 0; i < starx.length; i++) {
			double x = Math.floor(Math.random()*10)+1;
			if(x<3)
				g2.setColor(Color.darkGray);
			g2.fill(new Ellipse2D.Double(starx[i], stary[i], 2, 2));
		}
	}

	private void createStarArray() {
		starx= new double[200];
		stary = new double[200];
		for(int i = 0;i<starx.length;i++){
			starx[i] = Math.random()*650;
			stary[i] = Math.random()*500;
		}
	}

	private void drawRays(Graphics2D g2) {
		AffineTransform saveTr = g2.getTransform();
		g2.translate(250, 250);
		g2.rotate(theta);
		g2.setColor(new Color(248, 255, 0));
		for (int i = 0; i < 8; i++) {
			g2.rotate(2 * Math.PI / 8);
			g2.fill(triangle);
		}
		g2.setTransform(saveTr);
	}

	public void startAnimation() {
		// Start the animating thread.
		if (animatorThread == null) {
			animatorThread = new Thread(this);
		}
		animatorThread.start();
	}

	public void stopAnimation() {
		// Stop the animating thread.
		animatorThread = null;
	}

	public void run() {
		// Just to be nice, lower this thread's priority
		// so it can't interfere with other processing going on.
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		// Remember the starting time.
		long startTime = System.currentTimeMillis();

		// Remember which thread we are.
		Thread currentThread = Thread.currentThread();

		// This is the animation loop.
		while (currentThread == animatorThread) {
			// Advance the animation frame.
			// Display it.
			repaint();

			// Delay depending on how far we are behind.
			try {
				startTime += delay;
				Thread.sleep(Math.max(0, startTime - System.currentTimeMillis()));
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
