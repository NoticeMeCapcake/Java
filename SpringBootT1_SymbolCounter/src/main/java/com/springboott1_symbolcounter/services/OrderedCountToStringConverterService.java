package com.springboott1_symbolcounter.services;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Map;

public class OrderedCountToStringConverterService {
    private OrderedCountToStringConverterService() {}

    /**
     * Convert ordered symbols count to string
     * @param orderedSymbolsCount is a map of counts and corresponding symbols
     *                               where keys are counts and values are symbols
     * @return string representation of ordered symbols count,
     *         example: "a:3, b:2, c:1"
     */
    @NotNull
    public static String convert(@NotNull Map<Integer, LinkedList<Character>> orderedSymbolsCount) {
        return convertInternal(orderedSymbolsCount);
    }

    // P.S. можно добавить в строку скобки, если нужно чтобы результат был в формате json
    // я не совсем согласен с регламентом результата, и делал бы с фигурными скобками
    private static String convertInternal(Map<Integer, LinkedList<Character>> orderedSymbolsCount) {
        var result = new StringBuilder();

        for (var entry : orderedSymbolsCount.entrySet()) {
            for (var symbol : entry.getValue()) {
                result.append("\"%c\": %d, ".formatted(symbol, entry.getKey()));
            }
        }

        // remove the last comma
        result.delete(result.length() - 2, result.length());

        return result.toString();
    }
}
