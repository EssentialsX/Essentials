package com.earth2me.essentials.utils;

import net.ess3.api.IEssentials;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import static com.earth2me.essentials.I18n.tl;

public final class NumberUtil {

    private static final DecimalFormat twoDPlaces = new DecimalFormat("#,###.##");
    private static final DecimalFormat currencyFormat = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.US));

    // This field is likely to be modified in com.earth2me.essentials.Settings when loading currency format.
    // This ensures that we can supply a constant formatting.
    private static NumberFormat PRETTY_FORMAT = NumberFormat.getInstance(Locale.US);

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

    public static String displayCurrency(final BigDecimal value, final IEssentials ess) {
        String currency = formatAsPrettyCurrency(value);
        String sign = "";
        if (value.signum() < 0) {
            currency = currency.substring(1);
            sign = "-";
        }
        if (ess.getSettings().isCurrencySymbolSuffixed()) {
            return sign + tl("currency", currency, ess.getSettings().getCurrencySymbol());
        }
        return sign + tl("currency", ess.getSettings().getCurrencySymbol(), currency);
    }

    public static String displayCurrencyExactly(final BigDecimal value, final IEssentials ess) {
        String currency = value.toPlainString();
        String sign = "";
        if (value.signum() < 0) {
            currency = currency.substring(1);
            sign = "-";
        }
        if (ess.getSettings().isCurrencySymbolSuffixed()) {
            return sign + tl("currency", currency, ess.getSettings().getCurrencySymbol());
        }
        return sign + tl("currency", ess.getSettings().getCurrencySymbol(), currency);
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

    /**
     * Backport from Guava.
     */
    public static int constrainToRange(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }
}
