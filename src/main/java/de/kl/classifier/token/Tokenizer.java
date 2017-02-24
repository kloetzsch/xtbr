package de.kl.classifier.token;

import java.io.IOException;
import java.util.Collection;

/**
 *
 * @author konrad
 */
public interface Tokenizer
{
     Collection<String> tokenize(String featureString) throws IOException;
}
