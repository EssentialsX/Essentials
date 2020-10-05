package com.earth2me.essentials;

import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.StringUtil;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;

import java.util.*;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;


public enum MobData {

    BABY_AGEABLE("baby", Ageable.class, Data.BABY, true),
    ADULT_AGEABLE("adult", Ageable.class, Data.ADULT, true),
    BABY_CAT("kitten", MobCompat.CAT, Data.BABY, false),
    BABY_PIG("piglet", EntityType.PIG, Data.BABY, false),
    BABY_WOLF("puppy", EntityType.WOLF, Data.BABY, false),
    BABY_CHICKEN("chick", EntityType.CHICKEN, Data.BABY, false),
    BABY_HORSE("colt", EntityType.HORSE, Data.BABY, false),
    BABY_OCELOT("kitten", EntityType.OCELOT, Data.BABY, false),
    BABY_SHEEP("lamb", EntityType.SHEEP, Data.BABY, false),
    BABY_COW("calf", EntityType.COW.getEntityClass(), Data.BABY, false),
    BABY_VILLAGER("child", EntityType.VILLAGER, Data.BABY, false),
    TAMED_TAMEABLE("tamed", Tameable.class, Data.TAMED, true),
    TAME_TAMEABLE("tame", Tameable.class, Data.TAMED, false),
    RANDOM_SHEEP("random", EntityType.SHEEP, Data.COLORABLE, true),
    COLORABLE_SHEEP("", StringUtil.joinList(DyeColor.values()).toLowerCase(Locale.ENGLISH), EntityType.SHEEP, Data.COLORABLE, true),
    POLKA_HORSE("polka", EntityType.HORSE, Horse.Style.BLACK_DOTS, true),
    SOOTY_HORSE("sooty", EntityType.HORSE, Horse.Style.BLACK_DOTS, false),
    BLAZE_HORSE("blaze", EntityType.HORSE, Horse.Style.WHITE, true),
    SOCKS_HORSE("socks", EntityType.HORSE, Horse.Style.WHITE, false),
    LEOPARD_HORSE("leopard", EntityType.HORSE, Horse.Style.WHITE_DOTS, true),
    APPALOOSA_HORSE("appaloosa", EntityType.HORSE, Horse.Style.WHITE_DOTS, false),
    PAINT_HORSE("paint", EntityType.HORSE, Horse.Style.WHITEFIELD, true),
    MILKY_HORSE("milky", EntityType.HORSE, Horse.Style.WHITEFIELD, false),
    SPLOTCHY_HORSE("splotchy", EntityType.HORSE, Horse.Style.WHITEFIELD, false),
    BLACK_HORSE("black", EntityType.HORSE, Horse.Color.BLACK, true),
    CHESTNUT_HORSE("chestnut", EntityType.HORSE, Horse.Color.CHESTNUT, true),
    LIVER_HORSE("liver", EntityType.HORSE, Horse.Color.CHESTNUT, false),
    CREAMY_HORSE("creamy", EntityType.HORSE, Horse.Color.CREAMY, true),
    FLAXEN_HORSE("flaxen", EntityType.HORSE, Horse.Color.CREAMY, false),
    GRAY_HORSE("gray", EntityType.HORSE, Horse.Color.GRAY, true),
    DAPPLE_HORSE("dapple", EntityType.HORSE, Horse.Color.GRAY, false),
    BUCKSKIN_HORSE("buckskin", EntityType.HORSE, Horse.Color.DARK_BROWN, true),
    DARKBROWN_HORSE("darkbrown", EntityType.HORSE, Horse.Color.DARK_BROWN, false),
    DARK_HORSE("dark", EntityType.HORSE, Horse.Color.DARK_BROWN, false),
    DBROWN_HORSE("dbrown", EntityType.HORSE, Horse.Color.DARK_BROWN, false),
    BAY_HORSE("bay", EntityType.HORSE, Horse.Color.BROWN, true),
    BROWN_HORSE("brown", EntityType.HORSE, Horse.Color.BROWN, false),
    SADDLE_HORSE("saddle", EntityType.HORSE, Data.HORSESADDLE, true),
    GOLD_ARMOR_HORSE("goldarmor", EntityType.HORSE, EnumUtil.getMaterial("GOLDEN_HORSE_ARMOR", "GOLD_BARDING"), true),
    DIAMOND_ARMOR_HORSE("diamondarmor", EntityType.HORSE, EnumUtil.getMaterial("DIAMOND_HORSE_ARMOR", "DIAMOND_BARDING"), true),
    ARMOR_HORSE("armor", EntityType.HORSE, EnumUtil.getMaterial("IRON_HORSE_ARMOR", "IRON_BARDING"), true),
    SIAMESE_CAT("siamese", MobCompat.CAT, MobCompat.CatType.SIAMESE, true),
    WHITE_CAT("white", MobCompat.CAT, MobCompat.CatType.WHITE, false),
    RED_CAT("red", MobCompat.CAT, MobCompat.CatType.RED, true),
    ORANGE_CAT("orange", MobCompat.CAT, MobCompat.CatType.RED, false),
    TABBY_CAT("tabby", MobCompat.CAT, MobCompat.CatType.TABBY, true),
    BLACK_CAT("black", MobCompat.CAT, MobCompat.CatType.BLACK, true),
    TUXEDO_CAT("tuxedo", MobCompat.CAT, MobCompat.CatType.TUXEDO, true),
    BRITISH_SHORTHAIR_CAT("britishshorthair", MobCompat.CAT, MobCompat.CatType.BRITISH_SHORTHAIR, true),
    CALICO_CAT("calico", MobCompat.CAT, MobCompat.CatType.CALICO, true),
    PERSIAN_CAT("persian", MobCompat.CAT, MobCompat.CatType.PERSIAN, true),
    RAGDOLL_CAT("ragdoll", MobCompat.CAT, MobCompat.CatType.RAGDOLL, true),
    JELLIE_CAT("jellie", MobCompat.CAT, MobCompat.CatType.JELLIE, true),
    ALL_BLACK_CAT("allblack", MobCompat.CAT, MobCompat.CatType.BLACK, true),
    BABY_ZOMBIE("baby", EntityType.ZOMBIE.getEntityClass(), Data.BABYZOMBIE, true),
    ADULT_ZOMBIE("adult", EntityType.ZOMBIE.getEntityClass(), Data.ADULTZOMBIE, true),
    DIAMOND_SWORD_ZOMBIE("diamondsword", EntityType.ZOMBIE.getEntityClass(), Material.DIAMOND_SWORD, true),
    GOLD_SWORD_ZOMBIE("goldsword", EntityType.ZOMBIE.getEntityClass(), EnumUtil.getMaterial("GOLDEN_SWORD", "GOLD_SWORD"), true),
    IRON_SWORD_ZOMBIE("ironsword", EntityType.ZOMBIE.getEntityClass(), Material.IRON_SWORD, true),
    STONE_SWORD_ZOMBIE("stonesword", EntityType.ZOMBIE.getEntityClass(), Material.STONE_SWORD, false),
    SWORD_ZOMBIE("sword", EntityType.ZOMBIE.getEntityClass(), Material.STONE_SWORD, true),
    DIAMOND_SWORD_SKELETON("diamondsword", EntityType.SKELETON, Material.DIAMOND_SWORD, true),
    GOLD_SWORD_SKELETON("goldsword", EntityType.SKELETON, EnumUtil.getMaterial("GOLDEN_SWORD", "GOLD_SWORD"), true),
    IRON_SWORD_SKELETON("ironsword", EntityType.SKELETON, Material.IRON_SWORD, true),
    STONE_SWORD_SKELETON("stonesword", EntityType.SKELETON, Material.STONE_SWORD, false),
    SWORD_SKELETON("sword", EntityType.SKELETON, Material.STONE_SWORD, true),
    BOW_SKELETON("bow", EntityType.SKELETON, Material.BOW, true),
    POWERED_CREEPER("powered", EntityType.CREEPER, Data.ELECTRIFIED, true),
    ELECTRIC_CREEPER("electric", EntityType.CREEPER, Data.ELECTRIFIED, false),
    CHARGED_CREEPER("charged", EntityType.CREEPER, Data.ELECTRIFIED, false),
    SADDLE_PIG("saddle", EntityType.PIG, Data.PIGSADDLE, true),
    ANGRY_WOLF("angry", EntityType.WOLF, Data.ANGRY, true),
    RABID_WOLF("rabid", EntityType.WOLF, Data.ANGRY, false),
    VILLAGER("villager", EntityType.VILLAGER, MobCompat.VillagerProfession.NONE, true),
    ARMORER_VILLAGER("armorer", EntityType.VILLAGER, MobCompat.VillagerProfession.ARMORER, true),
    BUTCHER_VILLAGER("butcher", EntityType.VILLAGER, MobCompat.VillagerProfession.BUTCHER, true),
    CARTOGRAPHER_VILLAGER("cartographer", EntityType.VILLAGER, MobCompat.VillagerProfession.CARTOGRAPHER, true),
    CLERIC_VILLAGER("cleric", EntityType.VILLAGER, MobCompat.VillagerProfession.CLERIC, true),
    FARMER_VILLAGER("farmer", EntityType.VILLAGER, MobCompat.VillagerProfession.FARMER, true),
    FISHERMAN_VILLAGER("fisherman", EntityType.VILLAGER, MobCompat.VillagerProfession.FISHERMAN, true),
    FLETCHER_VILLAGER("fletcher", EntityType.VILLAGER, MobCompat.VillagerProfession.FLETCHER, true),
    LEATHERWORKER_VILLAGER("leatherworker", EntityType.VILLAGER, MobCompat.VillagerProfession.LEATHERWORKER, true),
    LIBRARIAN_VILLAGER("librarian", EntityType.VILLAGER, MobCompat.VillagerProfession.LIBRARIAN, true),
    MASON_VILLAGER("mason", EntityType.VILLAGER, MobCompat.VillagerProfession.MASON, true),
    NITWIT_VILLAGER("nitwit", EntityType.VILLAGER, MobCompat.VillagerProfession.NITWIT, true),
    SHEPHERD_VILLAGER("shepherd", EntityType.VILLAGER, MobCompat.VillagerProfession.SHEPHERD, true),
    TOOLSMITH_VILLAGER("toolsmith", EntityType.VILLAGER, MobCompat.VillagerProfession.TOOLSMITH, true),
    WEAPONSMITH_VILLAGER("weaponsmith", EntityType.VILLAGER, MobCompat.VillagerProfession.WEAPONSMITH, true),
    DESERT_VILLAGER("desert", EntityType.VILLAGER, "villagertype:DESERT", true),
    JUNGLE_VILLAGER("jungle", EntityType.VILLAGER, "villagertype:JUNGLE", true),
    PLAINS_VILLAGER("plains", EntityType.VILLAGER, "villagertype:PLAINS", true),
    SAVANNA_VILLAGER("savanna", EntityType.VILLAGER, "villagertype:SAVANNA", true),
    SNOWY_VILLAGER("snowy", EntityType.VILLAGER, "villagertype:SNOWY", true),
    SWAMP_VILLAGER("swamp", EntityType.VILLAGER, "villagertype:SWAMP", true),
    TAIGA_VILLAGER("taiga", EntityType.VILLAGER, "villagertype:TAIGA", true),
    SIZE_SLIME("", "<1-100>", EntityType.SLIME.getEntityClass(), Data.SIZE, true),
    NUM_EXPERIENCE_ORB("", "<1-2000000000>", EntityType.EXPERIENCE_ORB, Data.EXP, true),
    RED_PARROT("red", MobCompat.PARROT, "parrot:RED", true),
    GREEN_PARROT("green", MobCompat.PARROT, "parrot:GREEN", true),
    BLUE_PARROT("blue", MobCompat.PARROT, "parrot:BLUE", true),
    CYAN_PARROT("cyan", MobCompat.PARROT, "parrot:CYAN", true),
    GRAY_PARROT("gray", MobCompat.PARROT, "parrot:GRAY", true),
    KOB_TROPICAL_FISH("kob", MobCompat.TROPICAL_FISH, "tropicalfish:KOB", true),
    SUNSTREAK_TROPICAL_FISH("sunstreak", MobCompat.TROPICAL_FISH, "tropicalfish:SUNSTREAK", true),
    SNOOPER_TROPICAL_FISH("snooper", MobCompat.TROPICAL_FISH, "tropicalfish:SNOOPER", true),
    DASHER_TROPICAL_FISH("dasher", MobCompat.TROPICAL_FISH, "tropicalfish:DASHER", true),
    BRINELY_TROPICAL_FISH("brinely", MobCompat.TROPICAL_FISH, "tropicalfish:BRINELY", true),
    SPOTTY_TROPICAL_FISH("spotty", MobCompat.TROPICAL_FISH, "tropicalfish:SPOTTY", true),
    FLOPPER_TROPICAL_FISH("flopper", MobCompat.TROPICAL_FISH, "tropicalfish:FLOPPER", true),
    STRIPEY_TROPICAL_FISH("stripey", MobCompat.TROPICAL_FISH, "tropicalfish:STRIPEY", true),
    GLITTER_TROPICAL_FISH("glitter", MobCompat.TROPICAL_FISH, "tropicalfish:GLITTER", true),
    BLOCKFISH_TROPICAL_FISH("blockfish", MobCompat.TROPICAL_FISH, "tropicalfish:BLOCKFISH", true),
    BETTY_TROPICAL_FISH("betty", MobCompat.TROPICAL_FISH, "tropicalfish:BETTY", true),
    CLAYFISH_TROPICAL_FISH("clayfish", MobCompat.TROPICAL_FISH, "tropicalfish:CLAYFISH", true),
    BROWN_MUSHROOM_COW("brown", EntityType.MUSHROOM_COW, "mooshroom:BROWN", true),
    RED_MUSHROOM_COW("red", EntityType.MUSHROOM_COW, "mooshroom:RED", true),
    AGGRESSIVE_PANDA_MAIN("aggressive", MobCompat.PANDA, "pandamain:AGGRESSIVE", true),
    LAZY_PANDA_MAIN("lazy", MobCompat.PANDA, "pandamain:LAZY", true),
    WORRIED_PANDA_MAIN("worried", MobCompat.PANDA, "pandamain:WORRIED", true),
    PLAYFUL_PANDA_MAIN("playful", MobCompat.PANDA, "pandamain:PLAYFUL", true),
    BROWN_PANDA_MAIN("brown", MobCompat.PANDA, "pandamain:BROWN", true),
    WEAK_PANDA_MAIN("weak", MobCompat.PANDA, "pandamain:WEAK", true),
    AGGRESSIVE_PANDA_HIDDEN("aggressive_hidden", MobCompat.PANDA, "pandahidden:AGGRESSIVE", true),
    LAZY_PANDA_HIDDEN("lazy_hidden", MobCompat.PANDA, "pandahidden:LAZY", true),
    WORRIED_PANDA_HIDDEN("worried_hidden", MobCompat.PANDA, "pandahidden:WORRIED", true),
    PLAYFUL_PANDA_HIDDEN("playful_hidden", MobCompat.PANDA, "pandahidden:PLAYFUL", true),
    BROWN_PANDA_HIDDEN("brown_hidden", MobCompat.PANDA, "pandahidden:BROWN", true),
    WEAK_PANDA_HIDDEN("weak_hidden", MobCompat.PANDA, "pandahidden:WEAK", true),
    CREAMY_LLAMA("creamy", MobCompat.LLAMA, "llama:CREAMY", true),
    WHITE_LLAMA("white", MobCompat.LLAMA, "llama:WHITE", true),
    BROWN_LLAMA("brown", MobCompat.LLAMA, "llama:BROWN", true),
    GRAY_LLAMA("gray", MobCompat.LLAMA, "llama:GRAY", true),
    CREAMY_TRADER_LLAMA("creamy", MobCompat.TRADER_LLAMA, "llama:CREAMY", true),
    WHITE_TRADER_LLAMA("white", MobCompat.TRADER_LLAMA, "llama:WHITE", true),
    BROWN_TRADER_LLAMA("brown", MobCompat.TRADER_LLAMA, "llama:BROWN", true),
    GRAY_TRADER_LLAMA("gray", MobCompat.TRADER_LLAMA, "llama:GRAY", true)
    ;


