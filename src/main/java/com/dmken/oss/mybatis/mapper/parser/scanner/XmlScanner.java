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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Deque;
import java.util.LinkedList;

import com.dmken.oss.mybatis.mapper.parser.scanner.Token.TokenType;
import com.dmken.oss.mybatis.mapper.parser.scanner.exception.IllegalSymbolException;
import com.dmken.oss.mybatis.mapper.parser.scanner.exception.ScannerException;

/**
 * The scanner produces a stream of {@link Token tokens} from the code that can
 * than be used to create the document tree.
 *
 */
public class XmlScanner {
    /**
     * The default character set.
     *
     */
    private static final Charset DEFAULT_CHARSET = Charset.forName("ASCII");

    /**
     * The reader the code is coming from.
     *
     */
    private final Reader reader;

    /**
     * The current character.
     *
     */
    private int current;
    /**
     * The builder containing the last read characters that are relevant to
     * produce the next token.
     *
     */
    private StringBuilder builder;
    /**
     * The current line.
     *
     */
    private int line = 1;
    /**
     * The current column.
     *
     */
    private int column = 1;

    /**
     * Constructor of XmlScanner.
     *
     * @param in
     *            The input stream to read the code from.
     * @throws ScannerException
     *             If any error occurs.
     */
    public XmlScanner(final InputStream in) throws ScannerException {
        this.reader = new BufferedReader(new InputStreamReader(in, XmlScanner.DEFAULT_CHARSET));
    }

    /**
     * Scans the given input stream.
     *
     * @param in
     *            The input stream containing the code.
     * @return The parsed token stream.
     * @throws ScannerException
     *             If any error occurs.
     */
    public static Deque<Token> scan(final InputStream in) throws ScannerException {
        return new XmlScanner(in).scan();
    }

    /**
     * Scans the code and produces the token stream.
     *
     * @return The token stream.
     * @throws ScannerException
     *             If any error occurs.
     */
    public Deque<Token> scan() throws ScannerException {
        final Deque<Token> tokens = new LinkedList<>();

        this.current = this.read();

        boolean lastWasContent = false;
        boolean nextIsContent = false;
        while (this.current != -1) {
            if (Character.isWhitespace(this.current)) {
                this.skip();
                continue;
            }

            this.builder = new StringBuilder();

            final int currentLine = this.line;
            final int currentColumn = this.column;

            if (this.current == '<') {
                this.take();
                if (this.scanComment()) {
                    nextIsContent = lastWasContent;
                    lastWasContent = false;
                } else {
                    tokens.offer(new Token(currentLine, currentColumn, this.builder.toString(), TokenType.LANGEL));
                    nextIsContent = false;
                    lastWasContent = false;
                }
            } else if (nextIsContent) {
                nextIsContent = false;
                lastWasContent = true;

                this.scanContent();
                tokens.offer(new Token(currentLine, currentColumn, this.builder.toString().trim(), TokenType.CONTENT));
            } else {
                final TokenType type = this.scanToken();
                final Token token = new Token(currentLine, currentColumn, this.builder.toString(), type);
                tokens.offer(token);
                nextIsContent = type == TokenType.RANGEL;
                lastWasContent = false;
            }
        }

        return tokens;
    }

    /**
     * Assuming the next characters form a token, scans the token.
     *
     * @return The type of the scanned token.
     * @throws ScannerException
     *             If any error occurs.
     */
    private TokenType scanToken() throws ScannerException {
        if (this.isStringStart(this.current)) {
            this.scanString();
            return TokenType.STRINGLIT;
        }
        if (this.isIdentifierStart(this.current)) {
            return this.scanIdentifier();
        }
        final int old = this.current;
        this.take();
        switch (old) {
            case '>':
                return TokenType.RANGEL;
            case '=':
                return TokenType.EQUALS;
            case '/':
                return TokenType.SLASH;
            case '?':
                return TokenType.QMARK;
            case '!':
                return TokenType.EXMARK;
            default:
                throw new IllegalSymbolException(this.line, this.column, (char) old, "\"", "identifier", ">", "=", "/", "?", "!");
        }
    }

