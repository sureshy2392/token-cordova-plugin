package io.token.rpc.client.healthchecks;

import static java.lang.String.format;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * A thin wrapper on top of the {@link SocketAddress}. Provides proper
 * {@link #hashCode} and {@link #equals} implementations.
 */
final class ServerInfo {
    private final SocketAddress info;

    ServerInfo(SocketAddress info) {
        this.info = info;
    }

    InetSocketAddress getAddress() {
        return (InetSocketAddress) info;
    }

    SocketAddress toSocketAddress() {
        return info;
    }

    String toGrpcTarget() {
        InetSocketAddress address = getAddress();
        return format(
                "dns:///%s:%d",
                address.getAddress().getHostName(),
                address.getPort());
    }

    @Override
    public int hashCode() {
        return info.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ServerInfo)) {
            return false;
        }

        ServerInfo other = (ServerInfo) obj;
        return getAddress().equals(other.getAddress());
    }

    @Override
    public String toString() {
        return getAddress().toString();
    }
}
