/*
 * Copyright (C) 2008 Digital Sundhed (SDSD)
 *
 * All source code and information supplied as part of chronos
 * is copyright to its contributers.
 *
 * The source code has been released under a dual license - meaning you can
 * use either licensed version of the library with your code.
 *
 * It is released under the Common Public License 1.0, a copy of which can
 * be found at the link below.
 * http://www.opensource.org/licenses/cpl.php
 *
 * It is released under the LGPL (GNU Lesser General Public License), either
 * version 2.1 of the License, or (at your option) any later version. A copy
 * of which can be found at the link below.
 * http://www.gnu.org/copyleft/lesser.html
 */
package org.codehaus.mojo.chronos;

import org.codehaus.mojo.chronos.gc.GCSamples;
import org.codehaus.mojo.chronos.history.HistoricSample;
import org.codehaus.mojo.chronos.history.HistoricSamples;
import org.codehaus.mojo.chronos.responsetime.GroupedResponsetimeSamples;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jfree.data.time.Millisecond;

import java.io.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility class primarily for handling files.
 * 
 */
public class Utils {

    /** Prefix for GC files. */
    /* pp */static final String GC_FILE_PREFIX = "gc-";
    /** Extension for XML files. */
    /* pp */static final String XML_FILE_EXTENSION = ".xml";
    /** Extension for legacy serialized data files. */
    /* pp */static final String SERIALIZED_FILE_EXTENSION = ".ser";
    /** Prefix for history sample files. */
    /* pp */static final String HISTORYSAMPLE_FILE_PREFIX = "history-";
    /** Prefix for performance sample files. */
    /* pp */static final String PERFORMANCESAMPLE_FILE_PREFIX = "perf-";

    private static final int IGNORED_YEAR = 1970;

    /**
     * Converst the specified number of milli seconds into a <code>Millisecond</code> object.
     * 
     * @param millisecond
     *            The time represented in milli seconds.
     * @return The corresponding <code>Millisecond</code> instance.
     */
    public static Millisecond createMS(long millisecond) {
        return new Millisecond((int)millisecond, 0, 0, 0, 1, 1, IGNORED_YEAR);
    }

    /**
     * Retrieve the <code>ResourceBundle</code> for the specified <code>Locale</code>.
     * 
     * @param locale
     *            The <code>Locale</code>
     * @return The associated <code>ResourceBundle</code>.
     * @throws MissingResourceException
     *             If a <code>ResourceBundle</code> can not be found for the specified <code>Locale</code>.
     */
    public static final ResourceBundle getBundle(Locale locale) throws MissingResourceException {
        return ResourceBundle.getBundle("chronos", locale, Utils.class.getClassLoader());
    }

    /**
     * Retrieve the XML file containing the GC samples for the specified id.
     * 
     * @param baseDir
     *            The base directory of the build.
     * @param id
     *            The id of the JMeter test.
     * @return <code>File</code> pointing on the XML file.
     */
    public static File getGcSamplesXml(File baseDir, String id) {
        File chronosDir = getChronosDir(baseDir);
        return new File(new File(chronosDir, "gc"), GC_FILE_PREFIX + id + XML_FILE_EXTENSION);
    }

    /**
     * Read the saved <code>GSSamples</code> with the specified id.<br />
     * The method is backwards compatible, the method will look for previous .ser files - if found then the old files
     * will be loaded.<br />
     * 
     * @param baseDir
     *            The base directory of the build.
     * @param dataId
     *            The id of the JMeter test.
     * @return The corresponding <code>GCSamples</code> instance.
     * @throws IOException
     *             Thrown if loading the contents fails.
     * @throws JDOMException
     *             Thrown if parsing the contents fails.
     */
    public static GCSamples readGCSamples(File baseDir, String dataId) throws IOException, JDOMException {
        File gcSer = UtilsLegacy.getGcSamplesSer(baseDir, dataId);
        if(gcSer.exists()) {
            GCSamples gcSamples = (GCSamples)Utils.readObject(gcSer);
            return gcSamples;
        }

        GCSamples samples = new GCSamples();
        File gcDir = getGCSamplesDir(baseDir, dataId);
        File[] gcFiles = gcDir.listFiles();
        if(gcFiles != null) {
            Arrays.sort(gcFiles);
            for (int i = 0; i < gcFiles.length; i++) {
                if(gcFiles[i].isFile()) {
                    if(gcFiles[i].getName().startsWith(GC_FILE_PREFIX)) {
                        if(gcFiles[i].getName().endsWith(XML_FILE_EXTENSION)) {
                            GCSamples tmp = GCSamples.fromXML(gcFiles[i]);
                            samples.addAll(tmp);
                        }
                    }
                }
            }
        }
        return samples;
    }

