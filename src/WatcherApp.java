import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;


public class WatcherApp extends JFrame implements ActionListener, PropertyChangeListener {
	private static final long serialVersionUID = -1586582677634630080L;

	public static boolean HALT = false;
	
	JProgressBar prog;
	JTextField user;
	JTextField pw;
	JButton startButton;
	JButton stopButton;
	JButton quoteButton;
	JLabel progressUpdate;
	Names names = new Names();
	
	Random rand = new Random();
	
	List<WatchTask> workerlist = Collections.synchronizedList(new ArrayList<WatchTask>());
	List<WatchBubble> bubblelist = Collections.synchronizedList(new ArrayList<WatchBubble>());
	
	ResourceManager rsrc = new ResourceManager();

    private Point initialClick;

    BasicCookieStore cookieStore = new BasicCookieStore();
	CloseableHttpClient httpClient;
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new WatcherApp();
			}
		});
	}
	
	WatcherApp() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .setConnectionManager(cm)
                .build();
        

       //addMouseMoving();
        SpecialFUckinPanel sp = new SpecialFUckinPanel(rsrc.getImg("bg.png"));
        addComponentsToFrame(sp);
        this.setContentPane(sp);
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(400, 320);
        this.setLocation((screen.width-400)/2, (screen.height-320)/2);
        this.setUndecorated(true);
        this.setResizable(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}


	/**
	 * Swing is powerful juju and I can't explain why
	 * it works like this.
	 * @param pane
	 */
	private void addComponentsToFrame(SpecialFUckinPanel pane) {
		//make it look like a native application (Carrotlord)
		//even though you're an autist sperglord
		//I still appreciate this snippet.
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException exceptionInfo) {
		// Ignore this exception.
		} catch (ClassNotFoundException exceptionInfo) {
		// Ignore this exception.
		} catch (InstantiationException exceptionInfo) {
		// Ignore this exception.
		} catch (IllegalAccessException exceptionInfo) {
		// Ignore this exception.
		}
		
		pane.setLayout(new BorderLayout());
		JPanel things = new JPanel();
		things.setOpaque(false);
		things.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridy = 0;
		c.ipadx = 6;
		c.ipady = 6;
		
		things.add(new JLabel("Username:"), c);
		user = new JTextField(30);
		things.add(user, c);
		
		c.gridy++;
		
		things.add(new JLabel("Password:"), c);
		pw = new JPasswordField(30);
		things.add(pw, c);
		
		c.gridy++;
		
		JPanel buttonpanel = new JPanel();
		buttonpanel.setOpaque(false);
		c.gridwidth = GridBagConstraints.REMAINDER;
		things.add(buttonpanel, c);
		
		
		c.gridy = 0;
		c.gridwidth = 1;
		startButton = new JButton("Start");
		startButton.setOpaque(false);
		startButton.addActionListener(this);
		buttonpanel.add(startButton, c);
		
		stopButton = new JButton("Stop");
		stopButton.setOpaque(false);
		stopButton.addActionListener(this);
		buttonpanel.add(stopButton, c);

		/* I used this for debugging
        quoteButton = new JButton("Speak, Absol!");
        quoteButton.setOpaque(false);
        quoteButton.addActionListener(pane);
		buttonpanel.add(quoteButton);
		*/
		
		ImageIcon i = new ImageIcon(
				WatcherApp.class.getResource("close_default.png"));
		JLabel iwrap = new JLabel(i);
		
		iwrap.setLocation(new Point(0,0));
		
		pane.add(things, BorderLayout.NORTH);
		
		
		prog = new JProgressBar(0, names.getTotal());
		prog.setStringPainted(true);
		prog.setString(names.getProgress() + " / " + names.getTotal());
		prog.setValue(names.getProgress());
		pane.add(prog, BorderLayout.SOUTH);
		
	}

	@Override
	public void actionPerformed(ActionEvent eve) {
		Object src = eve.getSource();
		if (src == startButton) {
			if (user.getText().equals("reset")
					&& pw.getText().equals("reset")) {
				names.reset();
			}

			if (!checkLogin()) {
				if (!doLogin()) {
					JOptionPane.showMessageDialog(this, "Couldn't login :<");
					return;
				}
			}
            
            //ok we maybe logged in now
			HALT = false;
			WatchTask watcher = new WatchTask();
			watcher.addPropertyChangeListener(this);
			watcher.execute();
			workerlist.add(watcher);
			
			updateStartButton();
		} else if (src == stopButton) {
			HALT = true;
		}
	}
	
	void updateStartButton() {
		switch (workerlist.size()) {
		case 0:
			startButton.setText("Start");
			startButton.setEnabled(true);
			break;
		case 1:
			startButton.setText("More!");
			startButton.setEnabled(true);
			break;
		case 2:
			startButton.setText("More!!");
			startButton.setEnabled(true);
			break;
		case 3:
			startButton.setText("MORE!!!");
			startButton.setEnabled(true);
			break;
		default:
			startButton.setText("that's enough!");
			startButton.setEnabled(false);
			break;
		}
	}
	
	class WatchTask extends SwingWorker<Void, Void> {
		int offset = 0;
		int myIndex = -1;
		

		@Override
		protected Void doInBackground() throws Exception {
			int totalNames = names.getTotal();
			while (names.getProgress() < totalNames && !HALT) {
				String next = names.getNext();
				boolean result = watchUser(next);
				if (myIndex == -1) {
					for (myIndex = 0; myIndex < workerlist.size(); myIndex++) {
						if (workerlist.get(myIndex) == this)
							break;
					}
				}
				bubblelist.add(new WatchBubble(next, myIndex, result));
				//Thread.sleep(rand.nextInt(400) + 300); //pretend network delay
				offset = 700;
				this.firePropertyChange("progress", 0, names.getProgress());
			}
			return null;
		}
		
		@Override
		protected void done() {
			workerlist.remove(this);
			updateStartButton();
		}
		
		public void updatePosition(long timedif) {
			offset -= timedif*2;
			if (offset < 0)
				offset = 0;
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("progress")) {
	        int progress = (Integer) evt.getNewValue();
	        prog.setValue(progress);
	        prog.setString(progress + " / " + names.getTotal());
		}
	}
	
	private boolean doLogin() {
		HttpGet g = new HttpGet("http://furaffinity.net/login");
		try {
			CloseableHttpResponse rsp = httpClient.execute(g);
			System.out.println(rsp.getStatusLine());
			rsp.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		HttpUriRequest login = RequestBuilder.post()
				.setUri("https://www.furaffinity.net/login")
				.addParameter("action", "login")
				.addParameter("name", user.getText())
				.addParameter("pass", pw.getText())
				.addParameter("retard_protection", "1")
				.addParameter("submit", "Login to FurAffinity")//not likely they check this, but might as well
				.build();
		
        try {
        	CloseableHttpResponse response2 = httpClient.execute(login);
            
            response2.close();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
        return checkLogin();
	}
	
	private boolean checkLogin() {
		boolean aFound = false;
		boolean bFound = false;
		for (Cookie c : cookieStore.getCookies()) {
			if (c.getName().equals("a"))
				aFound = true;
			else if (c.getName().equals("b"))
				bFound = true;
		}
		return aFound && bFound;
	}

	/*
	 * it looks like these HTTP Gets are blocking - 
	 * but maybe it's just a limit on how fast FA can
	 * serve us. Maybe someone smarter than me
	 * knows what I'm doing wrong.
	 */
	private boolean watchUser(String n) {
		HttpGet g = new HttpGet("http://www.furaffinity.net/user/" + n);
		boolean couldWatch = false;
		try {
			CloseableHttpResponse rsp = httpClient.execute(g);
			HttpEntity e = rsp.getEntity();
			Scanner sc = new Scanner(e.getContent());
			while (sc.hasNext()) {
				String next = sc.nextLine();
				if (next.contains("/watch/")) {
					sc.close();
					rsp.close(); //this is fuckin with my threads
					String begin = next.substring(next.indexOf("/watch"));
					String path = begin.substring(0, begin.indexOf("\""));
					HttpGet watchlink = new HttpGet("http://www.furaffinity.net" + path);
					//HttpGet watchlink = new HttpGet("http://www.example.com");
					System.out.println(watchlink.getURI());
					try {
						CloseableHttpResponse rs2 = httpClient.execute(watchlink);
						Scanner sc2 = new Scanner(rs2.getEntity().getContent());
						while (sc2.hasNext()) {
							if (sc2.nextLine().contains("has been added")) {
								couldWatch = true;
								break;
							}
						}
						sc2.close();
						rs2.close(); //rip runescape 2 2013
					} catch (IOException e2) {
						
					}
					break;
				} else if (next.contains("/unwatch/")) {
					
					couldWatch = false;
					break;
				}
			}
			sc.close();
			rsp.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return couldWatch;
	}
	
	/**
	 * this is just a lazy-ass hack
	 * actually, come to think of it, this whole program is.
	 */
	private class SpecialFUckinPanel extends BgPanel {
		private static final long serialVersionUID = -4972290713631922598L;
		BufferedImage close1 = rsrc.getImg("close_dark.png");
		BufferedImage close2 = rsrc.getImg("close_default.png");
		BufferedImage close3 = rsrc.getImg("close_light.png");
		BufferedImage mini1 = rsrc.getImg("minimize_dark.png");
		BufferedImage mini2 = rsrc.getImg("minimize_default.png");
		BufferedImage mini3 = rsrc.getImg("minimize_light.png");
		BufferedImage closeimg = close2;
		BufferedImage miniimg = mini2;
		BufferedImage absolHead = rsrc.getImg("watcher_head.png");
		long lastUpdate;
		long quoteTime;

		String[] quotes = {
				"Believe in yourself,\n and you can achieve\n  anything.",
				"Always keep your\n password secure!",
				"No-one can live in\n the light all\n  of the time.",
				"All's fair in love and\n pageview whoring.",
				"Justice doesn't come easy\n but it's worth fighting for.",
				"\nPeace, Love and Equality",
				"\n        ABSOL!!",
				"\n  You are not special",
				"Keep following your star\n and maybe someday you'll\n  be the moon.",
				"\n Strength through Balance",
				"There's both light and dark\n in all of us, what matters\n  is what you do with it.",
				"\nHeed the herald of disaster",
				"The hero's triumph on\n cataclysm's eve wins\n  three symbols of virtue.", //im just fuckin around now
				"\n Adapt, or be left behind.",
				"Sometimes the wrong\n solution is the\n  only one left.",
				"I wish it didn't have to\n be this way.",
				"\n Love will tear us apart"
		};
		String[] myQot;
		public SpecialFUckinPanel(BufferedImage bg) {
			super(bg);
			MouseAdapter bla = new MouseAdapter() {
				Rectangle mini = new Rectangle(0,0,32,32);
				Rectangle close = new Rectangle(368, 0, 400, 32);
				@Override
				public void mousePressed(MouseEvent eve) {
					Point click = eve.getPoint();
					if (mini.contains(click)) {
						swapMini(mini1, mini);
					} else if (close.contains(click)) {
						swapClose(close1, close);
					}
				}
				
				@Override
				public void mouseReleased(MouseEvent eve) {
					Point click = eve.getPoint();
					if (close.contains(click)) {
						//if (wasDrag) {
						//	swapClose(close3, close);
						//} else {
							System.exit(0);
						//}
					} else if (mini.contains(click)) {
						//if (wasDrag) {
						//	swapMini(mini3, mini);
						//} else {
							WatcherApp.this.setState(Frame.ICONIFIED);
						//}
					}
				}
				
				@Override
				public void mouseMoved(MouseEvent eve) {
					Point click = eve.getPoint();
					if (mini.contains(click)) {
						swapMini(mini3, mini);
					} else {
						swapMini(mini2, mini);
					}
					if (close.contains(click)) {
						swapClose(close3, close);
					} else {
						swapClose(close2, close);
					}
				}
				
				@Override
				public void mouseDragged(MouseEvent eve) {
				}
			};
			
			this.addMouseListener(bla);
			this.addMouseMotionListener(bla);
			addMouseMoving();
			lastUpdate = System.currentTimeMillis();
			quoteTime = 0;
			//always start with the password one
			//this is important
			myQot = quotes[1].split("\n");
			
			//refresh loosely every 1/60 second
			Timer t = new Timer();
			t.schedule(new TimerTask() {

				@Override
				public void run() {
					SpecialFUckinPanel.this.repaint();
				}
				
			}, 1000, 18);
		}

		private void swapMini (BufferedImage b, Rectangle area) {
			if (miniimg != b) {
				miniimg = b;
				repaint(area);
			}
		}
		
		private void swapClose(BufferedImage s, Rectangle area) {
			if (closeimg != s) {
				closeimg = s;
				repaint(area);
			}
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(miniimg, 0, 0, null);
			g.drawImage(closeimg, 368, 0, null);
			for (int i = 0; i < myQot.length; i++) {
				g.drawString(myQot[i], 240, 120 + 15*i);
			}
			
			long timediff = System.currentTimeMillis() - lastUpdate;
			lastUpdate = System.currentTimeMillis();
			quoteTime += timediff;
			if (quoteTime > 60000){ //a minute?
				changeQuote();
				quoteTime = 0;
			}
			
			//draw the heads
			for (int i = 0; i < workerlist.size(); i++) {
				WatchTask w = workerlist.get(i);
				w.updatePosition(timediff);
				g.drawImage(absolHead, 40 + 90 * i, 250 - w.offset/100, null);
			}
			//draw the userbubbles
			Graphics2D g2d = (Graphics2D) g.create(); //gettin serious
			
			ArrayList<WatchBubble> nukeList = new ArrayList<WatchBubble>();
			try {
				for (WatchBubble wa : bubblelist) {
					wa.draw(g2d, timediff);
					if (wa.offset <= 0)
						nukeList.add(wa);
				}
			} catch (ConcurrentModificationException errrr) {
				//this happens when we add another while the painting still goes on
				//if so, just abandon this attempt and wait for next.
			}
			bubblelist.removeAll(nukeList);
		}
		
		private void addMouseMoving() {
	        addMouseListener(new MouseAdapter() {
	            public void mousePressed(MouseEvent e) {
	                initialClick = e.getPoint();
	                getComponentAt(initialClick);
	            }
	        });
	        addMouseMotionListener(new MouseMotionAdapter() {
	            @Override
	            public void mouseDragged(MouseEvent e) {

	                // get location of Window
	                int thisX = WatcherApp.this.getLocation().x;
	                int thisY = WatcherApp.this.getLocation().y;

	                // Determine how much the mouse moved since the initial click
	                int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
	                int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

	                // Move window to this position
	                int X = thisX + xMoved;
	                int Y = thisY + yMoved;
	                WatcherApp.this.setLocation(X, Y);
	            }
	        });
		}

		public void changeQuote(){
			myQot = quotes[rand.nextInt(quotes.length)].split("\n");
		}

	}
	
	private class WatchBubble {
		int offset;
		int position;
		String user;
		int w = -1;
		int h = -1;
		boolean success;
		
		WatchBubble(String name, int pos, boolean result) {
			user = name;
			offset = 5000;
			position = pos;
			success = result;
		}
		
		public void draw(Graphics2D g, long timediff) {
			offset -= timediff * 2.2;
			if (offset < 0) {
				offset = 0;
			}
			AlphaComposite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) offset/6000);
			g.setComposite(comp);
			if (w == -1) {
				FontMetrics f = g.getFontMetrics();
				w = f.stringWidth(user);
				h = f.getHeight();
			}
			int x = 64 + 90 * position - w/2;
			int y = 170 + offset/100;
			g.setColor(Color.white);
			g.fillRoundRect(x-4, y+4, w+8, h+8, 4, 4);
			if (success) {
				g.setColor(Color.decode("0x007F0E")); //it's green
			} else {
				g.setColor(Color.red);
			}
			g.drawString(user, x, y+h+4);
		}
	}
}
