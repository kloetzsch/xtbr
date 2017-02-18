package de.kl.classifier.token;

import java.util.Collection;

/**
 *
 * @author konrad
 */
public interface Tokenizer
{
     Collection<String> tokenize(String featureString);
}
