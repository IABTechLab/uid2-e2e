package app;

import app.component.Operator;

import java.util.Set;

public final class Uid2IntegApps extends Apps {
    public Uid2IntegApps() {
        super(Set.of(
                new Operator("https://operator-integ.uidapi.com", "UID2 Integ Public Operator", Operator.Type.PUBLIC)
        ));
    }
}
