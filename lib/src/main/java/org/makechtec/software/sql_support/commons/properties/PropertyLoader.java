package org.makechtec.software.sql_support.commons.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class PropertyLoader {

    private final String filename;
    private boolean isAlreadyLoaded;
    private Properties properties;

    public PropertyLoader(String filename) {
        this.filename = filename;
    }

    public Optional<String> getProperty(String key) {

        if (!isAlreadyLoaded) {
            try {
                loadFromFile();
                isAlreadyLoaded = true;
            } catch (IOException e) {
                return Optional.empty();
            }
        }

        return Optional.of(properties.getProperty(key));

    }

    private void loadFromFile() throws IOException {

        properties = new Properties();
        properties.load(new FileInputStream(filename));
    }

}
