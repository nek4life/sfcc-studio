package com.binarysushi.studio.cartridges

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.intellij.openapi.project.Project
import java.nio.file.Paths

class CartridgePathUtil {
    companion object {

        /**
         * Returns the current cartridge roots for this project.
         *
         * Currently, based on project settings, but could potentially be used to handle multiple
         * active server settings in the future
         */
        fun getActiveCartridgeRoots(project: Project): ArrayList<String> {
            val config = StudioConfigurationProvider.getInstance(project)
            return config.cartridgeRoots
        }

        /**
         * Matches and returns the absolute cartridge root path for a file
         *
         * /Users/username/project/cartridges/app_storefront_base
         */
        fun getCartridgeRootPathForFile(project: Project, localFilePath: String): String? {
            for (root in getActiveCartridgeRoots(project)) {
                if (localFilePath.replace("\\", "/").contains(getCartridgeNameFromRootPath(root))) {
                    return root
                }
            }
            return null
        }

        /**
         * Returns the cartridge name from absolute cartridge root path
         *
         * /Users/username/project/cartridges/app_storefront_base --> app_storefront_base
         */
        fun getCartridgeNameFromRootPath(cartridgeRootPath: String): String {
            return Paths.get(cartridgeRootPath).fileName.toString()
        }

        /**
         * Returns a path relative to cartridge root.
         *
         * app_storefront/some/file/name.js --> /Users/username/project/cartridges/app_storefront_base/some/file/name.js
         */
        fun getCartridgeRelativeFilePath(cartridgeRootPath: String, localFilePath: String): String {
            val relativePath = localFilePath.substring(cartridgeRootPath.length)
            val cartridgeName = getCartridgeNameFromRootPath(cartridgeRootPath)
            return "$cartridgeName$relativePath".replace("\\", "/")
        }

        /**
         * Returns an absolute system path from a cartridge relative path.
         *
         * app_storefront_base/some/file/name.js --> /Users/username/project/cartridges/app_storefront_base/some/file/name.js
         */
        fun getAbsolutFilePathFromCartridgeRelativePath(project: Project, localFilePath: String): String? {
            for (root in getActiveCartridgeRoots(project)) {
                val cartridgeName = getCartridgeNameFromRootPath(root)
                if (localFilePath.contains(cartridgeName)) {
                    val nioRoot = Paths.get(root).parent
                    return "$nioRoot$localFilePath"
                }
            }

            return null
        }
    }
}
