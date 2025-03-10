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
package io.github.loicgreffier.command;

import io.github.loicgreffier.util.VersionProvider;
import java.util.concurrent.Callable;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ScopeType;
import picocli.CommandLine.Spec;

@Component
@Command(name = "docsource", headerHeading = "@|bold Usage|@:", synopsisHeading = " ", descriptionHeading = "%n@|bold Description|@:%n%n", description = "These are common Docsource commands used in various situations.", parameterListHeading = "%n@|bold Parameters|@:%n", optionListHeading = "%n@|bold Options|@:%n", commandListHeading = "%n@|bold Commands|@:%n", subcommands = {
		Scan.class }, usageHelpAutoWidth = true, versionProvider = VersionProvider.class, mixinStandardHelpOptions = true)
public class Docsource implements Callable<Integer> {
	@Spec
	public CommandSpec commandSpec;

	@Option(names = { "-v", "--verbose" }, description = "Enable the verbose mode.", scope = ScopeType.INHERIT)
	public boolean verbose;

	/**
	 * Run the "docsource" command. When the command is run without any subcommand, the usage message is printed.
	 *
	 * @return The exit code.
	 */
	@Override
	public Integer call() {
		commandSpec.commandLine().getOut().println(new CommandLine(this).getUsageMessage());
		return 0;
	}
}
