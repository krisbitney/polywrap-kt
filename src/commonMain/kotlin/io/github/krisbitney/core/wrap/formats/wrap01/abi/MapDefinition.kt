package io.github.krisbitney.core.wrap.formats.wrap01.abi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Definition of a Map in a Wrap ABI.
 * @property type Type of the map.
 * @property kind Kind of the definition.
 * @property name Optional name of the map.
 * @property required Boolean indicating whether the map is required or not.
 * @property comment Optional comment associated with this definition.
 * @property array Definition of the inner array if the map is of array type.
 * @property scalar Definition of the inner scalar if the map is of scalar type.
 * @property map Definition of the inner map if the map is of map type.
 * @property _object Reference to the object if the map is of object type.
 * @property enum Reference to the enum if the map is of enum type.
 * @property unresolvedObjectOrEnum Reference to the unresolved object or enum if the map is of an unresolved type.
 * @property key Definition of the map key.
 * @property value Definition of the map value.
 */
@Serializable
data class MapDefinition(
    override val type: String,
    override val kind: Int,
    override val name: String? = null,
    override val required: Boolean? = false,
    override val comment: String? = null,
    override val array: ArrayDefinition? = null,
    override val scalar: ScalarDefinition? = null,
    override val map: MapDefinition? = null,
    @SerialName("object")
    override val _object: GenericDefinition? = null,
    override val enum: GenericDefinition? = null,
    override val unresolvedObjectOrEnum: GenericDefinition? = null,
    val key: MapKeyDefinition? = null,
    val value: GenericDefinition? = null // in practice, we sometimes see extra properties attached to value!
) : WithComment, AnyDefinition
