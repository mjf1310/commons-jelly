/*
 * Copyright 2002,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly.tags.xml;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.xpath.XPathComparator;
import org.apache.commons.jelly.xpath.XPathTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jaxen.XPath;
import org.jaxen.JaxenException;

import java.util.List;
import java.util.Collections;

/** A tag which defines a variable from an XPath expression.
  * This function creates a variable of type {@link List} or {@link org.dom4j.Node}
  * (for example {@link org.dom4j.Element} or {@link org.dom4j.Attribute}).
  * Thus, the variable created from xml:set can be
  * used from the other xml library functions.
  * 
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.6 $
  */
public class SetTag extends XPathTagSupport {

    /** The Log to which logging calls will be made. */
    private Log log = LogFactory.getLog(SetTag.class);
    
    /** The variable name to export. */
    private String var;
    
    /** The XPath expression to evaluate. */    
    private XPath select;
    
    /** Xpath comparator for sorting */
    private XPathComparator xpCmp = null;
	
	private boolean single=false;

    public SetTag() {

    }
    
    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (var == null) {
            throw new MissingAttributeException( "var" );
        }
        if (select == null) {
            throw new MissingAttributeException( "select" );
        }
        
        Object xpathContext = getXPathContext();        
        Object value = null;
        try { 
            value = select.evaluate(xpathContext);
        } 
        catch (JaxenException e) {
            throw new JellyTagException(e);
        }

        if (value instanceof List) {
            // sort the list if xpCmp is set.
            if (xpCmp != null && (xpCmp.getXpath() != null)) {
                Collections.sort((List)value, xpCmp);
            }
        }
		if (single==true) {
			if(value instanceof List) {
				List l = (List) value;
				if (l.size()==0)
					value=null;
				else
					value=l.get(0);
			}
		}
		

        //log.info( "Evaluated xpath: " + select + " as: " + value + " of type: " + value.getClass().getName() );
        
        context.setVariable(var, value);
    }
    
    // Properties
    //-------------------------------------------------------------------------                
    
    /** Sets the variable name to define for this expression
     */
    public void setVar(String var) {
        this.var = var;
    }
    
    /** Sets the XPath expression to evaluate. */
    public void setSelect(XPath select) {
        this.select = select;
    }
	
	/** If set to true will only take the first element matching.
		It then guarantees that the result is of type
		{@link org.dom4j.Node} thereby making sure that, for example,
		when an element is selected, one can directly call such methods
		as setAttribute.
		*/
	public void setSingle(boolean single) {
		this.single = single;
	}


    /** Sets the xpath expression to use to sort selected nodes.
     */
    public void setSort(XPath sortXPath) throws JaxenException {
        if (xpCmp == null) xpCmp = new XPathComparator();
        xpCmp.setXpath(sortXPath);
    }

    /**
     * Set whether to sort ascending or descending.
     */
    public void setDescending(boolean descending) {
        if (xpCmp == null) xpCmp = new XPathComparator();
        xpCmp.setDescending(descending);
    }
}
