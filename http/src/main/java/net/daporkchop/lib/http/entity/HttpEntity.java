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

package net.daporkchop.lib.http.entity;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.http.entity.content.encoding.ContentEncoding;
import net.daporkchop.lib.http.entity.content.encoding.StandardContentEncoding;
import net.daporkchop.lib.http.entity.content.type.ContentType;

/**
 * Represents some form of content that will be sent over an HTTP connection.
 *
 * @author DaPorkchop_
 */
//TODO: this is very primitive and needs to be made smarter somehow (what if we want to upload a really large file?)
public interface HttpEntity {
    /**
     * Wraps the given {@code byte[]} into a {@link HttpEntity} instance with the MIME type of {@code "application/octet-stream"}.
     *
     * @param data the data to wrap
     * @return a {@link HttpEntity} instance with the given data
     */
    static HttpEntity of(@NonNull byte[] data) {
        return of(ContentType.parse("application/octet-stream"), data);
    }

    /**
     * Wraps the given {@code byte[]} into a {@link HttpEntity} instance with the given content type.
     *
     * @param contentType the content type of the data
     * @param data     the data to wrap
     * @return a {@link HttpEntity} instance with the given data
     */
    static HttpEntity of(@NonNull String contentType, @NonNull byte[] data) {
        return of(ContentType.parse(contentType), data);
    }

    /**
     * Wraps the given {@code byte[]} into a {@link HttpEntity} instance with the given {@link ContentType}.
     *
     * @param type the {@link ContentType} of the data
     * @param data     the data to wrap
     * @return a {@link HttpEntity} instance with the given data
     */
    static HttpEntity of(@NonNull ContentType type, @NonNull byte[] data) {
        return new HttpEntityImpl(type, data);
    }

    /**
     * @return this entity's {@link ContentType}
     */
    ContentType type();

    /**
     * Gets the {@link ContentEncoding} used for encoding this entity's data.
     * <p>
     * The data returned by this {@link HttpEntity}'s various data methods must already be encoded using the {@link ContentEncoding} returned by this
     * method, as the internal HTTP engine will NOT do the encoding automatically.
     *
     * @return this entity's {@link ContentEncoding}
     */
    default ContentEncoding encoding() {
        return StandardContentEncoding.identity;
    }

    //methods below are related to data transfer

    /**
     * Gets the size (in bytes) of this entity's data.
     * <p>
     * If, for whatever reason, the data's size is not known in advance, this method should return {@code -1L}. This will result in the data being sent
     * using the "chunked" Transfer-Encoding rather than simply setting "Content-Length".
     *
     * @return the size (in bytes) of this entity's data, or {@code -1L} if it is not known
     */
    long length();

    /**
     * Gathers all of this entity's data into a single {@link ByteBuf}.
     *
     * This method is permitted to block as long as needed
     *
     * If the data is too large to be stored within a single {@link ByteBuf},
     * @return
     */
    ByteBuf allData();
}
