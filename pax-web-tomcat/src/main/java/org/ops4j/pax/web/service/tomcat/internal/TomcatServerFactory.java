/*
 * Copyright 2012 Romain Gilles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.ops4j.pax.web.service.tomcat.internal;

import org.apache.catalina.core.ContainerBase;
import org.ops4j.pax.web.service.spi.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Romain Gilles
 */
public class TomcatServerFactory implements ServerFactory {
	private static final Logger LOG = LoggerFactory
			.getLogger(TomcatServerFactory.class);
    private ContainerBase server;
    private ContainerBase developmentService = null;

	public TomcatServerFactory(ContainerBase server) {
	    this.server = server;
	}

    public TomcatServerFactory(ContainerBase server, ContainerBase developmentService) {
        this.server = server;
        this.developmentService = developmentService;
    }
    
    @Override
    public ServerWrapper newServer(Configuration configuration)
    {
        return new TomcatServerWrapper(server,developmentService);
    }
}