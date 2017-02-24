/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.kl.classifier.dict;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author konrad
 */
@Component
public class CsvCategoryDictionary implements CategoryDictionary
{
    List<String> dictionary = new ArrayList<>();

    @Autowired
    public CsvCategoryDictionary(
            @Value("${categories.file}") String categoryFile
    ) throws IOException
    {
        File file = new File(categoryFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        try (Stream<String> stream = Files.lines(Paths.get(categoryFile))) {
            stream.forEach(line -> this.dictionary.add(line));
        }
    }
    @Override
    public List<String> getCategories()
    {
        return new ArrayList(this.dictionary);
    }
    
}
