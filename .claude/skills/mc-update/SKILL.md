---
name: mc-update
description: Update EssentialsX for a new Minecraft version. Use when the user asks to update to, bump, or support a new Minecraft or Paper version (for example "update to 26.4", "add 26.3 mobs", "bump supported version"), or asks what must be checked for new mobs, items, wood types, armor, enchantments, potions, or Bukkit API changes.
---

# Minecraft update

This checklist comes from every Minecraft update PR from 1.14 to 26.3, and from the fix PRs that followed them.

Paths are relative to the repository root. `ESS` means `Essentials/src/main/java/com/earth2me/essentials`.

## Procedure

1. Find the old and new `paper-api` jars in `~/.gradle/caches/modules-2/files-2.1/io.papermc.paper/paper-api/`. If the new jar is not there, set `paperVersion` in `build-logic/src/main/kotlin/essentials.base-conventions.gradle.kts` and run `./gradlew :EssentialsX:compileJava` to download it.
2. Diff the constants of these classes between the two jars: `org.bukkit.entity.EntityType`, `org.bukkit.Material`, `org.bukkit.enchantments.Enchantment`, `org.bukkit.potion.PotionEffectType`, `org.bukkit.potion.PotionType`, `org.bukkit.TreeType`, and each `*$Variant` class under `org.bukkit.entity`. Use this command for each class:

   ```sh
   unzip -p <jar> org/bukkit/entity/EntityType.class > /tmp/x.class && javap -p /tmp/x.class | grep 'public static final'
   ```

3. Sort each added, removed, or renamed constant into a section of part 2 or part 3 below.
4. Do part 1. Then do each section that applies.
5. Apply part 4 to every line you write.
6. Run `./gradlew :EssentialsX:compileJava`. Fix errors.
7. Do not commit unless the user asks.
8. Report per section: what changed, and what you skipped and why.

---

## 1. Every update

### 1.1 Version constants

1. Open `ESS/utils/VersionUtil.java`.
2. Add a constant `vXX_Y_R01 = BukkitVersion.fromString("XX.Y-R0.1-SNAPSHOT")`.
   - For a patch release, change the string of the existing minor constant. Example: `v26_1_R01` holds `26.1.2`.
   - For a minor release, add a new constant. Do not rename or delete an old constant. Old constants gate features. Deleting `v1_20_4_R01` caused #6460.
3. Add the constant to `supportedVersions`. Keep the `ImmutableSet.of(...)` syntax. `build-logic/src/main/kotlin/VersionDataTask.kt` parses it with a regex.
4. Start a server. Confirm `BukkitVersion.fromString(Bukkit.getBukkitVersion())` parses. Spigot 1.14.3 and Paper 26.1 both changed the string format.

### 1.2 Documentation

1. Update the supported versions sentence in `README.md`.
2. Regenerate `items.json` with ItemDbGenerator.

---

## 2. New content

### 2.1 New mob

1. Add an entry to `ESS/Mob.java`: `NAME("CamelName", Enemies.X, "ENTITY_TYPE")`.
   - Use the string constructor. Do not use `EntityType.X`. The constant does not exist on old servers.
   - Set the plural suffix if it is not `s`. Fish use `""`. Fox uses `es`.
   - The display name must not contain a space.
   - Boats and rafts are also `Mob` entries.
2. Add `EntityType` constant to `ESS/MobCompat.java` only if `MobData` needs it.
3. Add the mob to `protect.prevent.spawn` in `Essentials/src/main/resources/config.yml`.
4. Check `entityToDisplayName` in `providers/BaseProviders/.../SpawnerItemProvider.java` if the enum name does not title-case well.

### 2.2 New mob variant or data

