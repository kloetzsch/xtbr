package de.kl.classifier.token;

import java.io.IOException;
import java.util.Collection;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 * This test is less a unit test but rather an integration test against the lucene api to see whether the lucene filtes
 * do the expected thing.
 *
 * @author konrad
 */
public class LuceneTokenizerTest
{

    private LuceneTokenizer instance = new LuceneTokenizer();

    @Test
    public void testTokenize() throws IOException
    {
        Collection<String> result = instance.tokenize("hallo");
        assertThat(result, hasItems("hallo"));
        result = instance.tokenize("hallo 5.6 reading here.");
        assertThat(result, hasItems("hallo", "5.6", "read", "here"));
        result = instance.tokenize("hallo i am is ");
        assertThat(result, hasItems("hallo", "i", "am"));
        assertThat(result, hasSize(3));
    }

}
