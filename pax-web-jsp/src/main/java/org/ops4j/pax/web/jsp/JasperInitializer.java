/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.web.jsp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspFactory;

import org.apache.catalina.Globals;
import org.apache.catalina.deploy.jsp.FunctionInfo;
import org.apache.catalina.deploy.jsp.TagAttributeInfo;
import org.apache.catalina.deploy.jsp.TagFileInfo;
import org.apache.catalina.deploy.jsp.TagInfo;
import org.apache.catalina.deploy.jsp.TagLibraryInfo;
import org.apache.catalina.deploy.jsp.TagLibraryValidatorInfo;
import org.apache.catalina.deploy.jsp.TagVariableInfo;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.runtime.JspFactoryImpl;
import org.apache.jasper.security.SecurityClassLoad;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.descriptor.tld.TagFileXml;
import org.apache.tomcat.util.descriptor.tld.TagXml;
import org.apache.tomcat.util.descriptor.tld.TaglibXml;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;
import org.apache.tomcat.util.descriptor.tld.ValidatorXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Initializer for the Jasper JSP Engine.
 */
public class JasperInitializer implements ServletContainerInitializer {

	private static final String MSG = "org.apache.jasper.servlet.JasperInitializer";
	private static final Logger LOG = LoggerFactory.getLogger(JasperInitializer.class);

	/**
	 * Preload classes required at runtime by a JSP servlet so that
	 * we don't get a defineClassInPackage security exception.
	 */
	static {
		JspFactoryImpl factory = new JspFactoryImpl();
		SecurityClassLoad.securityClassLoad(factory.getClass().getClassLoader());
		if (System.getSecurityManager() != null) {
			String basePackage = "org.apache.jasper.";
			try {
				factory.getClass().getClassLoader().loadClass(basePackage +
						"runtime.JspFactoryImpl$PrivilegedGetPageContext");
				factory.getClass().getClassLoader().loadClass(basePackage +
						"runtime.JspFactoryImpl$PrivilegedReleasePageContext");
				factory.getClass().getClassLoader().loadClass(basePackage +
						"runtime.JspRuntimeLibrary");
				factory.getClass().getClassLoader().loadClass(basePackage +
						"runtime.ServletResponseWrapperInclude");
				factory.getClass().getClassLoader().loadClass(basePackage +
						"servlet.JspServletWrapper");
			} catch (ClassNotFoundException ex) {
				throw new IllegalStateException(ex);
			}
		}

		if (JspFactory.getDefaultFactory() == null) {
			JspFactory.setDefaultFactory(factory);
		}
	}

