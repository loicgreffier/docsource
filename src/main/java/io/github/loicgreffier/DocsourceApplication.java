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
package io.github.loicgreffier;

import io.github.loicgreffier.command.Docsource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;
import picocli.jansi.graalvm.AnsiConsole;

/** This is the main class for the Docsource application. */
@SpringBootApplication
public class DocsourceApplication implements CommandLineRunner {
    private final IFactory factory;
    private final Docsource docsource;

    /**
     * Create a new Docsource application.
     *
     * @param factory The factory.
     * @param docsource The docsource command.
     */
    public DocsourceApplication(IFactory factory, Docsource docsource) {
        this.factory = factory;
        this.docsource = docsource;
    }

    /**
     * The main entry point of the Docsource application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(DocsourceApplication.class, args);
    }

    /**
     * Run the Docsource command line.
     *
     * @param args The command line arguments.
     */
    @Override
    public void run(String... args) {
        int exitCode;
        try (
        // Colors on Windows CMD (including for native)
        AnsiConsole ansi = AnsiConsole.windowsInstall()) {
            exitCode = new CommandLine(docsource, factory).execute(args);
        }
        System.exit(exitCode);
    }
}
