package org.codehaus.mojo.tomcat;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;

/**
 * Lists JNDI resources in Tomcat.
 * 
 * @goal resources
 * @author Mark Hobson <markhobson@gmail.com>
 * @version $Id$
 */
public class ResourcesMojo
    extends AbstractCatalinaMojo
{
    // ----------------------------------------------------------------------
    // Mojo Parameters
    // ----------------------------------------------------------------------

    /**
     * The class name of the resources to list, or <code>null</code> for all.
     * 
     * @parameter expression = "${maven.tomcat.type}"
     */
    private String type;

    // ----------------------------------------------------------------------
    // Protected Methods
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    protected void invokeManager()
        throws MojoExecutionException, TomcatManagerException, IOException
    {
        if ( type == null )
        {
            getLog().info( getMessage( "ResourcesMojo.listAllResources", getURL() ) );
        }
        else
        {
            getLog().info( getMessage( "ResourcesMojo.listTypedResources", type, getURL() ) );
        }

        log( getManager().getResources( type ) );
    }
}
