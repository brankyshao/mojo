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
package org.codehaus.mojo.chronos.history;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.chronos.Utils;
import org.codehaus.mojo.chronos.gc.GCSamples;
import org.codehaus.mojo.chronos.responsetime.GroupedResponsetimeSamples;
import org.jdom.JDOMException;

import java.io.File;
import java.io.IOException;

/**
 * Save a snapshot of the currently executed test to enable later historic reports.
 * 
 * @goal savehistory
 * @phase post-integration-test
 */
public class SaveHistoryMojo extends AbstractMojo {
    /**
     * The current maven project.
     * 
     * @parameter expression="${project}"
     */
    protected MavenProject project;

    /**
     * The directory where historic data are stored.
     * 
     * @parameter expression="${basedir}/target/chronos/history"
     */
    private File historydir;

    /**
     * The id of the currently executed performancetest.
     * 
     * @parameter default-value="performancetest"
     */
    private String dataid;

    public void execute() throws MojoExecutionException, MojoFailureException {
        File dataDirectory = Utils.ensureDir(new File(historydir, dataid));

        try {
            GroupedResponsetimeSamples responseSamples = Utils.readResponsetimeSamples(project.getBasedir(), dataid);
            
            GCSamples gcSamples = Utils.readGCSamples(project.getBasedir(), dataid);
            HistoricSample history = new HistoricSample(responseSamples, gcSamples);
            Utils.writeHistorySample(history, dataDirectory);
        } catch (IOException ex) {
            throw new MojoExecutionException("unable to find gcsamples with dataid=" + dataid, ex);
        } catch (JDOMException ex) {
            throw new MojoExecutionException("unable to find gcsamples with dataid=" + dataid, ex);
        }

    }

    public void setDataid(String dataid) {
        this.dataid = dataid;
    }

    public void setHistorydir(File historydir) {
        this.historydir = historydir;
    }
}