package com.binarysushi.studio.configuration;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

public class StudioCartridgePanel {
    private final Project myProject;
    private final StudioConfigurationProvider myConfigurationProvider;
    private JPanel panel1;
    private StudioCartridgeAddEditDeleteListPanel studioCartridgeAddEditDeleteListPanel1;

    StudioCartridgePanel(final Project project, StudioConfigurationProvider configurationProvider) {
        myProject = project;
        myConfigurationProvider = configurationProvider;
    }

    private void createUIComponents() {
        studioCartridgeAddEditDeleteListPanel1 = new StudioCartridgeAddEditDeleteListPanel("Cartridge Settings", myConfigurationProvider.getCartridgeRoots());
    }

    public JPanel createPanel () {
        return panel1;
    }

    public boolean isModified() {
        Object[] listItems = studioCartridgeAddEditDeleteListPanel1.getListItems();
        return !myConfigurationProvider.getCartridgeRoots().equals(new ArrayList<>(Arrays.asList(listItems)));
    }

    public ArrayList getListItems() {
        return new ArrayList<>(Arrays.asList(studioCartridgeAddEditDeleteListPanel1.getListItems()));
    }
}
