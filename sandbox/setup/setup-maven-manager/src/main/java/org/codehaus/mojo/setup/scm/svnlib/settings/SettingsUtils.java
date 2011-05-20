package org.codehaus.mojo.setup.scm.svnlib.settings;

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

import org.apache.maven.scm.providers.svn.settings.Settings;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author Robert Scholte
 * @since 1.0.0
 */
public class SettingsUtils
{
    
    //default values of primitive variables
    private static final boolean DEFAULT_TRUSTSERVERCERT = false;
    private static final boolean DEFAULT_USEAUTHCACHE = false;
    private static final boolean DEFAULT_USECYGWINPATH = false;
    private static final boolean DEFAULT_USENONINTERACTIVE = true;
    
    
    private static final String DEFAULT_CYGWINMOUNTPATH = "/cygwin";

    private SettingsUtils()
    {
        // don't allow construction.
    }

    /**
     * @param dominant
     * @param recessive
     */
    public static void merge( Settings dominant, Settings recessive )
    {
        if ( ( dominant == null ) || ( recessive == null ) )
        {
            return;
        }

        if ( StringUtils.isEmpty( dominant.getConfigDirectory() ) )
        {
            dominant.setConfigDirectory( recessive.getConfigDirectory() );
        }

        if ( dominant.isTrustServerCert() == DEFAULT_TRUSTSERVERCERT )
        {
            dominant.setTrustServerCert( recessive.isTrustServerCert() );
        }

        if ( dominant.isUseAuthCache() == DEFAULT_USEAUTHCACHE )
        {
            dominant.setUseAuthCache( recessive.isUseAuthCache() );
        }

        if ( dominant.isUseCygwinPath() == DEFAULT_USECYGWINPATH )
        { 
            dominant.setUseCygwinPath( recessive.isUseCygwinPath() );
        }

        if ( StringUtils.isEmpty( dominant.getCygwinMountPath() ) || 
                        DEFAULT_CYGWINMOUNTPATH.equals( dominant.getCygwinMountPath() ) )
        {
            dominant.setCygwinMountPath( recessive.getCygwinMountPath() );
        }

        if ( dominant.isUseNonInteractive() == DEFAULT_USENONINTERACTIVE )
        { 
            dominant.setUseNonInteractive( recessive.isUseNonInteractive() );
        }
        
    }
}
