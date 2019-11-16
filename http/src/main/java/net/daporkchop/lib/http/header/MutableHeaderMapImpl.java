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

import lombok.NonNull;

/**
 * A simple implementation of {@link MutableHeaderMap}.
 *
 * @author DaPorkchop_
 */
public class MutableHeaderMapImpl extends HeaderMapImpl implements MutableHeaderMap {
    public MutableHeaderMapImpl() {
    }

    public MutableHeaderMapImpl(@NonNull HeaderMap source) {
        this.putAll(source);
    }

    @Override
    public synchronized String put(@NonNull String key, @NonNull String value) {
        Header header = new HeaderImpl(key, value);
        key = key.toLowerCase();
        Header old = this.map.putIfAbsent(key, header);
        if (old == null) {
            //the header is new
            this.list.add(header);
            return null;
        } else {
            //the header already exists
            this.map.replace(key, old, header);
            this.list.set(this.list.indexOf(old), header);
            return old.value();
        }
    }

    @Override
    public synchronized String remove(@NonNull String key) {
        Header old = this.map.remove(key.toLowerCase());
        return old != null && this.list.remove(old) ? old.value() : null;
    }
}
