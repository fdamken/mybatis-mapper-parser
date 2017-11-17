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
package com.dmken.oss.mybatis.mapper.parser.scanner;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a token in the token stream that is produces by the
 * {@link XmlScanner scanner}.
 *
 */
@Data
@EqualsAndHashCode(exclude = { "line", "column" })
public class Token {
    /**
     * The source code line.
     *
     */
    private final int line;
    /**
     * The source code column.
     *
     */
    private final int column;
    /**
     * The actual token.
     *
     */
    private final String token;
    /**
     * The type of the token.
     *
     * @see TokenType
     */
    private final TokenType type;

    /**
     *
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.type.toString() + "(" + this.token + ")";
    }

    /**
     * The type of a token.
     *
     */
    @Getter
    @RequiredArgsConstructor
    @SuppressWarnings("javadoc")
    public static enum TokenType {
        LANGEL("<"),
        RANGEL(">"),
        EQUALS("="),
        SLASH("/"),
        QMARK("?"),
        EXMARK("!"),
        /**
         * Represents an identifier matching the regular expression
         * <code>^[a-zA-Z_\$][[a-zA-Z0-9_\$]]$</code>.
         *
         */
        IDENTIFIER(null),
        /**
         * Represents a string literal wrapped by <code>"</code> or
         * <code>'</code> that can contain any character.
         *
         */
        STRINGLIT(null),
        /**
         * Represents the content between two tags.
         *
         */
        CONTENT(null);

        private final String key;
    }
}
