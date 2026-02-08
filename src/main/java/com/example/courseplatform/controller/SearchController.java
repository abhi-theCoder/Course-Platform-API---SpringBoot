package com.example.courseplatform.controller;

import com.example.courseplatform.dto.SearchResultDTO;
import com.example.courseplatform.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> search(@RequestParam String q) {
        List<SearchResultDTO> results = searchService.search(q);
        return ResponseEntity.ok(Map.of("query", q, "results", results));
    }
}
