package io.github.loicgreffier.utils;

import io.github.loicgreffier.properties.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

/**
 * This class provides the current application version for -V option.
 */
@Component
@Scope("singleton")
public class VersionProvider implements CommandLine.IVersionProvider {
    @Autowired
    public AppProperties appProperties;

    /**
     * Get the current application version.
     *
     * @return The current application version.
     */
    @Override
    public String[] getVersion() {
        return new String[] {
            "Version " + appProperties.getVersion()
        };
    }
}