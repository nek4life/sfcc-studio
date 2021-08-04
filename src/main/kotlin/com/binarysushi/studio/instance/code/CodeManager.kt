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
    private fun createArchive(archiveName: String, dirs: List<File>): File {
        val tempDir = Paths.get(FileUtil.getTempDirectory(), "sfcc-studio").toFile()
        if (!tempDir.exists()) {
            FileUtil.createDirectory(tempDir)
        }

        val tempArchiveDir = Paths.get(tempDir.toString(), archiveName).toFile()
        FileUtil.createDirectory(tempArchiveDir)

        val zipFile = Paths.get(tempDir.toString(), "$archiveName.zip").toFile()

        try {
            val zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))

            for (dir in dirs) {
                if (dir.exists()) {
                    FileUtil.copyDir(dir, Paths.get(tempDir.toString(), dir.name).toFile())
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

    fun zipVersion(versionName: String, cartridgeDirs: List<File>): File {
        return createArchive(versionName, cartridgeDirs)
    }

    fun zipCartridge(cartridgeDir: File): File {
        return createArchive(cartridgeDir.name, listOf(cartridgeDir))
    }

    //    fun deployVersion(davClient: WebDavClient, version: String) {}
//
//    /**
//     * Deploys a cartridge to an SFCC instance. This consists of uploading an archive, removing the previous
//     * version, unzipping the archive and removing the temporary files
//     */
    fun deployCartridge(
        davClient: WebDavClient,
        version: String,
        cartridgeDir: File,
        indicator: ProgressIndicator?,
        consoleView: ConsoleView?
    ) {
        val serverVersionPath = "${TopLevelDavFolders.CARTRIDGES}/${version}"
        val serverCartridgePath = "${TopLevelDavFolders.CARTRIDGES}/${version}/${cartridgeDir.name}"
        val serverZipPath = "${TopLevelDavFolders.CARTRIDGES}/${version}/${cartridgeDir.name}.zip"

        indicator?.isIndeterminate = false

        val zipFile = zipCartridge(cartridgeDir)

        indicator?.fraction = .2

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

        indicator?.fraction = .4

        try {
            davClient.put(serverZipPath, zipFile, "application/octet-stream")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        indicator?.fraction = .6

        try {
            davClient.unzip(serverZipPath)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        indicator?.fraction = .8

        try {
            davClient.delete(serverZipPath)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        indicator?.fraction = 1.0

        FileUtil.delete(zipFile)

        val timeFormat = SimpleDateFormat("hh:mm:ss")
        val remoteUrl = "${davClient.baseURI}${TopLevelDavFolders.CARTRIDGES}/${version}/${cartridgeDir.name}"

        consoleView?.print(
            "[${timeFormat.format(Date())}] Uploaded: ",
            ConsoleViewContentType.NORMAL_OUTPUT
        )
        consoleView?.printHyperlink(
            cartridgeDir.name,
            OpenUrlHyperlinkInfo(remoteUrl)
        )
        consoleView?.print("\n", ConsoleViewContentType.NORMAL_OUTPUT)
    }

//    fun listVersions(api: OCAPIClient) {}
//    fun activateVersion(api: OCAPIClient, version: String) {}
}
