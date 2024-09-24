package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.ItemGroupQuery;
import com.earth2me.essentials.MetaItemStack;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.MaterialUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.VersionUtil;
import net.ess3.api.IEssentials;
import net.ess3.api.MaxMoneyException;
import net.ess3.api.events.SignBreakEvent;
import net.ess3.api.events.SignCreateEvent;
import net.ess3.api.events.SignInteractEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static com.earth2me.essentials.I18n.tlLiteral;

public class EssentialsSign {
    private static final String SIGN_OWNER_KEY = "sign-owner";
    protected static final BigDecimal MINTRANSACTION = new BigDecimal("0.01");
    private static final Set<Material> EMPTY_SET = new HashSet<>();
    protected transient final String signName;

    public EssentialsSign(final String signName) {
        this.signName = signName;
    }

    protected static boolean checkIfBlockBreaksSigns(final Block block) {
        final Block sign = block.getRelative(BlockFace.UP);
        if (MaterialUtil.isSignPost(sign.getType()) && isValidSign(new BlockSign(sign))) {
            return true;
        }
        final BlockFace[] directions = new BlockFace[] {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        for (final BlockFace blockFace : directions) {
            final Block signBlock = block.getRelative(blockFace);
            if (MaterialUtil.isWallSign(signBlock.getType()) || MaterialUtil.isWallHangingSign(signBlock.getType())) {
                try {
                    if (getWallSignFacing(signBlock) == blockFace && isValidSign(new BlockSign(signBlock))) {
                        return true;
                    }
                } catch (final NullPointerException ex) {
                    // Sometimes signs enter a state of being semi broken, having no text or state data, usually while burning.
                }
            }
        }
        return false;
    }

    /**
     * @deprecated use {@link #isValidSign(IEssentials, ISign)} if possible
     */
    @Deprecated
    public static boolean isValidSign(final ISign sign) {
        return sign.getLine(0).matches("§1\\[.*]");
    }

    public static boolean isValidSign(final IEssentials ess, final ISign sign) {
        if (!sign.getLine(0).matches("§1\\[.*]"))
            return false;

        // Validate that the sign is actually an essentials sign
        final String signName = ChatColor.stripColor(sign.getLine(0)).replaceAll("[^a-zA-Z]", "");
        for (final EssentialsSign essSign : ess.getSettings().enabledSigns()) {
            if (essSign.getName().equalsIgnoreCase(signName))
                return true;
        }

        return false;
    }

    private static BlockFace getWallSignFacing(final Block block) {
        if (VersionUtil.PRE_FLATTENING) {
            //noinspection deprecation
            final org.bukkit.material.Sign signMat = (org.bukkit.material.Sign) block.getState().getData();
            return signMat.getFacing();
        }

        final Directional signData = (Directional) block.getState().getBlockData();
        return signData.getFacing();
    }

    protected final boolean onSignCreate(final SignChangeEvent event, final IEssentials ess) {
        final ISign sign = new EventSign(event);
        final User user = ess.getUser(event.getPlayer());
        if (!(user.isAuthorized("essentials.signs." + signName.toLowerCase(Locale.ENGLISH) + ".create") || user.isAuthorized("essentials.signs.create." + signName.toLowerCase(Locale.ENGLISH)))) {
            // Return true, so other plugins can use the same sign title, just hope
            // they won't change it to §1[Signname]
            return true;
        }
        sign.setLine(0, AdventureUtil.miniToLegacy(tlLiteral("signFormatFail", this.signName)));

        final SignCreateEvent signEvent = new SignCreateEvent(sign, this, user);
        ess.getServer().getPluginManager().callEvent(signEvent);
        if (signEvent.isCancelled()) {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().info("SignCreateEvent cancelled for sign " + signEvent.getEssentialsSign().getName());
            }
            return false;
        }

        try {
            final boolean ret = onSignCreate(sign, user, getUsername(user), ess);
            if (ret) {
                sign.setLine(0, getSuccessName(ess));
            }
            return ret;
        } catch (final ChargeException | SignException ex) {
            showError(ess, user.getSource(), ex, signName);
        }
        setOwnerData(ess, user, sign);
        // Return true, so the player sees the wrong sign.
        return true;
    }

