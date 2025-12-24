package com.mukkebi.foodfinder.core.api.controller.v1;

import com.mukkebi.foodfinder.core.api.controller.v1.response.ExampleResponse;
import com.mukkebi.foodfinder.core.api.controller.v1.response.ExampleStatsResponse;
import com.mukkebi.foodfinder.core.domain.ExampleService;
import com.mukkebi.foodfinder.core.support.response.ApiResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
public class ExampleController {

    private final ExampleService exampleService;

    public ExampleController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @GetMapping("/api/v1/examples")
    public ApiResult<ExampleResponse> getExamples() {
        return ApiResult.success(exampleService.getExamples());
    }

    @GetMapping("/api/v1/examples/stats")
    public ApiResult<ExampleStatsResponse> getExampleStats(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        return ApiResult.success(exampleService.getExampleStats(from, to));
    }

    @PostMapping("api/v1/examples/{example}")
    public ApiResult<?> postExample(@PathVariable String example) {
        exampleService.postExample(example);
        return ApiResult.success();
    }
}
