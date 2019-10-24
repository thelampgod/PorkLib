/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.http;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Contains various constant values used frequently throughout the library.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class HTTP {
    //TODO: figure out which one of these is correct
    //public final Pattern HEADER_PATTERN = Pattern.compile("(a-zA-Z0-9!#\\$%&'\\*\\+-\\.\\^_`\\|~)+: (a-zA-Z0-9!#\\$%&'\\*\\+-\\.\\^_`\\|~)+");
    public final Pattern HEADER_PATTERN = Pattern.compile("([[:graph:]])+: ([[:graph:]])+");

    public final byte[] VERSION_BYTES = " HTTP/1.1".getBytes(StandardCharsets.ISO_8859_1);
    public final byte[] NEWLINE_BYTES = "\r\n".getBytes(StandardCharsets.ISO_8859_1);

    public final int MAX_HEADER_SIZE = 1 << 13; // 8 KiB
    public final int MAX_HEADER_COUNT = 256;
}
