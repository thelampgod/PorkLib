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

package net.daporkchop.lib.http.client.factory;

import lombok.NonNull;
import net.daporkchop.lib.http.RequestMethod;
import net.daporkchop.lib.http.client.ClientHttpSession;
import net.daporkchop.lib.http.client.HttpClient;
import net.daporkchop.lib.http.util.Constants;

import java.net.SocketAddress;

/**
 * Base interface for a factory for outgoing HTTP requests from an {@link HttpClient}.
 * <p>
 * Instances of implementations of this class may be re-used to issue new requests as long as the underlying {@link HttpClient} is still alive. Once
 * the {@link HttpClient} instance has been closed, the behavior of all methods is undefined.
 *
 * @author DaPorkchop_
 */
public interface RequestFactory {
    /**
     * @return the {@link HttpClient} instance that requests will be issued from
     */
    HttpClient client();

    /**
     * Sets the host string of the destination HTTP server (IP address or domain name).
     * <p>
     * For example, in the url {@code https://www.example.com:8080/files/data.zip}, this field would be set to {@code "www.example.com"}.
     * <p>
     * Calling this method will clear the value of {@link #address(SocketAddress)}.
     * <p>
     * In order for requests to be issued, users must either configure both {@link #host(String)} and {@link #port(int)}, or
     * configure {@link #address(SocketAddress)}.
     *
     * @param host the new host string
     * @return this {@link RequestFactory} instance
     */
    RequestFactory host(@NonNull String host);

    /**
     * Sets the port number of the destination HTTP server.
     * <p>
     * For example, in the url {@code https://www.example.com:8080/files/data.zip}, this field would be set to {@code 8080}.
     * <p>
     * Calling this method will clear the value of {@link #address(SocketAddress)}.
     * <p>
     * In order for requests to be issued, users must either configure both {@link #host(String)} and {@link #port(int)}, or
     * configure {@link #address(SocketAddress)}.
     *
     * @param port the new port number
     * @return this {@link RequestFactory} instance
     */
    RequestFactory port(int port);

    /**
     * Sets the remote address of the destination HTTP server.
     * <p>
     * For example, in the url {@code https://www.example.com:8080/files/data.zip}, this field would be set to
     * {@code new InetSocketAddress("www.example.com", 8080)}.
     * <p>
     * Calling this method will clear the values of both {@link #host(String)} and {@link #port(int)}.
     * <p>
     * In order for requests to be issued, either {@link #host(String)} and {@link #port(int)} or only {@link #address(SocketAddress)} must be set.
     *
     * @param address the new address
     * @return this {@link RequestFactory} instance
     */
    RequestFactory address(@NonNull SocketAddress address);

    /**
     * Sets the local (source) address that the request will be sent from.
     * <p>
     * If {@code null} (default), then whatever the default address is will be used.
     * <p>
     * Not all implementations will respect this option.
     *
     * @param localAddress the new local address (use {@code null} to reset)
     * @return this {@link RequestFactory} instance
     */
    RequestFactory localAddress(SocketAddress localAddress);

    /**
     * Sets the path of the HTTP request.
     * <p>
     * For example, in the url {@code https://www.example.com:8080/files/data.zip}, this field would need to be set to {@code "/files/data.zip"}.
     * <p>
     * In order for requests to be issued, this value must be set.
     *
     * @param path the new path
     * @return this {@link RequestFactory} instance
     */
    RequestFactory path(@NonNull String path);

    /**
     * Sets the method of the HTTP request.
     * <p>
     * This value is set to {@link RequestMethod#GET} by default.
     *
     * @param method the new HTTP method
     * @return this {@link RequestFactory} instance
     */
    RequestFactory method(@NonNull RequestMethod method);

    /**
     * Sets whether or not the request will be an HTTPS request.
     * <p>
     * This value is set to {@code false} by default.
     *
     * @param https whether or not the request will be an HTTPS request
     * @return this {@link RequestFactory} instance
     */
    RequestFactory https(boolean https);

    /**
     * Resets this {@link RequestFactory} instance to its default settings.
     *
     * @return this {@link RequestFactory} instance
     */
    RequestFactory reset();

    /**
     * Configures this {@link RequestFactory} instance to connect to the given URL.
     *
     * @param url the URL to connect to
     * @return this {@link RequestFactory} instance
     */
    default RequestFactory configure(@NonNull CharSequence url) {
        Constants.prepareRequestBuilderForUrl(this, url);
        return this;
    }

    /**
     * Creates a new HTTP session with the currently configured settings and issues it.
     *
     * @return the newly created HTTP session
     */
    ClientHttpSession send();
}
