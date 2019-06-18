package io.token.rpc.client.dagger;

import com.google.common.util.concurrent.Service;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

import javax.inject.Singleton;

/**
 * Dagger RPC client module. Configures a service that shuts down RPC clients.
 */
@Module
public final class RpcClientsShutdownModule {
    @Provides
    @Singleton
    @IntoSet
    Service provideRpcClientService() {
        return new RpcClientsService();
    }
}
