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

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a XML tag that is not self-closing (e.g.
 * <code>&lt;help&gt;Hello, World!&lt/help&gt;</code>). It contains children in
 * the form of {@link AbstractXmlValue XML values} that can either be
 * {@link XmlTag XML tags}, {@link SelfClosingXmlTag self-closing XML tags} or
 * {@link SimpleXmlValue simple XML values}, such as a string.
 *
 * @see SelfClosingXmlTag
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class XmlTag extends AbstractXmlTag {
    /**
     * The children of this XML tag.
     *
     */
    private final List<AbstractXmlValue> children;

    /**
     * Constructor of XmlTag.
     *
     * @param name
     *            See {@link AbstractXmlTag}.
     * @param parameters
     *            See {@link AbstractXmlTag}.
     * @param children
     *            The {@link #children} to set.
     */
    public XmlTag(final String name, final Map<String, String> parameters, final List<AbstractXmlValue> children) {
        super(name, parameters);

        this.children = children;
    }
}
