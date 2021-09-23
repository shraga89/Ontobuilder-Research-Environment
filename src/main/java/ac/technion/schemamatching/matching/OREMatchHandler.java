package ac.technion.schemamatching.matching;

import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;
import ac.technion.schemamatching.DBInterface.DBInterface;
import ac.technion.schemamatching.experiments.ExperimentDocumenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton class for matching schemas without the experimental overhead
 */
public class OREMatchHandler {
    protected OntoBuilderWrapper obw;
    private final XmlFileHandler xfh;

    /**
     * Base constructor, private (Singleton)
     */
    private OREMatchHandler()
    {
        obw = new OntoBuilderWrapper(); //This will fail when not connected to the internet due to validation error of xml
        xfh = new XmlFileHandler();

    }



    public XmlFileHandler getXfh() {
        return xfh;
    }
}


