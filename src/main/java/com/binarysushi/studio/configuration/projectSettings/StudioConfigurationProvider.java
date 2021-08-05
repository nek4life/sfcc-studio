package com.binarysushi.studio.configuration.projectSettings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;


@State(
        name = "StudioConfigurationProvider",
        storages = {
                @Storage("sfcc-settings.xml")
        }
)
public class StudioConfigurationProvider implements PersistentStateComponent<StudioConfigurationProvider.State> {
    private final State myState = new State();

    public static StudioConfigurationProvider getInstance(Project project) {
        return project.getService(StudioConfigurationProvider.class);
    }

    public String getHostname() {
        return myState.hostname;
    }

    public void setHostname(String hostname) {
        myState.hostname = hostname;
    }

    public String getUsername() {
        return myState.username;
    }

    public void setUsername(String username) {
        myState.username = username;
    }

    public String getPassword() {
        return myState.password;
    }

    public void setPassword(String password) {
        myState.password = password;
    }

    public String getVersion() {
        return myState.version;
    }

    public void setVersion(String version) {
        myState.version = version;
    }

    public boolean getAutoUploadEnabled() {
        return myState.autoUploadEnabled;
    }

    public void setAutoUploadEnabled(boolean autoUploadEnabled) {
        myState.autoUploadEnabled = autoUploadEnabled;
    }

    public ArrayList<String> getCartridgeRoots() {
        return myState.cartridgeRoots;
    }

    public void setCartridgeRoots(ArrayList<String> cartridgeRoots) {
        myState.cartridgeRoots = cartridgeRoots;
    }

    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(State state) {
        myState.hostname = state.hostname;
        myState.username = state.username;
        myState.password = state.password;
        myState.version = state.version;
        myState.autoUploadEnabled = state.autoUploadEnabled;
        myState.cartridgeRoots = state.cartridgeRoots;
    }

    public static class State {
        public String hostname;
        public String username;
        public String password;
        public String version;
        public boolean autoUploadEnabled;
        public ArrayList<String> cartridgeRoots = new ArrayList<>();
    }
}