    public enum Data {
        ADULT,
        BABY,
        CHEST,
        ADULTZOMBIE,
        BABYZOMBIE,
        HORSESADDLE,
        PIGSADDLE,
        ELECTRIFIED,
        ANGRY,
        TAMED,
        COLORABLE,
        EXP,
        SIZE
    }



    public static final Logger logger = Logger.getLogger("Essentials");

    MobData(String n, Object type, Object value, boolean isPublic) {
        this.nickname = n;
        this.matched = n;
        this.helpMessage = n;
        this.type = type;
        this.value = value;
        this.isPublic = isPublic;
    }

    MobData(String n, String h, Object type, Object value, boolean isPublic) {
        this.nickname = n;
        this.matched = n;
        this.helpMessage = h;
        this.type = type;
        this.value = value;
        this.isPublic = isPublic;
    }

    final private String nickname;
    final private String helpMessage;
    final private Object type;
    final private Object value;
    final private boolean isPublic;
    private String matched;

    public static LinkedHashMap<String, MobData> getPossibleData(final Entity spawned, boolean publicOnly) {
        LinkedHashMap<String, MobData> mobList = new LinkedHashMap<>();
        for (MobData data : MobData.values()) {
            if (data.type == null || (publicOnly && !data.isPublic)) continue;
            
            if (data.type instanceof EntityType && spawned.getType().equals(data.type)) {
                mobList.put(data.nickname.toLowerCase(Locale.ENGLISH), data);
            } else if (data.type instanceof Class && ((Class) data.type).isAssignableFrom(spawned.getClass())) {
                mobList.put(data.nickname.toLowerCase(Locale.ENGLISH), data);
            }
        }

        return mobList;
    }

