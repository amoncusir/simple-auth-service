package info.digitalpoet.auth.plugins.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDateTime

typealias SerializableCharArray = @Serializable(CharArrayAsStringSerializer::class) CharArray

object CharArrayAsStringSerializer: KSerializer<CharArray>
{
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SerializableCharArray", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): CharArray {
        return decoder.decodeString().toCharArray()
    }

    override fun serialize(encoder: Encoder, value: CharArray) {
        encoder.encodeString(value.concatToString())
    }
}