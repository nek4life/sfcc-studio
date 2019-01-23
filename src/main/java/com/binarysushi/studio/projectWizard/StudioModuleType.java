package com.binarysushi.studio.projectWizard;

import com.binarysushi.studio.StudioBundle;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class StudioModuleType extends ModuleType<StudioModuleBuilder> {
    private static final String ID = "SFCC_STUDIO_MODULE";

    public StudioModuleType() {
        super(ID);
    }

    @NotNull
    public static StudioModuleType getInstance() {
        return (StudioModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public StudioModuleBuilder createModuleBuilder() {
        return new StudioModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return StudioBundle.message("studio.project.name");
    }

    @NotNull
    @Override
    public String getDescription() {
        return StudioBundle.message("studio.project.description");
    }

    @Override
    public Icon getNodeIcon(boolean isOpened) {
        return null;
    }
}
