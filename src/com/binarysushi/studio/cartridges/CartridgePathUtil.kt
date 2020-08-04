package com.binarysushi.studio.cartridges

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.nio.file.Paths
import java.util.*

class CartridgePathUtil() {
    companion object {

        /**
         * Returns the current cartridge roots for this project. Currently based on project settings, but could
         * potentially be used to handle multiple active server settings
         */
        private fun getActiveCartridgeRoots(project: Project): ArrayList<String> {
            val config = project.service<StudioConfigurationProvider>()
            return config.cartridgeRoots
        }

        /**
         * Matches and returns the absolute cartridge root path for a file /some/filesystem/path/for/this/cartridge/app_storefront
         */
        fun getCartridgeRootPathForFile(project: Project, localFilePath: String): String? {
            for (root in getActiveCartridgeRoots(project)) {
                if (localFilePath.replace("\\", "/").contains(root)) {
                    return root
                }
            }
            return null
        }

        /**
         * Returns the cartridge name. app_storefront
         */
        private fun getCartridgeNameFromRootPath(cartridgeRootPath: String): String {
            return Paths.get(cartridgeRootPath).fileName.toString()
        }

        /**
         * Returns a path relative to cartridge root. app_storefront/some/file/name.js
         */
        fun getCartridgeRelativeFilePath(cartridgeRootPath: String, localFilePath: String): String {
            val relativePath = localFilePath.substring(cartridgeRootPath.length)
            val cartridgeName = getCartridgeNameFromRootPath(cartridgeRootPath)
            return "$cartridgeName$relativePath".replace("\\", "/")
        }

        fun getAbsolutFilePathFromCartridgeRelativePath(project: Project, localFilePath: String): String? {
            for (root in getActiveCartridgeRoots(project)) {
                val cartridgeName = getCartridgeNameFromRootPath(root)
                if (localFilePath.contains(cartridgeName)) {
                    val nioRoot = Paths.get(root).parent;
                    return "${nioRoot}${localFilePath}"
                }
            }

            return null
        }
    }
}
