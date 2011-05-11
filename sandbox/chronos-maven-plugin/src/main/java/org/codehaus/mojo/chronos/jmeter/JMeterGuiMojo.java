/*
 * Chronos - Copyright (C) 2011 National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * All source code and information supplied as part of Chronos is
 * copyright to National Board of e-Health.
 *
 * The source code has been released under a dual license - meaning you can
 * use either licensed version of the library with your code.
 *
 * It is released under the LGPL (GNU Lesser General Public License), either
 * version 2.1 of the License, or (at your option) any later version. A copy
 * of which can be found at the link below.
 * http://www.gnu.org/copyleft/lesser.html
 *
 * $HeadURL$
 * $Id$
 */
package org.codehaus.mojo.chronos.jmeter;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Invokes the JMeter gui.<br />
 * The purpose is to create a testplan with the artifacts of the current project in the classpath. This is necessary if
 * the testplan should contain unittests or javaclases from the project.
 * 
 * @author ksr@lakeside.dk
 * @goal jmetergui
 * @execute phase="package"
 */
public class JMeterGuiMojo extends JMeterMojo {
    /**
     * The "current" maven project as executed by the package phase. We use this to find dependencies and paths.
     * 
     * @parameter expression="${executedProject}"
     */
    protected MavenProject executedproject;

    public void execute() throws MojoExecutionException {
        ensureJMeter();
        
        JavaCommand java = getJavaLauncher();
        java.addArgument("-jar");
        String jmeterJar = getJmeterJar().getAbsolutePath();
        java.addArgument(jmeterJar);
        executeJmeter(java);
    }

    protected void appendGcArgs(JavaCommand java) {
    }

    protected final MavenProject getProject() {
        return executedproject;
    }
}
