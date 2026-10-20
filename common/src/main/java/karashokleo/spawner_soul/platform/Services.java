package karashokleo.spawner_soul.platform;

import karashokleo.spawner_soul.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

/** Resolves loader-specific implementations declared in META-INF/services. */
public final class Services {

    /** Platform adapter provided by the active loader. */
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    private Services() {
    }

    private static <T> T load(Class<T> service) {
        return ServiceLoader.load(service)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing platform service: " + service.getName()));
    }
}
