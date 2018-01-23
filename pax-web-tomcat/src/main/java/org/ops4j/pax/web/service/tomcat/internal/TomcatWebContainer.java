/*
 * TomcatWebContainer.java
 *
 * created at 31.05.2016 by utzig <j.utzig@seeburger.de>
 *
 * Copyright (c) SEEBURGER AG, Germany. All Rights Reserved.
 */
package org.ops4j.pax.web.service.tomcat.internal;

import java.net.URL;
import java.util.Dictionary;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.ops4j.pax.web.service.SharedWebContainerContext;
import org.ops4j.pax.web.service.WebContainer;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.NamespaceException;
import org.slf4j.LoggerFactory;

public class TomcatWebContainer implements WebContainer
{

    private StandardHost server;
    private BundleContext bundleContext;
    private Map<HttpContext,Context> contextMap = new ConcurrentHashMap<>();
    private TomcatServerWrapper controller;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(TomcatWebContainer.class);



    /**
     * @param server
     */
    public TomcatWebContainer(StandardHost server)
    {
        super();
        this.server = server;
        this.controller = new TomcatServerWrapper(server);
    }

    @Override
    public void registerServlet(String alias, Servlet servlet, Dictionary initparams, HttpContext context) throws ServletException, NamespaceException
    {
        registerServlet(servlet, alias, new String[]{}, initparams, 0, false, null, context);
    }

    @Override
    public void registerResources(String alias, String name, HttpContext context) throws NamespaceException
    {
        // TODO Auto-generated method stub


    }

