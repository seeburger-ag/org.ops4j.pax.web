/*
 * JBossWebBridge.java
 *
 * created at 11.04.2017 by Schwaninger <m.schwaninger@seeburger.de>
 *
 * Copyright (c) SEEBURGER AG, Germany. All Rights Reserved.
 */
package org.ops4j.pax.web.service.tomcat;

import org.apache.catalina.core.ContainerBase;
import org.ops4j.pax.web.service.spi.ServerControllerFactory;
import org.ops4j.pax.web.service.tomcat.internal.TomcatServerControllerFactory;
import org.ops4j.pax.web.service.tomcat.internal.TomcatServerFactory;
import org.ops4j.pax.web.service.tomcat.internal.TomcatServerStateFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class JBossWebBridge
{
    public void start(ContainerBase host, ContainerBase developmentService)
    {
        BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
        ServerControllerFactory factory = TomcatServerControllerFactory.newInstance(TomcatServerStateFactory.newInstance(new TomcatServerFactory(host,developmentService)));
        context.registerService(ServerControllerFactory.class, factory, null);
    }
}



