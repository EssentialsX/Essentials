package com.earth2me.essentials.signs;

import com.earth2me.essentials.*;
import com.earth2me.essentials.utils.MaterialUtil;
import com.earth2me.essentials.utils.NumberUtil;
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
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static com.earth2me.essentials.I18n.tl;


/**
 * <p>EssentialsSign class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class EssentialsSign {
    private static final Set<Material> EMPTY_SET = new HashSet<Material>();
    /** Constant <code>MINTRANSACTION</code> */
    protected static final BigDecimal MINTRANSACTION = new BigDecimal("0.01");
    protected transient final String signName;

    /**
     * <p>Constructor for EssentialsSign.</p>
     *
     * @param signName a {@link java.lang.String} object.
     */
    public EssentialsSign(final String signName) {
        this.signName = signName;
    }

    /**
     * <p>onSignCreate.</p>
     *
     * @param event a {@link org.bukkit.event.block.SignChangeEvent} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     */
    protected final boolean onSignCreate(final SignChangeEvent event, final IEssentials ess) {
        final ISign sign = new EventSign(event);
        final User user = ess.getUser(event.getPlayer());
        if (!(user.isAuthorized("essentials.signs." + signName.toLowerCase(Locale.ENGLISH) + ".create") || user.isAuthorized("essentials.signs.create." + signName.toLowerCase(Locale.ENGLISH)))) {
            // Return true, so other plugins can use the same sign title, just hope
            // they won't change it to §1[Signname]
            return true;
        }
        sign.setLine(0, tl("signFormatFail", this.signName));

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
        } catch (ChargeException | SignException ex) {
            showError(ess, user.getSource(), ex, signName);
        }
        // Return true, so the player sees the wrong sign.
        return true;
    }

    /**
     * <p>getSuccessName.</p>
     *
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a {@link java.lang.String} object.
     */
    public String getSuccessName(IEssentials ess) {
        String successName = getSuccessName();
        if (successName == null) {
            ess.getLogger().severe("signFormatSuccess message must use the {0} argument.");
        }
        return successName;
    }

    /**
     * <p>getSuccessName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSuccessName() {
        String successName = tl("signFormatSuccess", this.signName);
        if (successName.isEmpty() || !successName.contains(this.signName)) {
            // Set to null to cause an error in place of no functionality. This makes an error obvious as opposed to leaving users baffled by lack of
            // functionality.
            successName = null;
        }
        return successName;
    }

    /**
     * <p>getTemplateName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTemplateName() {
        return tl("signFormatTemplate", this.signName);
    }

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return this.signName;
    }

    /**
     * <p>getUsername.</p>
     *
     * @param user a {@link com.earth2me.essentials.User} object.
     * @return a {@link java.lang.String} object.
     */
    public String getUsername(final User user) {
        return user.getName().substring(0, user.getName().length() > 13 ? 13 : user.getName().length());
    }

    /**
     * <p>onSignInteract.</p>
     *
     * @param block a {@link org.bukkit.block.Block} object.
     * @param player a {@link org.bukkit.entity.Player} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     */
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
        } catch (Exception ex) {
            showError(ess, user.getSource(), ex, signName);
            return false;
        }
    }

    /**
     * <p>onSignBreak.</p>
     *
     * @param block a {@link org.bukkit.block.Block} object.
     * @param player a {@link org.bukkit.entity.Player} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     * @throws net.ess3.api.MaxMoneyException if any.
     */
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
        } catch (SignException ex) {
            showError(ess, user.getSource(), ex, signName);
            return false;
        }
    }

    /**
     * <p>onSignCreate.</p>
     *
     * @param sign a {@link com.earth2me.essentials.signs.EssentialsSign.ISign} object.
     * @param player a {@link com.earth2me.essentials.User} object.
     * @param username a {@link java.lang.String} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     * @throws com.earth2me.essentials.signs.SignException if any.
     * @throws com.earth2me.essentials.ChargeException if any.
     */
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        return true;
    }

    /**
     * <p>onSignInteract.</p>
     *
     * @param sign a {@link com.earth2me.essentials.signs.EssentialsSign.ISign} object.
     * @param player a {@link com.earth2me.essentials.User} object.
     * @param username a {@link java.lang.String} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     * @throws com.earth2me.essentials.signs.SignException if any.
     * @throws com.earth2me.essentials.ChargeException if any.
     * @throws net.ess3.api.MaxMoneyException if any.
     */
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException, MaxMoneyException {
        return true;
    }

    /**
     * <p>onSignBreak.</p>
     *
     * @param sign a {@link com.earth2me.essentials.signs.EssentialsSign.ISign} object.
     * @param player a {@link com.earth2me.essentials.User} object.
     * @param username a {@link java.lang.String} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     * @throws com.earth2me.essentials.signs.SignException if any.
     * @throws net.ess3.api.MaxMoneyException if any.
     */
    protected boolean onSignBreak(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, MaxMoneyException {
        return true;
    }

    /**
     * <p>onBlockPlace.</p>
     *
     * @param block a {@link org.bukkit.block.Block} object.
     * @param player a {@link org.bukkit.entity.Player} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     */
    protected final boolean onBlockPlace(final Block block, final Player player, final IEssentials ess) {
        User user = ess.getUser(player);
        try {
            return onBlockPlace(block, user, getUsername(user), ess);
        } catch (ChargeException | SignException ex) {
            showError(ess, user.getSource(), ex, signName);
        }
        return false;
    }

    /**
     * <p>onBlockInteract.</p>
     *
     * @param block a {@link org.bukkit.block.Block} object.
     * @param player a {@link org.bukkit.entity.Player} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     */
    protected final boolean onBlockInteract(final Block block, final Player player, final IEssentials ess) {
        User user = ess.getUser(player);
        try {
            return onBlockInteract(block, user, getUsername(user), ess);
        } catch (ChargeException | SignException ex) {
            showError(ess, user.getSource(), ex, signName);
        }
        return false;
    }

    /**
     * <p>onBlockBreak.</p>
     *
     * @param block a {@link org.bukkit.block.Block} object.
     * @param player a {@link org.bukkit.entity.Player} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     * @throws net.ess3.api.MaxMoneyException if any.
     */
    protected final boolean onBlockBreak(final Block block, final Player player, final IEssentials ess) throws MaxMoneyException {
        User user = ess.getUser(player);
        try {
            return onBlockBreak(block, user, getUsername(user), ess);
        } catch (SignException ex) {
            showError(ess, user.getSource(), ex, signName);
        }
        return false;
    }

    /**
     * <p>onBlockBreak.</p>
     *
     * @param block a {@link org.bukkit.block.Block} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     */
    protected boolean onBlockBreak(final Block block, final IEssentials ess) {
        return true;
    }

    /**
     * <p>onBlockExplode.</p>
     *
     * @param block a {@link org.bukkit.block.Block} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     */
    protected boolean onBlockExplode(final Block block, final IEssentials ess) {
        return true;
    }

    /**
     * <p>onBlockBurn.</p>
     *
     * @param block a {@link org.bukkit.block.Block} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     */
    protected boolean onBlockBurn(final Block block, final IEssentials ess) {
        return true;
    }

    /**
     * <p>onBlockIgnite.</p>
     *
     * @param block a {@link org.bukkit.block.Block} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     */
    protected boolean onBlockIgnite(final Block block, final IEssentials ess) {
        return true;
    }

    /**
     * <p>onBlockPush.</p>
     *
     * @param block a {@link org.bukkit.block.Block} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     */
    protected boolean onBlockPush(final Block block, final IEssentials ess) {
        return true;
    }

    /**
     * <p>checkIfBlockBreaksSigns.</p>
     *
     * @param block a {@link org.bukkit.block.Block} object.
     * @return a boolean.
     */
    protected static boolean checkIfBlockBreaksSigns(final Block block) {
        final Block sign = block.getRelative(BlockFace.UP);
        if (MaterialUtil.isSignPost(sign.getType()) && isValidSign(new BlockSign(sign))) {
            return true;
        }
        final BlockFace[] directions = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        for (BlockFace blockFace : directions) {
            final Block signBlock = block.getRelative(blockFace);
            if (MaterialUtil.isWallSign(signBlock.getType())) {
                try {
                    if (getWallSignFacing(signBlock) == blockFace && isValidSign(new BlockSign(signBlock))) {
                        return true;
                    }
                } catch (NullPointerException ex) {
                    // Sometimes signs enter a state of being semi broken, having no text or state data, usually while burning.
                }
            }
        }
        return false;
    }

    /** @deprecated, use {@link #isValidSign(IEssentials, ISign)} if possible */
    /**
     * <p>isValidSign.</p>
     *
     * @param sign a {@link com.earth2me.essentials.signs.EssentialsSign.ISign} object.
     * @return a boolean.
     */
    @Deprecated
    public static boolean isValidSign(final ISign sign) {
        return sign.getLine(0).matches("§1\\[.*\\]");
    }

    /**
     * <p>isValidSign.</p>
     *
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @param sign a {@link com.earth2me.essentials.signs.EssentialsSign.ISign} object.
     * @return a boolean.
     */
    public static boolean isValidSign(final IEssentials ess, final ISign sign) {
        if (!sign.getLine(0).matches("§1\\[.*\\]"))
            return false;

        // Validate that the sign is actually an essentials sign
        String signName = ChatColor.stripColor(sign.getLine(0)).replaceAll("[^a-zA-Z]", "");
        for (EssentialsSign essSign : ess.getSettings().enabledSigns()) {
            if (essSign.getName().equalsIgnoreCase(signName))
                return true;
        }

        return false;
    }

    /**
     * <p>onBlockPlace.</p>
     *
     * @param block a {@link org.bukkit.block.Block} object.
     * @param player a {@link com.earth2me.essentials.User} object.
     * @param username a {@link java.lang.String} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     * @throws com.earth2me.essentials.signs.SignException if any.
     * @throws com.earth2me.essentials.ChargeException if any.
     */
    protected boolean onBlockPlace(final Block block, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        return true;
    }

    /**
     * <p>onBlockInteract.</p>
     *
     * @param block a {@link org.bukkit.block.Block} object.
     * @param player a {@link com.earth2me.essentials.User} object.
     * @param username a {@link java.lang.String} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     * @throws com.earth2me.essentials.signs.SignException if any.
     * @throws com.earth2me.essentials.ChargeException if any.
     */
    protected boolean onBlockInteract(final Block block, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        return true;
    }

    /**
     * <p>onBlockBreak.</p>
     *
     * @param block a {@link org.bukkit.block.Block} object.
     * @param player a {@link com.earth2me.essentials.User} object.
     * @param username a {@link java.lang.String} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a boolean.
     * @throws com.earth2me.essentials.signs.SignException if any.
     * @throws net.ess3.api.MaxMoneyException if any.
     */
    protected boolean onBlockBreak(final Block block, final User player, final String username, final IEssentials ess) throws SignException, MaxMoneyException {
        return true;
    }

    /**
     * <p>getBlocks.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Material> getBlocks() {
        return EMPTY_SET;
    }

    /**
     * <p>areHeavyEventRequired.</p>
     *
     * @return a boolean.
     */
    public boolean areHeavyEventRequired() {
        return false;
    }

    private String getSignText(final ISign sign, final int lineNumber) {
        return sign.getLine(lineNumber).trim();
    }

    /**
     * <p>validateTrade.</p>
     *
     * @param sign a {@link com.earth2me.essentials.signs.EssentialsSign.ISign} object.
     * @param index a int.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @throws com.earth2me.essentials.signs.SignException if any.
     */
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

    /**
     * <p>validateTrade.</p>
     *
     * @param sign a {@link com.earth2me.essentials.signs.EssentialsSign.ISign} object.
     * @param amountIndex a int.
     * @param itemIndex a int.
     * @param player a {@link com.earth2me.essentials.User} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @throws com.earth2me.essentials.signs.SignException if any.
     */
    protected final void validateTrade(final ISign sign, final int amountIndex, final int itemIndex, final User player, final IEssentials ess) throws SignException {
        final String itemType = getSignText(sign, itemIndex);
        if (itemType.equalsIgnoreCase("exp") || itemType.equalsIgnoreCase("xp")) {
            int amount = getIntegerPositive(getSignText(sign, amountIndex));
            sign.setLine(amountIndex, Integer.toString(amount));
            sign.setLine(itemIndex, "exp");
            return;
        }
        final Trade trade = getTrade(sign, amountIndex, itemIndex, player, ess);
        final ItemStack item = trade.getItemStack();
        sign.setLine(amountIndex, Integer.toString(item.getAmount()));
        sign.setLine(itemIndex, itemType);
    }

    /**
     * <p>getTrade.</p>
     *
     * @param sign a {@link com.earth2me.essentials.signs.EssentialsSign.ISign} object.
     * @param amountIndex a int.
     * @param itemIndex a int.
     * @param player a {@link com.earth2me.essentials.User} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a {@link com.earth2me.essentials.Trade} object.
     * @throws com.earth2me.essentials.signs.SignException if any.
     */
    protected final Trade getTrade(final ISign sign, final int amountIndex, final int itemIndex, final User player, final IEssentials ess) throws SignException {
        return getTrade(sign, amountIndex, itemIndex, player, false, ess);
    }

    /**
     * <p>getTrade.</p>
     *
     * @param sign a {@link com.earth2me.essentials.signs.EssentialsSign.ISign} object.
     * @param amountIndex a int.
     * @param itemIndex a int.
     * @param player a {@link com.earth2me.essentials.User} object.
     * @param allowId a boolean.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a {@link com.earth2me.essentials.Trade} object.
     * @throws com.earth2me.essentials.signs.SignException if any.
     */
    protected final Trade getTrade(final ISign sign, final int amountIndex, final int itemIndex, final User player, final boolean allowId, final IEssentials ess) throws SignException {
        final String itemType = getSignText(sign, itemIndex);
        if (itemType.equalsIgnoreCase("exp") || itemType.equalsIgnoreCase("xp")) {
            final int amount = getIntegerPositive(getSignText(sign, amountIndex));
            return new Trade(amount, ess);
        }
        final ItemStack item = getItemStack(itemType, 1, allowId, ess);
        final int amount = Math.min(getIntegerPositive(getSignText(sign, amountIndex)), item.getType().getMaxStackSize() * player.getBase().getInventory().getSize());
        if (item.getType() == Material.AIR || amount < 1) {
            throw new SignException(tl("moreThanZero"));
        }
        item.setAmount(amount);
        return new Trade(item, ess);
    }

    /**
     * <p>validateInteger.</p>
     *
     * @param sign a {@link com.earth2me.essentials.signs.EssentialsSign.ISign} object.
     * @param index a int.
     * @throws com.earth2me.essentials.signs.SignException if any.
     */
    protected final void validateInteger(final ISign sign, final int index) throws SignException {
        final String line = getSignText(sign, index);
        if (line.isEmpty()) {
            throw new SignException("Empty line " + index);
        }
        final int quantity = getIntegerPositive(line);
        sign.setLine(index, Integer.toString(quantity));
    }

    /**
     * <p>getIntegerPositive.</p>
     *
     * @param line a {@link java.lang.String} object.
     * @return a int.
     * @throws com.earth2me.essentials.signs.SignException if any.
     */
    protected final int getIntegerPositive(final String line) throws SignException {
        final int quantity = getInteger(line);
        if (quantity < 1) {
            throw new SignException(tl("moreThanZero"));
        }
        return quantity;
    }

    /**
     * <p>getInteger.</p>
     *
     * @param line a {@link java.lang.String} object.
     * @return a int.
     * @throws com.earth2me.essentials.signs.SignException if any.
     */
    protected final int getInteger(final String line) throws SignException {
        try {
            final int quantity = Integer.parseInt(line);

            return quantity;
        } catch (NumberFormatException ex) {
            throw new SignException("Invalid sign", ex);
        }
    }

    /**
     * <p>getItemStack.</p>
     *
     * @param itemName a {@link java.lang.String} object.
     * @param quantity a int.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a {@link org.bukkit.inventory.ItemStack} object.
     * @throws com.earth2me.essentials.signs.SignException if any.
     */
    protected final ItemStack getItemStack(final String itemName, final int quantity, final IEssentials ess) throws SignException {
        return getItemStack(itemName, quantity, false, ess);
    }

    /**
     * <p>getItemStack.</p>
     *
     * @param itemName a {@link java.lang.String} object.
     * @param quantity a int.
     * @param allowId a boolean.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a {@link org.bukkit.inventory.ItemStack} object.
     * @throws com.earth2me.essentials.signs.SignException if any.
     */
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
        } catch (Exception ex) {
            throw new SignException(ex.getMessage(), ex);
        }
    }

    /**
     * <p>getItemMeta.</p>
     *
     * @param item a {@link org.bukkit.inventory.ItemStack} object.
     * @param meta a {@link java.lang.String} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a {@link org.bukkit.inventory.ItemStack} object.
     * @throws com.earth2me.essentials.signs.SignException if any.
     */
    protected final ItemStack getItemMeta(final ItemStack item, final String meta, final IEssentials ess) throws SignException {
        ItemStack stack = item;
        try {
            if (!meta.isEmpty()) {
                MetaItemStack metaStack = new MetaItemStack(stack);
                final boolean allowUnsafe = ess.getSettings().allowUnsafeEnchantments();
                metaStack.addStringMeta(null, allowUnsafe, meta, ess);
                stack = metaStack.getItemStack();
            }
        } catch (Exception ex) {
            throw new SignException(ex.getMessage(), ex);
        }
        return stack;
    }

    /**
     * <p>getMoney.</p>
     *
     * @param line a {@link java.lang.String} object.
     * @return a {@link java.math.BigDecimal} object.
     * @throws com.earth2me.essentials.signs.SignException if any.
     */
    protected final BigDecimal getMoney(final String line) throws SignException {
        final boolean isMoney = line.matches("^[^0-9-\\.][\\.0-9]+$");
        return isMoney ? getBigDecimalPositive(line.substring(1)) : null;
    }

    /**
     * <p>getBigDecimalPositive.</p>
     *
     * @param line a {@link java.lang.String} object.
     * @return a {@link java.math.BigDecimal} object.
     * @throws com.earth2me.essentials.signs.SignException if any.
     */
    protected final BigDecimal getBigDecimalPositive(final String line) throws SignException {
        final BigDecimal quantity = getBigDecimal(line);
        if (quantity.compareTo(MINTRANSACTION) < 0) {
            throw new SignException(tl("moreThanZero"));
        }
        return quantity;
    }

    /**
     * <p>getBigDecimal.</p>
     *
     * @param line a {@link java.lang.String} object.
     * @return a {@link java.math.BigDecimal} object.
     * @throws com.earth2me.essentials.signs.SignException if any.
     */
    protected final BigDecimal getBigDecimal(final String line) throws SignException {
        try {
            return new BigDecimal(line);
        } catch (ArithmeticException ex) {
            throw new SignException(ex.getMessage(), ex);
        } catch (NumberFormatException ex) {
            throw new SignException(ex.getMessage(), ex);
        }
    }

    /**
     * <p>getTrade.</p>
     *
     * @param sign a {@link com.earth2me.essentials.signs.EssentialsSign.ISign} object.
     * @param index a int.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a {@link com.earth2me.essentials.Trade} object.
     * @throws com.earth2me.essentials.signs.SignException if any.
     */
    protected final Trade getTrade(final ISign sign, final int index, final IEssentials ess) throws SignException {
        return getTrade(sign, index, 1, ess);
    }

    /**
     * <p>getTrade.</p>
     *
     * @param sign a {@link com.earth2me.essentials.signs.EssentialsSign.ISign} object.
     * @param index a int.
     * @param decrement a int.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a {@link com.earth2me.essentials.Trade} object.
     * @throws com.earth2me.essentials.signs.SignException if any.
     */
    protected final Trade getTrade(final ISign sign, final int index, final int decrement, final IEssentials ess) throws SignException {
        return getTrade(sign, index, decrement, false, ess);
    }

    /**
     * <p>getTrade.</p>
     *
     * @param sign a {@link com.earth2me.essentials.signs.EssentialsSign.ISign} object.
     * @param index a int.
     * @param decrement a int.
     * @param allowId a boolean.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @return a {@link com.earth2me.essentials.Trade} object.
     * @throws com.earth2me.essentials.signs.SignException if any.
     */
    protected final Trade getTrade(final ISign sign, final int index, final int decrement, final boolean allowId, final IEssentials ess) throws SignException {
        final String line = getSignText(sign, index);
        if (line.isEmpty()) {
            return new Trade(signName.toLowerCase(Locale.ENGLISH) + "sign", ess);
        }

        final BigDecimal money = getMoney(line);
        if (money == null) {
            final String[] split = line.split("[ :]+", 2);
            if (split.length != 2) {
                throw new SignException(tl("invalidCharge"));
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

    private static BlockFace getWallSignFacing(Block block) {
        try {
            final WallSign signData = (WallSign) block.getState().getBlockData();
            return signData.getFacing();
        } catch (NoClassDefFoundError e) {
            final org.bukkit.material.Sign signMat = (org.bukkit.material.Sign) block.getState().getData();
            return signMat.getFacing();
        }
    }


    static class EventSign implements ISign {
        private final transient SignChangeEvent event;
        private final transient Block block;
        private final transient Sign sign;

        EventSign(final SignChangeEvent event) {
            this.event = event;
            this.block = event.getBlock();
            this.sign = (Sign) block.getState();
        }

        @Override
        public final String getLine(final int index) {
            StringBuilder builder = new StringBuilder();
            for (char c : event.getLine(index).toCharArray()) {
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
        }
    }


    static class BlockSign implements ISign {
        private final transient Sign sign;
        private final transient Block block;

        BlockSign(final Block block) {
            this.block = block;
            this.sign = (Sign) block.getState();
        }

        @Override
        public final String getLine(final int index) {
            StringBuilder builder = new StringBuilder();
            for (char c : sign.getLine(index).toCharArray()) {
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
        }

        @Override
        public final Block getBlock() {
            return block;
        }

        @Override
        public final void updateSign() {
            sign.update();
        }
    }


    public interface ISign {
        String getLine(final int index);

        void setLine(final int index, final String text);

        Block getBlock();

        void updateSign();
    }
}
