package com.rust_assembly_explorer.settings

import com.rust_assembly_explorer.common.Bundle
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.NlsContexts
import javax.swing.JComponent

class PluginSettingsConfigurable : Configurable {
    private var component_ = PluginSettingsComponent()

    override fun getDisplayName(): @NlsContexts.ConfigurableName String {
        return Bundle.message("settings.configurable.name")
    }

    override fun createComponent(): JComponent {
        return component_.mainPanel
    }

    override fun isModified(): Boolean {
        return component_.pathBar.text != PLUGIN_SETTINGS.state.path
    }

    override fun apply() {
        PLUGIN_SETTINGS.state.path = component_.pathBar.text
    }

    override fun reset() {
        val state = PLUGIN_SETTINGS.state
        component_.pathBar.text = state.path
    }
}
