/* 
* FsFileReader.java
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

package org.springfield.lou.tools;

import java.io.BufferedReader;
import java.io.File;
import org.springfield.lou.application.Html5Application;
import org.springfield.lou.homer.LazyHomer;

/**
 * FsFileReader
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.tools
 */
public class FsFileReader {

	public static String getFileContent(Html5Application app, String ctype, String templatepath){
		String body;
		
		String basepath = "/springfield/tomcat/webapps/ROOT/eddie/";
		if (LazyHomer.isWindows()) basepath = "C:\\springfield\\tomcat\\webapps\\ROOT\\eddie\\";

		String filename = basepath+"domain"+File.separator+app.getDomain()+File.separator+"apps"+File.separator+app.getAppname()+File.separator+"components"+File.separator+templatepath;
		File file = new File(filename);
		if (!file.exists()) {
			// ok so not in the domain/app/component (step 1)
						
			filename = basepath+"domain"+File.separator+app.getDomain()+File.separator+"components"+File.separator+templatepath;
			file = new File(filename);
			if (!file.exists()) {
				// ok also not in domain/component

				filename = basepath+"apps"+File.separator+app.getAppname()+File.separator+"components"+File.separator+templatepath;
				file = new File(filename);
				if (!file.exists()) {
					// ok also not in app/component

					// so its in component
					filename = basepath+"components"+File.separator+templatepath;
				}
			}
		}
		
		try {
			BufferedReader br = new BufferedReader(new java.io.FileReader(filename));
			StringBuffer str = new StringBuffer();
			String line = br.readLine();
			while (line != null) {
				str.append(line);
				str.append("\n");
				line = br.readLine();
			 }
			br.close();
			body = str.toString();
		} catch (Exception e){
			System.out.println("File not found : "+filename);
			return null;
		}
		return body;
	}
}
