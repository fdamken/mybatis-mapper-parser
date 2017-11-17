/*-
 * #%L
 * MyBatis Mapper Parser
 * %%
 * Copyright (C) 2017 Fabian Damken
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.dmken.oss.mybatis.mapper.parser.tree;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents all available XML versions.
 *
 */
@Getter
@RequiredArgsConstructor
public enum XmlVersion {
    /**
     * Version <code>1.0</code>.
     *
     */
    XML_1_0("1.0");

    /**
     * The version string.
     *
     */
    private final String version;
}
