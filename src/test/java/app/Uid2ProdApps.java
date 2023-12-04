package app;

import app.component.Operator;

import java.util.Set;

public final class Uid2ProdApps extends Apps {
    public Uid2ProdApps() {
        super(Set.of(
                new Operator("https://prod.uidapi.com", "UID2 Prod Public Operator", Operator.Type.PUBLIC)
        ));
    }
}
