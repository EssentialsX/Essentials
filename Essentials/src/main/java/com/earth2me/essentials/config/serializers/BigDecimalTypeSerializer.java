package com.earth2me.essentials.config.serializers;

import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.function.Predicate;

/**
 * A Configurate type serializer for {@link BigDecimal}s.
 */
public class BigDecimalTypeSerializer extends ScalarSerializer<BigDecimal> {

    public BigDecimalTypeSerializer() {
        super(BigDecimal.class);
    }

    @Override
    public BigDecimal deserialize(Type type, Object obj) throws SerializationException {
        if (obj instanceof Double) {
            return BigDecimal.valueOf((double) obj);
        }

        if (obj instanceof Integer) {
            return BigDecimal.valueOf((int) obj);
        }

        if (obj instanceof Long) {
            return BigDecimal.valueOf((long) obj);
        }

        if (obj instanceof BigInteger) {
            return new BigDecimal((BigInteger) obj);
        }

        if (obj instanceof String) {
            try {
                return new BigDecimal((String) obj, MathContext.DECIMAL128);
            } catch (final NumberFormatException | ArithmeticException e) {
                throw new SerializationException(type, "Failed to coerce input value of type " + obj.getClass() + " to BigDecimal", e);
            }
        }

        throw new SerializationException(type, "Failed to coerce input value of type " + obj.getClass() + " to BigDecimal");
    }

    @Override
    protected Object serialize(BigDecimal item, Predicate<Class<?>> typeSupported) {
        return item.toString();
    }
}