    /**
     * Assuming the next characters form the content between two XML tags, scans
     * the content.
     *
     * @throws ScannerException
     *             If any error occurs.
     */
    private void scanContent() throws ScannerException {
        while (true) {
            if (this.current == '<') {
                try {
                    // Mark position.
                    this.reader.mark(1024 * 8);

                    // Hacky way to allow comments inside the content.
                    final int old = this.current;
                    this.skip();
                    if (this.scanComment()) {
                        // Remove whitespaces around the comment.
                        this.builder = new StringBuilder(this.builder.toString().trim());
                        this.skipWhitespace();
                    } else {
                        this.current = old;
                        // Reset position to ensure the correct character is
                        // read.
                        this.reader.reset();
                        break;
                    }
                } catch (final IOException cause) {
                    throw new ScannerException("Failed to mark/reset the stream!", cause);
                }
            }
            if (!this.scanEscapeSequences()) {
                this.take();
            }
        }
    }

    /**
     * Assuming the next characters form a string starting with <code>"</code>
     * or <code>'</code>, scans the string.
     *
     * @throws ScannerException
     *             If any error occurs.
     */
    private void scanString() throws ScannerException {
        final int endChar = this.current;
        this.skip();
        while (this.current != endChar) {
            if (!this.scanEscapeSequences()) {
                this.take();
            }
        }
        this.skip();
    }

    /**
     * Assuming the next characters form an identifier, scans the identifier.
     *
     * @throws ScannerException
     *             If any error occurs.
     * @return The actual token type (if the identifier was a keyword).
     */
    private TokenType scanIdentifier() throws ScannerException {
        this.take();
        while (this.isIdentifierPart(this.current)) {
            if (!this.scanEscapeSequences()) {
                this.take();
            }
        }

        final String id = this.builder.toString();
        for (final TokenType type : TokenType.keywords()) {
            if (type.isRepresentedBy(id)) {
                return type;
            }
        }
        return TokenType.IDENTIFIER;
    }

    /**
     * Checks whether the given character is the start of a string (either
     * <code>"</code> or <code>'</code>).
     *
     * @param c
     *            The character to check.
     * @return Whether the character represents the start of a string or not.
     */
    private boolean isStringStart(final int c) {
        return c == '"' || c == '\'';
    }

    /**
     * Checks whether the given character is the start of an identifier
     * (matching <code>[a-zA-Z_\$]</code>).
     *
     * @param c
     *            The character to check.
     * @return Whether the character represents the start of an identifier or
     *         not.
     */
    private boolean isIdentifierStart(final int c) {
        return Character.isAlphabetic(c) || c == '_' || c == '$';
    }

    /**
     * Checks whether the given character is part of an identifier (matching
     * <code>[a-zA-Z_0-9\$]</code>).
     *
     * @param c
     *            The character to check.
     * @return Whether the character is part of an identifier or not.
     */
    private boolean isIdentifierPart(final int c) {
        return this.isIdentifierStart(c) || Character.isDigit(c);
    }

    /**
     * Assuming the last read character is a <code>&lt;</code>, checks whether
     * the next characters form a comment and if so, skips it.
     *
     * @return Whether there was a comment or not.
     * @throws ScannerException
     *             If any error occurs.
     */
    private boolean scanComment() throws ScannerException {
        if (this.current != '!') {
            return false;
        }

        final int old = this.current;
        try {
            // Mark position.
            this.reader.mark(1024 * 8);

            this.skip();
            if (this.current == '-') {
                this.skip();
                if (this.current == '-') {
                    while (true) {
                        this.skip();
                        if (this.current == '-') {
                            this.skip();
                            if (this.current == '-') {
                                this.skip();
                                if (this.current == '>') {
                                    this.skip();
                                    return true;
                                } else {
                                    throw new IllegalSymbolException(this.line, this.column, (char) this.current, '>');
                                }
                            }
                        }
                    }
                } else {
                    throw new IllegalSymbolException(this.line, this.column, (char) this.current, '-');
                }
            } else {
                this.current = old;
                // Reset position to ensure the correct character is read.
                this.reader.reset();
                return false;
            }
        } catch (final IOException cause) {
            throw new ScannerException("Failed to mark/reset the stream!", cause);
        }
    }

