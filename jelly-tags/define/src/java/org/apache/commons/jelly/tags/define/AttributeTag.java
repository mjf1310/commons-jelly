/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/define/src/java/org/apache/commons/jelly/tags/define/AttributeTag.java,v 1.2 2003/01/26 00:07:23 morgand Exp $
 * $Revision: 1.2 $
 * $Date: 2003/01/26 00:07:23 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 * 
 * $Id: AttributeTag.java,v 1.2 2003/01/26 00:07:23 morgand Exp $
 */
package org.apache.commons.jelly.tags.define;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.impl.Attribute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * This tag is bound onto a Java Bean class. When the tag is invoked a bean will be created
 * using the tags attributes. 
 * The bean may also have an invoke method called invoke(), run(), execute() or some such method
 * which will be invoked after the bean has been configured.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Revision: 1.2 $
 */
public class AttributeTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(AttributeTag.class);

    /** the attribute definition */
    private Attribute attribute;    
        
    public AttributeTag() {
        attribute = new Attribute();
    }

    public AttributeTag(Attribute attribute) {
        this.attribute = attribute;
    }

    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws JellyTagException {
        BeanTag tag = (BeanTag) findAncestorWithClass( BeanTag.class );
        if ( tag == null ) {
            throw new JellyTagException( "This tag should be nested inside a <define:bean> or <define:jellybean> tag" );
        }

        tag.addAttribute( attribute );        
    }
    
    // Properties
    //-------------------------------------------------------------------------                    
    
    /**
     * Sets the name of the attribute 
     */
    public void setName(String name) {
        attribute.setName(name);
    }
    
    /**
     * Sets whether this attribute is mandatory or not
     */
    public void setRequired(boolean required) {
        attribute.setRequired(required);
    }
    
    /**
     * Sets the default value of this attribute
     */
    public void setDefaultValue(Expression defaultValue) {
        attribute.setDefaultValue(defaultValue);
    }
}