package org.llvm4j.llvm4j

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMMCJITMemoryManagerRef
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerAllocateCodeSectionCallback
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerAllocateDataSectionCallback
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerDestroyCallback
import org.bytedeco.llvm.LLVM.LLVMMemoryManagerFinalizeMemoryCallback
import org.llvm4j.llvm4j.util.Callback
import org.llvm4j.llvm4j.util.None
import org.llvm4j.llvm4j.util.Option
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.Some
import org.llvm4j.llvm4j.util.toBoolean

public class MCJITMemoryManager public constructor(ptr: LLVMMCJITMemoryManagerRef) : Owner<LLVMMCJITMemoryManagerRef> {
    public override val ref: LLVMMCJITMemoryManagerRef = ptr

    public class AllocateCodeSectionCallback(public override val closure: (Payload) -> BytePointer) :
        LLVMMemoryManagerAllocateCodeSectionCallback(),
        Callback<BytePointer, AllocateCodeSectionCallback.Payload> {
        public override fun call(p0: Pointer?, p1: Long, p2: Int, p3: Int, p4: BytePointer): BytePointer {
            val payload = p0?.let { Some(it) } ?: None
            val sectionName = p4.string
            val data = Payload(payload, p1, p2, p3, sectionName)

            p4.deallocate()

            return closure.invoke(data)
        }

        public data class Payload(
            public val payload: Option<Pointer>,
            public val size: Long,
            public val alignment: Int,
            public val sectionId: Int,
            public val sectionName: String
        )
    }

    public class AllocateDataSectionCallback(public override val closure: (Payload) -> BytePointer) :
        LLVMMemoryManagerAllocateDataSectionCallback(),
        Callback<BytePointer, AllocateDataSectionCallback.Payload> {
        public override fun call(p0: Pointer?, p1: Long, p2: Int, p3: Int, p4: BytePointer, p5: Int): BytePointer {
            val payload = p0?.let { Some(it) } ?: None
            val sectionName = p4.string
            val data = Payload(payload, p1, p2, p3, sectionName, p5.toBoolean())

            p4.deallocate()

            return closure.invoke(data)
        }

        public data class Payload(
            public val payload: Option<Pointer>,
            public val size: Long,
            public val alignment: Int,
            public val sectionId: Int,
            public val sectionName: String,
            public val isReadOnly: Boolean
        )
    }

    public class DestroyCallback(public override val closure: (Payload) -> Unit) :
        LLVMMemoryManagerDestroyCallback(),
        Callback<Unit, DestroyCallback.Payload> {
        public override fun call(p0: Pointer?) {
            val payload = p0?.let { Some(it) } ?: None
            val data = Payload(payload)

            return closure.invoke(data)
        }

        public data class Payload(public val payload: Option<Pointer>)
    }

    public class FinalizeMemoryCallback(public override val closure: (Payload) -> Int) :
        LLVMMemoryManagerFinalizeMemoryCallback(),
        Callback<Int, FinalizeMemoryCallback.Payload> {
        public override fun call(p0: Pointer?, p1: PointerPointer<*>): Int {
            val payload = p0?.let { Some(it) } ?: None
            val error = p1.getString(0)
            val data = Payload(payload, error)

            p1.deallocate()

            return closure.invoke(data)
        }

        public data class Payload(
            public val payload: Option<Pointer>,
            // TODO: Is this the correct type?
            public val errorMessage: String
        )
    }
}
