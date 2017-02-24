package de.kl.classifier.token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
        List<String> returnValue = new ArrayList<>();
        for (String part : featureString.split("\\s")) {
            if (part != null && !part.isEmpty())
            {
                returnValue.add(part);
            }
        }
        return returnValue;
    }
    
}
