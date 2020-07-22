package com.binarysushi.studio.cartridges

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.nio.file.Paths
import java.util.*

class CartridgePathUtil() {
    companion object {

        private fun getActiveCartridgeRoots(project: Project): ArrayList<String> {
            val config = project.service<StudioConfigurationProvider>()
            return config.cartridgeRoots
        }

        fun getCartridgeRootPathForFile(project: Project, localeFilePath: String): String? {
            for (root in getActiveCartridgeRoots(project)) {
                if (localeFilePath.replace("\\", "/").contains(root)) {
                    return root
                }
            }
            return null
        }

        private fun getCartridgeName(cartridgeRootPath: String): String {
            return Paths.get(cartridgeRootPath).fileName.toString()
        }

        fun getCartridgePath(cartridgeRootPath: String, localFilePath: String): String {
            val relativePath = localFilePath.substring(cartridgeRootPath.length)
            val cartridgeName = getCartridgeName(cartridgeRootPath)
            return "$cartridgeName$relativePath"
        }
    }
}
