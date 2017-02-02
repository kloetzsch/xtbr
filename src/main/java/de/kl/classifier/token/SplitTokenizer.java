package de.kl.classifier.token;

import java.util.Arrays;
import java.util.Collection;
import org.springframework.stereotype.Component;

/**
 *
 * @author konrad
 */
@Component
public class SplitTokenizer implements Tokenizer
{

    @Override
    public Collection<String> tokenize(String featureString)
    {
        return Arrays.asList(featureString.split("\\s"));
    }
    
}
