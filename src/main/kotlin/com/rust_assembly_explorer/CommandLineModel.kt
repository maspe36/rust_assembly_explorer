package com.rust_assembly_explorer

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

data class CommandLineModel(
    var output: OutputType = OutputType.ASM
) {
    fun toArgs(): List<String> = buildList {
        add("--rust")
        add("--include-constants")
        add("--everything")
        add(output.toArg())
    }
}

enum class OutputType {
    ASM,
    DISASM,
    LLVM,
    LLVM_INPUT,
    MIR,
    WASM,
    MCA,
    INTEL,
    ATT;

    val optionName: String
        get() = name.lowercase().replace('_', '-')

    val description: String
        get() = when (this) {
            ASM -> "Show assembly"
            DISASM -> "Disassembly binaries or object files"
            LLVM -> "Show llvm-ir"
            LLVM_INPUT -> "Show llvm-ir before any LLVM passes"
            MIR -> "Show Mid-level IR (MIR) output"
            WASM -> "Show WASM, needs wasm32-unknown-unknown target installed"
            MCA -> "Show llvm-mca analysis"
            INTEL -> "Use Intel style for assembly"
            ATT -> "Use AT&T style for assembly"
        }

    fun toArg(): String = "--${optionName}"
    override fun toString(): String = optionName
}

@Service(Service.Level.PROJECT)
class CommandLineModelService {
    val model = CommandLineModel()

    companion object {
        fun getInstance(project: Project): CommandLineModelService {
            return project.getService(CommandLineModelService::class.java)
        }
    }
}
