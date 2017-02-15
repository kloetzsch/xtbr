package de.kl;

import de.kl.classifier.Classifier;
import de.kl.dict.CsvFeatureDictionary;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class XtbrApplication
{

    public static void main(String[] args) throws Throwable
    {
        ConfigurableApplicationContext ctx = SpringApplication.run(XtbrApplication.class, args);

        Classifier bayes = ctx.getBean(Classifier.class);
        CsvFeatureDictionary dictionary = ctx.getBean(CsvFeatureDictionary.class);

        dictionary.getAllFeature().stream().forEach((entry) -> {
            bayes.learn(entry);
        });
    }

}
