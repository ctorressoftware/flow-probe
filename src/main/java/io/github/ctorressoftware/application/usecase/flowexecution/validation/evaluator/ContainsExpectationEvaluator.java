package io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator;

import io.github.ctorressoftware.domain.model.ExpectationOperator;
import io.github.ctorressoftware.domain.model.ExpectationResult;

import java.util.Objects;

public class ContainsExpectationEvaluator implements ExpectationEvaluator {

    @Override
    public ExpectationOperator operator() {
        return ExpectationOperator.CONTAINS;
    }

    @Override
    public ExpectationResult evaluate(Object sentence, Object subString) {

        boolean successful =
                sentence instanceof String sentenceStr
                        && subString instanceof String subStringStr
                        && sentenceStr.contains(subStringStr);

        return new ExpectationResult(
                successful,
                subString,
                sentence
        );
    }
}
