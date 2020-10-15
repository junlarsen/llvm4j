package io.vexelabs.bitbuilder.llvm.unit.ir.values.constants

import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.types.VectorType
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantInt
import io.vexelabs.bitbuilder.llvm.ir.values.constants.ConstantVector
import io.vexelabs.bitbuilder.rtti.cast
import org.spekframework.spek2.Spek
import kotlin.test.assertEquals

internal object ConstantVectorTest : Spek({
    val int32 by memoized { IntType(32) }
    val vec by memoized {
        ConstantVector(
            listOf(
                ConstantInt(int32, 1), ConstantInt(int32, 2),
                ConstantInt(int32, 3), ConstantInt(int32, 4)
            )
        )
    }

    test("each vector index item corresponds to the correct one") {
        val expected = listOf(1, 2, 3, 4)

        for (i in 0..3) {
            val idx = ConstantInt(int32, i)
            val elem = vec.getExtractElement(idx)
            val int = ConstantInt(elem.ref)

            assertEquals(expected[i], int.getSignedValue().toInt())
        }
    }

    test("replacing elements inside a vector") {
        val newItem = ConstantInt(int32, 100)
        val index = ConstantInt(int32, 1)
        val newVec = vec.getInsertElement(newItem, index)

        assertEquals(4, VectorType(newVec.getType().ref).getElementCount())

        val expected = listOf(1, 100, 3, 4)

        for (i in 0..3) {
            val idx = ConstantInt(int32, i)
            val elem = newVec.getExtractElement(idx)
            val int = ConstantInt(elem.ref)

            assertEquals(expected[i], int.getSignedValue().toInt())
        }
    }

    test("shuffle vector instruction") {
        val vec2 = ConstantVector(listOf(ConstantInt(int32, 1)))
        val mask = ConstantVector(listOf(ConstantInt(int32, 2)))
        val newVec = vec.getShuffleVector(vec2, mask)
        val vecSize = cast<VectorType>(newVec.getType()).getElementCount()

        assertEquals(1, vecSize)
    }
})