package de.kl.conf;

import de.kl.classifier.Classifier;
import de.kl.classifier.bayes.BayesClassifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author konrad
 */
@Configuration
public class xtbrConfiguration
{

    @Bean
    public Classifier getClassifier()
    {
        Classifier returnValue = new BayesClassifier();
        return returnValue;
    }
}
