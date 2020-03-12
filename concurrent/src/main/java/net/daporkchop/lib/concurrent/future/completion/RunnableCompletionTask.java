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

package net.daporkchop.lib.concurrent.future.completion;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import lombok.NonNull;
import net.daporkchop.lib.concurrent.future.completion.CompletionTask;

/**
 * A {@link CompletionTask} which will execute a {@link Runnable}.
 *
 * @author DaPorkchop_
 */
public class RunnableCompletionTask<V> extends CompletionTask<V, Void> {
    protected Runnable action;

    public RunnableCompletionTask(@NonNull EventExecutor executor, @NonNull Future<V> depends, boolean fork, @NonNull Runnable action) {
        super(executor, depends, fork);

        this.action = action;
    }

    @Override
    protected Void computeResult(V value) throws Exception {
        try {
            this.action.run();
            return null;
        } finally {
            this.action = null;
        }
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        this.action = null;

        return super.tryFailure(cause);
    }
}
