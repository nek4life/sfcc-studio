package com.binarysushi.studio.instance.code

import com.binarysushi.studio.instance.clients.TopLevelDavFolders
import com.binarysushi.studio.instance.clients.WebDavClient
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.ide.browsers.OpenUrlHyperlinkInfo
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.io.ZipUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipOutputStream

/**
 * Utilities for performing code related actions and interacting with the remote SFCC instance
 */
object CodeManager {
    private fun printToConsole(consoleView: ConsoleView, actionName: String, linkText: String, linkUrl: String) {
        val timeFormat = SimpleDateFormat("hh:mm:ss")

        consoleView.print(
            "[${timeFormat.format(Date())}] ${actionName}: ",
            ConsoleViewContentType.NORMAL_OUTPUT
        )
        consoleView.printHyperlink(
            linkText,
            OpenUrlHyperlinkInfo(linkUrl)
        )
        consoleView.print("\n", ConsoleViewContentType.NORMAL_OUTPUT)
    }

    /**
     * Creates a zip archive with the archiveName as the file name. The archive contains one directory
     * with the archiveName as the directory name with all the supplied cartridge folders as children
     *
     * archiveName.zip
     *     |-- archiveName
     *         |-- cartridge1
     *         |-- cartridge2
     *         |-- cartridge3
     *         |-- etc...
     *
     * This method cleans up the temporary directory, but does not clean up the temporary zip the
     * calling method should delete the file when done with it.
     */
    private fun createArchive(archiveName: String, dirs: List<File>, isVersion: Boolean = true): File {
        val tempDir = Paths.get(FileUtil.getTempDirectory(), "sfcc-studio").toFile()
        if (!tempDir.exists()) {
            FileUtil.createDirectory(tempDir)
        }

        val tempArchiveDir = Paths.get(tempDir.toString(), archiveName).toFile()
        FileUtil.createDirectory(tempArchiveDir)

        val zipFile = Paths.get(tempDir.toString(), "$archiveName.zip").toFile()
        val copyPath = if (isVersion) tempArchiveDir else tempDir

        try {
            val zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))

            for (dir in dirs) {
                if (dir.exists()) {
                    FileUtil.copyDir(dir, Paths.get(copyPath.toString(), dir.name).toFile())
                }
            }

            ZipUtil.addDirToZipRecursively(zipOutputStream, null, tempArchiveDir, archiveName, null, null)
            zipOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        FileUtil.delete(tempArchiveDir)

        return zipFile
    }

    private fun zipVersion(versionName: String, cartridgeDirs: List<File>): File {
        return createArchive(versionName, cartridgeDirs)
    }

    private fun zipCartridge(cartridgeDir: File): File {
        return createArchive(cartridgeDir.name, listOf(cartridgeDir), false)
    }

    /**
     * Deploys a cartridge to an SFCC instance. This consists of uploading an archive, removing the previous
     * version, unzipping the archive and removing the temporary files
     */
    fun deployVersion(
        davClient: WebDavClient,
        version: String,
        cartridgeDirs: List<File>,
        indicator: ProgressIndicator,
        consoleView: ConsoleView
    ) {
        val serverVersionPath = "${TopLevelDavFolders.CARTRIDGES}/${version}"
        val serverZipPath = "${TopLevelDavFolders.CARTRIDGES}/${version}.zip"

        indicator.isIndeterminate = false
        indicator.text = "Preparing archive..."
        indicator.fraction = .166

        val zipFile = zipVersion(version, cartridgeDirs)

        indicator.text = "Uploading archive..."
        indicator.fraction = .333

        try {
            davClient.put(serverZipPath, zipFile, "application/octet-stream")
        } catch (e: IOException) {
            e.printStackTrace()
        }


        indicator.text = "Removing previous version..."
        indicator.fraction = .5

        try {
            davClient.delete(serverVersionPath)
        } catch (e: IOException) {
            e.printStackTrace()
        }


        indicator.text = "Unzipping archive..."
        indicator.fraction = .66

        try {
            davClient.unzip(serverZipPath)
        } catch (e: IOException) {
            e.printStackTrace()
        }


        indicator.text = "Removing temporary files..."
        indicator.fraction = .83

        try {
            davClient.delete(serverZipPath)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        FileUtil.delete(zipFile)

        indicator.fraction = 1.0

        printToConsole(
            consoleView,
            "Cleaned Version",
            version,
            "${davClient.baseURI}${TopLevelDavFolders.CARTRIDGES}/${version}"
        )
    }


    /**
     * Deploys a cartridge to an SFCC instance. This consists of uploading an archive, removing the previous
     * version, unzipping the archive and removing the temporary files
     */
    fun deployCartridge(
        davClient: WebDavClient,
        version: String,
        cartridgeDir: File,
        indicator: ProgressIndicator,
        consoleView: ConsoleView
    ) {
        val serverVersionPath = "${TopLevelDavFolders.CARTRIDGES}/${version}"
        val serverCartridgePath = "${TopLevelDavFolders.CARTRIDGES}/${version}/${cartridgeDir.name}"
        val serverZipPath = "${TopLevelDavFolders.CARTRIDGES}/${version}/${cartridgeDir.name}.zip"

        indicator.isIndeterminate = false

        val zipFile = zipCartridge(cartridgeDir)

        indicator.fraction = .2

        try {
            if (!davClient.exists(serverVersionPath)) {
                davClient.createDirectory(serverVersionPath)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            if (davClient.exists(serverCartridgePath)) {
                davClient.delete(serverCartridgePath)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        indicator.fraction = .4

        try {
            davClient.put(serverZipPath, zipFile, "application/octet-stream")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        indicator.fraction = .6

        try {
            davClient.unzip(serverZipPath)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        indicator.fraction = .8

        try {
            davClient.delete(serverZipPath)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        indicator.fraction = 1.0

        FileUtil.delete(zipFile)

        printToConsole(
            consoleView,
            "Uploaded Cartridge",
            cartridgeDir.name,
            "${davClient.baseURI}${TopLevelDavFolders.CARTRIDGES}/${version}/${cartridgeDir.name}"
        )
    }

//    fun listVersions(api: OCAPIClient) {}
//    fun activateVersion(api: OCAPIClient, version: String) {}
}