    @Override
    public void unregister(String alias)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public HttpContext createDefaultHttpContext()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void registerServlet(String alias, Servlet servlet, Dictionary initParams, Integer loadOnStartup, Boolean asyncSupported, HttpContext httpContext) throws ServletException, NamespaceException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerServlet(Servlet servlet, String[] urlPatterns, Dictionary<String, ? > initParams, HttpContext httpContext) throws ServletException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerServlet(Servlet servlet, String[] urlPatterns, Dictionary<String, ? > initParams, Integer loadOnStartup, Boolean asyncSupported, HttpContext httpContext) throws ServletException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerServlet(Servlet servlet, String servletName, String[] urlPatterns, Dictionary<String, ? > initParams, HttpContext httpContext) throws ServletException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerServlet(Servlet servlet, String servletName, String[] urlPatterns, Dictionary<String, ? > initParams, Integer loadOnStartup, Boolean asyncSupported, HttpContext httpContext) throws ServletException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerServlet(Class< ? extends Servlet> servletClass, String[] urlPatterns, Dictionary<String, ? > initParams, HttpContext httpContext) throws ServletException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerServlet(Class< ? extends Servlet> servletClass, String[] urlPatterns, Dictionary<String, ? > initParams, Integer loadOnStartup, Boolean asyncSupported, HttpContext httpContext) throws ServletException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterServlet(Servlet servlet)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterServlets(Class< ? extends Servlet> servletClass)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerEventListener(EventListener listener, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterEventListener(EventListener listener)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerFilter(Filter filter, String[] urlPatterns, String[] servletNames, Dictionary<String, ? > initparams, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerFilter(Class< ? extends Filter> filterClass, String[] urlPatterns, String[] servletNames, Dictionary<String, String> initParams, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterFilter(Filter filter)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterFilter(Class< ? extends Filter> filterClass)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterFilter(String filterName)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setContextParam(Dictionary<String, ? > params, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSessionTimeout(Integer minutes, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerJsps(String[] urlPatterns, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerJsps(String[] urlPatterns, Dictionary<String, ? > initParams, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterJsps(HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterJsps(String[] urlPatterns, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerErrorPage(String error, String location, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterErrorPage(String error, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerWelcomeFiles(String[] welcomeFiles, boolean redirect, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterWelcomeFiles(String[] welcomeFiles, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerLoginConfig(String authMethod, String realmName, String formLoginPage, String formErrorPage, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterLoginConfig(HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerConstraintMapping(String constraintName, String mapping, String url, String dataConstraint, boolean authentication, List<String> roles, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterConstraintMapping(HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerServletContainerInitializer(ServletContainerInitializer servletContainerInitializer, Class< ? >[] classes, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public SharedWebContainerContext getDefaultSharedHttpContext()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void unregisterServletContainerInitializer(HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerJettyWebXml(URL jettyWebXmlURL, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setConnectorsAndVirtualHosts(List<String> connectors, List<String> virtualHosts, HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerJspServlet(String[] urlPatterns, HttpContext httpContext, String jspF)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerJspServlet(String[] urlPatterns, Dictionary<String, ? > dictionary, HttpContext httpContext, String jspF)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void begin(HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void end(HttpContext httpContext)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public SharedWebContainerContext createDefaultSharedHttpContext()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HttpContext createDefaultHttpContext(String contextID)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void registerServlet(Servlet servlet, String servletName, String[] urlPatterns, Dictionary<String, ? > initParams, Integer loadOnStartup, Boolean asyncSupported, MultipartConfigElement multiPartConfig, HttpContext httpContext) throws ServletException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerServlet(Class< ? extends Servlet> servletClass, String[] urlPatterns, Dictionary<String, ? > initParams, Integer loadOnStartup, Boolean asyncSupported, MultipartConfigElement multiPartConfig, HttpContext httpContext) throws ServletException
    {
        // TODO Auto-generated method stub

    }


    //util stuff

    private Context findOrCreateContext(HttpContext httpContext) {
        Context context = contextMap.get(httpContext);

        if (context == null) {
            context = createContext(httpContext);
        }
        return context;
    }

    private Context createContext(HttpContext httpContext) {
//        final Bundle bundle = contextModel.getBundle();
//        final BundleContext bundleContext = BundleUtils
//                .getBundleContext(bundle);

        StandardContext context = new StandardContext();

        server.addChild(context);
//        final Context context = server.addContext(
//                contextModel.getContextParams(),
//                getContextAttributes(bundleContext),
//                contextModel.getContextName(), contextModel.getHttpContext(),
//                contextModel.getAccessControllerContext(),
//                contextModel.getContainerInitializers(),
//                contextModel.getJettyWebXmlURL(),
//                contextModel.getVirtualHosts(), null /*contextModel.getConnectors() */,
//                server.getBasedir());

//        context.setParentClassLoader(contextModel.getClassLoader());
        context.setParentClassLoader(httpContext.getClass().getClassLoader());
        // TODO: is the context already configured?
        // TODO: how about security, classloader?
        // TODO: compare with JettyServerWrapper.addContext
        // TODO: what about the init parameters?

//        configureJspConfigDescriptor(context, contextModel);

//        final LifecycleState state = context.getState();
//        if (state != LifecycleState.STARTED && state != LifecycleState.STARTING
//                && state != LifecycleState.STARTING_PREP) {
//
//            LOG.debug("Registering ServletContext as service. ");
//            final Dictionary<String, String> properties = new Hashtable<String, String>();
//            properties.put("osgi.web.symbolicname", bundle.getSymbolicName());
//
//            final Dictionary<String, String> headers = bundle.getHeaders();
//            final String version = (String) headers
//                    .get(Constants.BUNDLE_VERSION);
//            if (version != null && version.length() > 0) {
//                properties.put("osgi.web.version", version);
//            }
//
//            String webContextPath = (String) headers.get(WEB_CONTEXT_PATH);
//            final String webappContext = (String) headers.get("Webapp-Context");
//
//            final ServletContext servletContext = context.getServletContext();
//
//            // This is the default context, but shouldn't it be called default?
//            // See PAXWEB-209
//            if ("/".equalsIgnoreCase(context.getPath())
//                    && (webContextPath == null || webappContext == null)) {
//                webContextPath = context.getPath();
//            }
//
//            // makes sure the servlet context contains a leading slash
//            webContextPath = webContextPath != null ? webContextPath
//                    : webappContext;
//            if (webContextPath != null && !webContextPath.startsWith("/")) {
//                webContextPath = "/" + webContextPath;
//            }
//
//            if (webContextPath == null) {
//                LOG.warn("osgi.web.contextpath couldn't be set, it's not configured");
//            }
//
//            properties.put("osgi.web.contextpath", webContextPath);
//
//            servletContextService = bundleContext.registerService(
//                    ServletContext.class, servletContext, properties);
//            LOG.debug("ServletContext registered as service. ");
//
//        }
        contextMap.put(httpContext, context);

        return context;
    }

    @Override
    public void setTrackingMode(String mode, HttpContext httpContext)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setSessionCookie(String sessionCookieName,
                                 Boolean sessionCookieHttpOnly,
                                 String sessionDomain,
                                 Boolean sessionCookieSecure,
                                 String sessionPath,
                                 HttpContext httpContext)
    {
        // TODO Auto-generated method stub
        
    }
}



