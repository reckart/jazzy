/* Created by bgalbs on Jan 30, 2003 at 11:38:39 PM */
package com.swabunga.spell.engine;

import java.util.*;
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
public class SpellDictionaryDisk extends SpellDictionaryASpell{
    private final static String DIRECTORY_WORDS = "words";
    private final static String DIRECTORY_DB = "db";
    private final static String FILE_CONTENTS = "contents";
    private final static String FILE_DB = "words.db";
    private final static String FILE_INDEX = "words.idx";

    /* maximum number of words an index entry can represent */
    private final static int INDEX_SIZE_MAX = 200;

    private File base;
    private File words;
    private File db;
    private Map index;
    protected boolean ready;

    /* used at time of creation of index to speed up determining the number of words per index entry */
    private List indexCodeCache = null;

    /**
     * NOTE: Do *not* create two instances of this class pointing to the same <code>File</code> unless
     * you are sure that a new dictionary does not have to be created. In the future, some sort of
     * external locking mechanism may be created that handles this scenario gracefully.
     *
     * @param base the base directory in which <code>SpellDictionaryDisk</code> can expect to find
     * its necessary files
     * @param block if a new word db needs to be created, there can be a considerable delay before
     * the constructor returns. If block is true, this method will block while the db is created
     * and return when done. If block is false, this method will create a thread to create the new
     * dictionary and return immediately.
     */
    public SpellDictionaryDisk(File base, File phonetic, boolean block) throws FileNotFoundException, IOException {
        super(phonetic);
        this.ready = false;

        this.base = base;
        this.words = new File(base, DIRECTORY_WORDS);
        this.db = new File(base, DIRECTORY_DB);

        if (!this.base.exists()) throw new FileNotFoundException("Couldn't find required path '" + this.base + "'");
        if (!this.words.exists()) throw new FileNotFoundException("Couldn't find required path '" + this.words + "'");
        if (!this.db.exists()) db.mkdirs();

        if (newDictionaryFiles()) {
            if (block) {
                buildNewDictionaryDatabase();
                loadIndex();
                ready = true;
            } else {
                Thread t = new Thread() {
                    public void run() {
                        try {
                            buildNewDictionaryDatabase();
                            loadIndex();
                            ready = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
            }
        } else {
            loadIndex();
        }
    }

    protected void buildNewDictionaryDatabase() throws FileNotFoundException, IOException {
        /* combine all dictionary files into one sorted file */
        File sortedFile = buildSortedFile();

        /* create the db for the sorted file */
        buildCodeDb(sortedFile);
        sortedFile.delete();

        /* build contents file */
        buildContentsFile();
    }

    public void addWord(String word) {
        throw new UnsupportedOperationException("addWord not yet implemented (sorry)");
    }

    public List getWords(String code) {
        Vector words = new Vector();

        int[] posLen = getStartPosAndLen(code);
        if (posLen != null) {
            try {
                InputStream input = new FileInputStream(new File(db, FILE_DB));
                input.skip(posLen[0]);
                byte[] bytes = new byte[posLen[1]];
                input.read(bytes, 0, posLen[1]);
                input.close();

                String data = new String(bytes);
                String[] lines = split(data,"\n");
                for (int i = 0; i < lines.length; i++) {
                    String[] s = split(lines[i],",");
                    if (s[0].equals(code)) words.addElement(s[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return words;
    }

    /**
     * Note -- this implementation can be optimized, if needs be.
     *
     * @param word
     * @return true if the word is spelled correctly
     */
    public boolean isCorrect(String word) {
        List words = getWords(getCode(word));
        if (words.contains(word)) return true;
        return false;
    }

    public boolean isReady() {
        return ready;
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
                    String[] s = split(line,",");
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

    private File buildSortedFile() throws FileNotFoundException, IOException {
        List w = new ArrayList();

        /*
         * read every single word into the list. eeek. if this causes problems,
         * we may wish to explore disk-based sorting or more efficient memory-based storage
         */
        File[] wordFiles = words.listFiles();
        for (int i = 0; i < wordFiles.length; i++) {
            BufferedReader r = new BufferedReader(new FileReader(wordFiles[i]));
            String word;
            while ((word = r.readLine()) != null) {
                if (!word.equals("")) {
                    w.add(word.trim());
                }
            }
            r.close();
        }

        Collections.sort(w);

        // FIXME - error handling for running out of disk space would be nice.
        File file = File.createTempFile("jazzy", "sorted");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String prev = null;
        for (int i = 0; i < w.size(); i++) {
            String word = (String) w.get(i);
            if ((prev != null) && (!prev.equals(word))) {
                writer.write(word);
                writer.newLine();
            }
            prev = word;
        }
        writer.close();

        return file;
    }

    private void buildCodeDb(File sortedWords) throws FileNotFoundException, IOException {
        List codeList = new ArrayList();

        BufferedReader reader = new BufferedReader(new FileReader(sortedWords));
        String word;
        while ((word = reader.readLine()) != null) {
            codeList.add(new CodeWord(this.getCode(word), word));
        }
        reader.close();

        Collections.sort(codeList);

        List index = new ArrayList();

        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(db, FILE_DB)));
        String currentCode = null;
        int currentPosition = 0;
        int currentLength = 0;
        for (int i = 0; i < codeList.size(); i++) {
            CodeWord cw = (CodeWord) codeList.get(i);
            String thisCode = cw.getCode();
//            if (thisCode.length() > 3) thisCode = thisCode.substring(0, 3);
            thisCode = getIndexCode(thisCode, codeList);
            String toWrite = cw.getCode() + "," + cw.getWord() + "\n";
            byte[] bytes = toWrite.getBytes();

            if (currentCode == null) currentCode = thisCode;
            if (!currentCode.equals(thisCode)) {
                index.add(new Object[] { currentCode, new int[] { currentPosition, currentLength }});
                currentPosition += currentLength;
                currentLength = bytes.length;
                currentCode = thisCode;
            } else {
                currentLength += bytes.length;
            }
            out.write(bytes);
        }
        out.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(db, FILE_INDEX)));
        for (int i = 0; i < index.size(); i++) {
            Object[] o = (Object[]) index.get(i);
            writer.write(o[0].toString());
            writer.write(",");
            writer.write(String.valueOf(((int[]) o[1])[0]));
            writer.write(",");
            writer.write(String.valueOf(((int[]) o[1])[1]));
            writer.newLine();
        }
        writer.close();
    }

    private void buildContentsFile() throws IOException {
        File[] wordFiles = words.listFiles();
        if (wordFiles.length > 0) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(db, FILE_CONTENTS)));
            for (int i = 0; i < wordFiles.length; i++) {
                writer.write(wordFiles[i].getName());
                writer.write(",");
                writer.write(String.valueOf(wordFiles[i].length()));
                writer.newLine();
            }
            writer.close();
        } else {
            new File(db, FILE_CONTENTS).delete();
        }
    }

    protected void loadIndex() throws IOException {
        index = new HashMap();
        File idx = new File(db, FILE_INDEX);
        BufferedReader reader = new BufferedReader(new FileReader(idx));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] fields = split(line,",");
            index.put(fields[0], new int[] { Integer.parseInt(fields[1]), Integer.parseInt(fields[2]) });
        }
        reader.close();
    }

