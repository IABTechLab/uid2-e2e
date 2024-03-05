package app;

import app.component.Core;
import app.component.Operator;

import java.util.Set;

public final class EuidIntegApps extends Apps {
    public EuidIntegApps() {
        super(Set.of(
                new Operator("https://integ.euid.eu", "EUID Integ - Public Operator", Operator.Type.PUBLIC),
                new Core("http://core.integ.euid.eu", "EUID Integ - Core")
        ));
    }
}
