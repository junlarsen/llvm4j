package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.TestUtils
import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.Metadata
import dev.supergrecko.vexe.llvm.ir.Module
import dev.supergrecko.vexe.llvm.ir.ModuleFlagBehavior
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.types.StructType
import dev.supergrecko.vexe.llvm.ir.types.VoidType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.llvm.setup
import dev.supergrecko.vexe.llvm.support.VerifierFailureAction
import org.junit.jupiter.api.assertDoesNotThrow
import org.spekframework.spek2.Spek
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal object ModuleTest : Spek({
    setup()

    val module: Module by memoized()
    val utils: TestUtils by memoized()

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

            assertEquals(1, subject.size())
            assertEquals(ModuleFlagBehavior.Override, subject.getBehavior(0))
        }

        test("retrieving out of bounds will fail") {
            val subject = module.getModuleFlags()

            assertEquals(0, subject.size())
            assertFailsWith<IndexOutOfBoundsException> {
                subject.getKey(1)
            }
        }
    }

    group("dumping the ir representation of the module") {
        test("printing to string") {
            val str = module.getIR().toString()

            assertTrue { str.isNotEmpty() }
        }

        test("printing to file") {
            val file = utils.getTemporaryFile()

            assertDoesNotThrow {
                module.saveIRToFile(file)
            }

            val content = Files.readAllLines(file.toPath())
                .joinToString("")

            assertTrue { content.isNotEmpty() }
        }
    }

    test("using inline assembler instructions") {
        module.apply {
            setInlineAssembly(".example")
            appendInlineAssembly("    push 0")
            appendInlineAssembly("    ret")
        }

        val expected = ".example\n    push 0\n    ret\n"

        assertEquals(expected, module.getInlineAssembly())
    }

    test("retrieving the context of the module") {
        // Our Spek memoized value uses the global Context
        val ctx = module.getContext()
        val global = Context.getGlobalContext()

        assertEquals(global.ref, ctx.ref)
    }

    test("finding types inside a module") {
        // Both uses global Context
        val type = StructType("EmptyType")
        val subject = module.getTypeByName("EmptyType")

        assertEquals(type.ref, subject?.ref)
    }

    group("verification") {
        test("verification of a valid module") {
            val success = module.verify(VerifierFailureAction.PrintMessage)

            assertTrue { success }
        }

        test("invalid module") {
            module.addGlobal("Nothing", VoidType()).apply {
                setInitializer(ConstantInt(IntType(32), 100))
            }

            val success = module.verify(VerifierFailureAction.ReturnStatus)

            assertFalse { success }
        }
    }
})
