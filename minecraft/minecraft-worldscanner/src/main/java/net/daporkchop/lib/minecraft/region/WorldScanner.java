/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.minecraft.region;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.minecraft.world.Column;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.minecraft.world.format.WorldManager;
import net.daporkchop.lib.minecraft.world.format.anvil.AnvilWorldManager;
import net.daporkchop.lib.primitive.lambda.consumer.bi.IntegerObjectConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class WorldScanner {
    @NonNull
    @Getter
    private final World world;

    private final Collection<ColumnProcessor> processors = new ArrayList<>();

    public WorldScanner addProcessor(@NonNull Consumer<Column> processor) {
        this.processors.add((current, estimatedTotal, column) -> processor.accept(column));
        return this;
    }

    public WorldScanner addProcessor(@NonNull ColumnProcessor processor) {
        this.processors.add(processor);
        return this;
    }

    public WorldScanner clear() {
        this.processors.clear();
        return this;
    }

    public WorldScanner run()   {
        return this.run(false);
    }

    public WorldScanner run(boolean parallel) {
        AtomicLong curr = new AtomicLong(0L);
        AtomicLong estimatedTotal = new AtomicLong(0L);
        WorldManager manager = this.world.getManager();
        if (manager instanceof AnvilWorldManager) {
            AnvilWorldManager anvilWorldManager = (AnvilWorldManager) manager;
            Collection<Vec2i> regions = anvilWorldManager.getRegions();
            estimatedTotal.set(regions.size() * 32L * 32L);
            (parallel ? regions.parallelStream() : regions.stream()).forEach(pos -> {
                int xx = pos.getX() << 5;
                int zz = pos.getY() << 5;
                for (int x = 31; x >= 0; x--) {
                    for (int z = 31; z >= 0; z--) {
                        Column column = this.world.getColumn(xx + x, zz + z);
                        if (column.load(false)) {
                            for (ColumnProcessor processor : this.processors) {
                                processor.handle(curr.getAndIncrement(), estimatedTotal.get(), column);
                            }
                            column.unload();
                        } else {
                            estimatedTotal.decrementAndGet();
                        }
                    }
                }
            });
        } else {
            throw new UnsupportedOperationException(String.format("Iteration over %s", manager.getClass().getCanonicalName()));
        }

        return this;
    }

    public interface ColumnProcessor {
        void handle(long current, long estimatedTotal, @NonNull Column column);
    }
}
