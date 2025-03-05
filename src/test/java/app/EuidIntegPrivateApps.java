package app;

import app.component.Operator;

import java.util.Set;

public final class EuidIntegPrivateApps extends Apps {
    public EuidIntegPrivateApps() {
        super(Set.of(
                new Operator("http://host:80", "EUID Integ - Private Operator", Operator.Type.PRIVATE)
        ));
    }
}
