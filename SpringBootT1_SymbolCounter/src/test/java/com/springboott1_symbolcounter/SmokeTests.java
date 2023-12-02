package com.springboott1_symbolcounter;

import com.springboott1_symbolcounter.controllers.SymbolCounterController;
import com.springboott1_symbolcounter.services.OrderedCountToStringConverterService;
import com.springboott1_symbolcounter.services.SymbolCounterService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.stream.Stream;

@SpringBootTest
class SmokeTests {
    private final static String INPUT = "aabbvbcdcdc";

    @Autowired
    private SymbolCounterController symbolCounterController;
    @Test
    void contextLoads() {
        Assertions.assertNotNull(symbolCounterController);
    }

    @Test
    void checkSymbolsToOrderedMap() {
        var expected = new TreeMap<Integer, LinkedList<Character>>(Comparator.reverseOrder());
        expected.put(2, Stream.of('a', 'd')
                .collect(LinkedList::new, LinkedList::add, LinkedList::addAll));
        expected.put(3, Stream.of('b', 'c')
                .collect(LinkedList::new, LinkedList::add, LinkedList::addAll));
        expected.put(1, Stream.of('v')
                .collect(LinkedList::new, LinkedList::add, LinkedList::addAll));

        var result = SymbolCounterService.countSymbols(INPUT);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void checkSymbolsToResultingString() {
        var expected = "\"b\": 3, \"c\": 3, \"a\": 2, \"d\": 2, \"v\": 1";

        var result = OrderedCountToStringConverterService.convert(
                SymbolCounterService.countSymbols(INPUT)
        );

        Assertions.assertEquals(expected, result);
    }

}
