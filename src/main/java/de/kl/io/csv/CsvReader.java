package de.kl.io.csv;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A stream based parser for parsing delimited text data from a file or a stream.
 *
 * The CsvReader implements Iterable to make it as convinient as possible to loop over an csv file. A CsvReader can be
 * used exactly one time for a file. After reaching the end of the file, the reader should be closed. If the file has to
 * be read a second time, a new reader has to be created.
 */
public class CsvReader implements Closeable, Iterable<Map<String, String>>
{

    private final UserSettings userSettings;
    private final List<String> headers;
    private final ReadingStatus readingStatus;
    private final ReadingBuffer readingBuffer;

    /**
     * Constructs a {@link CsvReader} wiht all dependencies set
     *
     * @param inputStream The stream to use as the data source.
     * @param userSettings defines custom settings for the csv reading. Provide an blan (new ...()), if you want to use
     * the defaults.
     * @throws java.io.IOException
     */
    public CsvReader(InputStream inputStream, UserSettings userSettings) throws IOException
    {
        this.userSettings = userSettings;
        checkArgument(inputStream == null, "Parameter inputStream must not be null.");
        checkArgument(inputStream.available() == 0, "The given input stream is either closed or empty.");
        checkArgument(userSettings == null, "Parameter userSettings must not be null.");
        this.readingBuffer = new ReadingBuffer(inputStream);
        this.readingStatus = new ReadingStatus();
        this.headers = new ArrayList<>(this.readLine());
    }

    /**
     * Gets the number of the current line.
     *
     * @return The index of the current line.
     */
    public long getCurrentLineNumber()
    {
        return this.readingStatus.lineNumber - 1;
    }

    private Map<String, String> getLine() throws IOException
    {
        return IntStream.range(0, headers.size()).boxed().collect(Collectors.toMap(headers::get, this.readLine()::get));
    }

    /**
     * Reads another line .
     *
     * @return the list of columns of the current line
     * @exception IOException Thrown if an error occurs while reading data from the source stream.
     */
    private List<String> readLine() throws IOException
    {
        readingStatus.returnValue.clear();
        readingStatus.lineIsEnded = false;
        while (!readingStatus.lineIsEnded && !readingBuffer.isExhausted()) {
            if (this.readingStatus.startedColumn) {
                readInStartedColumn(readingBuffer.getCurrentChar());
            } else {
                startColumn(readingBuffer.getCurrentChar());
            }
        }
        if (readingBuffer.isExhausted()) {
            if (!readingStatus.lineIsEnded) {
                endLine();
            }
            readingStatus.hasMoreData = false;
        }
        return readingStatus.returnValue;
    }

    private void readInStartedColumn(int currentChar)
    {
        if (currentChar == userSettings.textQualifier) {
            if (!readingStatus.isEscaped) {
                readingStatus.isEscaped = true;
            } else {
                readingStatus.columnBuffer.append((char) currentChar);
                readingStatus.isEscaped = false;
            }
        } else if (currentChar == userSettings.columnDelimiter) {
            if (readingStatus.isEscaped || !readingStatus.isQualified) {
                endColumn();
            } else {
                this.readingStatus.columnBuffer.append((char) currentChar);
            }
        } else if (currentChar == userSettings.lineDelimiter) {
            if (readingStatus.isEscaped || !readingStatus.isQualified) {
                endLine();
                checkSkipLine();
            } else {
                readingStatus.columnBuffer.append((char) currentChar);
            }
        } else {
            readingStatus.columnBuffer.append((char) currentChar);
        }
    }

    private void startColumn(int currentChar)
    {
        if (currentChar == userSettings.columnDelimiter) {
            readingStatus.returnValue.add("");
        } else {
            readingStatus.startedColumn = true;
            if (currentChar == userSettings.textQualifier) {
                readingStatus.isQualified = true;
            } else {
                readingStatus.isQualified = false;
                readingStatus.columnBuffer.append((char) currentChar);
            }
        }
    }

