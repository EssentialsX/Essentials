package com.earth2me.essentials.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Configuration {
    private static final Map<String, ValueParser> PARSERS = new HashMap<>();

    static {
        registerParser("special:default", new ValueParser());
        registerParser("special:enum", new ValueParser() {
            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            public <T> Object parseToJava(Class<T> type, Object object) {
                return Enum.valueOf((Class<? extends Enum>) type, ((String) object).toUpperCase());
            }
        });
    }

    private final File configFile;

    public Configuration(File configFile) {
        this.configFile = configFile;
    }

    public void load() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));

            if (getClass().isAnnotationPresent(Header.class)) {
                for (String line : getClass().getAnnotation(Header.class).value()) {
                    if (!line.isEmpty()) {
                        writer.write("#" + line);
                    }
                    writer.newLine();
                }
                writer.newLine();
            }

            List<String> builtPaths = new ArrayList<>();
            for (Field field : getClass().getDeclaredFields()) {
                int mod = field.getModifiers();
                if (!Modifier.isPublic(mod) || !Modifier.isStatic(mod) || Modifier.isFinal(mod) || Modifier.isTransient(mod)) {
                    continue;
                }

                String path = getPath(field.getName());
                String[] pathSplit = path.split("\\.");
                int depth = pathSplit.length - 1;
                String depthBuffer = "";
                if (depth > 0) {
                    depthBuffer = CharBuffer.allocate(depth).toString().replace("\0", "  ");
                }

                if (config.isSet(path)) {
                    field.set(null, getParser(field).parseToJava(field.getType(), config.get(path)));
                } else if (field.isAnnotationPresent(HiddenValue.class)) {
                    continue;
                }

                if (field.isAnnotationPresent(Separator.class)) {
                    for (int i = 0; i < field.getAnnotation(Separator.class).value(); i++) {
                        writer.newLine();
                    }
                }

                if (field.isAnnotationPresent(ConfigurationComment.class)) {
                    for (String line : field.getAnnotation(ConfigurationComment.class).value()) {
                        if (!line.isEmpty()) {
                            writer.write(depthBuffer + "#" + line);
                        }
                        writer.newLine();
                    }
                }

                // Build master paths if applicable
                if (depth > 0) {
                    String curPath = "";
                    int pathTraversal = 1;
                    for (String curSplit : pathSplit) {
                        if (pathTraversal > depth) {
                            continue;
                        }
                        pathTraversal++;

                        curPath = curPath + curSplit;
                        if (builtPaths.contains(curPath)) {
                            curPath = curPath + ".";
                            continue;
                        }
                        String[] curPathSplit = curPath.split("\\.");
                        String buffer = "";
                        if (curPathSplit.length - 1 > 0) {
                            buffer = CharBuffer.allocate(curPathSplit.length - 1).toString().replace("\0", "  ");
                        }
                        writer.write(buffer + curPathSplit[curPathSplit.length - 1] + ":");
                        writer.newLine();
                        builtPaths.add(curPath);
                        curPath = curPath + ".";
                    }

                    // We've traversed all the paths that should be there.
                    path = pathSplit[depth];
                }

                writer.write(depthBuffer + path + ": ");

                // Check if a list is the current object. If it is, apply the correct depth buffer to it.
                Object value = field.get(null);
                String[] parsed = getParser(field).parseToYAML(value).split("\\n");
                if (value instanceof Collection) {
                    for (String curElement : parsed) {
                        writer.write(depthBuffer + "  " + curElement);
                        writer.newLine();
                    }
                    if (field.isAnnotationPresent(ExampleValues.class) && parsed.length == 1) {
                        for (String example : field.getAnnotation(ExampleValues.class).value()) {
                            writer.write("#  - " + depthBuffer + getParser("special:default").parseToYAML(example));
                            writer.newLine();
                        }
                    }
                    continue;
                }

                writer.write(parsed[0]);
                writer.newLine();
            }

            writer.close();
        } catch (IllegalArgumentException | IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the yaml path from a field/method name.
     *
     * Uppercase characters indicate the previous character should be a dash.
     * @param name The name of the field/method to translate.
     * @return The translated path.
     */
    private String getPath(String name) {
        StringBuilder path = new StringBuilder();
        for (char curChar : name.toCharArray()) {
            if (curChar == '_') {
                path.append('.');
                continue;
            }

            if (path.length() != 0 && Character.isUpperCase(curChar)) {
                path.append('-');
            }
            path.append(curChar);
        }
        return path.toString().toLowerCase();
    }

    private static void registerParser(String name, ValueParser valueParser) {
        PARSERS.put(name.toLowerCase(), valueParser);
    }

    private static ValueParser getParser(String name) {
        return PARSERS.get(name.toLowerCase());
    }

    private static ValueParser getParser(Field field) {
        ValueParser parser = null;
        if (field.isAnnotationPresent(RestrictedValues.class)) {
            RestrictedValues rv = field.getAnnotation(RestrictedValues.class);
            parser = new RestrictedValuesParser(rv.defaultValue(), rv.values());
        }
        if (field.isAnnotationPresent(Parser.class)) {
            parser = getParser(field.getAnnotation(Parser.class).value());
        }
        if (parser == null) {
            parser = getParser(field.getType().getSimpleName());
        }
        if (parser == null && field.getType().isEnum()) {
            parser = getParser("special:enum");
        }
        if (parser == null) {
            parser = getParser("special:default");
        }
        return parser;
    }
}
