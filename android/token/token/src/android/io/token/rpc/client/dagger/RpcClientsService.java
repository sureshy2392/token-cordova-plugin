package io.token.rpc.client.dagger;

import com.google.common.util.concurrent.AbstractIdleService;
import io.token.rpc.client.RpcChannelFactory;

/**
 * Shuts down RPC clients.
 */
final class RpcClientsService extends AbstractIdleService {
    @Override
    protected void startUp() {}

    @Override
    protected void shutDown() {
        RpcChannelFactory.getAllChannels().close();
    }
}
