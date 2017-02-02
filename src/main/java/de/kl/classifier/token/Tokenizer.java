package de.kl.classifier.token;

import java.util.Collection;

/**
 *
 * @author konrad
 */
public interface Tokenizer
{
    <T> Collection<T> tokenize(String featureString);
}
