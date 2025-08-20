package com.meokplaylist.api.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BooleanRequest(
        Boolean isAvailable
){ }
