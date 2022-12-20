package io.lgr.docsource.utils;

import io.lgr.docsource.properties.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@Scope("singleton")
public class VersionProvider implements CommandLine.IVersionProvider {
    @Autowired
    public AppProperties appProperties;

    /**
     * Get the current application version for -V option
     * @return The application version
     */
    @Override
    public String[] getVersion() {
        return new String[] {
                "Docsource version " + appProperties.getVersion()
        };
    }
}