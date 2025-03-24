package com.earth2me.essentials.utils;

import com.earth2me.essentials.commands.InvalidModifierException;
import net.ess3.api.IEssentials;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import static com.earth2me.essentials.I18n.tlLiteral;

public final class NumberUtil {

    private static final BigDecimal THOUSAND = new BigDecimal(1000);
    private static final BigDecimal MILLION = new BigDecimal(1_000_000);
    private static final BigDecimal BILLION = new BigDecimal(1_000_000_000);
    private static final BigDecimal TRILLION = new BigDecimal(1_000_000_000_000L);

    private static final DecimalFormat twoDPlaces = new DecimalFormat("#,###.##");
    private static final DecimalFormat currencyFormat = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.US));

    // This field is likely to be modified in com.earth2me.essentials.Settings when loading currency format.
    // This ensures that we can supply a constant formatting.
    private static Locale PRETTY_LOCALE = Locale.US;
    private static NumberFormat PRETTY_FORMAT = NumberFormat.getInstance(PRETTY_LOCALE);

    static {
        twoDPlaces.setRoundingMode(RoundingMode.HALF_UP);
        currencyFormat.setRoundingMode(RoundingMode.FLOOR);

        PRETTY_FORMAT.setRoundingMode(RoundingMode.FLOOR);
        PRETTY_FORMAT.setGroupingUsed(true);
        PRETTY_FORMAT.setMinimumFractionDigits(2);
        PRETTY_FORMAT.setMaximumFractionDigits(2);
    }

    private NumberUtil() {
    }

    // this method should only be called by Essentials
    public static void internalSetPrettyFormat(final NumberFormat prettyFormat) {
        PRETTY_FORMAT = prettyFormat;
    }

    public static String shortCurrency(final BigDecimal value, final IEssentials ess) {
        if (ess.getSettings().isCurrencySymbolSuffixed()) {
            return formatAsCurrency(value) + ess.getSettings().getCurrencySymbol();
        }
        return ess.getSettings().getCurrencySymbol() + formatAsCurrency(value);
    }

    public static String formatDouble(final double value) {
        return twoDPlaces.format(value);
    }

    public static String formatAsCurrency(final BigDecimal value) {
        String str = currencyFormat.format(value);
        if (str.endsWith(".00")) {
            str = str.substring(0, str.length() - 3);
        }
        return str;
    }

    public static String formatAsPrettyCurrency(final BigDecimal value) {
        String str = PRETTY_FORMAT.format(value);
        if (str.endsWith(".00")) {
            str = str.substring(0, str.length() - 3);
        }
        return str;
    }

    /**
     * Note: this *can* return MiniMessage, make sure if this is sent to a player that it is wrapped in AdventureUtil#parsed.
     */
    public static String displayCurrency(final BigDecimal value, final IEssentials ess) {
        return displayCurrency(value, ess, false);
    }

    /**
     * Note: this *can* return MiniMessage, make sure if this is sent to a player that it is wrapped in AdventureUtil#parsed.
     */
    public static String displayCurrencyExactly(final BigDecimal value, final IEssentials ess) {
        return displayCurrency(value, ess, true);
    }

    private static String displayCurrency(final BigDecimal value, final IEssentials ess, final boolean exact) {
        String currency = exact ? value.toPlainString() : formatAsPrettyCurrency(value);
        String sign = "";
        if (value.signum() < 0) {
            currency = currency.substring(1);
            sign = "-";
        }
        if (ess.getSettings().isCurrencySymbolSuffixed()) {
            return sign + tlLiteral("currency", currency, ess.getSettings().getCurrencySymbol());
        }
        return sign + tlLiteral("currency", ess.getSettings().getCurrencySymbol(), currency);
    }

    public static String sanitizeCurrencyString(final String input, final IEssentials ess) {
        return input.replace(ess.getSettings().getCurrencySymbol(), "");
    }

    public static boolean isInt(final String sInt) {
        try {
            Integer.parseInt(sInt);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isLong(final String sLong) {
        try {
            Long.parseLong(sLong);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isPositiveInt(final String sInt) {
        if (!isInt(sInt)) {
            return false;
        }
        return Integer.parseInt(sInt) > 0;
    }

    public static boolean isNumeric(final String sNum) {
        for (final char sChar : sNum.toCharArray()) {
            if (!Character.isDigit(sChar)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isHexadecimal(final String sNum) {
        try {
            Integer.parseInt(sNum, 16);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    public static BigDecimal parseStringToBDecimal(final String sArg, final Locale locale) throws ParseException, InvalidModifierException {
        if (sArg.isEmpty()) {
            throw new IllegalArgumentException();
        }

        final String sanitizedString = sArg.replaceAll("[^0-9.,]", "");
        BigDecimal multiplier = null;

        switch (sArg.replace(sanitizedString, "").toUpperCase()) {
            case "": {
                break;
            }
            case "K": {
                multiplier = THOUSAND;
                break;
            }
            case "M": {
                multiplier = MILLION;
                break;
            }
            case "B": {
                multiplier = BILLION;
                break;
            }
            case "T": {
                multiplier = TRILLION;
                break;
            }
            default:
                throw new InvalidModifierException();
        }

        final NumberFormat format = NumberFormat.getInstance(locale);
        final Number parsed = format.parse(sanitizedString);
        BigDecimal amount = new BigDecimal(parsed.toString());

        if (multiplier != null) {
            amount = amount.multiply(multiplier);
        }
        return amount;
    }

    public static BigDecimal parseStringToBDecimal(final String sArg) throws ParseException, InvalidModifierException {
        return parseStringToBDecimal(sArg, PRETTY_LOCALE);
    }

    /**
     * Backport from Guava.
     */
    public static int constrainToRange(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }
}
