package com.rust_assembly_explorer.common

import com.intellij.execution.ExecutionListener
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.util.ExecUtil
import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.rust_assembly_explorer.CommandLineModelService
import com.rust_assembly_explorer.settings.PLUGIN_SETTINGS
import com.rust_assembly_explorer.tool_window.ExplorerToolWindowService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.rust.cargo.runconfig.command.CargoCommandConfiguration


@Service(Service.Level.PROJECT)
class CargoAsmService(private val project: Project, private val cs: CoroutineScope) {
    private val cliModelService = CommandLineModelService.getInstance(project)
    private val toolWindowService = ExplorerToolWindowService.getInstance(project)


    data class State(
        var cachedSymbols: MutableMap<String, String> = mutableMapOf(),
        var output: String = "",
    )

    private var state = State()

    fun run() {
        cs.launch {
            withBackgroundProgress(project, "Running `cargo asm`") {
                // TODO (SP) Parse the UI elements to build the command line args? Does that live in the service?
                val commandLine = GeneralCommandLine(PLUGIN_SETTINGS.state.path)
                    .withWorkDirectory(project.basePath)
                    .withParameters(cliModelService.model.toArgs())

                println(commandLine)

                // We do not set a timeout here because this _can_ run `cargo build`
                // TODO (SP) I may need to report progress here somehow?
                val processOutput: ProcessOutput = ExecUtil.execAndGetOutput(commandLine)

                // Handle the output
                if (processOutput.exitCode == 0) {
                    // TODO (SP) Write a module that parses this stdout and pulls the symbols out.
                    //  Probably a map with the file and line as the key?
                    //  But the value, maybe its a range back to the stored output?
                    //  There will be output there that is not in the symbol map which I need to display.

                    // TODO (SP) Display this output in a new UI window
                    state.output = processOutput.stdout

                    toolWindowService.updateAssemblyOutput(processOutput.stdout)
                } else {
                    println("Cargo ASM failed with exit code: ${processOutput.exitCode}")
                    println("Error output:\n${processOutput.stderr}")
                }
            }
        }
    }
}

/**
 * Listen for various Cargo Run Configurations triggered by the user and execute the [CargoAsmService].
 */
class CargoCommandListener : ExecutionListener {
    // TODO (SP) Maybe see if we can somehow hook into the VFS? or some other mechanism?
    //  Ultimately, `cargo build` needs to run, either by our hand or someone else's.

    companion object {
        private val TRIGGER_COMMANDS = listOf("asm", "build")
    }

    override fun processTerminated(
        executorId: String,
        env: ExecutionEnvironment,
        handler: ProcessHandler,
        exitCode: Int
    ) {
        val profile = env.runProfile

        // If this isn't a cargo command run configuration or the run failed, just skip.
        if (profile !is CargoCommandConfiguration || exitCode != 0) {
            return
        }

        // If the successful command starts with any of the commands we are listening for,
        // trigger our cargo asm service.
        profile.parametersHolder?.command?.let { run_command ->
            if (TRIGGER_COMMANDS.any { run_command.startsWith(it) }) {
                env.project.service<CargoAsmService>().run()
            }
        }
    }
}
