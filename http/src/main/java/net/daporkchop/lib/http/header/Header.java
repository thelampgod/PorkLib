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

package net.daporkchop.lib.http.header;

/**
 * A single HTTP header.
 * <p>
 * Implementations of this class are expected to have both {@link #hashCode()} and {@link #equals(Object)} only check for case-insensitive equality between
 * keys, not values. For comparing between values as well, {@link #deepHashCode()} and {@link #deepEquals(Object)} are provided.
 *
 * @author DaPorkchop_
 */
public interface Header {
    /**
     * @return the key (name) of the HTTP header
     */
    String key();

    /**
     * @return the raw value of the HTTP header
     */
    String value();

    /**
     * Computes the hash code of this header, using both the lower-cased representation of the key and the current value as inputs.
     *
     * @return this header's hash code
     */
    default int deepHashCode() {
        return this.key().toLowerCase().hashCode() * 31 + this.value().hashCode();
    }

    /**
     * Checks if this header is totally equal to a given object.
     * <p>
     * If the given object is also a header, both the keys (case-insensitive) and the values (case-sensitive) will be checked for equality.
     *
     * @param obj the other object
     * @return whether or not this header is totally equal to the given object
     */
    default boolean deepEquals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Header) {
            Header other = (Header) obj;
            return this.key().equalsIgnoreCase(other.key()) && this.value().equals(other.key());
        } else {
            return false;
        }
    }
}
