/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/fmt/src/java/org/apache/commons/jelly/tags/fmt/FormatDateTag.java,v 1.1 2003/01/18 06:35:27 dion Exp $
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
 * $Id: FormatDateTag.java,v 1.1 2003/01/18 06:35:27 dion Exp $
 */
package org.apache.commons.jelly.tags.fmt;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.expression.Expression;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Support for tag handlers for &lt;formatDate&gt;, the date and time formatting
 * tag in JSTL.
 * @author <a href="mailto:willievu@yahoo.com">Willie Vu</a>
 * @version $Revision: 1.1 $
 * @task i18n exception message
 */
public class FormatDateTag extends TagSupport {
	
	private static final String DEFAULT = "default";
	private static final String SHORT = "short";
	private static final String MEDIUM = "medium";
	private static final String LONG = "long";
	private static final String FULL = "full";
	
	private static final String DATE = "date";
	private static final String TIME = "time";
	private static final String DATETIME = "both";
	
	/** Holds value of property value. */
	private Expression value;
	
	/** Holds value of property type. */
	private Expression type;
	
	/** Holds value of property dataStyle. */
	private Expression dateStyle;
	
	/** Holds value of property timeStyle. */
	private Expression timeStyle;
	
	/** Holds value of property pattern. */
	private Expression pattern;
	
	/** Holds value of property timeZone. */
	private Expression timeZone;
	
	/** Holds value of property var. */
	private String var;
	
	/** Holds value of property scope. */
	private String scope;
	
	/** Evaluated type */
	private String etype;
	/** Evaluated dateStyle */
	private String edateStyle;
	/** Evaluated timeStyle */
	private String etimeStyle;
	
	/** Creates a new instance of FormatDateTag */
	public FormatDateTag() {
	}
	
	/**
	 * Evaluates this tag after all the tags properties have been initialized.
	 *
	 */
	public void doTag(XMLOutput output) throws Exception {
		
		if (scope != null && var == null) {
			throw new JellyException(
			"If 'scope' is specified, 'var' must be defined for this tag" );
		}
		
		Object valueInput = null;
		if (this.value != null) {
			valueInput = this.value.evaluate(context);
		}
		
		Date date = null;
		if (valueInput != null && valueInput instanceof Date) {
			date = (Date) valueInput;
		}
		
		if (date == null && var != null) {
			if (scope != null) {
				context.removeVariable(var, scope);
			}
			else {
				context.removeVariable(var);
			}
		}
		
		etype = DATE;
		if (this.type != null) {
			etype = (String) this.type.evaluate(context);
		}
		
		edateStyle = DEFAULT;
		if (this.dateStyle != null) {
			edateStyle = (String) this.dateStyle.evaluate(context);
		}
		
		etimeStyle = DEFAULT;
		if (this.timeStyle != null) {
			etimeStyle = (String) this.timeStyle.evaluate(context);
		}
		
		String epattern = null;
		if (this.pattern != null) {
			epattern = (String) this.pattern.evaluate(context);
		}
		
		Object etimeZone = null;
		if (this.timeZone != null) {
			etimeZone = this.timeZone.evaluate(context);
		}

		// Create formatter
		Locale locale = SetLocaleTag.getFormattingLocale(
			context,
			this,
			true,
			DateFormat.getAvailableLocales());
		
		String formatted = null;
		if (locale != null) {
			DateFormat formatter = createFormatter(locale);
			
			// Apply pattern, if present
			if (pattern != null) {
				try {
					((SimpleDateFormat) formatter).applyPattern(epattern);
				} catch (ClassCastException cce) {
					formatter = new SimpleDateFormat(epattern, locale);
				}
			}
			
			// Set time zone
			TimeZone tz = null;
			if ((etimeZone instanceof String)
			&& ((String) etimeZone).equals("")) {
				etimeZone = null;
			}
			if (etimeZone != null) {
				if (etimeZone instanceof String) {
					tz = TimeZone.getTimeZone((String) etimeZone);
				} else if (etimeZone instanceof TimeZone) {
					tz = (TimeZone) etimeZone;
				} else {
					throw new JellyException("Bad time zone");
				}
			} else {
				tz = TimeZoneTag.getTimeZone(context, this);
			}
			if (tz != null) {
				formatter.setTimeZone(tz);
			}
			formatted = formatter.format(date);
		} else {
			// no formatting locale available, use Date.toString()
			formatted = date.toString();
		}
				
		if (var != null) {
			if (scope != null) {
				context.setVariable(var, scope, formatted);
			}
			else {
				context.setVariable(var, formatted);
			}
		}
		else {
			// write the formatted
			output.write(formatted);
		}
	}
	
