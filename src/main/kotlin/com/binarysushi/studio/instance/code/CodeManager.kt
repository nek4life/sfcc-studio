package com.binarysushi.studio.instance.code

import com.binarysushi.studio.instance.clients.OCAPIClient
import com.binarysushi.studio.instance.clients.WebDavClient
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.io.ZipUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths
import java.util.zip.ZipOutputStream

/**
 * Utilities for performing code related actions and interacting with the remote SFCC instance
 */
object CodeManager {

    /**
     * Creates a zip archive with the version as the file name. The archive contains one directory
     * with the version as the name with all the supplied cartridge folders as children
     *
     * version.zip
     *     |-- version
     *         |-- cartridge1
     *         |-- cartridge2
     *         |-- cartridge3
     *         |-- etc...
     *
     * This method cleans up the temporary directory, but does not clean up the temporary zip
     */
    fun zipVersion(version: String, cartridgeDirs: List<File>): File {
        val tempDir = Paths.get(FileUtil.getTempDirectory(), "sfcc-studio").toFile()
        if (!tempDir.exists()) {
            FileUtil.createDirectory(tempDir)
        }

        val versionDir = Paths.get(tempDir.toString(), version).toFile()
        FileUtil.createDirectory(versionDir)

        val zipFile = Paths.get(tempDir.toString(), "$version.zip").toFile()

        try {
            val zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))

            for (dir in cartridgeDirs) {
                if (dir.exists()) {
                    FileUtil.copyDir(dir, Paths.get(versionDir.toString(), dir.name).toFile())
                }
            }

            ZipUtil.addDirToZipRecursively(zipOutputStream, null, versionDir, version, null, null)
            zipOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        FileUtil.delete(versionDir)

        return zipFile
    }

    fun zipCartridge(cartridgeDir: File) : File {
        val cartridgeName = cartridgeDir.name
        val tempDir = Paths.get(FileUtil.getTempDirectory(), "sfcc-studio-${cartridgeName}").toFile()
        if (!tempDir.exists()) {
            FileUtil.createDirectory(tempDir)
        }

        val tempCartridgeDir = Paths.get(tempDir.toString(), cartridgeName).toFile()
        FileUtil.createDirectory(tempCartridgeDir)

        val zipFile = Paths.get(tempDir.toString(), "$cartridgeName.zip").toFile()
        val zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))
        if (cartridgeDir.exists()) {
            FileUtil.copyDir(cartridgeDir, tempCartridgeDir)
        }

        ZipUtil.addDirToZipRecursively(zipOutputStream, null, cartridgeDir, cartridgeName, null, null)
        zipOutputStream.close()
        return zipFile
    }

    fun deployVersion(davClient: WebDavClient, version: String) {}

    /**
     * Deploys a cartridge to an SFCC instance. This consists of uploading an archive, removing the previous
     * version, unzipping the archive and removing the temporary files
     */
    fun deployCartridge(davClient: WebDavClient, version: String, path: String) {}
    fun listVersions(api: OCAPIClient) {}
    fun activateVersion(api: OCAPIClient, version: String) {}
}
