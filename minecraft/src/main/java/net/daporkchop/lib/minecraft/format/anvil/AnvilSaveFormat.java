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

package net.daporkchop.lib.minecraft.format.anvil;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.compression.context.PInflater;
import net.daporkchop.lib.compression.zlib.Zlib;
import net.daporkchop.lib.compression.zlib.ZlibMode;
import net.daporkchop.lib.minecraft.save.Save;
import net.daporkchop.lib.minecraft.save.SaveFormat;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.nbt.NBTFormat;
import net.daporkchop.lib.nbt.tag.CompoundTag;

import java.io.File;
import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public class AnvilSaveFormat implements SaveFormat {
    @Override
    public Save tryOpen(@NonNull File root, @NonNull SaveOptions options) throws IOException {
        File levelDatFile = new File(root, "level.dat");
        if (!PFiles.checkFileExists(levelDatFile)) {
            return null;
        }

        CompoundTag nbt;
        try (PInflater inflater = Zlib.PROVIDER.inflater(Zlib.PROVIDER.inflateOptions().withMode(ZlibMode.GZIP));
             DataIn in = inflater.decompressionStream(DataIn.wrapBuffered(levelDatFile))) {
            nbt = NBTFormat.BIG_ENDIAN.readCompound(in);
        }
        try (CompoundTag levelDat = nbt) {
            //System.out.println(levelDat);
            if (levelDat.contains("Data")) {
                return new AnvilSave(options, levelDat, root);
            }
            return null;
        }
    }
}
