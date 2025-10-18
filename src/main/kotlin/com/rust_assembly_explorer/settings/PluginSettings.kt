package com.rust_assembly_explorer.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "com.rust_assembly_explorer.settings.PluginSettings", storages = [Storage("RustAssemblyExplorer.xml")])
class PluginSettings : PersistentStateComponent<PluginSettings.State> {
    class State {
        var path = "${System.getenv("HOME")}/.cargo/bin/cargo-asm"
    }

    private var state = State()

    override fun getState(): State {
        return state
    }

    override fun loadState(s: State) {
        state = s
    }
}

// Singleton access for the persistent settings
val PLUGIN_SETTINGS = PluginSettings()
