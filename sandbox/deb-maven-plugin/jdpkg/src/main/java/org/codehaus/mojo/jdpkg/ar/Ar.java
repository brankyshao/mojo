package org.codehaus.mojo.deb.jdpkg.ar;

import static org.codehaus.mojo.deb.jdpkg.ar.ArUtil.close;
import static org.codehaus.mojo.deb.jdpkg.ar.ArUtil.readBytes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author <a href="mailto:trygve.laugstol@arktekk.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Ar {

    public static NewAr create() {
        return new NewAr();
    }

    public static CloseableIterable<ReadableArFile> read(File file) throws IOException {
        return new ArReader(file);
    }

    public static class NewAr {
        private Deque<ArFile> files = new ArrayDeque<ArFile>();

        public class NewArFile {
            ArFile file;

            public NewArFile withUid(int uid) {
                file.ownerId = uid;

                return this;
            }

            public NewArFile withGid(int gid) {
                file.groupId = gid;

                return this;
            }

            public NewAr done() {
                files.add(file);
                return NewAr.this;
            }
        }

        public NewAr addFileDone(File file) {
            files.add(ArFile.fromFile(file));

            return this;
        }

        public NewArFile addFile(File file) {
            NewArFile f = new NewArFile();
            f.file = ArFile.fromFile(file);
            return f;
        }

        public void storeToFile(File file) throws IOException {
            ArWriter writer = null;
            try {
                writer = new ArWriter(file);

                for (ArFile arFile : files) {
                    writer.add(arFile);
                }
            } finally {
                close(writer);
            }
        }
    }

    /*
    public static InputStream openFile(File arFile, String fileName) throws IOException {
        ArReader reader = null;
        try {
            reader = new ArReader(arFile);
            for (ArFile file : reader) {
                if (file.getName().equals(fileName)) {
                    return loadFile(file);
                }
            }

            throw new NoSuchFileInArchiveException();
        } finally {
            close(reader);
        }
    }
    */
}
