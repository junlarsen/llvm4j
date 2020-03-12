package dev.supergrecko.kllvm.core.type

import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

/**
 * Wrapper around llvm::IntegerType
 */
object IntegerType {
    /**
     * Obtain an i1 type from a context with specified bit width.
     *
     * - [LLVMInt1TypeInContext](https://llvm.org/doxygen/group__LLVMCCoreTypeInt.html#ga390b4c486c780eed40002b07933d13df)
     */
    public fun i1Type(): LLVMTypeRef = i1Type(LLVM.LLVMGetGlobalContext())
    public fun i1Type(context: LLVMContextRef): LLVMTypeRef {
        require(!context.isNull)

        return LLVM.LLVMInt1TypeInContext(context)
    }

    /**
     * Obtain an i8 type from a context with specified bit width.
     *
     * - [LLVMInt8TypeInContext](https://llvm.org/doxygen/group__LLVMCCoreTypeInt.html#ga7afaa9a2cb5dd3c5c06d65298ed195d4)
     */
    public fun i8Type(): LLVMTypeRef = i8Type(LLVM.LLVMGetGlobalContext())
    public fun i8Type(context: LLVMContextRef): LLVMTypeRef {
        require(!context.isNull)

        return LLVM.LLVMInt8TypeInContext(context)
    }

    /**
     * Obtain an i16 type from a context with specified bit width.
     *
     * - [LLVMInt16TypeInContext](https://llvm.org/doxygen/group__LLVMCCoreTypeInt.html#ga23a21172a069470b344a61672b299968)
     */
    public fun i16Type(): LLVMTypeRef = i16Type(LLVM.LLVMGetGlobalContext())
    public fun i16Type(context: LLVMContextRef): LLVMTypeRef {
        require(!context.isNull)

        return LLVM.LLVMInt32TypeInContext(context)
    }

    /**
     * Obtain an i32 type from a context with specified bit width.
     *
     * - [LLVMInt32TypeInContext](https://llvm.org/doxygen/group__LLVMCCoreTypeInt.html#ga5e69a2cc779db154a0b805ed6ad3c724)
     */
    public fun i32Type(): LLVMTypeRef = i32Type(LLVM.LLVMGetGlobalContext())
    public fun i32Type(context: LLVMContextRef): LLVMTypeRef {
        require(!context.isNull)

        return LLVM.LLVMInt32TypeInContext(context)
    }

    /**
     * Obtain an i64 type from a context with specified bit width.
     *
     * - [LLVMInt64TypeInContext](https://llvm.org/doxygen/group__LLVMCCoreTypeInt.html#ga23a21172a069470b344a61672b299968)
     */
    public fun i64Type(): LLVMTypeRef = i64Type(LLVM.LLVMGetGlobalContext())
    public fun i64Type(context: LLVMContextRef): LLVMTypeRef {
        require(!context.isNull)

        return LLVM.LLVMInt64TypeInContext(context)
    }

    /**
     * Obtain an i128 type from a context with specified bit width.
     *
     * - [LLVMInt128TypeInContext](https://llvm.org/doxygen/group__LLVMCCoreTypeInt.html#ga5f3cfd960e39ae448213d45db5da229a)
     */
    public fun i128Type(): LLVMTypeRef = i128Type(LLVM.LLVMGetGlobalContext())
    public fun i128Type(context: LLVMContextRef): LLVMTypeRef {
        require(!context.isNull)

        return LLVM.LLVMInt128TypeInContext(context)
    }

    /**
     * Obtain an integer type from a context with specified bit width.
     *
     * - [LLVMIntIntTypeInContext](https://llvm.org/doxygen/group__LLVMCCoreTypeInt.html#ga2e5db8cbc30daa156083f2c42989138d)
     */
    public fun iType(size: Int): LLVMTypeRef = iType(size, LLVM.LLVMGetGlobalContext())
    public fun iType(size: Int, context: LLVMContextRef): LLVMTypeRef {
        require(!context.isNull)
        require(size > 0)

        return LLVM.LLVMIntTypeInContext(context, size)
    }
}
