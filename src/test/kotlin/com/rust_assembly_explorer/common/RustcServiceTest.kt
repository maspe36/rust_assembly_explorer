package com.rust_assembly_explorer.common

import junit.framework.TestCase.assertEquals
import org.junit.Test

class RustcServiceTest {

    @Test
    fun `parseToolchainTarget with stable channel and default marker`() {
        val input = "stable-x86_64-unknown-linux-gnu (default)"
        assertEquals("x86_64-unknown-linux-gnu", parseToolchainTarget(input))
    }

    @Test
    fun `parseToolchainTarget with nightly channel`() {
        val input = "nightly-aarch64-apple-darwin (default)"
        assertEquals("aarch64-apple-darwin", parseToolchainTarget(input))
    }

    @Test
    fun `parseToolchainTarget with empty string`() {
        val input = ""
        assertEquals("", parseToolchainTarget(input))
    }
}
