package com.binarysushi.studio.cartridges

import com.binarysushi.studio.configuration.projectSettings.*
import com.intellij.openapi.components.*
import com.intellij.openapi.project.*
import com.intellij.openapi.vfs.*
import org.jetbrains.annotations.*
import java.nio.file.*

class StudioFileManager(private val project: @NotNull Project) {
    /**
     * Takes and Salesforce B2C module path and returns a new StudioFile
     * by matching the path with an active cartridge root. If no cartridge root
     * is matched then the StudioFile cannot be created.
     */
    fun getStudioFile(appPath: String): StudioFile? {
        var match: StudioFile? = null

        project.service<StudioConfigurationProvider>().cartridgeRoots.forEach {
            // Added trailing slash so that cartridges that contain part of
            // another cartridge name are not matched
            if (appPath.startsWith("$it/")) {
                match = StudioFile(appPath, Paths.get(it).fileName.toString())
            }
        }

        return match
    }

    fun getStudioFile(virtualFile: VirtualFile): StudioFile? {
        return getStudioFile(virtualFile.path)
    }
}