    /**
     * Read the saved <code>HistoricSamples</code><br />
     * The method is backwards compatible, the method will look for previous .ser files - if found then the old files
     * will be loaded.<br />
     * 
     * @param dataDirectory
     *            The base directory of the build.
     * @return The corresponding <code>HistoricSamples</code> instance.
     * @throws IOException
     *             Thrown if loading the contents fails.
     * @throws JDOMException
     *             Thrown if parsing the contents fails.
     */
    public static HistoricSamples readHistorySamples(File dataDirectory) throws IOException, JDOMException {
        HistoricSamples samples = new HistoricSamples();

        File[] historyFiles = dataDirectory.listFiles();
        if(historyFiles != null) {
            Arrays.sort(historyFiles);
            for (int i = 0; i < historyFiles.length; i++) {
                if(historyFiles[i].isFile()) {
                    if(historyFiles[i].getName().startsWith(HISTORYSAMPLE_FILE_PREFIX)) {
                        HistoricSample sample;
                        if(historyFiles[i].getName().endsWith(SERIALIZED_FILE_EXTENSION)) {
                            sample = (HistoricSample)Utils.readObject(historyFiles[i]);
                            samples.addHistoricSample(sample);
                        } else if(historyFiles[i].getName().endsWith(XML_FILE_EXTENSION)) {
                            sample = HistoricSample.fromXML(historyFiles[i]);
                            samples.addHistoricSample(sample);
                        }
                    }
                }
            }
        }

        return samples;
    }

    /**
     * Writes the <code>GCSamples</code> to an XML file.<br />
     * Once the file has been persisted - any old .ser files are removed.
     * 
     * @param samples
     *            The <code>GCSamples</code> to store.
     * @param id
     *            The id of the JMeter test.
     * @return <code>File</code> pointing on the created XML file.
     * @throws IOException
     *             Thrown if writing data to the file fails.
     */
    public static File writeGCSamples(GCSamples samples, String id) throws IOException {
        File file = Utils.getGcSamplesXml(new File("."), id);
        writeXmlToFile(file, samples.toXML(), new DocType("gcsamples", "SYSTEM", "chronos-gc.dtd"));

        // Delete old serialized samples file if present.
        File gcSer = UtilsLegacy.getGcSamplesSer(new File("."), id);
        if(gcSer.exists()) {
            gcSer.delete();
        }

        return file;
    }

    /**
     * Writes the <code>HistoricSample</code> to an XML file.<br />
     * 
     * @param history
     *            The <code>GCSamples</code> to store.
     * @param dataDirectory
     *            The directory to store the history in.
     * @throws IOException
     *             Thrown if writing data to the file fails.
     */
    public static void writeHistorySample(HistoricSample history, File dataDirectory) throws IOException {
        String fileName = HISTORYSAMPLE_FILE_PREFIX + history.getTimestamp() + XML_FILE_EXTENSION;
        File historyFile = new File(dataDirectory, fileName);
        if(historyFile.exists()) {
            historyFile.delete();
        }

        writeXmlToFile(historyFile, history.toXML(), new DocType("historysamples", "SYSTEM", "chronos-history.dtd"));
    }

    /**
     * Writes the <code>GroupedResponsetimeSamples</code> to an XML file.<br />
     * Once the file has been persisted - any old .ser files are removed.
     * 
     * @param samples
     *            The <code>GroupedResponsetimeSamples</code> to store.
     * @param dataId
     *            The dataId of the JMeter test.
     * @return <code>File</code> pointing on the created XML file.
     * @throws IOException
     *             Thrown if writing data to the file fails.
     */
    public static File writeResponsetimeSamples(GroupedResponsetimeSamples samples, String dataId, String jtlName) throws IOException {
        File dataDir = getOrCreateDataDir(new File("."), dataId);
        File performanceSamplesXml = new File(dataDir, PERFORMANCESAMPLE_FILE_PREFIX + jtlName + XML_FILE_EXTENSION);
        final DocType docType = new DocType("responsetimesamples", "SYSTEM", "chronos-responsetimesamples.dtd");
        writeXmlToFile(performanceSamplesXml, samples.toXML(), docType);

        File psSer = UtilsLegacy.getPerformanceSamplesSer(new File("."), dataId);
        if(psSer.exists()) {
            psSer.delete();
        }
        return performanceSamplesXml;
    }

