package org.simplenativehooks.staticResources;

import org.simplenativehooks.utilities.FileUtility;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public abstract class AbstractBootstrapResource {

    private static final Logger LOGGER = Logger.getLogger(AbstractBootstrapResource.class.getName());

    protected void extractResources() throws IOException, URISyntaxException {
        if (!FileUtility.createDirectory(getExtractingDest().getAbsolutePath())) {
            LOGGER.warning("Failed to extract " + getName() + " resources");
            return;
        }

        final String path = getRelativeSourcePath();
        FileUtility.extractFromCurrentJar(path, getExtractingDest(), this::correctExtension, this::postProcessing);
    }

    protected boolean postProcessing(String name) {
        return true;
    }

    protected abstract boolean correctExtension(String name);

    protected abstract String getRelativeSourcePath();

    protected abstract File getExtractingDest();

    protected abstract String getName();
}
