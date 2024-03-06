package app;

import app.component.*;

import java.util.Set;

public final class LocalPrivateApps extends Apps {
    public LocalPrivateApps() {
        super(Set.of(
                new Operator("http://localhost", 8180, "Private Operator", Operator.Type.PRIVATE)
        ));
    }
}
