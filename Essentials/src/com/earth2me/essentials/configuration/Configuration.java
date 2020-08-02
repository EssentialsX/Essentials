package com.earth2me.essentials.configuration;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.api.IItemDb;
import com.earth2me.essentials.signs.EssentialsSign;
import com.earth2me.essentials.signs.Signs;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public abstract class Configuration {
    private static final Map<String, ValueParser> PARSERS = new HashMap<>();

    protected static final Logger logger = Logger.getLogger("Essentials");
    private static IEssentials ess;

    static {
        registerParser("special:default", new ValueParser());
        registerParser("special:enum", new ValueParser() {
            @Override
            public String parseToYAML(Object object) {
                return super.parseToYAML((((Enum<?>) object).name().toLowerCase()));
            }

            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            public <T> Object parseToJava(Class<T> type, Object object) {
                return Enum.valueOf((Class<? extends Enum>) type, ((String) object).toUpperCase());
            }
        });
        registerParser("special:map", new KeyValueParser());
        registerParser("BigDecimal", new ValueParser() {
            @Override
            public <T> Object parseToJava(Class<T> type, Object object) {
                String str = String.valueOf(super.parseToJava(String.class, object));
                if (str.isEmpty()) {
                    return BigDecimal.ZERO;
                }
                try {
                    return new BigDecimal(str, MathContext.DECIMAL128);
                } catch (NumberFormatException | ArithmeticException e) {
                    return BigDecimal.ZERO;
                }
            }
        });
        registerParser("color", new ValueParser() {
            @Override
            public String parseToYAML(Object object) {
                if (object == null) {
                    return super.parseToYAML("");
                }
                return super.parseToYAML(FormatUtil.unformatString((String) object, null, true).substring(1));
            }

            @Override
            public <T> Object parseToJava(Class<T> type, Object object) {
                String value = (String) object;
                if (value.equalsIgnoreCase("none") || value.isEmpty()) {
                    return null;
                }

                try {
                    FormatUtil.parseHexColor(value);
                } catch (NumberFormatException ignored) {
                }

                try {
                    return ChatColor.valueOf(value.toUpperCase(Locale.ENGLISH)).toString();
                } catch (IllegalArgumentException ignored) {
                }

                ChatColor lastResort = ChatColor.getByChar(value);
                if (lastResort != null) {
                    return lastResort.toString();
                }
                return null;
            }
        });
        registerParser("csv:material", new ValueParser() {
            private final Map<String, Material> map = new HashMap<>();

            @Override
            public <T> Object parseToJava(Class<T> type, Object object) {
                map.clear();
                String value = (String) object;
                System.out.println(value);
                IItemDb itemDb = ess.getItemDb();
                if (itemDb == null || !itemDb.isReady()) {
                    System.out.println("yeah...");
                    logger.log(Level.FINE, "Skipping item spawn blacklist read; item DB not yet loaded.");
                    return map.values();
                }
                for (String itemName : value.split(",")) {
                    itemName = itemName.trim();
                    if (itemName.isEmpty()) {
                        continue;
                    }
                    try {
                        final ItemStack iStack = itemDb.get(itemName);
                        System.out.println(itemName);
                        System.out.println(iStack.getType());
                        map.put(itemName, iStack.getType());
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, tl("unknownItemInList", itemName, "item-spawn-blacklist"), ex);
                    }
                }
                return map.values();
            }

            @Override
            public String parseToYAML(Object object) {
                return super.parseToYAML(String.join(",", map.keySet())).trim();
            }
        });
        registerParser("signs", new ValueParser() {
            @Override
            public String parseToYAML(Object object) {
                List<String> yamlList = new ArrayList<>();
                if (object != null) {
                    //noinspection unchecked
                    for (EssentialsSign sign : (List<EssentialsSign>) object) {
                        yamlList.add(sign.getName().toLowerCase());
                    }
                }
                return super.parseToYAML(yamlList);
            }

            @Override
            public <T> Object parseToJava(Class<T> type, Object object) {
                List<EssentialsSign> list = new ArrayList<>();
                boolean color = false;
                //noinspection unchecked
                for (String name : (List<String>) super.parseToJava(List.class, object)) {
                    name = name.trim().toUpperCase(Locale.ENGLISH);
                    if (name.isEmpty()) {
                        continue;
                    }

                    if (name.equals("COLOR") || name.equals("COLOUR")) {
                        color = true;
                        continue;
                    }

                    try {
                        list.add(Signs.valueOf(name).getSign());
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, tl("unknownItemInList", name, "enabledSigns"));
                    }
                }

                if (!color && list.isEmpty()) {
                    return null;
                }
                return list;
            }
        });
    }

    private final File configFile;

    public Configuration(File configFile, IEssentials essentials) {
        this.configFile = configFile;
        ess = essentials;
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

                String path = getPath(field);
                String[] pathSplit = path.split("\\.");
                int depth = pathSplit.length - 1;
                String depthBuffer = "";
                if (depth > 0) {
                    depthBuffer = CharBuffer.allocate(depth).toString().replace("\0", "  ");
                }

                boolean isPreDefined = config.isSet(path);
                if (isPreDefined) {
                    Object parsed = getParser(field).parseToJava(field.getType(), config.get(path));
                    if (field.isAnnotationPresent(CheckRegex.class) && parsed instanceof String) {
                        CheckRegex check = field.getAnnotation(CheckRegex.class);
                        if (!((String) parsed).matches(check.regex())) {
                            parsed = check.defaultValue();
                        }
                    }
                    field.set(null, parsed);
                } else if (field.isAnnotationPresent(HiddenValue.class)) {
                    continue;
                }

                if (field.isAnnotationPresent(Separator.class)) {
                    for (int i = 0; i < field.getAnnotation(Separator.class).value(); i++) {
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

                        String curNode = buffer + curPathSplit[curPathSplit.length - 1];
                        if (field.isAnnotationPresent(SectionComment.class)) {
                            for (String comments : field.getAnnotation(SectionComment.class).value()) {
                                String[] nodeSplit = comments.split(":");
                                if (nodeSplit[0].equalsIgnoreCase(curNode) && nodeSplit.length > 1) {
                                    String comment = comments.replaceFirst(curNode + ":", "").trim();
                                    if (!comment.isEmpty()) {
                                        writer.write("#" + comment);
                                    }
                                    writer.newLine();
                                }
                            }
                        }
                        writer.write(curNode + ":");
                        writer.newLine();
                        builtPaths.add(curPath);
                        curPath = curPath + ".";
                    }

                    // We've traversed all the paths that should be there.
                    path = pathSplit[depth];
                }

                if (field.isAnnotationPresent(ConfigurationComment.class)) {
                    for (String line : field.getAnnotation(ConfigurationComment.class).value()) {
                        if (!line.isEmpty()) {
                            writer.write(depthBuffer + "#" + line);
                        }
                        writer.newLine();
                    }
                }

                boolean kleenean = !isPreDefined && field.isAnnotationPresent(Kleenean.class) && field.getType() == Boolean.class && !field.getType().isPrimitive();
                writer.write((kleenean ? "#" : "") + depthBuffer + path + ": ");

                // Check if a list is the current object. If it is, apply the correct depth buffer to it.
                Object value = field.get(null);
                if (kleenean && value == null) {
                    value = field.getAnnotation(Kleenean.class).value();
                }
                String[] parsed = getParser(field).parseToYAML(value).split("\\n");
                if (Map.class.isAssignableFrom(field.getType()) || Collection.class.isAssignableFrom(field.getType()) || parsed.length > 1) {
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
                    if (field.isAnnotationPresent(ExampleKeyValue.class) && parsed.length == 1) {
                        for (String entry : field.getAnnotation(ExampleKeyValue.class).value()) {
                            writer.write("#  " + depthBuffer + entry);
                            writer.newLine();
                        }
                    }
                    if (!field.isAnnotationPresent(NoSeparator.class)) {
                        writer.newLine();
                    }
                    continue;
                }

                writer.write(parsed[0]);
                writer.newLine();
                if (!field.isAnnotationPresent(NoSeparator.class)) {
                    writer.newLine();
                }
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
     * @param field The field who's name to translate.
     * @return The translated path.
     */
    private String getPath(Field field) {
        if (field.isAnnotationPresent(CustomPath.class)) {
            return field.getAnnotation(CustomPath.class).value();
        }

        StringBuilder path = new StringBuilder();
        for (char curChar : field.getName().toCharArray()) {
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

    protected static void registerParser(String name, ValueParser valueParser) {
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
            parser = getParser(field.getType());
        }
        return parser;
    }

    protected static ValueParser getParser(Class<?> type) {
        ValueParser parser = getParser(type.getSimpleName());
        if (parser == null && type.isEnum()) {
            parser = getParser("special:enum");
        }
        if (parser == null) {
            parser = getParser("special:default");
        }
        return parser;
    }
}
