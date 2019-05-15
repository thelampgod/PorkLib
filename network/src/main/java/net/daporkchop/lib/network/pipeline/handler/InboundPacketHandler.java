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

package net.daporkchop.lib.network.pipeline.handler;

import lombok.NonNull;
import net.daporkchop.lib.network.pipeline.event.MessageReceived;
import net.daporkchop.lib.network.session.AbstractUserSession;

/**
 * A handler for incoming packets.
 *
 * This will block the message from continuing down the pipeline. To
 *
 * @author DaPorkchop_
 */
public interface InboundPacketHandler<S extends AbstractUserSession<S>, P> extends MessageReceived<S, P, Object> {
    @Override
    default void messageReceived(@NonNull S session, @NonNull P msg, int channel, Callback<S, Object> next) {
        this.handle(session, msg, channel);
    }

    void handle(@NonNull S session, @NonNull P msg, int channel);
}
