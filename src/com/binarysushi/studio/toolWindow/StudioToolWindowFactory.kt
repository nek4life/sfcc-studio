package com.binarysushi.studio.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.HyperlinkLabel
import javax.swing.JComponent
import javax.swing.JPanel

class StudioToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val consoleService = project.service<StudioConsoleService>()
        val consoleView = consoleService.consoleView
        val syncLogContent = toolWindow.contentManager.factory.createContent(consoleView.component, "Sync Log", true)
        val quickLinksContent = toolWindow.contentManager.factory.createContent(linksComponent, "Quick Links", true)
        toolWindow.contentManager.addContent(syncLogContent)
        toolWindow.contentManager.addContent(quickLinksContent)
    }

    private val linksComponent: JComponent
        get() {
            val documentationLabel = HyperlinkLabel("Documentation")
            documentationLabel.setHyperlinkTarget("https://documentation.b2c.commercecloud.salesforce.com/DOC1/index.jsp")

            val slackLabel = HyperlinkLabel("Slack")
            slackLabel.setHyperlinkTarget("https://sfcc-unofficial.slack.com")

            val XChangeLabel = HyperlinkLabel("XChange")
            XChangeLabel.setHyperlinkTarget("https://xchange.demandware.com")

            val panel = JPanel()
            panel.add(documentationLabel)
            panel.add(XChangeLabel)
            panel.add(slackLabel)
            return panel
        }
}
