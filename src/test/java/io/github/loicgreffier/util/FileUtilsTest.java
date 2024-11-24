package io.github.loicgreffier.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FileUtilsTest {

    @Test
    void shouldDetectDocsify() throws IOException {
        assertTrue(FileUtils.isDocsify(Path.of("src/test/resources/docsify").toFile()));
    }

    @Test
    void shouldDetectHugo() {
        assertTrue(FileUtils.isHugo(Path.of("src/test/resources/hugo").toFile()));
    }
}
