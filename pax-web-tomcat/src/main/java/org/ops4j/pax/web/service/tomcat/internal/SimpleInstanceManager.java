/*
 * SimpleInstanceManager.java
 *
 * created at 08.06.2016 by esterman d.esterman@seeburger.de
 *
 * Copyright (c) SEEBURGER AG, Germany. All Rights Reserved.
 */
package org.ops4j.pax.web.service.tomcat.internal;


import java.lang.reflect.InvocationTargetException;

import javax.naming.NamingException;

import org.apache.tomcat.InstanceManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;


class SimpleInstanceManager implements InstanceManager
{

    private Bundle hostBundle;

    public SimpleInstanceManager(Bundle hostBundle)
    {
        this.hostBundle = hostBundle;
    }

    @Override
    public void destroyInstance(Object o) throws IllegalAccessException, InvocationTargetException {}

    @Override
    public Object newInstance(Class< ? > clazz) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException
    {
        return clazz == null ? null : clazz.newInstance();
    }

    @Override
    public Object newInstance(String className) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException
    {
        ClassLoader loader = getClass().getClassLoader();
        if(hostBundle!=null)
        {
            BundleWiring wiring = hostBundle.adapt(BundleWiring.class);
            loader = wiring.getClassLoader();
        }
        return newInstance(className, loader);
    }

    @Override
    public void newInstance(Object o) throws IllegalAccessException, InvocationTargetException, NamingException
    {
        try
        {
            o.getClass().newInstance();
        }
        catch (InstantiationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public Object newInstance(String className, ClassLoader classLoader) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException
    {
        Class< ? > clazz = null;
        try
        {
            clazz = classLoader.loadClass(className);
        }
        catch (Exception cnfe)
        {
            //fallback to own classloader
            clazz = getClass().getClassLoader().loadClass(className);
        }
        return newInstance(clazz);
    }

}