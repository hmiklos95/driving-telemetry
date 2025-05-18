package com.example.analyze;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("submit")
@RequiredArgsConstructor
public class SubmitController {

    private final Submitter submitter;

    @GetMapping
    public void submit() {
        submitter.submit();
    }
}
