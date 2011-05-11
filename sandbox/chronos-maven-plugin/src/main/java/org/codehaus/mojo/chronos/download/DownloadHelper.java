package org.codehaus.mojo.chronos.download;

import org.apache.maven.plugin.logging.Log;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadHelper {
    private static final int BUFFER = 2048;

    private String remoteLocation;
    private String destination;
    private final Log log;

    public DownloadHelper(String remoteLocation, String destination, Log log) {
        this.remoteLocation = remoteLocation;
        this.destination = destination;
        this.log = log;
    }

    public void downloadZipFile() throws IOException {
        log.info("Downloading " + remoteLocation + " storing it inside " + destination);
        URL u = new URL(remoteLocation);
        URLConnection uc = u.openConnection();
        int contentLength = uc.getContentLength();

        InputStream is = new BufferedInputStream(uc.getInputStream());
        byte[] inData = new byte[contentLength];
        int offset = 0;
        while (offset < contentLength) {
            final int bytesRead = is.read(inData, offset, inData.length - offset);
            if(bytesRead == -1)
                break;
            offset += bytesRead;
        }
        is.close();

        if(offset != contentLength) {
            throw new IOException("Only read " + offset + " bytes; Expected " + contentLength + " bytes");
        }

        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(inData)));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            writeOutputFile(zis, entry);
        }
        zis.close();
    }

    private void writeOutputFile(ZipInputStream zis, ZipEntry entry) throws IOException {
        String fileName = entry.getName();
        final int firstSeparator = fileName.indexOf(File.separatorChar);
        if (fileName.substring(0, firstSeparator).contains(new File(destination).getName())) {
            fileName = fileName.substring(firstSeparator);
        }
        final File outputFile = new File(destination, fileName);
        ensureDirExists(outputFile.getParentFile());
        if (entry.isDirectory()) {
            outputFile.mkdir();
        } else {
            log.debug("Saving file " + outputFile);
            final OutputStream fos = new BufferedOutputStream(new FileOutputStream(outputFile), BUFFER);
            try {
                int length;
                byte[] outData = new byte[BUFFER];
                while ((length = zis.read(outData, 0, BUFFER)) != -1) {
                    fos.write(outData, 0, length);
                }
                fos.flush();
            } finally {
                fos.close();
            }
        }
    }

    private void ensureDirExists(File dir) {
        if (!dir.exists()) {
            ensureDirExists(dir.getParentFile());
            dir.mkdir();
        }
    }
}