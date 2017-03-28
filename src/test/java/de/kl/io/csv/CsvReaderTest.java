package de.kl.io.csv;

import de.kl.io.csv.CsvReader.UserSettings;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author konrad
 */
public class CsvReaderTest
{

    @Test
    public void testSomeMethod() throws IOException
    {
        UserSettings settings = new CsvReader.UserSettings();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("head1\n\"value\"".getBytes());
        CsvReader instance = new CsvReader(inputStream, settings);
        for (Map<String, String> line : instance) {
            assertThat(line.get("head1"), is("value"));
            assertThat(line.size(), is(1));
            assertThat(instance.getCurrentLineNumber(), is(1L));
        }
    }

    @Test
    public void testSomeMethod1() throws IOException
    {
        UserSettings settings = new CsvReader.UserSettings();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("head1;head2\n\"value\";\"value2\"".getBytes());
        CsvReader instance = new CsvReader(inputStream, settings);
        for (Map<String, String> line : instance) {
            assertThat(line.get("head1"), is("value"));
            assertThat(line.get("head2"), is("value2"));
            assertThat(line.size(), is(2));
        }
        assertThat(instance.getCurrentLineNumber(), is(1L));
    }

    @Test
    public void testSomeMethod_emptyColumns() throws IOException
    {
        UserSettings settings = new CsvReader.UserSettings();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("head1;head2\n;".getBytes());
        CsvReader instance = new CsvReader(inputStream, settings);
        for (Map<String, String> line : instance) {
            assertThat(line.get("head1"), is(""));
            assertThat(line.get("head2"), is(""));
            assertThat(line.size(), is(2));
        }
        assertThat(instance.getCurrentLineNumber(), is(1L));
    }

    /**
     * qualifier is in the value
     *
     * @throws IOException
     */
    @Test
    public void testSomeMethod2() throws IOException
    {
        UserSettings settings = new CsvReader.UserSettings();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("head1\n\"val\"\"ue".getBytes());
        CsvReader instance = new CsvReader(inputStream, settings);
        for (Map<String, String> line : instance) {
            assertThat(line.get("head1"), is("val\"ue"));
            assertThat(line.size(), is(1));
        }
        assertThat(instance.getCurrentLineNumber(), is(1L));
    }

    /**
     * delimiter is in value
     *
     * @throws IOException
     */
    @Test
    public void testSomeMethod3() throws IOException
    {
        UserSettings settings = new CsvReader.UserSettings();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("head1\n\"val;ue\"".getBytes());
        CsvReader instance = new CsvReader(inputStream, settings);
        for (Map<String, String> line : instance) {
            assertThat(line.get("head1"), is("val;ue"));
            assertThat(line.size(), is(1));
        }
        assertThat(instance.getCurrentLineNumber(), is(1L));
    }

    /**
     * line break is in value
     *
     * @throws IOException
     */
    @Test
    public void testSomeMethod4() throws IOException
    {
        UserSettings settings = new CsvReader.UserSettings();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("head1\n\"val\nue\"".getBytes());
        CsvReader instance = new CsvReader(inputStream, settings);
        for (Map<String, String> line : instance) {
            assertThat(line.get("head1"), is("val\nue"));
            assertThat(line.size(), is(1));
        }
        assertThat(instance.getCurrentLineNumber(), is(1L));
    }

    /**
     * trims whitespaces
     *
     * @throws IOException
     */
    @Test
    public void testSomeMethod5() throws IOException
    {
        UserSettings settings = new CsvReader.UserSettings();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("head1\n\" value \"".getBytes());
        CsvReader instance = new CsvReader(inputStream, settings);
        for (Map<String, String> line : instance) {
            assertThat(line.get("head1"), is("value"));
            assertThat(line.size(), is(1));
        }
        assertThat(instance.getCurrentLineNumber(), is(1L));
    }

    /**
     * throws away additional junk at end of line.
     *
     * @throws IOException
     */
    @Test
    public void testSomeMethod6() throws IOException
    {
        UserSettings settings = new CsvReader.UserSettings();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("head1\n\"value\";\"junk\"\n\"value\"".getBytes());
        CsvReader instance = new CsvReader(inputStream, settings);
        int lineCounter = 1;
        for (Map<String, String> line : instance) {
            if (lineCounter == 1) {
                assertThat(line.get("head1"), is("value"));
                assertThat(line.size(), is(1));
                assertThat(instance.getCurrentLineNumber(), is(1L));
                lineCounter++;
            } else {
                assertThat(line.get("head1"), is("value"));
                assertThat(line.size(), is(1));
            }
        }
        assertThat(instance.getCurrentLineNumber(), is(2L));
    }

