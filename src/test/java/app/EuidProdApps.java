package app;

import app.component.Operator;

import java.util.Set;

public final class EuidProdApps extends Apps {
    public EuidProdApps() {
        super(Set.of(
                new Operator("https://prod.euid.eu", "EUID Prod Public Operator", Operator.Type.PUBLIC)
        ));
    }
}
