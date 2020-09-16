/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.binary.bit.packed;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.bit.BitArray;
import net.daporkchop.lib.common.math.PMath;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.pool.array.ArrayHandle;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Implementation of {@link BitArray} which packs all bits into a {@code long[]}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class PackedBitArray extends AbstractRefCounted implements BitArray {
    protected final long[] internalDataArray;

    protected final int bits;
    protected final int size;

    @Getter(AccessLevel.NONE)
    protected final Handle<long[]> handle;

    public PackedBitArray(int bits, int size)   {
        this(bits, size, new long[toInt(PMath.roundUp((long) size * (long) bits, 64L) >>> 6L)]);
    }

    public PackedBitArray(int bits, int size, @NonNull long[] arr)   {
        checkIndex(bits > 0 && bits <= 32, "bits (%d) must be in range [1-32]", bits);
        notNegative(size, "size");
        int minLength = toInt(PMath.roundUp((long) size * (long) bits, 64L) >>> 6L);
        checkArg(arr.length >= minLength, "length (%d) must be at least %d!", arr.length, minLength);

        this.internalDataArray = arr;
        this.bits = bits;
        this.size = size;
        this.handle = null;
    }

    public PackedBitArray(int bits, int size, @NonNull Handle<long[]> handle)   {
        checkIndex(bits > 0 && bits <= 32, "bits (%d) must be in range [1-32]", bits);
        notNegative(size, "size");
        int minLength = toInt(PMath.roundUp((long) size * (long) bits, 64L) >>> 6L);
        long[] arr = handle.get();
        int length = handle instanceof ArrayHandle ? ((ArrayHandle) handle).length() : arr.length;
        checkArg(length >= minLength, "length (%d) must be at least %d!", length, minLength);

        this.internalDataArray = arr;
        this.bits = bits;
        this.size = size;
        this.handle = handle;
    }

    @Override
    public int get(int i) {
        final long[] arr = this.internalDataArray;
        final int bits = this.bits;
        final int size = this.size;
        checkIndex(size, i);
        
        int start = i * bits;
        int firstPos = start >> 6;
        int endPos = ((i + 1) * bits - 1) >> 6;
        int relPos = start & 0x3F;
        if (firstPos == endPos) {
            return (int) ((arr[firstPos] >>> relPos) & ((1 << bits) - 1));
        } else {
            int endBitSubIndex = 64 - relPos;
            return (int) (((arr[firstPos] >>> relPos) | (arr[endPos] << endBitSubIndex)) & ((1 << bits) - 1));
        }
    }

    @Override
    public void set(int i, int value) {
        final long[] arr = this.internalDataArray;
        final int bits = this.bits;
        final int size = this.size;
        checkIndex(size, i);
        int mask = (1 << bits) - 1;
        checkArg((value & mask) == value, value);

        int start = i * bits;
        int firstPos = start >> 6;
        int endPos = ((i + 1) * bits - 1) >> 6;
        int relPos = start & 0x3F;
        arr[firstPos] = (arr[firstPos] & ~((long) mask << relPos)) | ((long) value << relPos);
        if (firstPos != endPos) {
            int endBitSubIndex = 64 - relPos;
            arr[endPos] = (arr[endPos] >>> endBitSubIndex << endBitSubIndex) | ((long) value >> endBitSubIndex);
        }
    }

    @Override
    public int replace(int i, int value) {
        final long[] arr = this.internalDataArray;
        final int bits = this.bits;
        final int size = this.size;
        checkIndex(size, i);
        int mask = (1 << bits) - 1;
        checkArg((value & mask) == value, value);

        int start = i * bits;
        int firstPos = start >> 6;
        int endPos = ((i + 1) * bits - 1) >> 6;
        int relPos = start & 0x3F;
        int old = (int) ((arr[firstPos] >>> relPos) & mask);
        arr[firstPos] = (arr[firstPos] & ~((long) mask << relPos)) | ((long) value << relPos);
        if (firstPos != endPos) {
            int endBitSubIndex = 64 - relPos;
            old |= ((int) (arr[endPos] << endBitSubIndex) & mask);
            arr[endPos] = (arr[endPos] >>> endBitSubIndex << endBitSubIndex) | ((long) value >> endBitSubIndex);
        }
        return old;
    }

    @Override
    public BitArray clone() {
        return new PackedBitArray(this.bits, this.size, this.internalDataArray.clone());
    }

    @Override
    public BitArray retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
        if (this.handle != null)    {
            this.handle.release();
        }
    }
}
