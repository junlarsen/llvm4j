package io.vexelabs.bitbuilder.llvm.debuginfo

import io.vexelabs.bitbuilder.llvm.internal.contracts.ForeignEnum
import org.bytedeco.llvm.global.LLVM

public enum class DIFlag(public override val value: Int) :
    ForeignEnum<Int> {
    Zero(LLVM.LLVMDIFlagZero),
    ;

    public companion object : ForeignEnum.CompanionBase<Int, DIFlag> {
        public override val map: Map<Int, DIFlag> by lazy {
            values().associateBy(DIFlag::value)
        }
    }
}

public enum class DWARFSourceLanguage(public override val value: Int) :
    ForeignEnum<Int> {
    ;

    public companion object : ForeignEnum.CompanionBase<Int, DWARFSourceLanguage> {
        public override val map: Map<Int, DWARFSourceLanguage> by lazy {
            values().associateBy(DWARFSourceLanguage::value)
        }
    }
}

public enum class DWARFEmission(public override val value: Int) :
    ForeignEnum<Int> {
    ;

    public companion object : ForeignEnum.CompanionBase<Int, DWARFEmission> {
        public override val map: Map<Int, DWARFEmission> by lazy {
            values().associateBy(DWARFEmission::value)
        }
    }
}

public enum class DWARDMacinfoRecordType(public override val value: Int) :
    ForeignEnum<Int> {
    ;

    public companion object : ForeignEnum.CompanionBase<Int, DWARDMacinfoRecordType> {
        public override val map: Map<Int, DWARDMacinfoRecordType> by lazy {
            values().associateBy(DWARDMacinfoRecordType::value)
        }
    }
}