    public static List<String> getValidHelp(final Entity spawned) {
        List<String> output = new ArrayList<>();
        LinkedHashMap<String, MobData> posData = getPossibleData(spawned, true);

        for (MobData data : posData.values()) {
            output.add(data.helpMessage);
        }
        return output;
    }

    public static MobData fromData(final Entity spawned, final String name) {
        if (name.isEmpty()) {
            return null;
        }

        LinkedHashMap<String, MobData> posData = getPossibleData(spawned, false);
        for (String data : posData.keySet()) {
            if (name.contains(data)) {
                return posData.get(data);
            }
        }
        return null;
    }

    public String getMatched() {
        return this.matched;
    }

    public void setData(final Entity spawned, final Player target, final String rawData) throws Exception {
        if (this.value.equals(Data.ANGRY)) {
            ((Wolf) spawned).setAngry(true);
        } else if (this.value.equals(Data.ADULT)) {
            ((Ageable) spawned).setAdult();
        } else if (this.value.equals(Data.BABY)) {
            ((Ageable) spawned).setBaby();
        } else if (this.value.equals(Data.ADULTZOMBIE)) {
            ((Zombie) spawned).setBaby(false);
        } else if (this.value.equals(Data.BABYZOMBIE)) {
            ((Zombie) spawned).setBaby(true);
        } else if (this.value.equals(Data.ELECTRIFIED)) {
            ((Creeper) spawned).setPowered(true);
        } else if (this.value.equals(Data.HORSESADDLE)) {
            final Horse horse = ((Horse) spawned);
            horse.setTamed(true);
            horse.setOwner(target);
            horse.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
        } else if (this.value.equals(Data.PIGSADDLE)) {
            ((Pig) spawned).setSaddle(true);
        } else if (this.value.equals(Data.TAMED)) {
            final Tameable tameable = ((Tameable) spawned);
            tameable.setTamed(true);
            tameable.setOwner(target);
        } else if (this.value.equals(Data.COLORABLE)) {
            final String color = rawData.toUpperCase(Locale.ENGLISH);
            try {
                if (color.equals("RANDOM")) {
                    final Random rand = new Random();
                    ((Colorable) spawned).setColor(DyeColor.values()[rand.nextInt(DyeColor.values().length)]);
                } else if (!color.isEmpty()) {
                    ((Colorable) spawned).setColor(DyeColor.valueOf(color));
                }
                this.matched = rawData;
            } catch (Exception e) {
                throw new Exception(tl("sheepMalformedColor"), e);
            }
        } else if (this.value.equals(Data.EXP)) {
            try {
                ((ExperienceOrb) spawned).setExperience(Integer.parseInt(rawData));
                this.matched = rawData;
            } catch (NumberFormatException e) {
                throw new Exception(tl("invalidNumber"), e);
            }
        } else if (this.value.equals(Data.SIZE)) {
            try {
                ((Slime) spawned).setSize(Integer.parseInt(rawData));
                this.matched = rawData;
            } catch (NumberFormatException e) {
                throw new Exception(tl("slimeMalformedSize"), e);
            }
        } else if (this.value instanceof Horse.Color) {
            ((Horse) spawned).setColor((Horse.Color) this.value);
        } else if (this.value instanceof Horse.Style) {
            ((Horse) spawned).setStyle((Horse.Style) this.value);
        } else if (this.value instanceof MobCompat.CatType) {
            MobCompat.setCatType(spawned, (MobCompat.CatType) this.value);
        } else if (this.value instanceof MobCompat.VillagerProfession) {
            MobCompat.setVillagerProfession(spawned, (MobCompat.VillagerProfession) this.value);
        } else if (this.value instanceof Material) {
            if (this.type.equals(EntityType.HORSE)) {
                ((Horse) spawned).setTamed(true);
                ((Horse) spawned).getInventory().setArmor(new ItemStack((Material) this.value, 1));
            } else if (this.type.equals(EntityType.ZOMBIE.getEntityClass()) || this.type.equals(EntityType.SKELETON)) {
                final EntityEquipment invent = ((LivingEntity) spawned).getEquipment();
                InventoryWorkaround.setItemInMainHand(invent, new ItemStack((Material) this.value, 1));
                InventoryWorkaround.setItemInMainHandDropChance(invent, 0.1f);
            }
        } else if (this.value instanceof String) {
            final String[] split = ((String) this.value).split(":");
            switch (split[0]) {
                case "parrot":
                    MobCompat.setParrotVariant(spawned, split[1]);
                    break;
                case "tropicalfish":
                    MobCompat.setTropicalFishPattern(spawned, split[1]);
                    break;
                case "mooshroom":
                    MobCompat.setMooshroomVariant(spawned, split[1]);
                    break;
                case "pandamain":
                    MobCompat.setPandaGene(spawned, split[1], true);
                    break;
                case "pandahidden":
                    MobCompat.setPandaGene(spawned, split[1], false);
                    break;
                case "llama":
                    MobCompat.setLlamaColor(spawned, split[1]);
                    break;
                case "villagertype":
                    MobCompat.setVillagerType(spawned, split[1]);
                    break;
            }
        } else {
            logger.warning("Unknown mob data type: " + this.toString());
        }
    }
}
