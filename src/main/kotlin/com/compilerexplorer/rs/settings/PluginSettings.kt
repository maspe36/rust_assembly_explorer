package com.compilerexplorer.rs.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "com.compilerexplorer.rs.settings.PluginSettings", storages = [Storage("PluginSettings.xml")])
class PluginSettings : PersistentStateComponent<PluginSettings.State> {
    class State {
        var url = URL_OPTIONS.get(0)
    }

    private var state_ = State()

    override fun getState(): State {
        return state_
    }

    override fun loadState(state: State) {
        state_ = state
    }
}

// Singleton access for the persistent settings
val PLUGIN_SETTINGS = PluginSettings()
