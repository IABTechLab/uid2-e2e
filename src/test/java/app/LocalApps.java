package app;

import app.component.*;

import java.util.Set;

public final class LocalApps extends Apps {
    public LocalApps() {
        super(Set.of(
                // TODO: Add optout, snowflake-admin, monitorstack
                new Localstack("http://localhost", 5001, "Localstack"),
                new Admin("http://localhost", 8089, "Admin"),
                new Core("http://localhost", 8088, "Core"),
                new Operator("http://localhost", 8888, "Public Operator", Operator.Type.PUBLIC),
                new Operator("http://localhost", 8081, "Private Operator", Operator.Type.PRIVATE),
                new Loki("http://localhost", 3100, "Loki"),
                new Prometheus("http://localhost", 9085, "Validator Prometheus")
        ));
    }
}