    public String getSuccessName(final IEssentials ess) {
        final String successName = getSuccessName();
        if (successName == null) {
            ess.getLogger().severe("signFormatSuccess message must use the {0} argument.");
        }
        return successName;
    }

    public String getSuccessName() {
        String successName = AdventureUtil.miniToLegacy(tlLiteral("signFormatSuccess", this.signName));
        if (successName.isEmpty() || !successName.contains(this.signName)) {
            // Set to null to cause an error in place of no functionality. This makes an error obvious as opposed to leaving users baffled by lack of
            // functionality.
            successName = null;
        }
        return successName;
    }

    public String getTemplateName() {
        return AdventureUtil.miniToLegacy(tlLiteral("signFormatTemplate", this.signName));
    }

    public String getName() {
        return this.signName;
    }

    public String getUsername(final User user) {
        // Truncate username to ensure it can fit on a sign
        return user.getName().substring(0, Math.min(user.getName().length(), 13));
    }

    public void setOwner(final IEssentials ess, final User user, final ISign signProvider, final int nameIndex, final String namePrefix) {
        setOwnerData(ess, user, signProvider);
        signProvider.setLine(nameIndex, namePrefix + getUsername(user));
    }

    public void setOwnerData(final IEssentials ess, final User user, final ISign signProvider) {
        if (ess.getSignDataProvider() == null) {
            return;
        }
        final Sign sign = (Sign) signProvider.getBlock().getState();
        ess.getSignDataProvider().setSignData(sign, SIGN_OWNER_KEY, user.getUUID().toString());
    }

    public boolean isOwner(final IEssentials ess, final User user, final ISign signProvider, final int nameIndex, final String namePrefix) {
        final Sign sign = (Sign) signProvider.getBlock().getState();
        if (ess.getSignDataProvider() == null || ess.getSignDataProvider().getSignData(sign, SIGN_OWNER_KEY) == null) {
            final boolean isLegacyOwner = FormatUtil.stripFormat(signProvider.getLine(nameIndex)).equalsIgnoreCase(getUsername(user));
            if (ess.getSignDataProvider() != null && isLegacyOwner) {
                ess.getSignDataProvider().setSignData(sign, SIGN_OWNER_KEY, user.getUUID().toString());
            }
            return isLegacyOwner;
        }

        if (user.getUUID().toString().equals(ess.getSignDataProvider().getSignData(sign, SIGN_OWNER_KEY))) {
            signProvider.setLine(nameIndex, namePrefix + getUsername(user));
            return true;
        }
        return false;
    }

    protected final boolean onSignInteract(final Block block, final Player player, final IEssentials ess) {
        final ISign sign = new BlockSign(block);
        final User user = ess.getUser(player);
        if (user.checkSignThrottle()) {
            return false;
        }
        try {
            if (user.getBase().isDead() || !(user.isAuthorized("essentials.signs." + signName.toLowerCase(Locale.ENGLISH) + ".use") || user.isAuthorized("essentials.signs.use." + signName.toLowerCase(Locale.ENGLISH)))) {
                return false;
            }

            final SignInteractEvent signEvent = new SignInteractEvent(sign, this, user);
            ess.getServer().getPluginManager().callEvent(signEvent);
            if (signEvent.isCancelled()) {
                return false;
            }

            return onSignInteract(sign, user, getUsername(user), ess);
        } catch (final Exception ex) {
            showError(ess, user.getSource(), ex, signName);
            return false;
        }
    }

