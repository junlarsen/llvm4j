package io.vexelabs.bitbuilder.llvm.unit.ir.values.constants

import io.vexelabs.bitbuilder.internal.cast
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.types.VectorType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.setup
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals

internal object ConstantVectorTest : Spek({
    setup()

    val context: Context by memoized()
    val i32 by memoized {
        context.getIntType(32)
    }
    val vec by memoized {
        i32.getConstantVector(
            i32.getConstant(1),
            i32.getConstant(2),
            i32.getConstant(3),
            i32.getConstant(4)
        )
    }

    test("each vector index item corresponds to the correct one") {
        val expected = listOf(1, 2, 3, 4)

        for (i in 0..3) {
            val idx = i32.getConstant(i)
            val elem = vec.getExtractElement(idx)
            val int = ConstantInt(elem.ref)

            assertEquals(expected[i], int.getSignedValue().toInt())
        }
    }

    test("replacing elements inside a vector") {
        val newItem = i32.getConstant(100)
        val index = i32.getConstant(1)
        val newVec = vec.getInsertElement(newItem, index)

        assertEquals(4, VectorType(newVec.getType().ref).getElementCount())

        val expected = listOf(1, 100, 3, 4)

        for (i in 0..3) {
            val idx = i32.getConstant(i)
            val elem = newVec.getExtractElement(idx)
            val int = ConstantInt(elem.ref)

            assertEquals(expected[i], int.getSignedValue().toInt())
        }
    }

    test("shuffle vector instruction") {
        val vec2 = i32.getConstantVector(i32.getConstant(1))
        val mask = i32.getConstantVector(i32.getConstant(2))
        val newVec = vec.getShuffleVector(vec2, mask)
        val vecSize = cast<VectorType>(newVec.getType()).getElementCount()

        assertEquals(1, vecSize)
    }
})
