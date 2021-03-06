package dk.thrane.compiler.type

import java.util.*

open class Type {
    companion object {
        fun checkCompatibility(theLeft: Type, theRight: Type): Boolean {
            var currentRight = theRight

            if (theLeft !is TypeLike) currentRight = fullyResolve(currentRight)

            when (theLeft) {
                is TypeInt, is TypeBool, is TypeChar -> return theLeft.javaClass == currentRight.javaClass
                is TypeNull, is TypeFunction, is TypeTypedef, is TypeUnresolved, is TypeUnit -> return false
                is TypeLike -> {
                    if (currentRight is TypeLike) {
                        if (theLeft.type == currentRight.type) {
                            return true
                        }
                        return false
                    }
                    return checkCompatibility(fullyResolve(theLeft), currentRight)
                }
                is TypeArray -> {
                    if (currentRight is TypeNull) return true
                    return currentRight is TypeArray && checkCompatibility(theLeft.type, currentRight.type)
                }
                is TypeRecord -> {
                    if (currentRight is TypeNull) return true
                    if (currentRight !is TypeRecord) return false
                    if (theLeft.fieldTypes.size != currentRight.fieldTypes.size) return false
                    for (i in 0 until theLeft.fieldTypes.size) {
                        val leftField = theLeft.fieldTypes[i]
                        val rightField = currentRight.fieldTypes[i]
                        if (leftField.first != rightField.first ||
                            !checkCompatibility(leftField.second, rightField.second)
                        ) {
                            return false
                        }
                    }
                    return true
                }
            }
            return false
        }

        fun fullyResolve(type: Type): Type {
            val visited: MutableSet<Type> = HashSet()
            var currentType = type

            while (currentType is TypeLike || currentType is TypeTypedef) {
                if (visited.contains(currentType)) throw IllegalStateException("Empty type detected!")
                visited.add(currentType)

                if (currentType is TypeLike) currentType = currentType.type
                if (currentType is TypeTypedef) currentType = currentType.type
            }
            return currentType
        }
    }
}

object TypeInt : Type()
object TypeBool : Type()
object TypeChar : Type()
object TypeNull : Type()
object TypeUnit : Type()

class TypeRecord(val fieldTypes: List<Pair<String, Type>>) : Type()
class TypeArray(val type: Type) : Type()
class TypeFunction(val parameterTypes: List<Pair<String, Type>>, val returnType: Type) : Type()
class TypeTypedef(val type: Type) : Type()
class TypeUnresolved(val identifier: String) : Type()
class TypeLike(val type: Type) : Type()
