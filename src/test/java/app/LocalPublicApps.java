package app;

import app.component.*;

import java.util.Set;

public final class LocalPublicApps extends Apps {
    public LocalPublicApps() {
        super(Set.of(
                new Operator("http://localhost", 8080, "Public Operator", Operator.Type.PUBLIC)
        ));
    }
}
