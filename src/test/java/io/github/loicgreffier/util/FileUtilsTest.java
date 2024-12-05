package io.github.loicgreffier.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class FileUtilsTest {

    @Test
    void shouldDetectDocsify() throws IOException {
        assertTrue(FileUtils.isDocsify("src/test/resources/docsify"));
    }

    @Test
    void shouldDetectHugo() {
        assertTrue(FileUtils.isHugo("src/test/resources/hugo"));
    }
}
