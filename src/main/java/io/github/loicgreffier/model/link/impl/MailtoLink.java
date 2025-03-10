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
package io.github.loicgreffier.model.link.impl;

import static io.github.loicgreffier.model.link.Link.Status.BROKEN;
import static io.github.loicgreffier.model.link.Link.Status.SUCCESS;

import io.github.loicgreffier.model.link.Link;
import java.io.File;

/** This class represents a mailto link. */
public class MailtoLink extends Link {
	private static final String EMAIL_REGEX = "(.+)@(.+)";

	public MailtoLink(File file, String path, String markdown, ValidationOptions validationOptions) {
		super(file, path, markdown, validationOptions);
	}

	/**
	 * Validate the link. Check if the link is a valid email address. If the link is a valid email address, the link is
	 * valid. If the link is not a valid email address, the link is broken.
	 */
	@Override
	public void validate() {
		if (path.substring(path.indexOf("mailto:")).matches(EMAIL_REGEX)) {
			status = SUCCESS;
			details = "OK";
		}
		else {
			status = BROKEN;
			details = "bad format";
		}
	}
}
