package com.binarysushi.studio.settings;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AddDeleteListPanel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

public class StudioCartridgeAddEditDeleteListPanel extends AddDeleteListPanel {
    final Project project = ProjectUtil.guessCurrentProject(getMainFormPanel());

    private JComponent getMainFormPanel() {
        return (JComponent) this.getParent();
    }

    public StudioCartridgeAddEditDeleteListPanel(String title, List initialList) {
        super(title, initialList);
    }

    @Nullable
    @Override
    protected Object findItemToAdd() {
        final FileChooserDescriptor folderChooserDescriptor = FileChooserDescriptorFactory.createMultipleFoldersDescriptor();
        folderChooserDescriptor.setTitle("Choose Cartridge Paths");
        folderChooserDescriptor.setRoots(project.getProjectFile());
        folderChooserDescriptor.withTreeRootVisible(true);
        folderChooserDescriptor.setShowFileSystemRoots(false);
        folderChooserDescriptor.setHideIgnored(true);
//        VirtualFile chosen = FileChooser.chooseFile(folderChooserDescriptor, project, null);
        VirtualFile[] chosen = FileChooser.chooseFiles(folderChooserDescriptor, project, null);
        ArrayList<String> paths = new ArrayList<String>();

        for (VirtualFile virtualFile: chosen) {
            ArrayList existingPaths = new ArrayList<>(Arrays.asList(this.getListItems()));
            if (!existingPaths.contains(virtualFile.getPath())) {
                addElement(virtualFile.getPath());
            }
        }

        return null;
    }



}