    /**
     * removes carriage return from last header
     *
     * @throws IOException
     */
    @Test
    public void testSomeMethod7() throws IOException
    {
        UserSettings settings = new CsvReader.UserSettings();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("head1\r\n\"value\"".getBytes());
        CsvReader instance = new CsvReader(inputStream, settings);
        for (Map<String, String> line : instance) {
            assertThat(line.get("head1"), is("value"));
            assertThat(line.size(), is(1));
        }
        assertThat(instance.getCurrentLineNumber(), is(1L));
    }

    /**
     * removes carriage return from last value
     *
     * @throws IOException
     */
    @Test
    public void testSomeMethod8() throws IOException
    {
        UserSettings settings = new CsvReader.UserSettings();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("head1\n\"value\"\r".getBytes());
        CsvReader instance = new CsvReader(inputStream, settings);
        for (Map<String, String> line : instance) {
            assertThat(line.get("head1"), is("value"));
            assertThat(line.size(), is(1));
        }
        assertThat(instance.getCurrentLineNumber(), is(1L));
    }

    /**
     * combined test for custom user settings.
     *
     * @throws IOException
     */
    @Test
    public void testSomeMethod_otherUserSettings() throws IOException
    {
        UserSettings settings = new CsvReader.UserSettings();
        settings.setColumnDelimiter(',');
        settings.setTextQualifier('\'');
        settings.setLineDelimiter('|');
        ByteArrayInputStream inputStream = new ByteArrayInputStream("head1,head2,head3|'val,ue1','val''ue2','val|ue3'".getBytes());
        CsvReader instance = new CsvReader(inputStream, settings);
        for (Map<String, String> line : instance) {
            assertThat(line.get("head1"), is("val,ue1"));
            assertThat(line.get("head2"), is("val'ue2"));
            assertThat(line.get("head3"), is("val|ue3"));
            assertThat(line.size(), is(3));
        }
        assertThat(instance.getCurrentLineNumber(), is(1L));
    }

    /**
     * skips empty line, if configured
     *
     * @throws IOException
     */
    @Test
    public void testSomeMethod9() throws IOException
    {
        UserSettings settings = new CsvReader.UserSettings();
        settings.setSkipEmptyLines(true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("head1\n\"\"\n\"value\"".getBytes());
        CsvReader instance = new CsvReader(inputStream, settings);
        for (Map<String, String> line : instance) {
            assertThat(line.get("head1"), is("value"));
            assertThat(line.size(), is(1));
            assertThat(instance.getCurrentLineNumber(), is(1L));
        }
        assertThat(instance.getCurrentLineNumber(), is(1L));
    }

    /**
     * don't skip empty lines, if not configured
     *
     * @throws IOException
     */
    @Test
    public void testSomeMethod10() throws IOException
    {
        UserSettings settings = new CsvReader.UserSettings();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("head1\n\"\"\n\"value\"".getBytes());
        CsvReader instance = new CsvReader(inputStream, settings);
        int lineCounter = 1;
        for (Map<String, String> line : instance) {
            if (lineCounter == 1) {
                assertThat(line.get("head1"), is(""));
                assertThat(line.size(), is(1));
                assertThat(instance.getCurrentLineNumber(), is(1L));
                lineCounter++;
            } else {
                assertThat(line.get("head1"), is("value"));
                assertThat(line.size(), is(1));
            }
        }
        assertThat(instance.getCurrentLineNumber(), is(2L));
    }

    /**
     * check if input is ok
     *
     * @throws IOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSomeMethod11() throws IOException
    {
        UserSettings settings = new CsvReader.UserSettings();
        CsvReader instance = new CsvReader(null, settings);
    }

    /**
     * check if input is ok
     *
     * @throws IOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSomeMethod12() throws IOException
    {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("d".getBytes());
        CsvReader instance = new CsvReader(inputStream, null);
    }

    /**
     * check if input is ok
     *
     * @throws IOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSomeMethod_inputStreamNotAvailable() throws IOException
    {
        UserSettings settings = new CsvReader.UserSettings();
        InputStream inputStream = mock(InputStream.class);
        when(inputStream.available()).thenReturn(0);
        CsvReader instance = new CsvReader(inputStream, settings);
    }

}
