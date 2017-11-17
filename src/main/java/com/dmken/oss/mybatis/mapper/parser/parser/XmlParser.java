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
package com.dmken.oss.mybatis.mapper.parser.parser;

import static com.dmken.oss.mybatis.mapper.parser.scanner.Token.TokenType.*;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dmken.oss.mybatis.mapper.parser.parser.exception.IllegalTokenException;
import com.dmken.oss.mybatis.mapper.parser.parser.exception.ParserException;
import com.dmken.oss.mybatis.mapper.parser.scanner.Token;
import com.dmken.oss.mybatis.mapper.parser.scanner.Token.TokenType;
import com.dmken.oss.mybatis.mapper.parser.tree.AbstractXmlTag;
import com.dmken.oss.mybatis.mapper.parser.tree.AbstractXmlValue;
import com.dmken.oss.mybatis.mapper.parser.tree.SelfClosingXmlTag;
import com.dmken.oss.mybatis.mapper.parser.tree.SimpleXmlValue;
import com.dmken.oss.mybatis.mapper.parser.tree.XmlDocument;
import com.dmken.oss.mybatis.mapper.parser.tree.XmlTag;
import com.dmken.oss.mybatis.mapper.parser.tree.XmlVersion;
import com.dmken.oss.mybatis.mapper.parser.util.FormatUtil;

/**
 * The parser to parse the token stream produced by the scanner into an
 * {@link XmlDocument XML document}.
 *
 */
public class XmlParser {
    /**
     * The remaining tokens.
     *
     */
    private final Deque<Token> tokens;

    /**
     * The token that is currently getting processed.
     *
     */
    private Token currentToken;

