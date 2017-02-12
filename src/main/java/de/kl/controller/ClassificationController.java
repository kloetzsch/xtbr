package de.kl.controller;

import de.kl.classifier.Classifier;
import de.kl.classifier.token.Tokenizer;
import de.kl.dict.CategoryDictionary;
import de.kl.dict.FeatureDictionary;
import java.util.Collection;
import java.util.Collections;
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

    private final Classifier classifier;
    private final CategoryDictionary categoryDictionary;
    private final FeatureDictionary featureDictionary;
    private final Tokenizer tokenizer;

    @Autowired
    public ClassificationController(
            Classifier classifier,
            CategoryDictionary categoryDictionary,
            FeatureDictionary featureDictionary,
            Tokenizer tokenizer
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
        Collection result = this.classifier.classifyDetailed(tokenizer.tokenize(inputText));
        model.addAttribute("result", result);
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
