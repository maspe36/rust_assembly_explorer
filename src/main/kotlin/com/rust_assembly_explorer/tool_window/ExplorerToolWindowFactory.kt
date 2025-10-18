package com.rust_assembly_explorer.tool_window

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowType
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.content.Content
import com.intellij.ui.dsl.builder.bindItem
import java.awt.BorderLayout
import javax.swing.JPanel

import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.toNullableProperty
import com.rust_assembly_explorer.CommandLineModelService
import com.rust_assembly_explorer.OutputType


class ExplorerToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        toolWindow.setType(ToolWindowType.DOCKED, null)
        val toolWindowContent = ExplorerToolWindow(project, toolWindow)
        val content: Content =
            ContentFactory.getInstance().createContent(toolWindowContent.contentPanel, "", false)
        toolWindow.getContentManager().addContent(content)
    }

    class ExplorerToolWindow(project: Project, toolWindow: ToolWindow) {
        private val cliModelService = CommandLineModelService.getInstance(project)
        val contentPanel = JPanel()

        init {
            val settingsBar = panel {
                row {
                    label("Format:")
                    comboBox(OutputType.entries)
                        .onChanged { cliModelService.model.output = it.item }
                        .comment("Select output format")
                }
            }

            contentPanel.add(settingsBar, BorderLayout.NORTH)
        }
    }
}
