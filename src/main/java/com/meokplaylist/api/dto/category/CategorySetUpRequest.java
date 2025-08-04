package com.meokplaylist.api.dto.category;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CategorySetUpRequest(List<String> categoryNames) { }
