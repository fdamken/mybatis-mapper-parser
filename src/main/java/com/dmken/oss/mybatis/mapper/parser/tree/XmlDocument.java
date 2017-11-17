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

import java.nio.charset.Charset;

import lombok.Data;

/**
 * Represents the XML document.
 *
 * <p>
 * An XML document contains data about the document, such as the XML version and
 * the character set. The actual document is contained in the {@link #rootTag
 * root XML tag} and is recursively nested.
 * </p>
 *
 */
@Data
public class XmlDocument {
    /**
     * The version of the XML document.
     *
     */
    private final XmlVersion version;
    /**
     * The encoding the XML document.
     *
     */
    private final Charset encoding;
    /**
     * The actual document represented by the root XML tag.
     *
     */
    private final XmlTag rootTag;
}
