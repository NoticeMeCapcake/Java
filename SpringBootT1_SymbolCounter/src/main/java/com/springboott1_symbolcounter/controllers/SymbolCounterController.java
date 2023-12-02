package com.springboott1_symbolcounter.controllers;

import com.springboott1_symbolcounter.services.OrderedCountToStringConverterService;
import com.springboott1_symbolcounter.services.SymbolCounterService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/counter")
public class SymbolCounterController {

    /**
     * Count the number of occurrences of each symbol in the input string
     * @param input is a sequence of symbols, with maximum length 100_000
     * @return a string with the number of occurrences of each symbol, sorted by descending order; example: "a:3, b:2, c:1"
     */
    @GetMapping(produces = "application/json", consumes = "application/json")
    public String getSymbolsCount(@NotNull @Validated @Length(min = 1, max = 100_000) @RequestBody String input) {
        log.info("Input: {}", input);
        var orderedSymbolsCount = SymbolCounterService.countSymbols(input);
        var result = OrderedCountToStringConverterService.convert(orderedSymbolsCount);
        log.info("Result: {}", result);
        return result;
    }



}
