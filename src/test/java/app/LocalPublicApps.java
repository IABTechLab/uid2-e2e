package app;

import app.component.Operator;

import java.util.Set;

public final class LocalPublicApps extends Apps {
    public LocalPublicApps() {
        super(Set.of(
                new Operator("http://localhost", 8888, "Public Operator", Operator.Type.PUBLIC)
        ));
    }
}
