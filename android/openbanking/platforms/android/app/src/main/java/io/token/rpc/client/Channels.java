package io.token.rpc.client;

import static java.lang.String.format;

import io.grpc.ManagedChannel;

import java.io.Closeable;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A list of active channels.
 */
public final class Channels implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(Channels.class);
    private static final long SHUTDOWN_DURATION = 5_000L;

    private final WeakHashMap<ManagedChannel, String> channels = new WeakHashMap<>();

    /**
     * Adds a new channel to track. Purges all the closed channels before
     * adding the new one.
     *
     * @param target channel gRPC target
     * @param channel channel
     */
    public synchronized void add(String target, ManagedChannel channel) {
        Iterator<ManagedChannel> iterator = channels.keySet().iterator();
        while (iterator.hasNext()) {
            ManagedChannel ch = iterator.next();
            if (ch.isShutdown()) {
                logger.trace("Removing closed channel: {} -> {}", channels.get(ch), ch);
                iterator.remove();
            }
        }

        logger.trace("Adding channel: {} -> {}", target, channel);
        channels.put(channel, target);
    }

    /**
     * Closes all the open client channels. Invoked on service shutdown.
     */
    public synchronized void close() {
        for (Map.Entry<ManagedChannel, String> entry : channels.entrySet()) {
            ManagedChannel channel = entry.getKey();
            String target = entry.getValue();
            logger.info("Initiating RPC client shutdown: {} -> {}", target, channel);
            channel.shutdown();
        }

        for (Map.Entry<ManagedChannel, String> entry : channels.entrySet()) {
            ManagedChannel channel = entry.getKey();
            String target = entry.getValue();
            try {
                channel.awaitTermination(SHUTDOWN_DURATION, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                logger.error(format("Failed to shutdown channel %s -> %s", target, channel), ex);
            }
        }
    }
}
