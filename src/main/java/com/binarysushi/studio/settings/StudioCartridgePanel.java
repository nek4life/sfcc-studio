package com.binarysushi.studio.settings;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

public class StudioCartridgePanel {
    private final Project myProject;
    private final StudioSettingsProvider mySettingsProvider;
    private JPanel panel1;
    private StudioCartridgeAddEditDeleteListPanel studioCartridgeAddEditDeleteListPanel1;

    StudioCartridgePanel(final Project project, StudioSettingsProvider settingsProvider) {
        myProject = project;
        mySettingsProvider = settingsProvider;
    }

    private void createUIComponents() {
        studioCartridgeAddEditDeleteListPanel1 = new StudioCartridgeAddEditDeleteListPanel("Cartridge Settings", mySettingsProvider.getCartridgeRoots());
    }

    public JPanel createPanel () {
        return panel1;
    }

    public boolean isModified() {
        Object[] listItems = studioCartridgeAddEditDeleteListPanel1.getListItems();
        return !mySettingsProvider.getCartridgeRoots().equals(new ArrayList<>(Arrays.asList(listItems)));
    }

    public ArrayList getListItems() {
        return new ArrayList<>(Arrays.asList(studioCartridgeAddEditDeleteListPanel1.getListItems()));
    }
}
