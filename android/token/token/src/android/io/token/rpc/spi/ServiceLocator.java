// package io.token.rpc.spi;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// import java.util.Iterator;
// import java.util.LinkedList;
// import java.util.ServiceConfigurationError;
// import java.util.ServiceLoader;

// /**
//  * A service locator to aid with rpc service implementation library lookup.
//  */
// public final class ServiceLocator {
//     private static Logger logger = LoggerFactory.getLogger(ServiceLocator.class);

//     private ServiceLocator() {
//         // preventing initialization
//     }

//     /**
//      * Looks up a provider service from classpath.
//      *
//      * @return a ManagedChannelBuilderProvider
//      */
//     public static ManagedChannelBuilderProvider lookupProviderService() {
//         LinkedList<ManagedChannelBuilderProvider> providers = new LinkedList<>();

//         System.out.println("Entere for service locatore===");
//         int debug;
//         debug = 2;


//         ServiceLoader<ManagedChannelBuilderProvider> loader =
//                 ServiceLoader.load(ManagedChannelBuilderProvider.class);

//         Iterator<ManagedChannelBuilderProvider> iterator = loader.iterator();

//         while (iterator.hasNext()) {
//             try {
//                 ManagedChannelBuilderProvider provider = iterator.next();
//                 providers.add(provider);
//             } catch (ServiceConfigurationError ex) {
//                 logger.debug(ex.getMessage(), ex);
//             }
//         }

//         if (providers.isEmpty()) {
//             logger.error("No ManagedChannelBuilderProvider found in classpath. "
//                     + "Did you import Netty or OkHttp library?");
//             throw new ServiceConfigurationError("ManagedChannelBuilderProvider no found.");
//         }

//         if (providers.size() > 1) {
//             logger.warn("Multiple ManagedChannelBuilderProviders found in classpath! "
//                     + "Did you import multiple implementations?");
//         }

//         return providers.getFirst();
//     }
// }
