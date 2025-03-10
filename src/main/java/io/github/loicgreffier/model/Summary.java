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
package io.github.loicgreffier.model;

import lombok.Getter;

/** This class represents the summary of the scan. */
@Getter
public class Summary {
    private Long successRelative;
    private Long successExternal;
    private Long successMail;
    private Long brokenRelative;
    private Long brokenExternal;
    private Long brokenMail;

    /**
     * Constructor.
     *
     * @param successRelative The number of relative links that are not broken.
     * @param successExternal The number of external links that are not broken.
     * @param successMail The number of mail links that are not broken.
     * @param brokenRelative The number of relative links that are broken.
     * @param brokenExternal The number of external links that are broken.
     * @param brokenMail The number of mail links that are broken.
     */
    public Summary(
            Long successRelative,
            Long successExternal,
            Long successMail,
            Long brokenRelative,
            Long brokenExternal,
            Long brokenMail) {
        this.successRelative = successRelative;
        this.successExternal = successExternal;
        this.successMail = successMail;
        this.brokenRelative = brokenRelative;
        this.brokenExternal = brokenExternal;
        this.brokenMail = brokenMail;
    }

    /**
     * Count the number of broken links.
     *
     * @return The number of broken links.
     */
    public Long countBrokenLinks() {
        return brokenRelative + brokenExternal + brokenMail;
    }
}
