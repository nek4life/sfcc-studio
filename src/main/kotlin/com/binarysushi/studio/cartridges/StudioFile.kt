package com.binarysushi.studio.cartridges

import com.binarysushi.studio.configuration.projectSettings.*
import com.intellij.openapi.components.*
import com.intellij.openapi.project.*
import com.intellij.openapi.util.io.*
import com.intellij.openapi.vfs.*
import java.nio.file.*

/**
 * A class that represents a SFCC Studio file and provides many of the needed
 * path representations for both the local file system and the remote file system.
 *
 * All file manipulations are done with a system independent path. Similar to how
 * the IDE stores path representations internally.
 */
class StudioFile(private val fileSystemPath: String, private val cartridgeName: String) {
    constructor(virtualFile: VirtualFile, cartridgeName: String) : this(virtualFile.path, cartridgeName)

    private val independentPath = FileUtil.toSystemIndependentName(fileSystemPath)
    private val nioPath = Paths.get(independentPath)
    private val pathParts = nioPath.map { it.fileName.toString() }
    private val cartridgeNameIndex = pathParts.indexOf(cartridgeName)


    fun isInActiveCartridgePath(project: Project): Boolean {
        return project.service<StudioConfigurationProvider>().cartridgeRoots.indexOf(getRootPath()) != -1
    }

    /**
     * Determines if the current file or directory belongs to a cartridge path
     */
    fun isCartridgeChild(): Boolean {
        return !isModulesChild()
    }

    /**
     * Determines if the current file or directory belongs to a modules path
     */
    fun isModulesChild(): Boolean {
        return cartridgeName == "modules"
    }

    /**
     * Get the containing cartridge name for the current file
     */
    fun getCartridgeName(): String {
        return cartridgeName
    }

    /**
     * Returns an absolute cartridge root path as represented by the file system
     *
     * /User/binarysushi/projects/storefront_reference_architecture/cartridges/app_storefront_base
     */
    fun getRootPath(): String {
        return Paths.get("/", *pathParts.subList(0, cartridgeNameIndex - 1).toTypedArray()).toString()
    }

    /**
     * Returns an absolute path to the file as represented by the file system
     *
     * /User/binarysushi/projects/storefront_reference_architecture/cartridges/app_storefront_base/cartridge/controllers/Account.js
     */
    fun getPath(): String {
        return fileSystemPath
    }

    /**
     * Returns a relative path for use throughout the Salesforce B2C application
     *
     * app_storefront_base/cartridge/controllers/Account.js
     */
    fun getModulePath(): String {
        return try {
            pathParts.subList(cartridgeNameIndex, pathParts.size).joinToString("/")
        } catch (e: IndexOutOfBoundsException) {
            ""
        }

    }
}