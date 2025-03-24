package app;

import app.component.*;

import java.util.Set;

public final class LocalApps extends Apps {
    public LocalApps() {
        super(Set.of(
                new Localstack("http://localhost", 5001, "Local - Localstack"),
                new Admin("http://localhost", 8089, "Local - Admin"),
                new Core("http://localhost", 8088, "Local - Core"),
                new Operator("http://localhost", 8888, "Local - Public Operator", Operator.Type.PUBLIC),
                new Operator("http://localhost", 8180, "Local - Private Operator", Operator.Type.PRIVATE),
                new Optout("http://localhost", 8081, "Local - Optout"),
                new Prometheus("http://localhost", 9085, "Local - Validator Prometheus")
        ));
    }
}
