package com.rust_assembly_explorer.common

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.util.ExecUtil
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

/**
 * Parses rustup toolchain output to extract the target triple.
 * Examples:
 * - "stable-x86_64-unknown-linux-gnu (default)" -> "x86_64-unknown-linux-gnu"
 * - "nightly-aarch64-apple-darwin" -> "aarch64-apple-darwin"
 * - "1.70.0-x86_64-pc-windows-msvc (override)" -> "x86_64-pc-windows-msvc"
 */
fun parseToolchainTarget(toolchainOutput: String): String {
    val trimmed = toolchainOutput.trim()

    // Remove anything in parentheses at the end
    val withoutParens = trimmed.substringBefore("(").trim()

    // Remove the channel prefix (stable-, nightly-, beta-, or version like 1.70.0-)
    val parts = withoutParens.split("-", limit = 2)

    return if (parts.size >= 2) parts[1] else withoutParens
}

@Service(Service.Level.PROJECT)
class RustcService(private val project: Project, private val cs: CoroutineScope) {
    // TODO (SP) Okay, this throws an exception because its run on the EDT. It doesn't really matter because
    //  this should essentially be instant... but fine. I don't like my code throwing for no reason.

    fun getActiveToolchain(): String {
        val output = ExecUtil.execAndGetOutput(
            GeneralCommandLine("rustup", "show", "active-toolchain")
                .withWorkDirectory(project.basePath)
        )

        if (output.exitCode != 0) return ""

        return parseToolchainTarget(output.stdout)
    }

    fun getTargetList(): List<String> {
        val output = ExecUtil.execAndGetOutput(
            GeneralCommandLine("rustc", "--print", "target-list")
                .withWorkDirectory(project.basePath)
        )

        return if (output.exitCode == 0) {
            output.stdout.lines().filter { it.isNotBlank() }
        } else {
            emptyList()
        }
    }

    companion object {
        fun getInstance(project: Project): RustcService {
            return project.getService(RustcService::class.java)
        }
    }
}
