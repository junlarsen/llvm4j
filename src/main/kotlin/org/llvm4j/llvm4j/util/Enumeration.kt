package org.llvm4j.llvm4j.util

/**
 * Common trait for any enum with an out-of-order ordinal (like most of LLVMs C++ enums)
 *
 * This allows conversion between the C++ integer and the Kotlin enum.
 *
 * To properly use this class, extend the [Enumeration] class on the companion object and
 * use [EnumVariant] for the enum class itself.
 *
 * ```kotlin
 * public enum class CodeGenFileType(public override val value: Int) : Enumeration.EnumVariant {
 *   AssemblyFile(LLVM.LLVMAssemblyFile),
 *   ObjectFile(LLVM.LLVMObjectFile);
 *   public companion object : Enumeration<CodeGenFileType>(values())
 * }
 * ```
 */
public abstract class Enumeration<E : Enumeration.EnumVariant>(public val entries: Array<E>) {
    private val associated: Map<Int, E> = entries.associateBy { it.value }
    private val reverse: Map<E, Int> by lazy { associated.entries.associate { Pair(it.value, it.key) } }
    /**
     * Turn an integer value (from the C++ enum) into the Kotlin equivalent
     *
     * @return [Some] if enum variant was found, otherwise [None]
     */
    public fun from(id: Int): Option<E> = associated[id]?.let { Some(it) } ?: None

    /**
     * Turn an enum variant into the C++ integer form
     *
     * @return [Some] if enum variant was found, otherwise [None]
     */
    public fun into(id: E): Option<Int> = reverse[id]?.let { Some(it) } ?: None

    /**
     * Trait allowing an enum to have a set of defined variants or a fallback variant which may catch all cases which
     * otherwise would result in [None]
     *
     * To use this enum kind, create a sealed class with subclasses as the enum members. Extend this class on the
     * companion object.
     *
     * ```kotlin
     * public sealed class AttributeIndex(public override val value: Int) : Enumeration.EnumVariant {
     *   public object Return : AttributeIndex(LLVM.LLVMAttributeReturnIndex)
     *   public object Function : AttributeIndex(LLVM.LLVMAttributeFunctionIndex)
     *   public class Unknown(value: Int) : AttributeIndex(value)
     *   public companion object : Enumeration.WithFallback<AttributeIndex>({ Unknown(it) }) {
     *     public override val entries: Array<out AttributeIndex> by lazy { arrayOf(Return, Function) }
     *   }
     * }
     * ```
     *
     * In the above implementation we have to use `by lazy { .. }` otherwise we may collide into a race condition in
     * Kotlin where we try to query an object which is yet to be initialized. Failing to delegate by lazy results in
     * null values in the [entries] array.
     *
     * @property catch lambda responsible for producing the wildcard [E] given [Int]
     * @property entries all valid entries for this enum
     */
    public abstract class WithFallback<E : EnumVariant>(
        private val catch: (Int) -> E,
    ) {
        public abstract val entries: Array<out E>
        /**
         * Turn an integer value (from the C++ enum) into the Kotlin equivalent
         *
         * @return the enum variant associated with the integer
         */
        public fun from(id: Int): E = entries.firstOrNull { it.value == id }
            ?: catch(id)

        /**
         * Turn an enum variant into the C++ integer form
         *
         * @return the integer value of the enum variant
         */
        public fun into(id: E): Int = entries.first { it == id }.value
    }

    /**
     * Anything which shall represent an extendable enum must have a value for the catch-all kind.
     */
    public interface EnumVariant {
        public val value: Int
    }
}
