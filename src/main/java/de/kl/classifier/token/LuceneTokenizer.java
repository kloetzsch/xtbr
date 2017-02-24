package de.kl.classifier.token;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;

/**
 * This tokenizer uses lucene to split the feature string into tokens, filers out english stop words and stems them.
 *
 * @author konrad
 */
public class LuceneTokenizer implements Tokenizer
{

    private static final Logger LOGGER = LoggerFactory.getLogger(LuceneTokenizer.class);
    private final StandardTokenizer tokenizer;
    private final CharTermAttribute attr;
    private final PorterStemFilter porterStemFilter;

    public LuceneTokenizer()
    {
        AttributeFactory factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;
        this.tokenizer = new StandardTokenizer(factory);
        StopFilter stopFilter = new StopFilter(tokenizer, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
        this.porterStemFilter = new PorterStemFilter(stopFilter);
        this.attr = porterStemFilter.addAttribute(CharTermAttribute.class);

    }

    @Override
    public Collection<String> tokenize(String featureString) throws IOException
    {
        List<String> returnValue = new ArrayList<>();
        synchronized (this) {
            tokenizer.setReader(new StringReader(featureString));
            tokenizer.reset();
            while (porterStemFilter.incrementToken()) {
                returnValue.add(attr.toString());
            }
            tokenizer.close();
        }
        LOGGER.debug("Tokenized {} into {}.", featureString, returnValue);
        return returnValue;
    }

}