	@Override
	public void onStartup(Set<Class<?>> types, ServletContext context) throws ServletException {
		if (LOG.isDebugEnabled()) {
			LOG.debug(Localizer.getMessage(MSG + ".onStartup", context.getServletContextName()));
		}

		// Setup a simple default Instance Manager
		if (context.getAttribute(InstanceManager.class.getName()) == null) {
			context.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
		}

		boolean validate = Boolean.parseBoolean(
				context.getInitParameter("org.apache.jasper.XML_VALIDATE_TLD"));
		String blockExternalString = context.getInitParameter(
		                "org.apache.jasper.XML_BLOCK_EXTERNAL");
		boolean blockExternal;
		if (blockExternalString == null) {
			blockExternal = true;
		} else {
			blockExternal = Boolean.parseBoolean(blockExternalString);
		}

		// scan the application for TLDs
		TldScanner scanner = newTldScanner(context, true, validate, blockExternal);
		try {
			scanner.scan();
		} catch (IOException | SAXException e) {
			throw new ServletException(e);
		}

		// add any listeners defined in TLDs
		for (String listener : scanner.getListeners()) {
			try {
				context.addListener(listener);
			} catch (RuntimeException e) {
				if (e.getCause() instanceof ClassNotFoundException) {
					LOG.error("Could not add listener from scanned TLD to context. " +
							"The referenced class could not be found (missing import): {}", e.getMessage());
				} else {
					throw e;
				}
			}
		}
		HashMap<String, TagLibraryInfo> jspTagLibraries = new HashMap<String, TagLibraryInfo>();
		
		Map<String,TldResourcePath> tlsResourceMap = scanner.getUriTldResourcePathMap();
		Map<TldResourcePath, TaglibXml> tldResourcePathTaglibXmlMap = scanner.getTldResourcePathTaglibXmlMap();
		
		Set<String> urls = tlsResourceMap.keySet();
		for (Iterator<String> iterator = urls.iterator(); iterator.hasNext();)
        {
            String url = iterator.next();
            TldResourcePath path = tlsResourceMap.get(url);
            TaglibXml taglibXml = tldResourcePathTaglibXmlMap.get(path);

            TagLibraryInfo tagLibraryInfo = new TagLibraryInfo();
            tagLibraryInfo.setPath(path.getUrl().toString());
            tagLibraryInfo.setJspversion(taglibXml.getJspVersion());
            tagLibraryInfo.setInfo(taglibXml.getInfo());
            tagLibraryInfo.setTlibversion(taglibXml.getTlibVersion());
            tagLibraryInfo.setShortname(taglibXml.getShortName());
            tagLibraryInfo.setVersion(taglibXml.getJspVersion());

            TagLibraryValidatorInfo tagLibraryValidatorInfo = new TagLibraryValidatorInfo();
            ValidatorXml validatorXml = taglibXml.getValidator();
            if(validatorXml!=null)
            {
                if(validatorXml.getValidatorClass()!=null)
                {
                    tagLibraryValidatorInfo.setValidatorClass(validatorXml.getValidatorClass());
                }
                if(validatorXml.getInitParams()!=null)
                {
                    tagLibraryValidatorInfo.getInitParams().putAll(validatorXml.getInitParams());
                }
                tagLibraryInfo.setValidator(tagLibraryValidatorInfo);
            }
            //add tags
            List<TagXml> tags = taglibXml.getTags();
            if(tags!=null)
            {
                for (Iterator<TagXml> iterator2 = tags.iterator(); iterator2.hasNext();)
                {
                    TagXml tagXml = iterator2.next();
                    TagInfo tInfo = new TagInfo();
                    tInfo.setBodyContent(tagXml.getBodyContent());
                    tInfo.setDisplayName(tagXml.getDisplayName());
                    tInfo.setInfoString(tagXml.getInfo());

                    tInfo.setLargeIcon(tagXml.getLargeIcon());
                    tInfo.setSmallIcon(tagXml.getSmallIcon());
                    tInfo.setTagClassName(tagXml.getTagClass());
                    tInfo.setTagName(tagXml.getName());
                    List<javax.servlet.jsp.tagext.TagAttributeInfo> tai = tagXml.getAttributes();
                    if(tai!=null)
                    {
                        for (Iterator<javax.servlet.jsp.tagext.TagAttributeInfo> iterator3 = tai.iterator(); iterator3.hasNext();)
                        {
                            javax.servlet.jsp.tagext.TagAttributeInfo tagAttributeInfo = iterator3.next();
                            tInfo.addTagAttributeInfo(createAttributeInfo(tagAttributeInfo));
                        }
                    }

                    List<javax.servlet.jsp.tagext.TagVariableInfo> tvi = tagXml.getVariables();
                    if(tvi!=null)
                    {
                        for (Iterator<javax.servlet.jsp.tagext.TagVariableInfo> iterator4 = tvi.iterator(); iterator4.hasNext();)
                        {
                            javax.servlet.jsp.tagext.TagVariableInfo tagVariableInfo = iterator4.next();
                            tInfo.addTagVariableInfo(createVariableInfo(tagVariableInfo));
                        }
                    }
                    if(tagXml.hasDynamicAttributes())
                    {
                        tInfo.setDynamicAttributes("true");
                    }
                    else
                    {
                        tInfo.setDynamicAttributes("false");
                    }

                    tagLibraryInfo.addTagInfo(tInfo);
                }
            }
            //add tag files
            List<TagFileXml> tagFiles = taglibXml.getTagFiles();
            if(tagFiles!=null)
            {
                for (Iterator<TagFileXml> iterator2 = tagFiles.iterator(); iterator2.hasNext();)
                {
                    TagFileXml tagFileXml = iterator2.next();
                    TagFileInfo tfi = new TagFileInfo();
                    tfi.setName(tagFileXml.getName());
                    tfi.setPath(tagFileXml.getPath());
                    tagLibraryInfo.addTagFileInfo(tfi);
                }
            }

            //add funtions
            List<javax.servlet.jsp.tagext.FunctionInfo> fi = taglibXml.getFunctions();
            if(fi!=null)
            {
                for (Iterator<javax.servlet.jsp.tagext.FunctionInfo> iterator3 = fi.iterator(); iterator3.hasNext();)
                {
                    javax.servlet.jsp.tagext.FunctionInfo functionInfo = iterator3.next();
                    tagLibraryInfo.addFunctionInfo(createFunctionInfo(functionInfo));
                }
            }

            //add listeners
            List<String> listeners = taglibXml.getListeners();
            if(listeners!=null)
            {
                for (Iterator<String> iterator4 = listeners.iterator(); iterator4.hasNext();)
                {
                    String listener = iterator4.next();
                    tagLibraryInfo.addListener(listener);
                }
            }

            jspTagLibraries.put(url,tagLibraryInfo);
        }

		context.setAttribute(Globals.JSP_TAG_LIBRARIES, jspTagLibraries);

		/*context.setAttribute(TldCache.SERVLET_CONTEXT_ATTRIBUTE_NAME,
				new TldCache(context, scanner.getUriTldResourcePathMap(),
						scanner.getTldResourcePathTaglibXmlMap()));*/

		// context.addServlet("jsp", JspServlet.class);
		// context.getServletRegistration("jsp").addMapping("*.jsp");
	}

