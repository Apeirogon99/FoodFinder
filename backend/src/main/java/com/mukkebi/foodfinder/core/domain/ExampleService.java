package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.api.controller.v1.response.ExampleResponse;
import com.mukkebi.foodfinder.core.api.controller.v1.response.ExampleStatsResponse;
import com.mukkebi.foodfinder.core.enums.EntityStatus;
import com.mukkebi.foodfinder.core.support.error.CoreException;
import com.mukkebi.foodfinder.core.support.error.ErrorType;
import com.mukkebi.foodfinder.storage.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExampleService {
    private final ExampleRepository exampleRepository;

    public ExampleResponse getExamples() {
        List<Example> examples = exampleRepository.findAll();
        return new ExampleResponse(
                examples.stream()
                        .map(Example::getExample)
                        .toList()
        );
    }

    public ExampleStatsResponse getExampleStats(LocalDate from, LocalDate to) {
        return exampleRepository.getStats(from, to);
    }

    public void postExample(String example) {
        if(exampleRepository.existsByExampleAndStatus(example, EntityStatus.ACTIVE)) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        exampleRepository.save(new Example(example));
    }
}
