package com.binarysushi.studio.toolWindow

import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.ide.browsers.OpenUrlHyperlinkInfo
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
}