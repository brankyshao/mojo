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
package org.codehaus.mojo.chronos;

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.chronos.gc.GCSamples;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSamples;
import org.jdom.JDOMException;

/**
 * Checks the latest performancetests to verify that performance targets have been met.
 * 
 * @author ksr@lakeside.dk
 * @goal check
 * @phase verify
 */
public class CheckMojo extends AbstractMojo {
    private static final int DEFAULT_DURATION = 20000;

    /**
     * The maximum allowed ratio of time spent garbage collecting.
     * 
     * @parameter
     */
    protected double gctimeratio;

    /**
     * The maximum memory (in kb) garbagecollected pr second.
     * 
     * @parameter
     */
    protected double collectedprsecond;

    /**
     * The minimum required maximum throughput (in requests/sec).
     * 
     * @parameter
     */
    protected double maxthroughput;

    /**
     * The maximum acceptable average responsetime (in millis).
     * 
     * @parameter
     */
    protected double responsetimeaverage;

    /**
     * The maximum acceptable a95 percentage responsetime (in millis).
     * 
     * @parameter
     */
    protected double responsetime95;

    /**
     * The maximum acceptable responsetime (in millis).
     * 
     * @parameter
     */
    protected double responsetimemax;

    /**
     * responsetimeDivider may be used when the response time of a single request is so low that the granularity of the
     * system timer corrupts the response time measured.
     * 
     * @parameter default-value = 1
     */
    protected int responsetimedivider = 1;

    /**
     * The timeinterval to base moving average calculations on (in millis).
     * 
     * @parameter default-value = 20000
     */
    protected int averageduration = DEFAULT_DURATION; // 20 seconds

    /**
     * The id of the JMeter test
     * @parameter default-value="performancetest"
     */
    protected String dataid;

    /**
     * The current maven project.
     * 
     * @parameter expression="${project}"
     */
    private MavenProject project;
    
    /**
     * Determines if the build should fail if at least one of the samples failed.
     * 
     * @parameter default-value=false
     */
    protected boolean stopOnFailed;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            ResponsetimeSamples rtSamples = Utils.readResponsetimeSamples(project.getBasedir(), dataid);
            if(rtSamples == null) {
                throw new MojoExecutionException("Response time samples not found for " + dataid);
            }
            long totalTime = rtSamples.getTotalTime();
            validateMaxThroughput(rtSamples);
            validateFailedSamples(rtSamples);
            validateAverageResponsetime(rtSamples);
            validatePercentile95Responsetime(rtSamples);
            validateMaxResponsetime(rtSamples);
            GCSamples gcSamples = Utils.readGCSamples(project.getBasedir(), dataid);
            if(gcSamples != null) {
                validateGCTime(gcSamples, totalTime);
                validateCollectedPrSecond(gcSamples, totalTime);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failure", e);
        } catch (JDOMException e) {
            throw new MojoExecutionException("Failure", e);
        }
    }

    private void validateFailedSamples(ResponsetimeSamples samples) throws MojoExecutionException {
        if(stopOnFailed) {
            int failed = samples.getFailed();
            if(failed > 0) {
                throw new MojoExecutionException(failed + " JMeter sample(s) failed");
            }
        }
    }

    private void validateGCTime(GCSamples gcSamples, long totalTime) throws MojoExecutionException {
        if(gctimeratio <= 0) {
            return;
        }
        double actualRatio = gcSamples.getGarbageCollectionRatio(totalTime);
        if(actualRatio > gctimeratio) {
            throw new MojoExecutionException("To much time spent garbagecollection. Ratio of time spent was "
                    + actualRatio + " but acceptable level was " + gctimeratio);
        }
    }

    private void validateCollectedPrSecond(GCSamples samples, long totalTime) throws MojoExecutionException {
        if(collectedprsecond <= 0) {
            return;
        }
        double actual = samples.getCollectedKBPerSecond(totalTime);
        if(actual > collectedprsecond) {
            throw new MojoExecutionException("To much stuff garbagecollected. Garbagecollected pr second was " + actual
                    + "kb but acceptable level was " + collectedprsecond);
        }
    }

    private void validateMaxThroughput(ResponsetimeSamples samples) throws MojoExecutionException {
        if(maxthroughput <= 0) {
            return;
        }
        double actual = samples.getMaxAverageThroughput(averageduration, responsetimedivider);
        if(actual < maxthroughput) {
            throw new MojoExecutionException("Throughput too low. Throughput was " + actual
                    + " but required throughput was " + maxthroughput);
        }
    }

    private void validateAverageResponsetime(ResponsetimeSamples samples) throws MojoExecutionException {
        if(responsetimeaverage <= 0) {
            return;
        }
        double actual = samples.getAverage(responsetimedivider);
        if(actual > responsetimeaverage) {
            throw new MojoExecutionException("Average responsetime too high. Average was " + actual
                    + " but acceptable was " + responsetimeaverage);
        }
    }

    private void validatePercentile95Responsetime(ResponsetimeSamples samples) throws MojoExecutionException {
        if(responsetime95 <= 0) {
            return;
        }
        double actual = samples.getPercentile95(responsetimedivider);
        if(actual > responsetime95) {
            throw new MojoExecutionException("95 percentile responsetime too high. Measured was " + actual
                    + " but acceptable was " + responsetime95);
        }
    }

    private void validateMaxResponsetime(ResponsetimeSamples samples) throws MojoExecutionException {
        if(responsetimemax <= 0) {
            return;
        }
        double actual = samples.getMax(responsetimedivider);
        if(actual > responsetimemax) {
            throw new MojoExecutionException("Max responsetime too high. Measured was " + actual
                    + " but acceptable was " + responsetimemax);
        }
    }
}
