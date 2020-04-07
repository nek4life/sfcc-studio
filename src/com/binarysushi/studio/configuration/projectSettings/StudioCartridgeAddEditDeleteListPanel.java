package com.binarysushi.studio.configuration.projectSettings;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AddDeleteListPanel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudioCartridgeAddEditDeleteListPanel extends AddDeleteListPanel {
    private final Project myProject;

    private JComponent getMainFormPanel() {
        return (JComponent) this.getParent();
    }

    public StudioCartridgeAddEditDeleteListPanel(String title, List initialList, Project project) {
        super(title, initialList);
        myProject = project;
    }

    @Nullable
    @Override
    protected Object findItemToAdd() {
        final FileChooserDescriptor folderChooserDescriptor = FileChooserDescriptorFactory.createMultipleFoldersDescriptor();
        final VirtualFile basePath = LocalFileSystem.getInstance().findFileByPath(myProject.getBasePath());
        folderChooserDescriptor.setTitle("Choose Cartridge Paths");
        folderChooserDescriptor.setRoots(basePath);
        folderChooserDescriptor.withTreeRootVisible(true);
        folderChooserDescriptor.setShowFileSystemRoots(false);
        folderChooserDescriptor.setHideIgnored(true);
        VirtualFile[] chosen = FileChooser.chooseFiles(folderChooserDescriptor, myProject, basePath);

        for (VirtualFile virtualFile : chosen) {
            ArrayList existingPaths = new ArrayList<>(Arrays.asList(this.getListItems()));
            if (!existingPaths.contains(virtualFile.getPath())) {
                addElement(virtualFile.getPath());
            }
        }

        return null;
    }


}
