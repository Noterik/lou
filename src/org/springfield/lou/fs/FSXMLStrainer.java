package org.springfield.lou.fs;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * Takes a DOM4J XML Document and filters out any crap as defined in the filter
 * 
 * @author david_ammeraal
 * @version 0.1
 * @since 2014-04-03
 */

public class FSXMLStrainer {
	/**
	 * Document to be filtered.
	 */
	private Document doc;
	/**
	 * Filter to be applied on document. Check /example-filter.xml for more information.
	 */
	private Document filter;
	
	/**
	 * Constructor, takes a filter and instantiates the Strainer. 
	 * @param filter
	 * @throws IncorrectFilterException
	 */
	public FSXMLStrainer(Document filter) throws IncorrectFilterException{
		validateFilter(filter);
		this.filter = filter;
	}
	
	/**
	 * Validates the filter, checks if there arn't multiple <all || include || exclude> on a single level. 
	 * 
	 * @param filter
	 * @throws IncorrectFilterException
	 */
	private void validateFilter(Document filter) throws IncorrectFilterException{
		
		//Define which types to check for.
		//TODO The types should probably be converted to classes in case we want to add more in the future, but for now, this suffices. 
		List<String> inclusionTypes = new ArrayList<String>();
		inclusionTypes.add("include");
		inclusionTypes.add("exclude");
		inclusionTypes.add("all");
		
		//Get a list of all <include || exclude || all> in this filter.
		List<Node> nodesToCheck = filter.selectNodes("//*[self::include or self::exclude or self::all]");
		for(Iterator<Node> i = nodesToCheck.iterator(); i.hasNext();){
			Node node = i.next();
			String checkPath = "./*[";
			int s = 0;
			for(Iterator<String> c = inclusionTypes.iterator(); c.hasNext();){
				String type = c.next();
				if(!type.equals(node.getName())){
					if(s != 0){
						checkPath += " or ";
					}
					checkPath += "self::" + type;
					s++;
				}
				
				
			}
			checkPath += "]";
			
			//For each <include || exclude || all> check if there is another one active on this level, if that is the case the filter is incorrect and an exception should be thrown. 
			if(node.getParent().selectNodes(checkPath).size() > 0){
				throw new IncorrectFilterException("More than one inclusion type detected for this filter, this is not allowed!");
			}
		}
	}
	
	/**
	 * Starts parsing and filtering document from the root element.
	 */
	private void traverseDocument(){
		Element rootElement = doc.getRootElement();
		
		//Apply the filter on the first element, the function uses recursion to go through the entire tree. 
		executeFilterOnElement(rootElement);
		
	}
	
	/**
	 * Looks up the filter from the filter document for this element. Then detects what kind of filter it is and applies it. 
	 * @param el
	 */
	private void executeFilterOnElement(Element el){
		String path = getFilterPath(el);
		Element filter = getFilter(path);
		if(filter != null){
			if(filter.getName().equals("all")){
				handleAll(el, filter);
			}else if(filter.getName().equals("include")){
				if(filter.selectSingleNode("./property") != null){
					handleIncludeProperties(el, filter);
				}
				handleIncludeNodes(el, filter);
			}else if(filter.getName().equals("exclude")){
				if(filter.selectSingleNode("./property") != null){
					handleExcludeProperties(el, filter);
				}
				handleExcludeNodes(el, filter);
			}
		}
	}
	
	/**
	 * Doesn't filter anything out of the node on its own level. So just lets it pass through and starts the filtering process on the lower nodes. 
	 * @param el
	 * @param filter
	 */
	private void handleAll(Element el, Element filter){
		ArrayList<Element> childsToFilter = new ArrayList<Element>();
		
		List<Node> children = el.selectNodes("./*[not(self::properties)]");
		for(Iterator<Node> i = children.iterator(); i.hasNext();){
			Element child = (Element) i.next();
			childsToFilter.add(child);
		}
		
		for(Iterator<Element> i = childsToFilter.iterator(); i.hasNext();){
			Element child = i.next();
			executeFilterOnElement(child);
		}
	}
	
