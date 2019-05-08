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

package net.daporkchop.lib.network.session;

import io.netty.util.concurrent.Future;
import lombok.NonNull;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.transport.NetSession;

/**
 * A channel is the actual data transfer layer on top of a {@link PSession}. A single {@link PSession} may have multiple
 * channels (or not, depending on whether the underlying protocol allows for it), allowing for parallel sending of
 * packets.
 * <p>
 * Data transfer protocols that do not have native support for multi-channeling (such as TCP) will not benefit from
 * use of multiple channels, as they inherently do not support more than one channel (unlike protocols such as SCTP
 * or RakNet).
 *
 * @author DaPorkchop_
 */
public interface PChannel extends Reliable<PChannel> {
    /**
     * Gets this channel's underlying session.
     *
     * @return this channel's underlying session
     */
    <S extends AbstractUserSession<S>> S session();

    /**
     * Gets this channel's underlying internal session.
     *
     * @return this channel's underlying internal session
     */
    NetSession internalSession();

    /**
     * Gets this channel's id.
     *
     * @return this channel's id
     */
    int id();

    /**
     * Gets the local endpoint associated with this channel's underlying session.
     *
     * @return the local endpoint associated with this channel's underlying session
     */
    default <E extends PEndpoint<E>> E endpoint() {
        return this.internalSession().endpoint();
    }

    /**
     * Sends a single packet to the remote endpoint over this channel.
     * <p>
     * This method may be blocking or not, depending on the implementation.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    channel's transport protocol, this channel's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @return this channel
     */
    PChannel send(@NonNull Object packet, Reliability reliability);

    /**
     * Sends a single packet to the remote endpoint over this channel, using this channel's fallback reliability
     * level.
     * <p>
     * This method may be blocking or not, depending on the implementation.
     *
     * @param packet the packet to be sent
     * @return this channel
     */
    default PChannel send(@NonNull Object packet) {
        return this.send(packet, this.fallbackReliability());
    }

    /**
     * Sends a single packet to the remote endpoint over this channel.
     * <p>
     * This method is non-blocking, and returns a future that may be used to track the packet as it is sent.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    channel's transport protocol, this channel's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @return a future which may be used to track the packet as it is sent
     */
    Future<Void> sendFuture(@NonNull Object packet, Reliability reliability);

    /**
     * Sends a single packet to the remote endpoint over this channel, using this channel's fallback reliability
     * level.
     * <p>
     * This method is non-blocking, and returns a future that may be used to track the packet as it is sent.
     *
     * @param packet the packet to be sent
     * @return a future which may be used to track the packet as it is sent
     */
    default Future<Void> sendFuture(@NonNull Object packet) {
        return this.sendFuture(packet, this.fallbackReliability());
    }
}
