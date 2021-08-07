package com.binarysushi.studio.toolWindow

import com.intellij.execution.filters.OpenFileHyperlinkInfo
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.ide.browsers.OpenUrlHyperlinkInfo
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.text.SimpleDateFormat
import java.util.*

object ConsolePrinter {
    fun printToConsole(consoleView: ConsoleView, actionName: String, linkText: String, linkUrl: String) {
        val timeFormat = SimpleDateFormat("hh:mm:ss")

        consoleView.print(
            "[${timeFormat.format(Date())}] [${actionName}] ",
            ConsoleViewContentType.NORMAL_OUTPUT
        )
        consoleView.printHyperlink(
            linkText,
            OpenUrlHyperlinkInfo(linkUrl)
        )
        consoleView.print("\n", ConsoleViewContentType.NORMAL_OUTPUT)
    }

    fun printLocalAndRemoteFile(
        project: Project,
        consoleView: ConsoleView,
        message: String,
        localText: String,
        localFile: VirtualFile?,
        remoteText: String,
        remoteUrl: String?
    ) {
        val timeFormat = SimpleDateFormat("hh:mm:ss")

        consoleView.print(
            "[${timeFormat.format(Date())}] [${message}] ",
            ConsoleViewContentType.NORMAL_OUTPUT
        )

        if (localFile == null) {
            consoleView.print(localText, ConsoleViewContentType.NORMAL_OUTPUT)
        } else {
            consoleView.printHyperlink(
                localText,
                OpenFileHyperlinkInfo(project, localFile, 0)
            )
        }

        consoleView.print(" --> ", ConsoleViewContentType.NORMAL_OUTPUT)

        if (remoteUrl == null) {
            consoleView.print(remoteText, ConsoleViewContentType.NORMAL_OUTPUT)
        } else {
            consoleView.printHyperlink(
                remoteText,
                OpenUrlHyperlinkInfo(remoteUrl)
            )
        }
        consoleView.print("\n", ConsoleViewContentType.NORMAL_OUTPUT)
    }
}