	/** Setter for property value.
	 * @param value New value of property value.
	 *
	 */
	public void setValue(Expression value) {
		this.value = value;
	}
	
	/** Setter for property type.
	 * @param type New value of property type.
	 *
	 */
	public void setType(Expression type) {
		this.type = type;
	}
	
	/** Setter for property dataStyle.
	 * @param dataStyle New value of property dataStyle.
	 *
	 */
	public void setDateStyle(Expression dateStyle) {
		this.dateStyle = dateStyle;
	}
	
	/** Setter for property timeStyle.
	 * @param timeStyle New value of property timeStyle.
	 *
	 */
	public void setTimeStyle(Expression timeStyle) {
		this.timeStyle = timeStyle;
	}
	
	/** Setter for property pattern.
	 * @param pattern New value of property pattern.
	 *
	 */
	public void setPattern(Expression pattern) {
		this.pattern = pattern;
	}
	
	/** Setter for property timeZone.
	 * @param timeZone New value of property timeZone.
	 *
	 */
	public void setTimeZone(Expression timeZone) {
		this.timeZone = timeZone;
	}
	
	/** Setter for property var.
	 * @param var New value of property var.
	 *
	 */
	public void setVar(String var) {
		this.var = var;
	}
	
	/** Setter for property scope.
	 * @param scope New value of property scope.
	 *
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	//*********************************************************************
	// Private utility methods
	
	private DateFormat createFormatter(Locale loc) throws JellyException {
		DateFormat formatter = null;
		
		if ((etype == null) || DATE.equalsIgnoreCase(etype)) {
			formatter = DateFormat.getDateInstance(
			getStyle(edateStyle, "FORMAT_DATE_INVALID_DATE_STYLE"),
			loc);
		} else if (TIME.equalsIgnoreCase(etype)) {
			formatter = DateFormat.getTimeInstance(
			getStyle(etimeStyle, "FORMAT_DATE_INVALID_TIME_STYLE"),
			loc);
		} else if (DATETIME.equalsIgnoreCase(etype)) {
			formatter = DateFormat.getDateTimeInstance(
			getStyle(edateStyle, "FORMAT_DATE_INVALID_DATE_STYLE"),
			getStyle(etimeStyle, "FORMAT_DATE_INVALID_TIME_STYLE"),
			loc);
		} else {
			throw new JellyException("Date format invalue");
		}
		
		return formatter;
	}
	
	/*
	 * Converts the given string description of a formatting style for
	 * dates and times to the corresponding java.util.DateFormat constant.
	 *
	 * @param style String description of formatting style for dates and times
	 * @param errCode Error code to throw if given style is invalid
	 *
	 * @return java.util.DateFormat constant corresponding to given style
	 *
	 * @throws JellyException if the given style is invalid
	 */
	public static int getStyle(String style, String errCode)
	throws JellyException {
		int ret = DateFormat.DEFAULT;
		
		if (style != null) {
			if (DEFAULT.equalsIgnoreCase(style)) {
				ret = DateFormat.DEFAULT;
			} else if (SHORT.equalsIgnoreCase(style)) {
				ret = DateFormat.SHORT;
			} else if (MEDIUM.equalsIgnoreCase(style)) {
				ret = DateFormat.MEDIUM;
			} else if (LONG.equalsIgnoreCase(style)) {
				ret = DateFormat.LONG;
			} else if (FULL.equalsIgnoreCase(style)) {
				ret = DateFormat.FULL;
			} else {
				throw new JellyException("Invalid style " + errCode);
			}
		}
		
		return ret;
	}
	
}