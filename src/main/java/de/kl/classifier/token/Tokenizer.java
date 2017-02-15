package de.kl.classifier.token;

import java.util.Collection;

/**
 *
 * @author konrad
 * @param <T>
 */
public interface Tokenizer<T>
{
    <T> Collection<T> tokenize(String featureString);
}
