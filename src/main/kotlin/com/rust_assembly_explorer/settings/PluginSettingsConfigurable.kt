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
        return component_.main_panel_
    }

    override fun isModified(): Boolean {
        return component_.url_.editor.item.toString() != PLUGIN_SETTINGS.state.url
    }

    override fun apply() {
        PLUGIN_SETTINGS.state.url = component_.url_.editor.item.toString()
    }

    override fun reset() {
        val state = PLUGIN_SETTINGS.state
        component_.url_.editor.item = state.url
    }
}
