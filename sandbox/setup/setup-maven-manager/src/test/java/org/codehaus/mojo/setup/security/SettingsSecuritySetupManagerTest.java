package org.codehaus.mojo.setup.security;

import java.io.File;

import org.apache.maven.scm.util.FilenameUtils;
import org.codehaus.mojo.setup.AbstractSetupManager;
import org.codehaus.mojo.setup.AbstractSetupManagerTest;

public class SettingsSecuritySetupManagerTest
    extends AbstractSetupManagerTest
{

    @Override
    protected AbstractSetupManager getSetupManager()
        throws Exception
    {
        return (AbstractSetupManager) super.lookup( "org.codehaus.mojo.setup.SetupManager", "settings-security" );
    }

    public void testTargetFilePath()
        throws Exception
    {
        File targetFile = ( (SettingsSecuritySetupManager) getSetupManager() ).getTargetFile( null );
        
        assertTrue( FilenameUtils.normalizeFilename( targetFile.getAbsolutePath() ).endsWith( "/.m2/settings-security.xml" ) );
    }
}
