package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import org.llvm4j.llvm4j.testing.assertIsNone
import org.llvm4j.llvm4j.testing.assertIsOk
import org.llvm4j.llvm4j.testing.assertIsSome
import org.llvm4j.llvm4j.util.None
import java.io.File
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertTrue

const val MODULE_IR = """; ModuleID = 'module.ll'
source_filename = "test.c"
target datalayout = "p:64:64:64"
target triple = "x86_64-apple-macosx10.7.0"
"""

const val MODULE_ASM1 = """test_function:
    pushl $10
    pushl $10
    call  sum_function
    addl  $8, %esp
    ret
sum_function:
    movl 8(%esp), %eax
    addl 4(%esp), %eax
    ret
"""

const val MODULE_ASM2 = """test_function:
    pushl $10
    pushl $10
    call  sum_function
    addl  $8, %esp
    ret
"""

const val MODULE_ASM3 = """sum_function:
    movl 8(%esp), %eax
    addl 4(%esp), %eax
    ret
"""

class ModuleTest {
    @Test fun `Test properties are consistent`() {
        val ctx = Context()
        val mod = ctx.createModule("test_module")

        assertEquals("test_module", mod.getModuleIdentifier())
        assertEquals(ctx.ref, mod.getContext().ref)

        mod.setModuleIdentifier("module.ll")
        mod.setSourceFileName("test.c")
        mod.setDataLayout("p:64:64:64")
        mod.setTarget("x86_64-apple-macosx10.7.0")

        assertEquals("module.ll", mod.getModuleIdentifier())
        assertEquals("test.c", mod.getSourceFileName())
        assertEquals("p:64:64:64", mod.getDataLayout())
        assertEquals("x86_64-apple-macosx10.7.0", mod.getTarget())
        assertEquals(MODULE_IR, mod.getAsString())

        val subject = mod.clone()

        assertEquals("module.ll", subject.getModuleIdentifier())
        assertEquals("test.c", subject.getSourceFileName())
        assertEquals("p:64:64:64", subject.getDataLayout())
        assertEquals("x86_64-apple-macosx10.7.0", subject.getTarget())
        assertEquals(ctx.ref, subject.getContext().ref)
        assertEquals(MODULE_IR, mod.getAsString())
    }

    @Test fun `Test dumping module`() {
        val ctx = Context()
        val mod = ctx.createModule("test_module")
        val file = File.createTempFile("moduletest.kt", "ModuleTest.ll").also {
            it.deleteOnExit()
        }

        assertIsOk(mod.dumpToFile(file))

        val content = Files.readAllLines(file.toPath()).joinToString("") { "$it\n" }

        assertTrue { file.exists() }
        assertEquals(mod.getAsString(), content)

        file.delete()

        assertIsOk(mod.dumpToFile(file))
    }

    @Test fun `Test module metadata flags`() {
        val ctx = Context()
        val mod = ctx.createModule("test_module")
        val subject1 = mod.getModuleFlags()

        assertEquals(0, subject1.size())
        assertIsNone(mod.getModuleFlag("nope"))
    }

    @Test fun `Test module inline assembler instructions`() {
        val ctx = Context()
        val mod = ctx.createModule("test_module")

        assertEquals("", mod.getInlineAsm())

        mod.setInlineAsm(MODULE_ASM2)
        assertEquals(MODULE_ASM2, mod.getInlineAsm())

        mod.appendInlineAsm(MODULE_ASM3)
        assertEquals(MODULE_ASM1, mod.getInlineAsm())

        mod.setInlineAsm("")
        assertEquals("", mod.getInlineAsm())
    }

    @Test fun `Test finding named types`() {
        val ctx = Context()
        val mod = ctx.createModule("test_module")
        val i8 = ctx.getInt8Type()

        assertIsNone(mod.getTypeByName("struct_t"))

        val subject = ctx.getNamedStructType("struct_t")
        subject.setElementTypes(i8, i8)

        assertIsSome(mod.getTypeByName("struct_t"))
        assertEquals(subject.ref, mod.getTypeByName("struct_t").get().ref)
    }

    @Test fun `Test finding named metadata`() {
        val ctx = Context()
        val mod = ctx.createModule("test_module")

        assertIsNone(mod.getNamedMetadata("key"))

        val subject1 = mod.getOrCreateNamedMetadata("key")
        val subject2 = mod.getNamedMetadata("key")

        assertIsSome(subject2)
        assertEquals(subject1.ref, subject2.get().ref)
    }

    @Test fun `Test finding named functions`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val fn = ctx.getFunctionType(i32, i32)
        val mod = ctx.createModule("test_module")

        assertIsNone(mod.getFunction("factorial"))

        val subject1 = mod.addFunction("factorial", fn)
        val subject2 = mod.getFunction("factorial")

        assertIsSome(subject2)
        assertEquals(subject1.ref, subject2.get().ref)

        assertIsNone(mod.getGlobalIndirectFunction("indirect_fn"))

        val subject3 = mod.addGlobalIndirectFunction("indirect_fn", fn, AddressSpace.Generic, None)
        val subject4 = mod.getGlobalIndirectFunction("indirect_fn")

        assertIsSome(subject4)
        assertEquals(subject3.ref, subject4.get().ref)
    }
}