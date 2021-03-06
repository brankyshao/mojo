package org.codehaus.mojo.deb.jdpkg.ar;

import junit.framework.TestCase;
import static org.codehaus.mojo.deb.jdpkg.ar.ArWriterTest.*;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;

/**
 * @author <a href="mailto:trygve.laugstol@arktekk.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ArTest extends TestCase {
    private File file1 = new File("file1");
    private String file1Contents = "Hello World";
    private File file2 = new File("file2");
    private String file2Contents = "I'm going to be skipped!";
    private File file3 = new File("file3");
    private String file3Contents = "Hello World, weee!"; // odd number of characters
    private File myAr = new File("my.ar");

    public ArTest() throws Exception {
        Writer writer = new OutputStreamWriter(new FileOutputStream(file1), "UTF-16");
        writer.write(file1Contents);
        writer.close();

        writer = new OutputStreamWriter(new FileOutputStream(file2), "UTF-16");
        writer.write(file2Contents);
        writer.close();

        writer = new OutputStreamWriter(new FileOutputStream(file3), "UTF-16");
        writer.write(file3Contents);
        writer.close();
    }

    public void testAr() throws Exception {
        writeFiles();

        Ar.create().
                addFile(file1).withUid(10).withGid(100).done().
                addFileDone(file2).
                addFileDone(file3).
                storeToFile(myAr);

        assertTrue(myAr.canRead());
        assertEquals(8 +
                60 + file1.length() +
                60 + file2.length(),
                60 + file3.length(),
                myAr.length());

        CloseableIterable<ReadableArFile> reader = Ar.read(myAr);
        Iterator<ReadableArFile> it = reader.iterator();

        ReadableArFile arFile;

        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());

        arFile = it.next();
        assertNotNull(arFile);
        assertEquals(file1.getName(), arFile.name);
        assertEquals(file1.lastModified() / 1000, arFile.lastModified);
        assertEquals(10, arFile.ownerId);
        assertEquals(100, arFile.groupId);
        assertEquals(420, arFile.mode);
        assertEquals(file1.length(), arFile.size);
        assertOpenable(file1, arFile, file1Contents);

        assertTrue(it.hasNext());
        assertTrue(it.hasNext());

        it.next();

        assertTrue(it.hasNext());
        assertTrue(it.hasNext());

        arFile = it.next();
        assertNotNull(arFile);
        assertEquals(file3.getName(), arFile.name);
        assertEquals(file3.lastModified() / 1000, arFile.lastModified);
        assertEquals(0, arFile.ownerId);
        assertEquals(0, arFile.groupId);
        assertEquals(420, arFile.mode);
        assertEquals(file3.length(), arFile.size);
        assertOpenable(file3, arFile, file3Contents);

        assertFalse(it.hasNext());
        assertFalse(it.hasNext());
        assertFalse(it.hasNext());
    }

    private void assertOpenable(File file, ReadableArFile arFile, String contents) throws IOException {
        InputStream is = arFile.open();
        byte[] bytes = IOUtil.toByteArray(is);

        assertEquals("File length vs bytes read.", file.length(), bytes.length);
        assertEquals(contents, new String(bytes, "UTF-16"));
        is.close();
        is.close();

        try {
            arFile.open();
            fail("Expected IOException when opening a ReadableArFile twice.");
        }
        catch (RuntimeException ex) {
            assertTrue(ex.getMessage().indexOf("already been read") > 0);
            // expected
        }
    }
}