    /**
     * Constructor of XmlParser.
     *
     * @param tokens
     *            The token stream to parse.
     */
    public XmlParser(final Deque<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses the given tokens and produces a {@link XmlDocument XML document}.
     *
     * @param tokens
     *            The token stream to parse.
     * @return The parses {@link XmlDocument XML document}.
     * @throws ParserException
     *             If any error occurs.
     */
    public static XmlDocument parse(final Deque<Token> tokens) throws ParserException {
        return new XmlParser(tokens).parse();
    }

    /**
     * Parses the token stream and produces the {@link XmlDocument XML
     * document}.
     *
     * @return The {@link XmlDocument XML document}.
     * @throws ParserException
     *             If any error occurs.
     */
    public XmlDocument parse() throws ParserException {
        this.skip();

        this.expect(LANGEL);

        XmlVersion version = null;
        Charset encoding = null;
        if (this.optional(QMARK)) {
            this.expect(IDENTIFIER, "xml", false);

            boolean anyParsed = false;
            do {
                anyParsed = false;
                if (this.optional(IDENTIFIER, "version", false)) {
                    this.expect(EQUALS);
                    final String versionString = this.expect(STRINGLIT).getToken();
                    version = XmlVersion.findByVersion(versionString);
                    if (version == null) {
                        throw new ParserException("Unsupported version " + versionString + "!");
                    }
                    anyParsed = true;
                }
                if (this.optional(IDENTIFIER, "encoding", false)) {
                    this.expect(EQUALS);
                    final String encodingString = this.expect(STRINGLIT).getToken();
                    try {
                        encoding = Charset.forName(encodingString);
                    } catch (final UnsupportedCharsetException cause) {
                        throw new ParserException("Unsupported encoding " + encodingString + "!", cause);
                    }
                    anyParsed = true;
                }
            } while (anyParsed);
            this.expect(QMARK);
            this.expect(RANGEL);

            this.expect(LANGEL);
        }
        String doctype = null;
        if (this.optional(EXMARK)) {
            // TODO: Parse DOCTYPE.

            this.expect(IDENTIFIER, "DOCTYPE", false);

            // Mock.
            doctype = "";
            doctype += "DOCTYPE";
            while (!this.is(RANGEL)) {
                doctype += ' ' + this.skip().getToken();
            }
            this.skip();
            // Mock.

            this.expect(LANGEL);
        }
        final AbstractXmlTag rootTag = this.parseTag();
        if (!(rootTag instanceof XmlTag)) {
            throw new ParserException("Unexpected self-closing XML tag on root level! Expected normal.");
        }
        return new XmlDocument(version, encoding, doctype, (XmlTag) rootTag);
    }

    /**
     * Parses the next character assuming they form a tag while the opening
     * <code>&lt</code> is already parsed.
     *
     * @return The parsed {@link AbstractXmlTag tag}.
     * @throws ParserException
     *             If any error occurs.
     */
    private AbstractXmlTag parseTag() throws ParserException {
        final String name = this.expect(IDENTIFIER).getToken();
        final Map<String, String> parameters = new HashMap<>();
        while (this.is(IDENTIFIER)) {
            final String key = this.expect(IDENTIFIER).getToken();
            this.expect(EQUALS);
            final String value = this.expect(STRINGLIT).getToken();

            parameters.put(key, value);
        }
        if (this.optional(SLASH)) {
            this.expect(RANGEL);
            return new SelfClosingXmlTag(name, parameters);
        }
        this.expect(RANGEL);
        final List<AbstractXmlValue> children = new ArrayList<>();
        while (!this.is(LANGEL) || this.tokens.peek().getType() != SLASH) {
            children.add(this.parseValue());
        }
        this.expect(LANGEL);
        this.expect(SLASH);
        this.expect(IDENTIFIER, name, true);
        this.expect(RANGEL);
        return new XmlTag(name, parameters, children);
    }

    /**
     * Parses the next tokens assuming they form a {@link AbstractXmlValue XML
     * value} (either content or a tag).
     *
     * @return The parsed {@link AbstractXmlValue XML value}.
     * @throws ParserException
     *             If any error occurs.
     */
    private AbstractXmlValue parseValue() throws ParserException {
        if (this.is(CONTENT)) {
            return new SimpleXmlValue(this.skip().getToken());
        } else if (this.optional(LANGEL)) {
            return this.parseTag();
        } else {
            throw new IllegalTokenException(this.currentToken, CONTENT, LANGEL);
        }
    }

    /**
     * Checks whether the current token has the given type.
     *
     * @param type
     *            The type to check for.
     * @return The expected token, if any.
     * @throws ParserException
     *             If the current token does not match the expected criteria.
     */
    private Token expect(final TokenType type) throws ParserException {
        if (!this.is(type)) {
            throw new IllegalTokenException(this.currentToken, type);
        }
        return this.skip();
    }

    /**
     * Checks whether the current token has the given type and the given
     * content.
     *
     * @param type
     *            The type to check for.
     * @param token
     *            The content to check for.
     * @param caseSensitive
     *            Whether the content has has to be case-sensitive.
     * @return The expected token, if any.
     * @throws ParserException
     *             If the current token does not match the expected criteria.
     */
    private Token expect(final TokenType type, final String token, final boolean caseSensitive) throws ParserException {
        if (!this.is(type, token, caseSensitive)) {
            throw new ParserException("Illegal token " + this.currentToken + ". Expected " + new Token(0, 0, token, type) + " at "
                    + FormatUtil.formatSourceLocation(this.currentToken.getLine(), this.currentToken.getColumn()));
        }
        return this.skip();
    }

    /**
     * Checks whether the current token has the given type.
     *
     * <p>
     * If the token was found, it is skipped. If not, no tokens are skipped.
     * </p>
     *
     * @param type
     *            The type to check for.
     * @return Whether the current token matches the expected criteria.
     */
    private boolean optional(final TokenType type) {
        if (this.is(type)) {
            this.skip();
            return true;
        }
        return false;
    }

    /**
     * Checks whether the current token has the given type and the given
     * content.
     *
     * <p>
     * If the token was found, it is skipped. If not, no tokens are skipped.
     * </p>
     *
     * @param type
     *            The type to check for.
     * @param token
     *            The content to check for.
     * @param caseSensitive
     *            Whether the content has has to be case-sensitive.
     * @return Whether the current token matches the expected criteria.
     */
    private boolean optional(final TokenType type, final String token, final boolean caseSensitive) {
        if (this.is(type, token, caseSensitive)) {
            this.skip();
            return true;
        }
        return false;
    }

    /**
     * Checks whether the current token has the given type.
     *
     * <p>
     * No token will be skipped.
     * </p>
     *
     * @param type
     *            The type to check for.
     * @return Whether the current token matches the expected criteria.
     */
    private boolean is(final TokenType type) {
        return this.currentToken.getType() == type;
    }

    /**
     * Checks whether the current token has the given type and the given
     * content.
     *
     * <p>
     * No token will be skipped.
     * </p>
     *
     * @param type
     *            The type to check for.
     * @param token
     *            The content to check for.
     * @param caseSensitive
     *            Whether the content has has to be case-sensitive.
     * @return Whether the current token matches the expected criteria.
     */
    private boolean is(final TokenType type, final String token, final boolean caseSensitive) {
        return this.is(type) && (caseSensitive ? this.currentToken.getToken().equals(token)
                : this.currentToken.getToken().equalsIgnoreCase(token));
    }

    /**
     * Skips the current token and polls the next token from the token stream.
     *
     * @return The old token.
     */
    private Token skip() {
        final Token old = this.currentToken;
        this.currentToken = this.tokens.poll();
        return old;
    }
}
