package com.binarysushi.studio.webdav

import com.binarysushi.studio.instance.code.CodeManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import java.io.File

class UploadCartridgeAction: AnAction() {
    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isVisible = file!!.isDirectory
    }


    override fun actionPerformed(e: AnActionEvent) {
        val zipFile = CodeManager.zipCartridge(File("/Users/choinierec/projects/orv-orvis-sfcc-website/app_orv"))
    }
}