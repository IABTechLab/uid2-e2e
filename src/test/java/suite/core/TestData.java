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
}
