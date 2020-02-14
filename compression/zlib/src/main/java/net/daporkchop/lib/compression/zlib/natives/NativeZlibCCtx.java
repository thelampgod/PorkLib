/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.compression.zlib.natives;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.compression.CCtx;
import net.daporkchop.lib.compression.util.StreamingWrapperCCtx;
import net.daporkchop.lib.compression.zlib.ZlibCCtx;
import net.daporkchop.lib.natives.util.exception.InvalidBufferTypeException;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
final class NativeZlibCCtx extends StreamingWrapperCCtx implements ZlibCCtx {
    private final int strategy;
    private final int mode;

    NativeZlibCCtx(@NonNull NativeZlib provider, int level, int strategy, int mode) {
        super(provider, provider.deflater(level, strategy, mode), level);

        this.strategy = strategy;
        this.mode = mode;
    }

    @Override
    public ZlibCCtx reset() {
        super.reset();
        return this;
    }

    @Override
    public ZlibCCtx dict(@NonNull ByteBuf dict) throws InvalidBufferTypeException {
        super.dict(dict);
        return this;
    }
}
