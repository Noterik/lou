/* 
* ApplicationClassLoader.java
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

package org.springfield.lou.application;

import java.io.*;
import java.security.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.PropertyPermission;
import java.util.jar.*;

/**
 * Class loader to load jar packages
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application
 *
 */
//public class ApplicationClassLoader extends ClassLoader {
public class ApplicationClassLoader extends SecureClassLoader {

	
	private String jarFile;
	private Hashtable<String, Class<?>> classes = new Hashtable<String, Class<?>>(); 
	
	public ApplicationClassLoader() {
	}
	   
	public void setJarName(String appname,String id) { // e.g. 23-Aug-2013-22:12
		int pos = appname.indexOf("html5application/");
	    appname = appname.substring(pos+17);
	    jarFile = "/springfield/lou/apps/"+appname+"/"+id+"/jar/smt_"+appname+"app.jar";
	}
	    
	public Class<?> findClass(String className) {
		
		PermissionCollection perm = this.getPermissions(null);
		//System.out.println("PERM="+perm);
		
		Enumeration<Permission> pi = perm.elements();

		while(pi.hasMoreElements()){
			Permission p = pi.nextElement();
			System.out.println("PERMC="+p.getName());
		}
		
		
		int pos = className.indexOf("html5application/");
        if (pos!=-1) {     
        	String namepart = (""+className.charAt(pos+17)).toUpperCase();
        	namepart += className.substring(pos+18)+"Application";
        	className = "org.springfield.lou.application.types."+namepart;
        }
        	                
        //System.out.println("I NEED TO LOAD A CLASS FROM JAR !!! "+className);
        byte classByte[];  
        Class<?> result = null;  
      
        result = (Class<?>) classes.get(className); //checks in cached classes  
        if (result != null) {  
        	return result;  
        }  
      
        try {
          	ClassLoader wl = ApplicationManager.class.getClassLoader();
           	Class<?> r1 =  wl.loadClass(className);
           	//System.out.println("WC="+r1);
           	return r1;
        }  catch (Exception e) { }  
                
        try {  
        	Class<?> r2 = findSystemClass(className);  
            System.out.println("SC="+r2);
            return r2;
        } catch (Exception e) { }  
      
        try {  
        	//System.out.println("JAR FILE: " + jarFile);
        	JarFile jar = new JarFile(jarFile);  
            // System.out.println("JARFILE="+jar+" "+jarFile);
            String filename = className.replace('.','/');
            System.out.println("CLASSLOADED WANTS TO LOAD "+filename + ".class");
            JarEntry entry = jar.getJarEntry(filename + ".class");  
            // System.out.println("JARENTRY="+entry);
            InputStream is = jar.getInputStream(entry);  
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();  
            int nextValue = is.read();  
            while (-1 != nextValue) {  
            	byteStream.write(nextValue);  
            	nextValue = is.read();  
            }  
      
            classByte = byteStream.toByteArray(); 
           
            result = defineClass(className, classByte, 0, classByte.length);

            classes.put(className, result);  
            return result;  
        } catch (Exception e) {  
         	System.out.println("ApplicationClassLoader");
          	e.printStackTrace();
          	return null;  
        }  
	}
}
