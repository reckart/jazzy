/* Created by bgalbs on Jan 30, 2003 at 11:38:39 PM */
package com.swabunga.spell.engine;

import java.util.List;
import java.util.ArrayList;
import java.io.*;

/**
 * An implementation of <code>SpellDictionary</code> that doesn't cache any words in memory. Avoids the huge
 * footprint of <code>SpellDictionaryHashMap</code> at the cost of relatively minor latency. A future version
 * of this class that implements some caching strategies might be a good idea in the future, if there's any
 * demand for it.
 * <p>
 * This class makes use of the "classic" Java IO library (java.io). However, it could probably benefit from
 * the new IO APIs (java.nio) and it is anticipated that a future version of this class, probably called
 * <code>SpellDictionaryDiskNIO</code> will appear at some point.
 *
 * @author Ben Galbraith (ben@galbraiths.org)
 * @version 0.1
 * @since 0.5
 */
public class SpellDictionaryDisk extends SpellDictionaryASpell implements SpellDictionary {
    private final static String DIRECTORY_WORDS = "words";
    private final static String DIRECTORY_DB = "db";
    private final static String FILE_CONTENTS = "contents";
    private final static String FILE_DB = "words.db";
    private final static String FILE_INDEX = "words.idx";

    private File base;
    private File words;
    private File db;

    /**
     *
     * @param base the base directory in which <code>SpellDictionaryDisk</code> can expect to find
     * its necessary files
     */
    public SpellDictionaryDisk(File base) throws FileNotFoundException, IOException {
        this.base = base;
        this.words = new File(base, DIRECTORY_WORDS);
        this.db = new File(base, DIRECTORY_DB);

        if (!this.base.exists()) throw new FileNotFoundException("Couldn't find required path '" + this.base + "'");
        if (!this.words.exists()) throw new FileNotFoundException("Couldn't find required path '" + this.words + "'");
        if (!this.db.exists()) db.mkdirs();

        if (newDictionaryFiles()) {
            buildNewDictionaryDatabase();
        }
    }

    private void buildNewDictionaryDatabase() {
        // not implemented yet
    }

    public void addWord(String word) {
    }

    public String getCode(String word) {
        return null;
    }

    public List getWords(String code) {
        return null;
    }

    public boolean isCorrect(String word) {
        return false;
    }

    private boolean newDictionaryFiles() throws FileNotFoundException, IOException {
        /* load in contents file, which indicates the files and sizes of the last db build */
        List contents = new ArrayList();
        File c = new File(db, FILE_CONTENTS);
        if (c.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(c));
                String line;
                while ((line = reader.readLine()) != null) {
                    // format of file should be [filename],[size]
                    String[] s = line.split(",");
                    contents.add(new FileSize(s[0], Integer.parseInt(s[1])));
                }
            } catch (FileNotFoundException e) {
                throw e;
            } catch (IOException e) {
                throw e;
            } finally {
                if (reader != null) reader.close();
            }
        }

        /* compare this to the actual directory */
        boolean changed = false;
        File[] wordFiles = words.listFiles();
        if (contents.size() != wordFiles.length) {
            // if the size of the contents list and the number of word files are different, it
            // means we've definitely got to reindex
            changed = true;
        } else {
            // check and make sure that all the word files haven't changed on us
            for (int i = 0; i < wordFiles.length; i++) {
                FileSize fs = new FileSize(wordFiles[i].getName(), wordFiles[i].length());
                if (!contents.contains(fs)) {
                    changed = true;
                    break;
                }
            }
        }

        return changed;
    }

    private class FileSize {
        private String filename;
        private long size;

        public FileSize(String filename, long size) {
            this.filename = filename;
            this.size = size;
        }

        public String getFilename() {
            return filename;
        }

        public long getSize() {
            return size;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FileSize)) return false;

            final FileSize fileSize = (FileSize) o;

            if (size != fileSize.size) return false;
            if (!filename.equals(fileSize.filename)) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = filename.hashCode();
            result = (int) (29 * result + size);
            return result;
        }
    }
}
