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

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a simple XML value that can be a child of an {@link XmlTag XML
 * tag}. This is used if an {@link XmlTag XML tag} does not contain
 * child-{@link AbstractXmlTag xml tags} but a simple value (e.g.
 * <code>&lt;help&gt;Hello, World!&lt;/help&gt;</code>).
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SimpleXmlValue extends AbstractXmlValue {
    /**
     * The data represented by this value.
     *
     */
    private final String data;
}
