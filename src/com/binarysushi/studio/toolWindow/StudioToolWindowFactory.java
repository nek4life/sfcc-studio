package com.binarysushi.studio.toolWindow;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class StudioToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        StudioConsoleService consoleService = ServiceManager.getService(project, StudioConsoleService.class);
        ConsoleView consoleView = consoleService.getConsoleView();
        Content syncLogContent = toolWindow.getContentManager().getFactory().createContent(consoleView.getComponent(), "Sync Log", true);
        Content quickLinksContent = toolWindow.getContentManager().getFactory().createContent(getLinksComponent(), "Quick Links", true);



        toolWindow.getContentManager().addContent(syncLogContent);
        toolWindow.getContentManager().addContent(quickLinksContent);
    }

    private JComponent getLinksComponent() {
        HyperlinkLabel documentationLabel = new HyperlinkLabel("Documentation");
        documentationLabel.setHyperlinkTarget("https://documentation.b2c.commercecloud.salesforce.com/DOC1/index.jsp");

        HyperlinkLabel slackLabel  = new HyperlinkLabel("Slack");
        slackLabel.setHyperlinkTarget("https://sfcc-unofficial.slack.com");

        HyperlinkLabel XChangeLabel  = new HyperlinkLabel("XChange");
        XChangeLabel.setHyperlinkTarget("https://xchange.demandware.com");

        JPanel panel = new JPanel();




        panel.add(documentationLabel);
        panel.add(XChangeLabel);
        panel.add(slackLabel);


        return panel;
    }
}