	/**
	 * Removes any properties that arn't defined in the <include /> as a <property />.
	 * @param el
	 * @param f
	 */
	private void handleIncludeProperties(Element el, Element f){
		List<Node> nodesToRemove = new ArrayList<Node>();
		
		Element properties = (Element) el.selectSingleNode("./properties");
		for(Iterator<Node> i = properties.elementIterator(); i.hasNext();){
			Element property = (Element) i.next();
			if(f.selectSingleNode("./property[@id=\"" + property.getName() + "\"]") == null){
				nodesToRemove.add(property);
			}
		}
		
		for(Iterator<Node> i = nodesToRemove.iterator(); i.hasNext();){
			properties.remove(i.next());
		}
	}
	
	/**
	 * Removes properties that are defined in the <exclude /> and keeps the rest.
	 * @param el
	 * @param f
	 */
	private void handleExcludeProperties(Element el, Element f){
		List<Node> nodesToRemove = new ArrayList<Node>();
		
		Element properties = (Element) el.selectSingleNode("./properties");
		for(Iterator<Node> i = properties.elementIterator(); i.hasNext();){
			Element property = (Element) i.next();
			if(f.selectSingleNode("./property[@id=\"" + property.getName() + "\"]") != null){
				nodesToRemove.add(property);
			}
		}
		
		for(Iterator<Node> i = nodesToRemove.iterator(); i.hasNext();){
			properties.remove(i.next());
		}
	}
	
	/**
	 * Removes any children that aren't defined in the <include />, starts the filtering process on lower node. 
	 * @param el
	 * @param f
	 */
	private void handleIncludeNodes(Element el, Element f){
		ArrayList<Node> nodesToRemove = new ArrayList<Node>();
		ArrayList<Element> childsToFilter = new ArrayList<Element>();
		
		List<Node> children = el.selectNodes("./*[not(self::properties)]");
		for(Iterator<Node> i = children.iterator(); i.hasNext();){
			Element child = (Element) i.next();
			if(f.selectSingleNode("./node[@id=\"" + child.getName() + "\"]") != null){
				childsToFilter.add(child);
			}else{
				nodesToRemove.add(child);
			}
		}
		
		for(Iterator<Node> i = nodesToRemove.iterator(); i.hasNext();){
			el.remove(i.next());
		}
		
		for(Iterator<Element> i = childsToFilter.iterator(); i.hasNext();){
			Element child = i.next();
			executeFilterOnElement(child);
		}
	}
	
	/**
	 * Removes any children that are defined in the <exclude />, leave everything else alone. Starts the filtering process for lower nodes. 
	 * @param el
	 * @param f
	 */
	private void handleExcludeNodes(Element el, Element f){
		ArrayList<Node> nodesToRemove = new ArrayList<Node>();
		
		List<Node> children = el.selectNodes("/*[not(self::properties)]");
		for(Iterator<Node> i = children.iterator(); i.hasNext();){
			Element child = (Element) i.next();
			if(f.selectSingleNode("./node[@id=\"" + child.getName() + "\"]") != null){
				nodesToRemove.add(child);
			}
		}
		
		for(Iterator<Node> i = nodesToRemove.iterator(); i.hasNext();){
			el.remove(i.next());
		}
	}
	
	/**
	 * Gets the path for the filter node for a given element. 
	 * @param el
	 * @return The path to the filter
	 */
	private String getFilterPath(Element el){	
		String filterPath = "";
		if(el.getParent() == null){
			filterPath += "/filter/*[self::include or self::exclude or self::all]";
		}else{
			String parentPath = getFilterPath(el.getParent());
			filterPath += parentPath + "/node[@id=\"" + el.getName() + "\"]/*[self::include or self::exclude or self::all]";
		}
		return filterPath;
	}
	
	/**
	 * Returns the filter element from the filter document for a given element. 
	 * @param path
	 * @return the filter element for a given element
	 */
	public Element getFilter(String path){
		return (Element) filter.selectSingleNode(path);
	}
	
	/**
	 * Gets the filtered readable xml for the given document.
	 * @param doc The document to be filtered.
	 * @return filtered readable xml for the given document. 
	 */
	public Document getFilteredFSXML(Document doc){
		this.doc = doc;
		traverseDocument();
		
		OutputFormat format = OutputFormat.createPrettyPrint();
		StringWriter sw = new StringWriter();
		XMLWriter writer;
		try {
			writer = new XMLWriter(sw, format );
			writer.write(doc);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Document newDoc;
		try {
			newDoc = DocumentHelper.parseText(sw.toString());
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		this.doc = null;
		return newDoc;
	}
	
}
