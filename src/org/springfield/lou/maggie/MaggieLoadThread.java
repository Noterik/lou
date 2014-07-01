package org.springfield.lou.maggie;

import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;

public class MaggieLoadThread extends Thread{
	private MaggieLoader ml;
	private FSList fslist;
	private String url;
	private static Object obj;
	private int tnumber;
	
	// trying to be multithreaded.
	public MaggieLoadThread(Object o,MaggieLoader m,String u,FSList f,int i) {
		obj = o;
		tnumber = i;
		ml = m;
		fslist = f;
		url = u;
		start();
	}
	
	public void run() {
		System.out.println("Maggie thread ("+tnumber+") started");
		String provider = ml.getNextProvider();
		while (provider!=null) {
			FSListManager.add(url+provider+"/video",fslist);
			System.out.println(tnumber+" DONE ADDING VIDEO TO CACHE="+url+provider+"/video "+fslist.size());
	
			FSListManager.add(url+provider+"/audio",fslist);
			System.out.println(tnumber+" DONE ADDING AUDIO TO CACHE="+url+provider+"/audio "+fslist.size());
			
			FSListManager.add(url+provider+"/series",fslist);
			System.out.println(tnumber+" DONE ADDING SERIES TO CACHE="+url+provider+"/series "+fslist.size());
			
			FSListManager.add(url+provider+"/picture",fslist);
			System.out.println(tnumber+" DONE ADDING PICTURE TO CACHE="+url+provider+"/picture "+fslist.size());
			
			FSListManager.add(url+provider+"/doc",fslist);
			System.out.println(tnumber+" DONE ADDING DOC TO CACHE="+url+provider+"/doc "+fslist.size());
	
			provider = ml.getNextProvider();
		}
		System.out.println("Maggie thread ("+tnumber+") done");

	}
}
