package org.llvm4j.llvm4j

import org.junit.jupiter.api.Test
import org.llvm4j.llvm4j.testing.assertIsNone
import org.llvm4j.llvm4j.testing.assertIsSome
import org.llvm4j.llvm4j.util.None
import org.llvm4j.llvm4j.util.Some
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IndirectFunctionTest {
    @Test fun `Test IndirectFunction properties`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val void = ctx.getVoidType()
        val fnTy = ctx.getFunctionType(i32, i32)
        val resolverTy = ctx.getFunctionType(void)
        val mod = ctx.createModule("test_module")
        val subject1 = mod.addFunction("test_resolver", resolverTy)
        val subject2 = mod.addGlobalIndirectFunction("test_main", fnTy, AddressSpace.Generic, Some(subject1))
        val subject3 = mod.addGlobalIndirectFunction("test_main2", fnTy, AddressSpace.Generic, None)

        assertTrue { subject2.hasResolver() }
        assertFalse { subject3.hasResolver() }
        assertIsSome(subject2.getResolver())
        assertIsNone(subject3.getResolver())
        assertEquals(ValueKind.GlobalIFunc, subject2.getValueKind())
        assertEquals(subject1.ref, subject2.getResolver().get().ref)
        assertEquals("test_main", subject2.getName())

        subject3.setResolver(subject1)
        subject2.setName("test_main1")

        assertTrue { subject3.hasResolver() }
        assertIsSome(subject3.getResolver())
        assertIsSome(mod.getGlobalIndirectFunction("test_main1"))
        assertEquals("test_main1", subject2.getName())
        assertEquals(subject1.ref, subject3.getResolver().get().ref)
    }

    @Test fun `Test deleting and erasure`() {
        val ctx = Context()
        val i32 = ctx.getInt32Type()
        val void = ctx.getVoidType()
        val fnTy = ctx.getFunctionType(i32, i32)
        val resolverTy = ctx.getFunctionType(void)
        val mod = ctx.createModule("test_module")
        val subject1 = mod.addFunction("test_resolver", resolverTy)
        val subject2 = mod.addGlobalIndirectFunction("test_main", fnTy, AddressSpace.Generic, Some(subject1))
        val subject3 = mod.addGlobalIndirectFunction("test_main2", fnTy, AddressSpace.Generic, None)

        assertIsSome(mod.getGlobalIndirectFunction("test_main"))
        assertIsSome(mod.getGlobalIndirectFunction("test_main2"))

        subject2.detach()
        subject3.delete()

        assertIsNone(mod.getGlobalIndirectFunction("test_main"))
        assertIsNone(mod.getGlobalIndirectFunction("test_main2"))
    }
}