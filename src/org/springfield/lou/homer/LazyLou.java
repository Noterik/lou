/* 
* LazyLou.java
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

package org.springfield.lou.homer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.springfield.lou.application.remoteregister.RemoteServlet;

/**
 * LazyLou handles the remote controlm
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.homer
 *
 */
class LazyLou extends Thread 
{
	private static boolean running = false;
	
	private static int[][] buttons = {
		{2307, 661, 550, 660, 549, 660, 550, 660, 550, 660, 550, 660, 549}, //0, button1
		{2307, 661, 1139, 660, 550, 660, 550, 660, 549, 660, 550, 660, 550}, //1, button2
		{2307, 661, 550, 660, 1139, 660, 550, 660, 550, 660, 549, 660, 550}, //2, button3
		{2307, 661, 1139, 660, 1140, 660, 549, 663, 547, 663, 549, 660, 549}, //3,button4
		{2307, 661, 550, 660, 549, 660, 1140, 660, 550, 660, 549, 660, 550}, //4, button5
		{2307, 661, 1140, 660, 549, 660, 1140, 660, 550, 660, 549, 660, 550}, //5, button6
		{2307, 661, 550, 660, 1139, 660, 1140, 660, 549, 660, 550, 660, 550}, //6, button7
		{2307, 661, 1140, 660, 1139, 660, 1140, 660, 550, 660, 549, 660, 550}, //7, button8
		{2307, 661, 550, 660, 550, 660, 549, 660, 1140, 660, 550, 660, 549}, //8, button9
		{2307, 661, 1140, 660, 549, 660, 550, 660, 1140, 660, 549, 660, 550}, //9, button0
		{2299, 663, 545, 660, 545, 663, 1126, 666, 1128, 666, 542, 663, 549}, //10, red
		{2293, 669, 1140, 653, 553, 653, 1140, 653, 1140, 653, 553, 653, 547}, //11, green
		{2327, 635, 580, 626, 1140, 655, 1164, 626, 1166, 628, 577, 628, 580}, //12, yellow
		{2308, 658, 1142, 658, 552, 658, 1142, 658, 552, 658, 1142, 660, 550}, //13, power
		{2296, 667, 1133, 660, 1133, 658, 545, 660, 1134, 658, 547, 660, 1134}, //14, pause
		{2300, 661, 578, 628, 1164, 628, 580, 628, 1162, 628, 1162, 632, 1136}, //15, play
		{555, 661, 1139, 660, 1140, 660, 549, 660, 1140, 660, 550, 660}, //16, eject
		{2307, 661, 550, 660, 1139, 660, 550, 660, 550, 660, 1139, 660, 550},//17, volup
		{2307, 661, 1140, 660, 1139, 660, 550, 660, 550, 660, 1139, 660, 550},//18 voldown
		{2294, 669, 544, 663, 544, 660, 549, 658, 547, 660, 1133, 660, 547},//19, chanup
		{2291, 672, 1128, 663, 544, 663, 545, 663, 542, 663, 1129, 663, 544}//120, chandown
	};
	
	private static long lastSuccess = 0;
	
	public LazyLou() {
		if (!running) {
			running = true;
			start();
		}
	}
	
	public void run() {
    	System.out.println("There are " + buttons.length + " buttons mapped");
    	try {
    		DatagramSocket dsoc=new DatagramSocket(65432);
	        while (true) {
	            byte buff[]=new byte[1024];
	            DatagramPacket dpack=new DatagramPacket(buff,buff.length);
	            dsoc.receive(dpack);
	            String data =new String(dpack.getData());
	            //System.out.println("---- data="+data);
	            if(System.currentTimeMillis()-lastSuccess>300)decode(data);
	            //decode(data);
	            //System.out.println("------------------------------------------------");
	        }
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    public static void decode(String data) {
        StringTokenizer tok = new StringTokenizer(data);
        ArrayList<Integer> signalValues =  new ArrayList<Integer>();
        int buttonId;
        //System.out.print(tok.nextToken() + " - ");
        //System.out.print(tok.nextToken() + " ::");
        tok.nextToken();
        tok.nextToken();
        for(int i=2; i<tok.countTokens(); i++){
        	signalValues.add(Integer.parseInt(tok.nextToken(), 16));
        }
        
        if((buttonId=checkButton(signalValues))!=-1){
        	lastSuccess = System.currentTimeMillis();
        	String buttonClicked = null;
        	switch(buttonId){
	        	case 0:
	        		buttonClicked = "1";
	        		break;
	        	case 1:
	        		buttonClicked = "2";
	        		break;
	        	case 2:
	        		buttonClicked = "3";
	        		break;
	        	case 3:
	        		buttonClicked = "4";
	        		break;
	        	case 4:
	        		buttonClicked = "5";
	        		break;
	        	case 5:
	        		buttonClicked = "6";
	        		break;
	        	case 6:
	        		buttonClicked = "7";
	        		break;
	        	case 7:
	        		buttonClicked = "8";
	        		break;
	        	case 8:
	        		buttonClicked = "9";
	        		break;
	        	case 9:
	        		buttonClicked = "0";
	        		break;
	        	case 10:
	        		buttonClicked = "red";
	        		break;
	        	case 11:
	        		buttonClicked = "green";
	        		break;
	        	case 12:
	        		buttonClicked = "yellow";
	        		break;
	        	case 13:
	        		buttonClicked = "power";
	        		break;
	        	case 14:
	        		buttonClicked = "pause";
	        		break;
	        	case 15:
	        		buttonClicked = "play";
	        		break;
	        	case 16:
	        		buttonClicked = "eject";
	        		break;
	        	case 17:
	        		buttonClicked = "volumeup";
	        		break;
	        	case 18:
	        		buttonClicked = "volumedown";
	        		break;
	        	case 19:
	        		buttonClicked = "channelup";
	        		break;
	        	case 20:
	        		buttonClicked = "channeldown";
	        		break;
        	}
        	System.out.println(buttonClicked);
        	
        	String body = "<fsxml><message id=\"1\"><properties><msg>buttonClicked("+buttonClicked+")</msg></properties></message></fsxml>";
        	//TODO: the IP address should be configurable
        	RemoteServlet.handle(body,"buttonClicked("+buttonClicked+")","192.168.1.30");
        }
        
        for(int i=0;i<signalValues.size();i++) System.out.print(signalValues.get(i) + " ");
        System.out.println("");
    }
 
    public static int checkButton(ArrayList<Integer> signalValues){
    	boolean cont;
    	for(int i=0; i<buttons.length; i++){
    		cont=false;
	    	for(int j=0; j<buttons[i].length; j++){
	    		
	    		int minVal = (int) (buttons[i][j] - (buttons[i][j] * 0.1));
	    		int maxVal = (int) (buttons[i][j] + (buttons[i][j] * 0.1));
	    		
	    		try{
	    			if(signalValues.get(j)<minVal || signalValues.get(j)>maxVal){
	    				cont = true;
	    				break;
	    			}
	    		}catch(java.lang.IndexOutOfBoundsException e){
	    			cont = true;
	    			break;
	    		}
	    	}
	    	if(cont == true) continue;
	    	return i;
    	}
    	return -1;
    }
}