	protected TldScanner newTldScanner(ServletContext context, boolean namespaceAware,
									   boolean validate, boolean blockExternal) {
		return new TldScanner(context, namespaceAware, validate, blockExternal);
	}
	
	protected TagAttributeInfo createAttributeInfo(javax.servlet.jsp.tagext.TagAttributeInfo tai)
	{
	    TagAttributeInfo result = new TagAttributeInfo();
	    String description = tai.getDescription();
	    String expectedTypeName = tai.getExpectedTypeName();
	    String methodSignature = tai.getMethodSignature();
	    String name = tai.getName();
	    String typeName = tai.getTypeName();

	    result.setDescription(description);
	    result.setExpectedTypeName(expectedTypeName);
	    result.setMethodSignature(methodSignature);
	    result.setName(name);
	    result.setType(typeName);

	    if(tai.canBeRequestTime())
	    {
	        result.setReqTime("true");
	    }
	    if(tai.isRequired())
	    {
	        result.setRequired("true");
	    }
	    if(tai.isDeferredMethod())
	    {
	        result.setDeferredMethod("true");
	    }
	    if(tai.isDeferredValue())
	    {
	        result.setDeferredValue("true");
	    }
	    if(tai.isFragment())
	    {
	        result.setFragment("true");
	    }

	    return result;
	}
	
    protected TagVariableInfo createVariableInfo(javax.servlet.jsp.tagext.TagVariableInfo tvi)
    {
        TagVariableInfo result = new TagVariableInfo();

        String className = tvi.getClassName();
        boolean declare = tvi.getDeclare();
        String nameFromAttr = tvi.getNameFromAttribute();
        String nameGiven = tvi.getNameGiven();
        int scope = tvi.getScope();

        result.setClassName(className);
        result.setDeclare(String.valueOf(declare));
        result.setNameFromAttribute(nameFromAttr);
        result.setNameGiven(nameGiven);
        result.setScope(String.valueOf(scope));

        return result;
    }
    
    protected FunctionInfo createFunctionInfo(javax.servlet.jsp.tagext.FunctionInfo fi)
    {
        FunctionInfo result = new FunctionInfo();
        String functionClass = fi.getFunctionClass();
        String functionSignature = fi.getFunctionSignature();
        String name = fi.getName();

        result.setFunctionClass(functionClass);
        result.setFunctionSignature(functionSignature);
        result.setName(name);
        return result;
    }
}
