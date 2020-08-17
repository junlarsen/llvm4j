package io.vexelabs.bitbuilder.llvm.internal.contracts

/**
 * Enum to wrap any enum classes.
 *
 * This is necessary because the JVM does not allow modifying the ordinal of an
 * enum kind and thus we need to keep track of the underlying value ourselves.
 *
 * Multiple of the enums from LLVM have modified ordinals which is why we need
 * to do this.
 */
public interface ForeignEnum<T : Number> {
    /**
     * The foreign ordinal for the enum member. If a foreign enum variant has
     * the ordinal 99, then [value] will be 99 for that member.
     */
    public val value: T

    /**
     * Interface for performing reverse lookup on enum members. Because
     * foreign APIs will return us the ordinal number, we need a good and
     * efficient way of finding the Kotlin Enum variant by its ordinal.
     *
     * This interface should be implemented on the companion object of the
     * enum class.
     */
    public interface CompanionBase<T : Number, E : ForeignEnum<T>> {
        /**
         * A map of the foreign ordinals to the enum variants
         *
         * This map should always be lazily initialized because this entire
         * interface exists only if the user needs to query the Kotlin enum
         * variant from its [value]
         */
        public val map: Map<T, E>

        /**
         * Perform the lookup, finding the kotlin enum variant
         *
         * This should accept the foreign enum ordinal. If any other value is
         * passed and the enum variant was not found it is a critical error
         * and a [Unreachable] should be thrown.
         *
         * @throws Unreachable if the enum variant was not found
         */
        public operator fun get(ordinal: T): E {
            return map[ordinal] ?: throw Unreachable()
        }
    }
}
