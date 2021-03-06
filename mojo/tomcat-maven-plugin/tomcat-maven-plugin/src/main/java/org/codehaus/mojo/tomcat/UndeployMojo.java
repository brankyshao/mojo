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
 * Undeploy a WAR from Tomcat.
 * 
 * @goal undeploy
 * @author Mark Hobson <markhobson@gmail.com>
 * @version $Id$
 */
public class UndeployMojo
    extends AbstractWarCatalinaMojo
{
    // ----------------------------------------------------------------------
    // Mojo Parameters
    // ----------------------------------------------------------------------

    /**
     * Whether to fail the build if the web application cannot be undeployed.
     * 
     * @parameter expression = "${maven.tomcat.failOnError}" default-value = "true"
     */
    private boolean failOnError;

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
        getLog().info( getMessage( "UndeployMojo.undeployingApp", getDeployedURL() ) );

        try
        {
            log( getManager().undeploy( getPath() ) );
        }
        catch ( TomcatManagerException exception )
        {
            if ( failOnError )
            {
                throw exception;
            }

            getLog().warn( getMessage( "UndeployMojo.undeployError", exception.getMessage() ) );
        }
    }
}
