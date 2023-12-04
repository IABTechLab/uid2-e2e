package app;

import app.component.App;

import java.util.Set;

public abstract class Apps {
    private final Set<App> apps;

    public Apps(Set<App> apps) {
        this.apps = apps;
    }

    public Set<App> getApps() {
        return apps;
    }
}