    protected final boolean onSignBreak(final Block block, final Player player, final IEssentials ess) throws MaxMoneyException {
        final ISign sign = new BlockSign(block);
        final User user = ess.getUser(player);
        try {
            if (!(user.isAuthorized("essentials.signs." + signName.toLowerCase(Locale.ENGLISH) + ".break") || user.isAuthorized("essentials.signs.break." + signName.toLowerCase(Locale.ENGLISH)))) {
                return false;
            }

            final SignBreakEvent signEvent = new SignBreakEvent(sign, this, user);
            ess.getServer().getPluginManager().callEvent(signEvent);
            if (signEvent.isCancelled()) {
                return false;
            }

            return onSignBreak(sign, user, getUsername(user), ess);
        } catch (final SignException ex) {
            showError(ess, user.getSource(), ex, signName);
            return false;
        }
    }

    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        return true;
    }

    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException, MaxMoneyException {
        return true;
    }

    protected boolean onSignBreak(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, MaxMoneyException {
        return true;
    }

    protected final boolean onBlockPlace(final Block block, final Player player, final IEssentials ess) {
        final User user = ess.getUser(player);
        try {
            return onBlockPlace(block, user, getUsername(user), ess);
        } catch (final ChargeException | SignException ex) {
            showError(ess, user.getSource(), ex, signName);
        }
        return false;
    }

    protected final boolean onBlockInteract(final Block block, final Player player, final IEssentials ess) {
        final User user = ess.getUser(player);
        try {
            return onBlockInteract(block, user, getUsername(user), ess);
        } catch (final ChargeException | SignException ex) {
            showError(ess, user.getSource(), ex, signName);
        }
        return false;
    }

    protected final boolean onBlockBreak(final Block block, final Player player, final IEssentials ess) throws MaxMoneyException {
        final User user = ess.getUser(player);
        try {
            return onBlockBreak(block, user, getUsername(user), ess);
        } catch (final SignException ex) {
            showError(ess, user.getSource(), ex, signName);
        }
        return false;
    }

    protected boolean onBlockBreak(final Block block, final IEssentials ess) {
        return true;
    }

    protected boolean onBlockExplode(final Block block, final IEssentials ess) {
        return true;
    }

    protected boolean onBlockBurn(final Block block, final IEssentials ess) {
        return true;
    }

    protected boolean onBlockIgnite(final Block block, final IEssentials ess) {
        return true;
    }

    protected boolean onBlockPush(final Block block, final IEssentials ess) {
        return true;
    }

    protected boolean onBlockPlace(final Block block, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        return true;
    }

    protected boolean onBlockInteract(final Block block, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        return true;
    }

    protected boolean onBlockBreak(final Block block, final User player, final String username, final IEssentials ess) throws SignException, MaxMoneyException {
        return true;
    }

    public Set<Material> getBlocks() {
        return EMPTY_SET;
    }

    public boolean areHeavyEventRequired() {
        return false;
    }

    private String getSignText(final ISign sign, final int lineNumber) {
        return sign.getLine(lineNumber).trim();
    }

    protected final void validateTrade(final ISign sign, final int index, final IEssentials ess) throws SignException {
        final String line = getSignText(sign, index);
        if (line.isEmpty()) {
            return;
        }
        final Trade trade = getTrade(sign, index, 0, ess);
        final BigDecimal money = trade.getMoney();
        if (money != null) {
            sign.setLine(index, NumberUtil.shortCurrency(money, ess));
        }
    }

    protected final void validateTrade(final ISign sign, final int amountIndex, final int itemIndex, final User player, final IEssentials ess) throws SignException {
        final String itemType = getSignText(sign, itemIndex);
        if (itemType.equalsIgnoreCase("exp") || itemType.equalsIgnoreCase("xp")) {
            final int amount = getIntegerPositive(getSignText(sign, amountIndex));
            sign.setLine(amountIndex, Integer.toString(amount));
            sign.setLine(itemIndex, "exp");
            return;
        }
        if(itemType.startsWith("~")){
            final int amount = getIntegerPositive(getSignText(sign, amountIndex));
            sign.setLine(amountIndex, Integer.toString(amount));
            sign.setLine(itemIndex, "§6~§r"+itemType.substring(1));
            return;
        }
        final Trade trade = getTrade(sign, amountIndex, itemIndex, player, ess);
        final ItemStack item = trade.getItemStack();
        sign.setLine(amountIndex, Integer.toString(item.getAmount()));
        sign.setLine(itemIndex, itemType);
    }

    protected final Trade getTrade(final ISign sign, final int amountIndex, final int itemIndex, final User player, final IEssentials ess) throws SignException {
        return getTrade(sign, amountIndex, itemIndex, player, false, ess);
    }

    protected final Trade getTrade(final ISign sign, final int amountIndex, final int itemIndex, final User player, final boolean allowId, final IEssentials ess) throws SignException {
        final String itemType = getSignText(sign, itemIndex);
        if (itemType.equalsIgnoreCase("exp") || itemType.equalsIgnoreCase("xp")) {
            final int amount = getIntegerPositive(getSignText(sign, amountIndex));
            return new Trade(amount, ess);
        }
        if(itemType.startsWith("§6~§r")){
            final int amount = Math.min(getIntegerPositive(getSignText(sign, amountIndex)), 64 * player.getBase().getInventory().getSize());
            final ItemGroupQuery query = new ItemGroupQuery(itemType.substring(5), amount);
            return new Trade(query, ess);
        }
        final ItemStack item = getItemStack(itemType, 1, allowId, ess);
        final int amount = Math.min(getIntegerPositive(getSignText(sign, amountIndex)), item.getType().getMaxStackSize() * player.getBase().getInventory().getSize());
        if (item.getType() == Material.AIR || amount < 1) {
            throw new SignException("moreThanZero");
        }
        item.setAmount(amount);
        return new Trade(item, ess);
    }

    protected final void validateInteger(final ISign sign, final int index) throws SignException {
        final String line = getSignText(sign, index);
        if (line.isEmpty()) {
            throw new SignException("emptySignLine", index + 1);
        }
        final int quantity = getIntegerPositive(line);
        sign.setLine(index, Integer.toString(quantity));
    }

    protected final int getIntegerPositive(final String line) throws SignException {
        final int quantity = getInteger(line);
        if (quantity < 1) {
            throw new SignException("moreThanZero");
        }
        return quantity;
    }

    protected final int getInteger(final String line) throws SignException {
        try {
            return Integer.parseInt(line);
        } catch (final NumberFormatException ex) {
            throw new SignException("invalidSign");
        }
    }

    protected final ItemStack getItemStack(final String itemName, final int quantity, final IEssentials ess) throws SignException {
        return getItemStack(itemName, quantity, false, ess);
    }

    protected final ItemStack getItemStack(final String itemName, final int quantity, final boolean allowId, final IEssentials ess) throws SignException {
        if (allowId && ess.getSettings().allowOldIdSigns()) {
            final Material newMaterial = ess.getItemDb().getFromLegacy(itemName);
            if (newMaterial != null) {
                return new ItemStack(newMaterial, quantity);
            }
        }

        try {
            final ItemStack item = ess.getItemDb().get(itemName);
            item.setAmount(quantity);
            return item;
        } catch (final Exception ex) {
            throw new SignException(ex, "errorWithMessage", ex.getMessage());
        }
    }

    protected final ItemStack getItemMeta(final ItemStack item, final String meta, final IEssentials ess) throws SignException {
        return this.getItemMeta(null, item, meta, ess);
    }

    protected final ItemStack getItemMeta(final CommandSource source, final ItemStack item, final String meta, final IEssentials ess) throws SignException {
        ItemStack stack = item;
        try {
            if (!meta.isEmpty()) {
                final MetaItemStack metaStack = new MetaItemStack(stack);
                final boolean allowUnsafe = ess.getSettings().allowUnsafeEnchantments();
                metaStack.addStringMeta(source, allowUnsafe, meta, ess);
                stack = metaStack.getItemStack();
            }
        } catch (final Exception ex) {
            throw new SignException(ex, "errorWithMessage", ex.getMessage());
        }
        return stack;
    }

    protected final BigDecimal getMoney(final String line, final IEssentials ess) throws SignException {
        final boolean isMoney = line.matches("^[^0-9-.]?[.0-9]+[^0-9-.]?$");
        return isMoney ? getBigDecimalPositive(line, ess) : null;
    }

    protected final BigDecimal getBigDecimalPositive(final String line, final IEssentials ess) throws SignException {
        final BigDecimal quantity = getBigDecimal(line, ess);
        if (quantity.compareTo(MINTRANSACTION) < 0) {
            throw new SignException("moreThanZero");
        }
        return quantity;
    }

    protected final BigDecimal getBigDecimal(final String line, final IEssentials ess) throws SignException {
        try {
            return new BigDecimal(NumberUtil.sanitizeCurrencyString(line, ess));
        } catch (final ArithmeticException | NumberFormatException ex) {
            throw new SignException(ex, "errorWithMessage", ex.getMessage());
        }
    }

    protected final Trade getTrade(final ISign sign, final int index, final IEssentials ess) throws SignException {
        return getTrade(sign, index, 1, ess);
    }

    protected final Trade getTrade(final ISign sign, final int index, final int decrement, final IEssentials ess) throws SignException {
        return getTrade(sign, index, decrement, false, ess);
    }

    protected final Trade getTrade(final ISign sign, final int index, final int decrement, final boolean allowId, final IEssentials ess) throws SignException {
        final String line = getSignText(sign, index);
        if (line.isEmpty()) {
            return new Trade(signName.toLowerCase(Locale.ENGLISH) + "sign", ess);
        }

        final BigDecimal money = getMoney(line, ess);
        if (money == null) {
            final String[] split = line.split("[ :]+", 2);
            if (split.length != 2) {
                throw new SignException("invalidCharge");
            }
            final int quantity = getIntegerPositive(split[0]);

            final String item = split[1].toLowerCase(Locale.ENGLISH);
            if (item.equalsIgnoreCase("times")) {
                sign.setLine(index, (quantity - decrement) + " times");
                sign.updateSign();
                return new Trade(signName.toLowerCase(Locale.ENGLISH) + "sign", ess);
            } else if (item.equalsIgnoreCase("exp") || item.equalsIgnoreCase("xp")) {
                sign.setLine(index, quantity + " exp");
                return new Trade(quantity, ess);
            } else {
                final ItemStack stack = getItemStack(item, quantity, allowId, ess);
                sign.setLine(index, quantity + " " + item);
                return new Trade(stack, ess);
            }
        } else {
            return new Trade(money, ess);
        }
    }

    private void showError(final IEssentials ess, final CommandSource sender, final Throwable exception, final String signName) {
        ess.showError(sender, exception, "\\ sign: " + signName);
    }

    public interface ISign {
        String getLine(final int index);

        void setLine(final int index, final String text);

        Block getBlock();

        void updateSign();
    }

    static class EventSign implements ISign {
        private final transient SignChangeEvent event;
        private final transient Block block;
        private transient Sign sign;

        EventSign(final SignChangeEvent event) {
            this.event = event;
            this.block = event.getBlock();
            this.sign = (Sign) block.getState();
        }

        @Override
        public final String getLine(final int index) {
            final StringBuilder builder = new StringBuilder();
            for (final char c : event.getLine(index).toCharArray()) {
                if (c < 0xF700 || c > 0xF747) {
                    builder.append(c);
                }
            }
            return builder.toString();
            //return event.getLine(index); // Above code can be removed and replaced with this line when https://github.com/Bukkit/Bukkit/pull/982 is merged.
        }

        @Override
        public final void setLine(final int index, final String text) {
            event.setLine(index, text);
            sign.setLine(index, text);
            updateSign();
        }

        @Override
        public Block getBlock() {
            return block;
        }

        @Override
        public void updateSign() {
            sign.update();
            sign = (Sign) block.getState();
        }
    }

    static class BlockSign implements ISign {
        private final transient Block block;
        private transient Sign sign;

        BlockSign(final Block block) {
            this.block = block;
            this.sign = getSign();
        }

        @Override
        public final String getLine(final int index) {
            final StringBuilder builder = new StringBuilder();
            for (final char c : getSign().getLine(index).toCharArray()) {
                if (c < 0xF700 || c > 0xF747) {
                    builder.append(c);
                }
            }
            return builder.toString();
            //return event.getLine(index); // Above code can be removed and replaced with this line when https://github.com/Bukkit/Bukkit/pull/982 is merged.
        }

        @Override
        public final void setLine(final int index, final String text) {
            sign.setLine(index, text);
            updateSign();
        }

        @Override
        public final Block getBlock() {
            return block;
        }

        private Sign getSign() {
            // Starting in 1.19, the block state is no longer persistent I guess? We must get it every time to prevent
            // sign updates from not taking effect.
            return (Sign) block.getState();
        }

        @Override
        public final void updateSign() {
            sign.update();
            sign = getSign();
        }
    }
}
