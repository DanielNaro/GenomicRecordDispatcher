package es.upc.dmag.dispatcher;

import java.io.File;

public class WrittingResult {
    private final File outputFile;
    private final TagsCollection tags;


    public WrittingResult(File outputFile, TagsCollection tags) {
        this.outputFile = outputFile;
        this.tags = tags;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public TagsCollection getTags() {
        return tags;
    }
}