1. Add entries to `ESS/MobData.java`. Use the string form `"kind:VARIANT"`. Example: `"wolf:PALE"`.
2. Add a `case "kind":` to the `setData` switch.
3. Add `setKindVariant(Entity, String)` to `MobCompat.java`. Gate it with `VersionUtil.getServerBukkitVersion().isLowerThan(vXX_Y_R01)`.
4. Use `RegistryUtil.valueOf(Variant.class, name)` for registry types. Do not use `Enum.valueOf`.
5. Do not call `NewClass.Variant.values()` in an enum initializer. Axolotl did this. It threw `NoClassDefFoundError` on 1.16 (#4376).
6. Catch `Throwable` around reflective lookups. Cow variant threw NPE on old servers (#6121).
7. For a boolean flag, add a `Data` constant and a branch in `setData`. Example: `GOAT_SCREAMING`.

### 2.3 New rideable or saddle mob

1. Add `SADDLE_<MOB>` to `MobData.java` with a `Data.<MOB>SADDLE` constant.
2. Add `set<Mob>Saddle(Entity, Player)` to `MobCompat.java`. Tame the mob. Set the owner. Set the saddle.
3. Check `ESS/craftbukkit/Inventories.java` if the mob adds a player inventory slot. Body and saddle slots (41, 42) broke `/clearinventory` in 1.21.6 (#6218).

### 2.4 New item or block

1. Check each `MaterialUtil` set that lists names by hand. See table.
2. Check `ESS/utils/LocationUtil.java`:
   - Add a block that hurts a standing player to `DAMAGING_TYPES`. Magma, campfire, berry bush and wither rose were missed for 2 years (#3537).
   - `HOLLOW_MATERIALS` comes from `Material.isTransparent()`. Add a passable block that is not transparent. Example: `LIGHT` (#4601). Remove a transparent block that is solid. Examples: `BARRIER`, `DIRT_PATH`, `FARMLAND`.
3. Check for a new event that reveals player position. Sculk sensor needed vanish handling in `EssentialsPlayerListener` (#5262).

| `ESS/utils/MaterialUtil.java` set | Add when |
|---|---|
| `HELMETS`, `CHESTPLATES`, `LEGGINGS`, `BOOTS` | New armor tier |
| `LEATHER_ARMOR` | New dyeable armor |
| `BEDS` | New bed. `STRAW_BED` in 26.3. |
| `BANNERS` | New banner colour |
| `SIGN_POSTS`, `WALL_SIGNS`, `HANGING_SIGNS`, `HANGING_WALL_SIGNS` | New wood type |
| `MOB_HEADS` | New head. `PIGLIN_HEAD` in 1.20. |
| `POTIONS` | New potion container |
| `EDITABLE_BOOKS` | New book type |
| `FIREWORKS`, `FIREWORK_CHARGE` | New firework item |

`EnumUtil.getAllMatching` skips a name that does not exist. Adding a name is safe on old servers.

### 2.5 Renamed item, block or entity

1. Grep `ESS` for `Material.OLD_NAME` and `EntityType.OLD_NAME`. Replace with `EnumUtil.getMaterial("NEW", "OLD")` or a `MobCompat` constant. `EntityType.PIG_ZOMBIE` in a static map broke 1.16 (#3412). `EntityType.FIREWORK` broke `/firework` in 1.20.6.
2. For a renamed `EntityType`, add `getEntityType("NEW", "OLD")` to `MobCompat.java`. Update `Mob.java`, `MobData.java` and `SpawnMob.java`.

### 2.6 New wood type

1. Add `<WOOD>_SIGN`, `<WOOD>_WALL_SIGN`, `<WOOD>_HANGING_SIGN`, `<WOOD>_WALL_HANGING_SIGN` to the four sign sets in `MaterialUtil.java`. Crimson and warped were missed in 1.16 (#3487).
2. Add `<WOOD>_BOAT` to `Mob.java`. Boats are separate entity types since 1.21.3.
3. Add `<WOOD>` to `MobCompat.BoatVariant` for servers before 1.21.3.
4. Add `<WOOD>_BOAT` to `MobData.java`.
5. Add `<WOOD>_CHEST_BOAT` to `Mob.java`.

### 2.7 New tree type

1. `Commandtree` reads `TreeType.values()`. It needs no code change.
2. Add the name to `treeCommandUsage` and `treeCommandUsage1` in `messages.properties`.
3. `Commandbigtree` has four hard-coded types. Add a case if the tree has a big variant.
4. Bukkit adds `TreeType` late. 26.3-pre-2 has no `POPLAR`.

### 2.8 New armor tier

1. Add the four pieces to `HELMETS`, `CHESTPLATES`, `LEGGINGS`, `BOOTS` in `MaterialUtil.java`.
2. Add a branch to the armor keyword chain in `ESS/SpawnMob.java`. The chain has `netherite`, `copper`, `diamond`, `gold`, `leather`, `iron`.
3. For horse armor, add `<TIER>_HORSE_ARMOR` to `MobData.java`. Example: `NETHERITE_HORSE_ARMOR`.

### 2.9 New enchantment

1. Add a `try { // XX.Y }` block to `ESS/Enchantments.java`.
2. Look up by name: `Enchantment.getByName("NAME")`. Check for null. Do not use `Enchantment.NAME`.
3. Add `ENCHANTMENTS.put("name", e)` and `ALIASENCHANTMENTS.put("alias", e)`.
4. Swift sneak (1.19) and lunge (1.21.11) were missed. Contributors added them months later.
5. Display and permission names come from `Enchantments.getRealName()`. It uses the key on 1.13+.

### 2.10 New potion effect or potion type

1. Add a `try { // XX.Y }` block to `ESS/Potions.java`. Catch `Throwable`.
2. Add `POTIONS.put("name", PotionEffectType.NAME)` and `ALIASPOTIONS.put(...)`.
3. `ModernPotionMetaProvider` maps `LONG_` and `STRONG_` prefixes. Check a new potion follows that pattern.

### 2.11 New banner pattern

No code change. `PatternTypeProvider` reads the registry.

### 2.12 New colour family

Check `MaterialUtil.getColorOf` for a colour name that is not a `DyeColor`.

### 2.13 New spawn egg

No code change. `FlatSpawnEggProvider` derives the egg from `EntityType.name() + "_SPAWN_EGG"`.

### 2.14 New command for new content

1. Gate the command with `VersionUtil`. Return `tl("unsupportedFeature")` on old servers. Examples: `/beezooka`, `/ice`.
2. Add the command to `plugin.yml` and `messages.properties`. Check message parameters. `iceOther` shipped without `{0}`.

---

## 3. API changes

### 3.1 Enum becomes a registry

Enchantment, PotionEffectType, Sound, PatternType, Villager.Profession, Cat.Type, Biome and Boat.Type all changed.

1. Replace `EnumUtil.valueOf` with `RegistryUtil.valueOf(Class, names...)`. Replace `.values()` with `RegistryUtil.values(Class)`.
2. If a static method moved from an enum to an interface, code compiled against the new API throws `IncompatibleClassChangeError` on old servers. `catch (Exception)` does not catch it. `PatternType.getByIdentifier` did this (#6553).
3. For such a case, add a provider pair:
   - Interface in `providers/BaseProviders/src/main/java/net/ess3/provider/`.
   - `Modern*Provider` in BaseProviders with `@ProviderData(weight = 1)` and a `@ProviderTest`.
   - `Legacy*Provider` in `providers/1_12Provider` or `providers/1_8Provider`. These compile against the old API.
   - Register with `providerFactory.registerProvider(Legacy.class, Modern.class)` in `Essentials.java`.
4. In a `@ProviderTest`, use `getMethod`, not `getDeclaredMethod`. 1.21 moved spawner methods to a super-interface (80bdc5269).

### 3.2 Removed API class

`PotionData`, `Potion`, `BannerMeta.setBaseColor` and `Achievement` were removed.

1. Move code that uses the removed class to a provider in `1_12Provider` or `1_8Provider`.
2. Add a modern provider in BaseProviders.
3. Remove the method from `OfflinePlayer`, `FakeWorld` and `FakeServer` if it was an override.

### 3.3 Changed method signature

1. Read the Bukkit diff for changed return types on methods EssentialsX calls.
2. A changed return type changes the call descriptor. The call throws `NoSuchMethodError` on old servers. `PlayerPickupArrowEvent#getArrow` did this (#3175). `InventoryView` becoming an interface did this (#5851).
3. Use a provider or reflection for the changed call.

### 3.4 New API methods on Player, World, Server

1. Add stubs to `providers/1_8Provider/.../OfflinePlayer.java` and `providers/1_12Provider/.../FakeWorld.java` if they fail to compile.
2. Tests use MockBukkit since 1.21.6. `FakeServer` no longer needs updates.

### 3.5 New event

1. Put the handler in a separate `*Listener_XX_Y` class.
2. Register it behind a `VersionUtil` check or `Class.forName`.
3. Do not put a new event in a listener that loads on old servers. `EntityTransformEvent` failed on 1.13.1 (#2942).

### 3.6 Spigot NMS obfuscation

1. Add `VXX_Y_RN` to `providers/NMSReflectionProvider/.../ReflUtil.java` if Spigot bumps the NMS revision.
2. Update the obfuscated `MinecraftServer#isRunning` name table in `ReflServerStateProvider.java`. This broke on 1.18, 1.19, 1.19.3, 1.20.6 and 1.21.10.
3. Paper uses `PaperServerStateProvider`. Test on Spigot to see this fail.

### 3.7 Paper internal classes

Paper config classes move. `ReflOnlineModeProvider` broke on 1.19 when config moved to `io.papermc.paper.configuration.GlobalConfiguration`. Check every reflective Paper access.

### 3.8 World height

1. Do not hard-code `0`, `-1`, `128` or `256` as a Y bound.
2. Use `WorldInfoProvider.getMinHeight`, `getMaxHeight` and `getLogicalHeight`.
3. Grep `LocationUtil`, `RandomTeleport`, `Commandtop`, `Commandsethome`, `Teleport`, `AsyncTeleport` and `SpawnMob`. 1.18 needed three fix PRs (#4657, #4667, #4715).

### 3.9 Item data components

1. Check `MetaItemStack.parseStringMeta`. The `[...]` component branch calls `UnsafeValues#modifyItemStack`. Its argument shape changed in 1.20.5 and again in 26.1 (#6570).
2. Check `Commandbook`. Writable and written books have separate meta since 1.20.6 (#6064).
3. Check `Commandskull`. Skull owner needs a resolved profile since 1.20.4 (#6188).
4. Check `MetaItemStack.canSpawn()`.

### 3.10 Vanilla messages

Check the vanilla changelog for a new client message that duplicates an EssentialsX message. 1.15 added "Respawn point set". `EssentialsPlayerListener` now gates `bedSet` (#2943).

### 3.11 Version string format

1. `BukkitVersion.VERSION_PATTERN` in `VersionUtil.java` must parse the new string.
2. Supported suffixes: `-snapshot-N`, `-pre-N`, `-rc-N`, `.build.N-channel`, `.local`, `-R0.1-SNAPSHOT`.
3. `compareDevSpecifier` orders `snapshot < pre < rc < release`.
4. `isSupportedVersion` compares major, minor and patch. A patch release needs a constant change.
5. 26.1 needed four fix PRs for this (#6480, #6483, #6500, #6501).

### 3.12 Adventure and Paper native paths

Each Paper API shift produced a gate:
- `Essentials.initAdventureFacet` gates the Paper facet at `v1_18_2_R01` (#6562).
- `EssentialsSpawnPlayerListener` gates `getRespawnLocation` at `v1_20_4_R01` (#6460).
- `EssentialsPlayerListener` gates configuration events at `v1_21_8_R01`.

Check each gate against the new version.

---

## 4. Compatibility rules

EssentialsX runs on 1.8.8 through the current version. Every update PR must keep that.

1. Do not reference a new `Material`, `EntityType`, `Enchantment` or `PotionEffectType` constant in a static field or enum initializer. Use `EnumUtil.getMaterial`, `EnumUtil.getEntityType` or a name lookup.
2. Catch `Throwable`, not `Exception`, around reflective lookups and provider selection. A swallowed `NoSuchFieldError` selected the wrong provider (7d6195375).
3. Use only Guava and Gson methods that exist in 1.8.8. `Ints.constrainToRange` and `JsonArray#add(String)` do not (#4708, 576866540).
4. Compile legacy providers in `1_8Provider` or `1_12Provider`. Code compiled against the new API can carry the wrong bytecode.
5. Gate with `isLowerThan(<next major>)`, not `isLowerThanOrEqualTo(<patch>)`. Forks use nonstandard revision numbers (43eff69a2).
6. Do not delete a `VersionUtil` constant.
7. Test on Paper latest, Spigot latest, 1.12.2 and 1.8.8. Most follow-up fixes in this history came from old servers.

---

## 5. Test

1. Run `./gradlew test`.
2. Run `./gradlew runServer`.
3. Run `/ess version`. Confirm the support status is FULL.
4. Run `/spawnmob <newmob>`, `/spawnmob <mob>:<variant>`.
5. Run `/give @s <newitem>`, `/give @s minecraft:<newitem>`.
6. Run `/enchant <newenchant>`, `/potion <neweffect>`.
7. Place an EssentialsX sign on each new sign type.
8. Run `/tree <newtree>` if Bukkit has the `TreeType`.
9. Run `/kit` with the new armor. Confirm auto-equip.
10. Teleport onto each new damaging block. Confirm `/tp` refuses.
11. Repeat on Spigot for `ReflServerStateProvider`.
