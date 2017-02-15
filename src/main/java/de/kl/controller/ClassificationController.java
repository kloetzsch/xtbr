package de.kl.controller;

import de.kl.classifier.Classification;
import de.kl.classifier.Classifier;
import de.kl.classifier.token.Tokenizer;
import de.kl.dict.CategoryDictionary;
import de.kl.dict.FeatureDictionary;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author konrad
 */
@Controller
public class ClassificationController
{

    private final Classifier<String,String> classifier;
    private final CategoryDictionary categoryDictionary;
    private final FeatureDictionary featureDictionary;
    private final Tokenizer<String> tokenizer;

    @Autowired
    public ClassificationController(
            Classifier<String,String> classifier,
            CategoryDictionary categoryDictionary,
            FeatureDictionary featureDictionary,
            Tokenizer<String> tokenizer
    )
    {
        this.classifier = classifier;
        this.categoryDictionary = categoryDictionary;
        this.featureDictionary = featureDictionary;
        this.tokenizer = tokenizer;
    }

    @RequestMapping("")
    public String getIt(Model model)
    {
        model.addAttribute("result", Collections.EMPTY_LIST);
        return this.addDefaultValuesToModel(model);
    }

    @RequestMapping(value = "/classify", method = RequestMethod.POST)
    public String classifyRawText(String inputText, Model model)
    {
        Collection<Classification<String,String>> result = this.classifier.classifyDetailed(tokenizer.tokenize(inputText));
        List<Classification<String,String>> resultAsList = new ArrayList<>();
        resultAsList.addAll(result);
        Collections.sort(resultAsList, (Classification<String, String> o1, Classification<String, String> o2) -> {
            return -1 * Float.compare(o1.getProbability(), o2.getProbability());
        });

        model.addAttribute("result", resultAsList);
        return this.addDefaultValuesToModel(model);
    }

    @RequestMapping(value = "/train", method = RequestMethod.POST)
    public String classifyRawText(String inputText, String category, Model model)
    {
        this.classifier.learn(category, tokenizer.tokenize(inputText));
        this.featureDictionary.addFeature(inputText, category);
        model.addAttribute("result", Collections.EMPTY_LIST);
        return this.addDefaultValuesToModel(model);
    }

    private String addDefaultValuesToModel(Model model)
    {
        model.addAttribute("categories", this.categoryDictionary.getCategories());
        return "overview";
    }
}
