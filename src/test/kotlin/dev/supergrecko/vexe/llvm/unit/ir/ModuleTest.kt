package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.Metadata
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.ModuleFlagBehavior
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.StructType
import dev.supergrecko.vexe.llvm.ir.types.VoidType
import dev.supergrecko.vexe.llvm.setup
import dev.supergrecko.vexe.llvm.support.VerifierFailureAction
import dev.supergrecko.vexe.llvm.utils.cleanup
import dev.supergrecko.vexe.test.TestSuite
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal object ModuleTest : Spek({
    setup()

    val module: Module by memoized()

    group("module source file names") {
        test("assigning a module identifier") {
            module.setSourceFileName("test.tmp")

            assertEquals("test.tmp", module.getSourceFileName())
        }

        test("names are preserved after module cloning") {
            module.setSourceFileName("test.tmp")

            val subject = module.clone()

            assertEquals("test.tmp", subject.getSourceFileName())
        }
    }

    group("module identifier names") {
        test("assigning a module identifier") {
            module.setModuleIdentifier("id")

            assertEquals("id", module.getModuleIdentifier())
        }

        test("ids are preserved after module cloning") {
            module.setModuleIdentifier("id")

            val subject = module.clone()

            assertEquals("id", subject.getModuleIdentifier())
        }
    }

    group("module data layouts") {
        test("assigning a data layout") {
            module.setDataLayout("p:64:64:64")

            assertEquals("p:64:64:64", module.getDataLayout())
        }

        test("layouts are preserved after module cloning") {
            module.setDataLayout("p:64:64:64")

            val subject = module.clone()

            assertEquals("p:64:64:64", subject.getDataLayout())
        }
    }

    group("module target triples") {
        test("assigning a target triple") {
            module.setTarget("x86_64-apple-macosx10.7.0")

            assertEquals("x86_64-apple-macosx10.7.0", module.getTarget())
        }

        test("targets are preserved after module cloning") {
            module.setTarget("x86_64-apple-macosx10.7.0")

            val subject = module.clone()

            assertEquals("x86_64-apple-macosx10.7.0", subject.getTarget())
        }
    }

    group("module flag entries") {
        test("setting a metadata flag and finding it") {
            val md = Metadata("example")
            module.addModuleFlag(ModuleFlagBehavior.Override, "example", md)

            val subject = module.getModuleFlag("example")

            assertNotNull(subject)
        }

        test("retrieving all the module flags") {
            val md = Metadata("example")
            module.addModuleFlag(ModuleFlagBehavior.Override, "example", md)

            val subject = module.getModuleFlags()

            assertEquals(ModuleFlagBehavior.Override, subject.getBehavior(0))
        }
    }
})

internal class ModuleTest2 : TestSuite({
    describe("Fetching a function which does not exist returns null") {
        val module = Module("utils.ll")

        assertNull(module.getFunction("utils"))

        cleanup(module)
    }

    describe("Fetching an existing function returns said function") {
        val module = Module("utils.ll").apply {
            addFunction(
                "utils",
                FunctionType(
                    VoidType(),
                    listOf(),
                    false
                )
            )

            assertNotNull(getFunction("utils"))
        }

        cleanup(module)
    }

    describe("Write the module byte-code to file") {
        val file = getTemporaryFile("out.bc")
        val module = Module("utils.ll").also {
            it.writeBitCodeToFile(file)
        }

        assertTrue { file.length() > 0 }

        cleanup(module)
    }

    describe("Write the module byte-code to file path") {
        val file = getTemporaryFile("out.bc")
        val module = Module("utils.ll").also {
            it.writeBitCodeToFile(file.absolutePath)
        }

        assertTrue { file.exists() }

        cleanup(module)
    }

    describe("Writing to MemoryBuffer") {
        val context = Context()
        val module = Module("utils.ll", context)

        val buf = module.toMemoryBuffer()

        assertNotNull(buf.ref)

        cleanup(module, context)
    }

    describe("Get module from MemoryBuffer") {
        val context = Context()
        val module = Module("utils.ll", context)
        val buf = module.toMemoryBuffer()
        val mod = buf.getModule(context)

        assertEquals("utils.ll", mod.getSourceFileName())

        cleanup(mod, module, context)
    }

    describe("Verification of a valid module") {
        val context = Context()
        val module = Module("utils.ll", context)
        val res = module.verify(VerifierFailureAction.ReturnStatus)

        assertEquals(true, res)

        cleanup(module, context)
    }

    describe("Creation of function inside module") {
        val fnTy = FunctionType(IntType(32), listOf(), false)
        val module = Module("utils.ll")
        val fn = module.addFunction("utils", fnTy)

        assertEquals(0, fn.getParameterCount())

        cleanup(module)
    }

    describe("Assigning a data layout") {
        val module = Module("utils.ll").apply {
            setDataLayout("e-m:e-i64:64-f80:128-n8:16:32:64-S128")
        }

        assertEquals(
            "e-m:e-i64:64-f80:128-n8:16:32:64-S128", module.getDataLayout()
        )

        cleanup(module)
    }

    describe("Assigning a target triple") {
        val module = Module("utils.ll").apply {
            setTarget("x86_64-pc-linux")
        }

        assertEquals("x86_64-pc-linux", module.getTarget())

        cleanup(module)
    }

    describe("Retrieving LLVM IR") {
        val module = Module("utils.ll")
        val ir = module.toString()

        assertTrue { ir.isNotEmpty() }

        cleanup(module)
    }

    describe("Setting module inline assembly") {
        val module = Module("utils.ll").apply {
            setInlineAssembly(".example")
            appendInlineAssembly("    push 0")
            appendInlineAssembly("    ret")
        }
        val result = ".example\n    push 0\n    ret\n"

        // LLVM Appends a new line after any inline-asm changes
        assertEquals(result, module.getInlineAssembly())

        cleanup(module)
    }

    describe("Retrieving Context from Module") {
        val ctx = Context()
        val module = Module("utils.ll", ctx)

        assertEquals(ctx.ref, module.getContext().ref)

        cleanup(module, ctx)
    }

    describe("Retrieving a type from a module") {
        val ctx = Context()
        val type = StructType("TestStruct", ctx)
        val module = Module("utils.ll", ctx)
        val found = module.getTypeByName("TestStruct")

        assertNotNull(found)
        assertEquals(type.ref, found.ref)

        cleanup(module, ctx)
    }
})
