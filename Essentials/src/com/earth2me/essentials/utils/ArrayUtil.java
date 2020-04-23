package com.earth2me.essentials.utils;

import java.util.Arrays;

public class ArrayUtil {

    public static <T> T[] removeFirst(T[] array, int amount) {
        T[] newArray;

        if (array.length <= amount) {
            newArray = Arrays.copyOf(array, 0);
        } else {
            newArray = Arrays.copyOfRange(array, amount, array.length - 1);
        }

        return newArray;
    }

}
