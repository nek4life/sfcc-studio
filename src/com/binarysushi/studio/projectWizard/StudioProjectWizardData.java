package com.binarysushi.studio.projectWizard;

public class StudioProjectWizardData {
    public final String hostname;
    public final String username;
    public final String password;
    public final String version;
    public final Boolean autoUploadEnabled;

    public StudioProjectWizardData(String hostname, String username, String password, String version, Boolean autoUploadEnabled) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.version = version;
        this.autoUploadEnabled = autoUploadEnabled;
    }
}
