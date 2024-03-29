package com.earth2me.essentials.utils;

import net.kyori.adventure.util.HSVLike;

/**
 * Most of this code was "borrowed" from KyoriPowered/adventure and is subject to their MIT license;
 *
 * MIT License
 *
 * Copyright (c) 2017-2020 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public final class DownsampleUtil {
    private final static NamedTextColor[] VALUES = new NamedTextColor[] {new NamedTextColor("0", 0x000000), new NamedTextColor("1", 0x0000aa), new NamedTextColor("2", 0x00aa00), new NamedTextColor("3", 0x00aaaa), new NamedTextColor("4", 0xaa0000), new NamedTextColor("5", 0xaa00aa), new NamedTextColor("6", 0xffaa00), new NamedTextColor("7", 0xaaaaaa), new NamedTextColor("8", 0x555555), new NamedTextColor("9", 0x5555ff), new NamedTextColor("a", 0x55ff55), new NamedTextColor("b", 0x55ffff), new NamedTextColor("c", 0xff5555), new NamedTextColor("d", 0xff55ff), new NamedTextColor("e", 0xffff55), new NamedTextColor("f", 0xffffff)};

    private DownsampleUtil() {
    }

    public static String nearestTo(final int rgb) {
        final HSVLike any = HSVLike.fromRGB((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff);

        float matchedDistance = Float.MAX_VALUE;
        NamedTextColor match = VALUES[0];
        for (final NamedTextColor potential : VALUES) {
            final float distance = distance(any, potential.hsv);
            if (distance < matchedDistance) {
                match = potential;
                matchedDistance = distance;
            }
            if (distance == 0) {
                break;
            }
        }
        return match.code;
    }

    private static float distance(final HSVLike self, final HSVLike other) {
        final float hueDistance = 3 * Math.abs(self.h() - other.h());
        final float saturationDiff = self.s() - other.s();
        final float valueDiff = self.v() - other.v();
        return hueDistance * hueDistance + saturationDiff * saturationDiff + valueDiff * valueDiff;
    }

    private static final class NamedTextColor {
        private final String code;
        private final int value;
        private final HSVLike hsv;

        private NamedTextColor(final String code, final int value) {
            this.code = code;
            this.value = value;
            this.hsv = HSVLike.fromRGB(this.red(), this.green(), this.blue());
        }

        private int red() {
            return (value >> 16) & 0xff;
        }

        private int green() {
            return (value >> 8) & 0xff;
        }

        private int blue() {
            return value & 0xff;
        }
    }
}
