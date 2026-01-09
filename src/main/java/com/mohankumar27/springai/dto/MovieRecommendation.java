package com.mohankumar27.springai.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record MovieRecommendation(
        @JsonPropertyDescription("The full title of the movie")
        String title,
        @JsonPropertyDescription("A sentiment score between 0.0 (hate) and 1.0 (love)")
        double sentimentScore,
        @JsonPropertyDescription("The primary genre, e.g., Sci-Fi, Drama")
        String genre
) {}
