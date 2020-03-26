package com.binarysushi.studio.browseActions

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

class StudioBrowseDocumentationAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        BrowserUtil.browse("https://documentation.b2c.commercecloud.salesforce.com/DOC1/index.jsp")
    }
}
