package suite.core;

import app.AppsMap;
import app.component.Core;
import org.junit.jupiter.params.provider.Arguments;

import java.util.HashSet;
import java.util.Set;

public final class TestData {
    private TestData() {
    }

    public static Set<Arguments> baseArgs() {
        Set<Core> cores = AppsMap.getApps(Core.class);
        Set<Arguments> args = new HashSet<>();
        for (Core core : cores) {
            args.add(Arguments.of(core));
        }

        return args;
    }

    public static Set<Arguments> refreshArgs() {
        Set<Core> cores = AppsMap.getApps(Core.class);
        Set<Arguments> args = new HashSet<>();
        for (Core core : cores) {
            args.add(Arguments.of(core, "/key/acl/refresh", "keys_acl"));
            args.add(Arguments.of(core, "/key/refresh", "keys"));
        }

        return args;
    }

    public static Set<Arguments> refreshArgsEncrypted() {
        Set<Core> cores = AppsMap.getApps(Core.class);
        Set<Arguments> args = new HashSet<>();
        for (Core core : cores) {
            args.add(Arguments.of(core, "/key/keyset/refresh", "keysets"));
            args.add(Arguments.of(core, "/key/keyset-keys/refresh", "keyset_keys"));
            args.add(Arguments.of(core, "/clients/refresh", "client_keys"));
            args.add(Arguments.of(core, "/sites/refresh", "sites"));
            args.add(Arguments.of(core, "/client_side_keypairs/refresh", "client_side_keypairs"));
        }

        return args;
    }

    public static Set<Arguments> collectionEndpointArgs() {
        Set<Core> cores = AppsMap.getApps(Core.class);
        Set<Arguments> args = new HashSet<>();
        for (Core core : cores) {
            args.add(Arguments.of(core, "/salt/refresh", "salts"));
        }

        return args;
    }

    public static Set<Arguments> optOutRefreshArgs() {
        Set<Core> cores = AppsMap.getApps(Core.class);
        Set<Arguments> args = new HashSet<>();
        for (Core core : cores) {
            args.add(Arguments.of(core, "/operators/refresh", "operators"));
            args.add(Arguments.of(core, "/partners/refresh", "partners"));
        }

        return args;
    }

}
