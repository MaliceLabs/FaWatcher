import java.util.ArrayList;
import java.util.Scanner;
import java.util.prefs.Preferences;


public class Names {
	private int progress = 0;
	public int getProgress() {return progress;}
	public int getTotal() {return names.length;}
	Preferences prefs = Preferences.userNodeForPackage(Names.class);
	
	public void reset() {
		progress = 0;
	}
	
	Names() {
		progress = prefs.getInt("AAAAAA", 0);
	}
	
	public synchronized String getNext() {
		String n = names[progress];
		progress++;
		prefs.putInt("AAAAAA", progress);
		return n;
	}
	
	private final static String[] names = loadNames();
	
	private final static String[] loadNames() {
		ArrayList<String> namelist = new ArrayList<String>();
		Scanner sc = new Scanner(Names.class.getResourceAsStream("ayear.txt"));
		while (sc.hasNext()) {
			namelist.add(sc.nextLine());
		}
		sc.close();
		return namelist.toArray(new String[0]);
	}
}
