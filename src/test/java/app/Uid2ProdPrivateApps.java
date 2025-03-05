package app;

import app.component.Operator;

import java.util.Set;

public final class Uid2ProdPrivateApps extends Apps {
    public Uid2ProdPrivateApps() {
        super(Set.of(
                new Operator("http://host:8080", "UID2 Prod - Private Operator", Operator.Type.PRIVATE)
        ));
    }
}
