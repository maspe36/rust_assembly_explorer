package com.rust_assembly_explorer.tool_window

import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowType
import com.intellij.ui.TextFieldWithAutoCompletion
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.content.Content
import com.intellij.ui.dsl.builder.AlignX
import java.awt.BorderLayout
import javax.swing.JPanel

import com.intellij.ui.dsl.builder.panel
import com.rust_assembly_explorer.CommandLineModelService
import com.rust_assembly_explorer.OutputType
import com.rust_assembly_explorer.common.RustcService


@Service(Service.Level.PROJECT)
class ExplorerToolWindowService(private val project: Project) {
    private var toolWindow: ExplorerToolWindowFactory.ExplorerToolWindow? = null

    fun setToolWindow(window: ExplorerToolWindowFactory.ExplorerToolWindow) {
        toolWindow = window
    }

    fun updateAssemblyOutput(assemblyCode: String) {
        invokeLater {
            runWriteAction {
                toolWindow?.setAssemblyOutput(assemblyCode)
            }
        }
    }

    companion object {
        fun getInstance(project: Project): ExplorerToolWindowService {
            return project.getService(ExplorerToolWindowService::class.java)
        }
    }
}


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
        private val rustcService = RustcService.getInstance(project)
        val contentPanel = JPanel()
        private val editor: EditorEx

        init {
            // Register this tool window with the service
            ExplorerToolWindowService.getInstance(project).setToolWindow(this)

            val targetField = TextFieldWithAutoCompletion.create(
                project,
                rustcService.getTargetList(),
                false,
                rustcService.getActiveToolchain()
            )

            targetField.setPreferredWidth(200)

            // Make sure the initial value of the CLI Model is set
            cliModelService.model.target = targetField.text

            targetField.addDocumentListener(object : DocumentListener {
                override fun documentChanged(event: com.intellij.openapi.editor.event.DocumentEvent) {
                    cliModelService.model.target = targetField.text
                }
            })

            val settingsBar = panel {
                row {
                    label("Target:")
                    cell(targetField)
                        // TODO (SP) I don't like this formatting. It shrinks and just looks weird.
                        .align(AlignX.FILL)
                    label("Output:")
                    comboBox(OutputType.entries)
                        .onChanged { cliModelService.model.output = it.item }
                }
            }

            // Create editor for assembly code output
            val editorFactory = EditorFactory.getInstance()
            val document = editorFactory.createDocument("")
            editor = editorFactory.createEditor(document, project) as EditorEx

            // Configure editor for read-only assembly code viewing
            editor.settings.isLineNumbersShown = true
            editor.settings.isLineMarkerAreaShown = false
            editor.settings.isFoldingOutlineShown = true
            editor.settings.isRightMarginShown = false
            editor.setViewer(true) // Make it read-only

            // Set assembly or plain text file type for syntax highlighting
            val asmFileType = FileTypeManager.getInstance().getFileTypeByExtension("asm")
            editor.setHighlighter(
                EditorHighlighterFactory.getInstance().createEditorHighlighter(project, asmFileType)
            )

            contentPanel.layout = BorderLayout()
            contentPanel.add(settingsBar, BorderLayout.NORTH)
            contentPanel.add(editor.component, BorderLayout.CENTER)
        }

        // Method to update the assembly output
        fun setAssemblyOutput(assemblyCode: String) {
            editor.document.setText(assemblyCode)
        }

        // Clean up editor when tool window is disposed
        fun dispose() {
            EditorFactory.getInstance().releaseEditor(editor)
        }
    }
}
