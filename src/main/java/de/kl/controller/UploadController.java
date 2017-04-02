/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.kl.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import de.kl.io.csv.CsvReader;
import de.kl.io.csv.CsvReader.UserSettings;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author konrad
 */
@RequestMapping("data/upload")
@Controller
public class UploadController
{

    private final MongoTemplate mongoTemplate;
    private final DBCollection rawDataCollection;

    @Autowired
    public UploadController(MongoTemplate mongoTemplate
    )
    {
        this.mongoTemplate = mongoTemplate;
        this.rawDataCollection = this.mongoTemplate.getCollection("rawData");

    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getUploadForm()
    {
        return "data/upload";
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) throws IOException
    {

        try (CsvReader reader = new CsvReader(file.getInputStream(), new UserSettings())) {
            for (Map<String, String> csvLine : reader) {
                DBObject dbObject = new BasicDBObject(csvLine);
                this.rawDataCollection.insert(dbObject);
            }
            redirectAttributes.addFlashAttribute("successMessage",
                    "You successfully uploaded " + file.getOriginalFilename() + "! Added " + reader.getCurrentLineNumber()
                            + " records to the document store");
        }

        return "redirect:/data/upload";
    }
}
