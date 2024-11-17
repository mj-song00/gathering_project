package com.sparta.gathering.common.contributor;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;

public class CustomFunction {
    public static BooleanExpression match(StringPath target, String keyword) {

        if (keyword.isEmpty()) {
            return null;
        }
        String processedKeywords = String.join(" ",
                java.util.Arrays.stream(keyword.trim().split(" "))
                        .map(word -> "+" + word)
                        .toArray(String[]::new)
        );

        return Expressions.numberTemplate(
                Double.class,
                "FUNCTION('MATCH_AGAINST', {0}, {1})",
                target,
                processedKeywords
        ).gt(0);


    }
}
