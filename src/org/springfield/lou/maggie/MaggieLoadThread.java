package org.springfield.lou.maggie;

import org.springfield.lou.fs.FSList;
import org.springfield.lou.fs.FSListManager;

public class MaggieLoadThread extends Thread{
	private MaggieLoader ml;
	private FSList fslist;
	private String url;
	private static Object obj;
	
	// trying to be multithreaded.
	public MaggieLoadThread(Object o,MaggieLoader m,String u,FSList f) {
		obj = o;
		ml = m;
		fslist = f;
		url = u;
		start();
	}
	
	public void run() {
		System.out.println("ADDING TO CACHE="+url);
		FSListManager.add(url,fslist);
		System.out.println("DONE ADDING TO CACHE="+url+" "+fslist.size());
       // synchronized (obj) {
        //    obj.notify();
       // }
	}
}