    /**
     * Read the saved <code>GroupedResponsetimeSamples</code> with the specified id.<br />
     * The method is backwards compatible, the method will look for previous .ser files - if found then the old files
     * will be loaded.<br />
     *
     * @param dataDirectory
     *            The base directory of the build.
     * @param dataId
     *            The id of the JMeter test.
     * @return The corresponding <code>GroupedResponsetimeSamples</code> instance.
     * @throws IOException
     *             Thrown if loading the contents fails.
     * @throws JDOMException
     *             Thrown if parsing the contents fails.
     */
    public static GroupedResponsetimeSamples readResponsetimeSamples(File dataDirectory, String dataId)
            throws IOException, JDOMException {
        File psSer = UtilsLegacy.getPerformanceSamplesSer(dataDirectory, dataId);
        if(psSer.exists()) {
            GroupedResponsetimeSamples samples = (GroupedResponsetimeSamples)Utils.readObject(psSer);
            return samples;
        }

        File chronosDir = getChronosDir(new File("."));
        File dir = new File(chronosDir, dataId);
        GroupedResponsetimeSamples result = new GroupedResponsetimeSamples();
        File[] dirContent = listFilesWithExtension(dir, "xml");
        for (int i = 0; i < dirContent.length; i++) {
            result.addAll(GroupedResponsetimeSamples.fromXmlFile(dirContent[i]));
        }
        return result;
    }

    private static File getGCSamplesDir(File baseDir, String dataId) {
        File chronosDir = getChronosDir(baseDir);
        return new File(chronosDir, "gc");
    }

    private static Serializable readObject(File ser) throws IOException {
        ObjectInputStream input = new ObjectInputStream(new FileInputStream(ser));
        try {
            return (Serializable)input.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            input.close();
        }
    }

    private static void writeXmlToFile(File file, Element xml, DocType doctype) throws IOException {
        Document doc = new Document(xml);
        doc.setDocType(doctype);
        ensureDir(file.getParentFile());

        if(doctype != null) {
            // Ensure DTD
            InputStream dtdIs = Utils.class.getResourceAsStream(doctype.getSystemID());
            File dtd = new File(file.getParentFile(), doctype.getSystemID());
            writeTextFile(dtd, dtdIs);
        }

        FileWriter writer = new FileWriter(file);
        new XMLOutputter(Format.getCompactFormat()).output(doc, writer);
        writer.flush();
        writer.close();
    }

    private static void writeTextFile(File file, InputStream is) throws IOException {
        OutputStream out = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.flush();
        out.close();
        is.close();
    }

    public static File getOrCreateDataDir(File baseDir, String dataId) {
        File chronosDir = getChronosDir(baseDir);
        File dataDir = new File(chronosDir, dataId);
        return ensureDir(dataDir);
    }

    /**
     * Based on the specied base directory - return the chronos directory of the running build.<br />
     * If the chronos directory does not exits - it will be created.
     *
     * @param baseDir
     *            The base directory of the build.
     * @return <code>File</code> pointing on the chronos directory.
     */
    public static File getChronosDir(File baseDir) {
        File target = new File(baseDir, "target");
        File chronos = new File(target, "chronos");
        return ensureDir(chronos);
    }

    public static File ensureDir(File directory) {
        if(!directory.exists()) {
            ensureDir(directory.getParentFile());
            directory.mkdir();
        }
        return directory;
    }

    public static String getAdjustedFileName(File source, String prefix, String extension) {
        return new StringBuffer().append(prefix).append(removeExtension(source.getName())).append('.').append(extension).toString();
    }

    public static String removeExtension(String fileName) {
        final int separatorIndex = fileName.indexOf('.');
        return fileName.substring(0, separatorIndex);
    }

    public static File[] listFilesWithExtension(File dir, final String extension) {
        File[] dirContent = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith("." + extension);
            }
        });
        return dirContent;
    }
}