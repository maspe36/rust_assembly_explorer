package com.rust_assembly_explorer.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import java.awt.Dimension
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel

// TODO (SP) clean up the UI messages. These are wonky, I need some consistency
// TODO (SP) Provide better UX feedback if the tool isn't found?
class PluginSettingsComponent {
    val pathBar = TextFieldWithBrowseButton().apply {
        addBrowseFolderListener(
            TextBrowseFolderListener(
                FileChooserDescriptorFactory.createSingleFileDescriptor()
                    .withTitle("Assembly Viewing Tool")
                    .withDescription("Choose the path to your cargo-show-asm executable")
            )
        )
    }

    private val pathRow = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.X_AXIS)

        add(JBLabel("Path:"))
        add(Box.createRigidArea(Dimension(5, 0))) // small gap

        add(pathBar)
    }

    val mainPanel = FormBuilder.createFormBuilder()
        .addComponent(pathRow)
        .addComponentFillVertically(JPanel(), 0)
        .getPanel()
}
