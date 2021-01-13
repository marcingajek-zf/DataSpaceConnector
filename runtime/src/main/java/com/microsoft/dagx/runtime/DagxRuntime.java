package com.microsoft.dagx.runtime;

import com.microsoft.dagx.monitor.ConsoleMonitor;
import com.microsoft.dagx.spi.system.ServiceExtension;
import com.microsoft.dagx.spi.system.SystemExtension;
import com.microsoft.dagx.spi.types.TypeManager;
import com.microsoft.dagx.system.DefaultServiceExtensionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Main entrypoint for the default runtime.
 */
public class DagxRuntime {

    public static void main(String... arg) {
        TypeManager typeManager = new TypeManager();
        ConsoleMonitor monitor = new ConsoleMonitor();

        List<ServiceExtension> serviceExtensions = loadExtensions(ServiceExtension.class);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown(serviceExtensions, monitor)));

        bootServiceExtensions(serviceExtensions, typeManager, monitor);

        monitor.info("DA-GX ready");
    }

    private static void shutdown(List<ServiceExtension> serviceExtensions, ConsoleMonitor monitor) {
        serviceExtensions.forEach(ServiceExtension::shutdown);
        monitor.info("DA-GX shutdown complete");
    }

    private static void bootServiceExtensions(List<ServiceExtension> serviceExtensions, TypeManager typeManager, ConsoleMonitor monitor) {
        DefaultServiceExtensionContext context = new DefaultServiceExtensionContext(typeManager, monitor);
        serviceExtensions.forEach(extension -> extension.initialize(context));
        serviceExtensions.forEach(ServiceExtension::start);
    }

    public static <T extends SystemExtension> List<T> loadExtensions(Class<T> type) {
        List<T> extensions = new ArrayList<>();
        ServiceLoader.load(type).iterator().forEachRemaining(extensions::add);
        return extensions;
    }

}
