package com.springboott1_symbolcounter.services;

import org.jetbrains.annotations.NotNull;

import java.util.*;


public class SymbolCounterService {
    private SymbolCounterService() {}

    /**
     * Count symbols in a string
     * @param input is the sequence of symbols
     * @return a map of symbols and their count, where the key is the count and the value is the symbol
     */
    @NotNull
    public static Map<Integer, LinkedList<Character>> countSymbols(@NotNull String input) {
        return getOrderedSymbols(countSymbolsInternal(input));
    }

    private static Map<Character, Integer> countSymbolsInternal(String input) {
        var symbolsCount = new HashMap<Character, Integer>();

        for (int i = 0; i < input.length(); i++) {
            var symbol = input.charAt(i);
            if (symbolsCount.containsKey(symbol)) {
                symbolsCount.put(symbol, symbolsCount.get(symbol) + 1);
            } else {
                symbolsCount.put(symbol, 1);
            }
        }

        return symbolsCount;
    }

    private static TreeMap<Integer, LinkedList<Character>> getOrderedSymbols(Map<Character, Integer> symbolsCount) {
        // From the highest element to the lowest one
        var orderedSymbols = new TreeMap<Integer, LinkedList<Character>>(Comparator.reverseOrder());
        for (var symbol : symbolsCount.entrySet()) {
            if (orderedSymbols.containsKey(symbol.getValue())) {
                orderedSymbols.get(symbol.getValue()).add(symbol.getKey());
            } else {
                var list = new LinkedList<Character>();
                list.add(symbol.getKey());
                orderedSymbols.put(symbol.getValue(), list);
            }
        }
        return orderedSymbols;
    }
}
