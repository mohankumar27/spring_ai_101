package com.mohankumar27.springai.dto;

import java.util.List;

public record Recipe (
        String title,
        List<String> ingredients,
        List<String> instructions,
        int prepTimeMinutes
) {}
