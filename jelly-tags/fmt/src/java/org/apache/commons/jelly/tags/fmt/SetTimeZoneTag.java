/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/fmt/src/java/org/apache/commons/jelly/tags/fmt/SetTimeZoneTag.java,v 1.1 2003/01/18 06:35:27 dion Exp $
 * $Revision: 1.1 $
 * $Date: 2003/01/18 06:35:27 $
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
 * $Id: SetTimeZoneTag.java,v 1.1 2003/01/18 06:35:27 dion Exp $
 */
package org.apache.commons.jelly.tags.fmt;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.expression.Expression;
import java.util.TimeZone;

/**
 * Support for tag handlers for &lt;setTimeZone&gt;, the time zone setting
 * tag in JSTL.
 * @author <a href="mailto:willievu@yahoo.com">Willie Vu</a>
 * @version $Revision: 1.1 $
 *
 */
public class SetTimeZoneTag extends TagSupport {
		
	private Expression value;
	
	private String var;
	
	private String scope;
	
	/** Creates a new instance of SetLocaleTag */
	public SetTimeZoneTag() {
	}
	
	/**
	 * Evaluates this tag after all the tags properties have been initialized.
	 *
	 */
	public void doTag(XMLOutput output) throws Exception {
		TimeZone timeZone = null;
		
		Object valueInput = null;
		if (this.value != null) {
			valueInput = this.value.evaluate(context);
		}
		
		
		if (valueInput == null) {
			timeZone = TimeZone.getTimeZone("GMT");
		} 
		else if (valueInput instanceof String) {
			if (((String) valueInput).trim().equals("")) {
				timeZone = TimeZone.getTimeZone("GMT");
			} else {
				timeZone = TimeZone.getTimeZone((String) valueInput);
			}
		} else {
			timeZone = (TimeZone) valueInput;
		}
		
		if (scope != null) {
			context.setVariable(Config.FMT_TIME_ZONE, scope, timeZone);
		}
		else {
			context.setVariable(Config.FMT_TIME_ZONE, timeZone);
		}
	}
	
	public void setValue(Expression value) {
		this.value = value;
	}
	
	public void setVar(String var) {
		this.var = var;
	}
	
	public void setScope(String scope) {
		this.scope = scope;
	}
}