    /**
     * Assuming the current character is a <code>&</code>, checks whether it
     * forms an escape sequence and if so, skips it and replaces it with the
     * real character.
     *
     * @return Whether there was an escape sequence or not.
     * @throws ScannerException
     *             If any error occurs.
     */
    private boolean scanEscapeSequences() throws ScannerException {
        if (this.current != '&') {
            return false;
        }

        this.skip();
        if (this.current == 'l') {
            this.skip();
            if (this.current == 't') {
                this.skip();
                if (this.current == ';') {
                    this.skip();
                    this.append('<');
                } else {
                    throw new IllegalSymbolException(this.line, this.column, (char) this.current, ';');
                }
            } else {
                throw new IllegalSymbolException(this.line, this.column, (char) this.current, 't');
            }
        } else if (this.current == 'g') {
            this.skip();
            if (this.current == 't') {
                this.skip();
                if (this.current == ';') {
                    this.skip();
                    this.append('>');
                } else {
                    throw new IllegalSymbolException(this.line, this.column, (char) this.current, ';');
                }
            } else {
                throw new IllegalSymbolException(this.line, this.column, (char) this.current, 't');
            }
        } else if (this.current == 'a') {
            this.skip();
            if (this.current == 'm') {
                this.skip();
                if (this.current == 'p') {
                    this.skip();
                    if (this.current == ';') {
                        this.skip();
                        this.append('&');
                    } else {
                        throw new IllegalSymbolException(this.line, this.column, (char) this.current, ';');
                    }
                } else {
                    throw new IllegalSymbolException(this.line, this.column, (char) this.current, 'p');
                }
            } else if (this.current == 'p') {
                this.skip();
                if (this.current == 'o') {
                    this.skip();
                    if (this.current == 's') {
                        this.skip();
                        if (this.current == ';') {
                            this.skip();
                            this.append('\'');
                        } else {
                            throw new IllegalSymbolException(this.line, this.column, (char) this.current, ';');
                        }
                    } else {
                        throw new IllegalSymbolException(this.line, this.column, (char) this.current, 's');
                    }
                } else {
                    throw new IllegalSymbolException(this.line, this.column, (char) this.current, 'o');
                }
            } else {
                throw new IllegalSymbolException(this.line, this.column, (char) this.current, 'm', 'p');
            }
        } else if (this.current == 'q') {
            this.skip();
            if (this.current == 'u') {
                this.skip();
                if (this.current == 'o') {
                    this.skip();
                    if (this.current == 't') {
                        this.skip();
                        if (this.current == ';') {
                            this.skip();
                            this.append('"');
                        } else {
                            throw new IllegalSymbolException(this.line, this.column, (char) this.current, ';');
                        }
                    } else {
                        throw new IllegalSymbolException(this.line, this.column, (char) this.current, 't');
                    }
                } else {
                    throw new IllegalSymbolException(this.line, this.column, (char) this.current, 'o');
                }
            } else {
                throw new IllegalSymbolException(this.line, this.column, (char) this.current, 'u');
            }
        } else {
            throw new IllegalSymbolException(this.line, this.column, (char) this.current, 'l', 'g', 'a', 'q');
        }

        return true;
    }

    /**
     * Appends the current character to the builder and skips it.
     *
     * @throws ScannerException
     *             If any error occurs.
     */
    private void take() throws ScannerException {
        this.append(this.current);
        this.skip();
    }

    /**
     * Appends the given character (identified by the integer value) to the
     * builder.
     *
     * @param val
     *            The code of the character to append.
     */
    private void append(final int val) {
        this.builder.append((char) val);
    }

    /**
     * Skips the next whitespaces.
     *
     * @throws ScannerException
     *             If any error occurs.
     */
    private void skipWhitespace() throws ScannerException {
        while (Character.isWhitespace(this.current)) {
            this.skip();
        }
    }

    /**
     * Skips the next character (updates current and throws the old away).
     *
     * @throws ScannerException
     *             If any error occurs.
     */
    private void skip() throws ScannerException {
        // Handle all possible line breaks according to the Unicode standard.
        int old = this.current;
        this.current = this.read();
        if (old == '\r' && this.current == '\n') {
            old = this.current;
            this.current = this.read();
        }
        if (old == '\n' || old == '\r' || old == '\u000b' || old == '\u000c' || old == '\u0085' || old == '\u2028'
                || old == '\u2029') {
            this.line++;
            this.column = 1;
        } else {
            this.column++;
        }

        if (old == -1 && this.current == -1) {
            throw new ScannerException("Unexpected end of stream!");
        }
    }

    /**
     * Reads the next character from the stream.
     *
     * @return The next character or <code>-1</code> of no such character
     *         exists.
     * @throws ScannerException
     *             If any error occurs.
     */
    private int read() throws ScannerException {
        try {
            return this.reader.read();
        } catch (final IOException cause) {
            throw new ScannerException("Failed to read from the input!", cause);
        }
    }
}
