/* 
* EuscreenxlpreviewApplication.java
* 
* Copyright (c) 2012 Noterik B.V.
* 
* This file is part of Lou, related to the Noterik Springfield project.
*
* Lou is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Lou is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Lou.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.springfield.lou.maggie;

import java.util.*;

import org.springfield.lou.application.components.types.OpenappsComponent;
import org.springfield.fs.*;

public class MaggieLoader extends Thread {
	
	private ArrayList<String> providers = new ArrayList<String>();
	private static Object obj = new Object();
	
	public MaggieLoader() {
		providers.add("nisv");
		providers.add("dw");
		providers.add("lcva");
		providers.add("rte");
		providers.add("tvc");
		providers.add("tvr");
		providers.add("ina");
		providers.add("nina");
		providers.add("orf");
		providers.add("sase");
		providers.add("kb");
		providers.add("nava");
		providers.add("ctv");
		providers.add("rtp");
		providers.add("henaa");
		providers.add("dr");
		providers.add("rtbf");
		providers.add("rai");
		providers.add("luce");
		providers.add("rtvs");
		providers.add("bbc");
		providers.add("vrt");
		providers.add("tvp");
		start();
	}
	
	public synchronized String getNextProvider() {
		if (providers.size()>0) {
			String provider = providers.get(0);
			providers.remove(0);
			System.out.println("PROVIDERS LEFT="+providers.size());
			return provider;
		}
		return null;
	}

	
	public void run() {
		FSList test = FSListManager.get("/domain/euscreenxl/user/*/*");
		if (test!=null) {
			System.out.println("PREVIEW CACHING IGNORED (ALLREADY IN CACHE THIS SHOULD NOT HAPPEN!");
			return;
		} else {
			System.out.println("PREVIEW CACHING");
			FSListManager.put("/domain/euscreenxl/user/*/*",new FSList());
		}
		long starttime = new Date().getTime(); // we track the request time for debugging only

    	FSList fslist = new FSList("/domain/euscreenxl/user/*/*");
		FSListManager.put("/domain/euscreenxl/user/*/*",fslist);
		for (int i=0;i<4;i++) { // start multiple threads
				new MaggieLoadThread(obj, this,"/domain/euscreenxl/user/eu_",fslist,i);
		}		
    			

	}

	


}
