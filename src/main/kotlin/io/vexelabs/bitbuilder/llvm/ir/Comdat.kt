package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.ForeignEnum
import org.bytedeco.llvm.LLVM.LLVMComdatRef
import org.bytedeco.llvm.global.LLVM

public class Comdat internal constructor() : ContainsReference<LLVMComdatRef> {
    public override lateinit var ref: LLVMComdatRef
        internal set

    public constructor(comdat: LLVMComdatRef) : this() {
        ref = comdat
    }

    /**
     * Get the selection kind of this comdat
     *
     * @see LLVM.LLVMGetComdatSelectionKind
     */
    public fun getSelectionKind(): SelectionKind {
        val kind = LLVM.LLVMGetComdatSelectionKind(ref)

        return SelectionKind[kind]
    }

    /**
     * Set the selection kind of this comdat
     *
     * @see LLVM.LLVMSetComdatSelectionKind
     */
    public fun setSelectionKind(kind: SelectionKind) {
        LLVM.LLVMSetComdatSelectionKind(ref, kind.value)
    }

    public enum class SelectionKind(public override val value: Int) :
        ForeignEnum<Int> {
        Any(LLVM.LLVMAnyComdatSelectionKind),
        ExactMatch(LLVM.LLVMExactMatchComdatSelectionKind),
        Largest(LLVM.LLVMLargestComdatSelectionKind),
        NoDuplicates(LLVM.LLVMNoDuplicatesComdatSelectionKind),
        SameSize(LLVM.LLVMSameSizeComdatSelectionKind);

        public companion object : ForeignEnum.CompanionBase<Int, SelectionKind> {
            public override val map: Map<Int, SelectionKind> by lazy {
                values().associateBy(SelectionKind::value)
            }
        }
    }
}
