package com.compilerexplorer.rs.settings

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import java.awt.Dimension
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel


val URL_OPTIONS = arrayOf("http://localhost:10240", "https://godbolt.org", "https://compiler-explorer.org")

class PluginSettingsComponent {
    val url_ = ComboBox(URL_OPTIONS).apply {
        isEditable = true
        maximumSize = Dimension(Int.MAX_VALUE, preferredSize.height) // allow horizontal growth
    }
    var test_connection_ = JButton("Test Connection")

    private val url_row_ = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.X_AXIS)

        add(JBLabel("Compiler Explorer URL:"))
        add(Box.createRigidArea(Dimension(5, 0))) // small gap

        add(url_)
        add(Box.createRigidArea(Dimension(5, 0))) // small gap

        add(test_connection_)
    }

    val main_panel_ = FormBuilder.createFormBuilder()
        .addComponent(url_row_)
        .addComponentFillVertically(JPanel(), 0)
        .getPanel()

    init {
        test_connection_.addActionListener {
            // Make a call to the URL to verify that compiler explorer is available
            thisLogger().warn("Testing connection to \"${url_.editor.item}\"...")
        }
    }
}
