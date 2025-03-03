package app;

import app.component.Operator;

import java.util.Set;

public final class PrivateApps extends Apps {
    public PrivateApps() {
        super(Set.of(
                new Operator("http://host:8080", "Private Operator", Operator.Type.PRIVATE)
        ));
    }
}