    private int[] getStartPosAndLen(String code) {
        while (code.length() > 0) {
            int[] posLen = (int[]) index.get(code);
            if (posLen == null) {
                code = code.substring(0, code.length() - 1);
            } else {
                return posLen;
            }
        }
        return null;
    }

    private String getIndexCode(String code, List codes) {
        if (indexCodeCache == null) indexCodeCache = new ArrayList();

        if (code.length() <= 1) return code;

        for (int i = 0; i < indexCodeCache.size(); i++) {
            String c = (String) indexCodeCache.get(i);
            if (code.startsWith(c)) return c;
        }

        int foundSize = -1;
        boolean cacheable = false;
        for (int z = 1; z < code.length(); z++) {
            String thisCode = code.substring(0, z);
            int count = 0;
            for (int i = 0; i < codes.size(); ) {
                if (i == 0) {
                    i = Collections.binarySearch(codes, new CodeWord(thisCode, ""));
                    if (i < 0) i = 0;
                }

                CodeWord cw = (CodeWord) codes.get(i);
                if (cw.getCode().startsWith(thisCode)) {
                    count++;
                    if (count > INDEX_SIZE_MAX) break;
                } else if (cw.getCode().compareTo(thisCode) > 0) break;
                i++;
            }
            if (count <= INDEX_SIZE_MAX) {
                cacheable = true;
                foundSize = z;
                break;
            }
        }

        String newCode = (foundSize == -1) ? code : code.substring(0, foundSize);
        if (cacheable) indexCodeCache.add(newCode);
        return newCode;
    }
    
    private static String[] split(String input, String delimiter){
      StringTokenizer st = new StringTokenizer(input,delimiter);
      int count = st.countTokens();
      String[] out = new String[count];
      
      for(int i = 0; i < count; i++){
        out[i] = st.nextToken();
      }
      
      return out;
    }

    private class CodeWord implements Comparable {
        private String code;
        private String word;

        public CodeWord(String code, String word) {
            this.code = code;
            this.word = word;
        }

        public String getCode() {
            return code;
        }

        public String getWord() {
            return word;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CodeWord)) return false;

            final CodeWord codeWord = (CodeWord) o;

            if (!word.equals(codeWord.word)) return false;

            return true;
        }

        public int hashCode() {
            return word.hashCode();
        }

        public int compareTo(Object o) {
            return code.compareTo(((CodeWord) o).getCode());
        }
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