    /**
     */
    private void endColumn()
    {
        // must be called before setting startedColumn = false
        String currentValue = readingStatus.columnBuffer.toString().trim();
        readingStatus.columnBuffer.setLength(0);
        readingStatus.startedColumn = false;
        readingStatus.isQualified = false;
        readingStatus.isEscaped = false;
        readingStatus.returnValue.add(currentValue);
    }

    /**
     * Closes the input stream
     */
    @Override
    public void close() throws IOException
    {
        this.readingBuffer.close();
    }

    private void endLine()
    {
        endColumn();
        readingStatus.lineNumber++;
        readingStatus.lineIsEnded = true;
    }

    private void checkSkipLine()
    {
        if (userSettings.skipEmptyLines
                && Collections.frequency(readingStatus.returnValue, "") == readingStatus.returnValue.size()) {
            readingStatus.returnValue.clear();
            readingStatus.lineNumber--;
            readingStatus.lineIsEnded = false;
        }
    }

    /**
     * Provides a iterator that can iterate over all lines of the csv file and returns a map from header to value for
     * each line.
     *
     * @return
     */
    @Override
    public Iterator<Map<String, String>> iterator()
    {
        return new Iterator<Map<String, String>>()
        {

            @Override
            public boolean hasNext()
            {
                return readingStatus.hasMoreData;
            }

            @Override
            public Map<String, String> next()
            {
                try {
                    return getLine();
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }
        };

    }

    /**
     * This class is necessary because the buffer must always read one char ahead of the current char because the end of
     * file must be detected before the iterator returns true on hasNext
     */
    private class ReadingBuffer implements Closeable
    {

        private final InputStream inputStream;

        private ReadingBuffer(InputStream inputStream) throws IOException
        {
            this.inputStream = inputStream;
            this.currentChar = inputStream.read();
        }

        private int currentChar;

        private boolean isExhausted()
        {
            return (currentChar == -1);
        }

        private int getCurrentChar() throws IOException
        {
            int returnValue = currentChar;
            this.currentChar = inputStream.read();
            return returnValue;
        }

        @Override
        public void close() throws IOException
        {
            inputStream.close();
        }
    }

    /**
     *
     */
    private class ReadingStatus
    {

        private boolean hasMoreData = true;
        private boolean startedColumn = false;
        private long lineNumber = 0;
        private final StringBuilder columnBuffer = new StringBuilder();
        boolean isQualified = false;
        boolean isEscaped = false;
        boolean lineIsEnded = false;
        List<String> returnValue = new ArrayList<>();

    }

    /**
     * This configuration class bundles all settings that can be customised be the user.
     */
    public static class UserSettings
    {

        private int textQualifier = '"';
        private int columnDelimiter = ';';
        private int lineDelimiter = '\n';
        private boolean skipEmptyLines = false;

        /**
         * Set the character that is used to qualify a cell.
         *
         * If a cell is qualified, the columnDelimiter and the lineDelimiter may be part of the cell value.
         *
         * @param textQualifier
         */
        public void setTextQualifier(int textQualifier)
        {
            this.textQualifier = textQualifier;
        }

        /**
         * Sets the delimiter between two columns.
         *
         * The default is ";"
         *
         * @param columnDelimiter
         */
        public void setColumnDelimiter(int columnDelimiter)
        {
            this.columnDelimiter = columnDelimiter;
        }

        /**
         * Sets the columnDelimiter between two lines.
         *
         * The default is '\n'
         *
         * @param lineDelimiter
         */
        public void setLineDelimiter(int lineDelimiter)
        {
            this.lineDelimiter = lineDelimiter;
        }

        /**
         * If true, empty csv lines are skipped
         *
         * @param skipEmptyLines
         */
        public void setSkipEmptyLines(boolean skipEmptyLines)
        {
            this.skipEmptyLines = skipEmptyLines;
        }
    }

    /**
     * Checks the given argument.
     *
     * @param condition must be true, if false, a IllegalArgumentexeption is thrown.
     * @param message The message if the exception, if the condition is not met.
     */
    private void checkArgument(boolean condition, String message)
    {
        if (condition) {
            throw new IllegalArgumentException(message);
        }
    }

}
