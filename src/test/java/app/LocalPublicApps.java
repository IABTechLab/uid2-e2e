package app;

import app.component.Core;
import app.component.Operator;

import java.util.Set;

public final class LocalPublicApps extends Apps {
    public LocalPublicApps() {
        super(Set.of(
                new Core("http://localhost", 8088, "Local - Core"),
                new Operator("http://localhost", 8888, "Local - Public Operator", Operator.Type.PUBLIC)
        ));
    }
}
