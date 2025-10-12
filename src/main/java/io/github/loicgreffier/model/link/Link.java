/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.github.loicgreffier.model.link;

import java.io.File;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jline.jansi.Ansi;

import static org.jline.jansi.Ansi.Color.GREEN;
import static org.jline.jansi.Ansi.Color.RED;

/** This class represents a link. */
@Getter
@RequiredArgsConstructor
public abstract class Link {
    protected final File file;
    protected final String path;
    protected final String markdown;
    protected Status status;
    protected String details;

    @NonNull protected final ValidationOptions validationOptions;

    /** Validate the link. */
    public abstract void validate();

    /**
     * Get the Ansi string representation of the link.
     *
     * @return The Ansi string representation of the link.
     */
    public String toAnsiString() {
        return "@|bold,cyan " + path + "|@ (@|bold," + getAnsiColor() + " " + details + "|@)";
    }

    /**
     * Get the Ansi color of the link.
     *
     * @return The Ansi color of the link.
     */
    private Ansi.Color getAnsiColor() {
        return switch (status) {
            case SUCCESS -> GREEN;
            case BROKEN -> RED;
        };
    }

    /** This class represents the validation options. */
    @Getter
    @Builder
    public static class ValidationOptions {
        private String currentDir;
        private String contentDirectory;
        private String imageDirectory;
        private String indexFilename;
        private boolean allAbsolute;
        private boolean imageAbsolute;
        private boolean skipExternal;
        private boolean skipRelative;
        private boolean skipMailto;
        private boolean insecure;
    }

    /** This enum represents the link status. */
    public enum Status {
        SUCCESS,
        BROKEN
    }
}
