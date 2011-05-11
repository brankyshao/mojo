package org.codehaus.mojo.setup.scm.starteam.settings;

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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.scm.provider.starteam.util.StarteamUtil;
import org.apache.maven.scm.providers.starteam.settings.Settings;
import org.apache.maven.scm.providers.starteam.settings.io.xpp3.StarteamXpp3Reader;
import org.apache.maven.scm.providers.starteam.settings.io.xpp3.StarteamXpp3Writer;
import org.codehaus.mojo.setup.AbstractSetupManager;
import org.codehaus.mojo.setup.SetupMergeException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * @author Robert Scholte
 * @since 1.0.0
 * @plexus.component role="org.codehaus.mojo.setup.SetupManager" role-hint="starteam"
 */
public final class StarteamSetupManager
    extends AbstractSetupManager
{
    private StarteamXpp3Reader starteamReader = new StarteamXpp3Reader();

    private StarteamXpp3Writer starteamWriter = new StarteamXpp3Writer();

    @Override
    public File getTargetFile( MavenSession session )
    {
        return StarteamUtil.getSettingsFile();
    }

    @Override
    protected Reader merge( Reader dominantReader, Reader recessiveReader )
        throws SetupMergeException
    {
        Reader result = null;
        try
        {
            Settings dominantAndMergedSettings = starteamReader.read( dominantReader );
            Settings recessiceSettings = starteamReader.read( recessiveReader );

            SettingsUtils.merge( dominantAndMergedSettings, recessiceSettings );

            Writer writer = new StringWriter();
            starteamWriter.write( writer, dominantAndMergedSettings );
            result = new StringReader( writer.toString() );
        }
        catch ( IOException e )
        {
            throw new SetupMergeException( e.getMessage() );
        }
        catch ( XmlPullParserException e )
        {
            throw new SetupMergeException( e.getMessage() );
        }
        return result;
    }

    @Override
    protected String getPrototypeFilename()
    {
        return "starteam-settings.xml";
    }
}
