package com.earth2me.essentials.config.processors;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.meta.Processor;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteIfIncompleteProcessor implements Processor<Object> {
    @Override
    public void process(Object value, ConfigurationNode destination) {
        if (value == null) {
            return;
        }

        try {
            if (value instanceof IncompleteEntity && ((IncompleteEntity) value).isIncomplete()) {
                destination.set(null);
            } else if (value instanceof List<?>) {
                boolean modified = false;
                final List<?> newList = new ArrayList<>((List<?>) value);
                for (final Object o : (List<?>) value) {
                    if (o instanceof IncompleteEntity && ((IncompleteEntity) o).isIncomplete()) {
                        newList.remove(o);
                        modified = true;
                    }
                }
                if (modified) {
                    destination.set(newList);
                }
            } else if (value instanceof Map<?, ?>) {
                boolean modified = false;
                final Map<?, ?> newMap = new HashMap<>((Map<?, ?>) value);
                for (final Map.Entry<?, ?> entry : newMap.entrySet()) {
                    if (entry.getValue() instanceof IncompleteEntity && ((IncompleteEntity) entry.getValue()).isIncomplete()) {
                        newMap.remove(entry.getKey());
                        modified = true;
                    }
                }
                if (modified) {
                    destination.set(newMap);
                }
            }
        } catch (final SerializationException e) {
            e.printStackTrace();
        }
    }

    public interface IncompleteEntity {
        boolean isIncomplete();
    }
}
