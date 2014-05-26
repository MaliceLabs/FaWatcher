/*
 *  FA Watcher - Mass-watch FurAffinity users
    Copyright (C) 2014  TheEqualizer

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
