package com.earth2me.essentials.configuration;

import java.util.Arrays;
import java.util.List;

public class RestrictedValuesParser extends ValueParser {

    private final String defaultValue;
    private final List<String> values;

    public RestrictedValuesParser(String defaultValue, String[] values) {
        this.defaultValue = defaultValue;
        this.values = Arrays.asList(values);
    }

    @Override
    public <T> Object parseToJava(Class<T> type, Object object) {
        Object returnObj = super.parseToJava(type, object);
        if (object instanceof String && type.isAssignableFrom(String.class)) {
            String str = (String) object;
            if (values.contains(str) || defaultValue.equals(str)) {
                return str;
            }
            return defaultValue;
        }
        return returnObj;
    }
}
