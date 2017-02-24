package de.kl.controller;

import de.kl.classifier.dict.CategoryDictionary;
import de.kl.classifier.test.MatrixTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author konrad
 */
@Controller
@RequestMapping("test")
public class TestController
{

    private final MatrixTest matrixTest;
    private final CategoryDictionary categoryDictionary;

    @Autowired
    public TestController(
            MatrixTest matrixTest,
            CategoryDictionary categoryDictionary
    )
    {
        this.matrixTest = matrixTest;
        this.categoryDictionary = categoryDictionary;
    }
    
    @RequestMapping("matrix")
    public String getMatrixTest(Model model) {
        model.addAttribute("testMatrix",this.matrixTest.getTestMatrix());
        model.addAttribute("categories", this.categoryDictionary.getCategories());
        return "testScreen";
    }
}
