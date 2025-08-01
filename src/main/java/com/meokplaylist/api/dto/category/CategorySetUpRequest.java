package com.meokplaylist.api.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategorySetUpRequest(
   @NotBlank String moodBigObject,
   @NotBlank String moodSmallObject,
   @NotBlank String foodBigObject,
   @NotBlank String foodSmallObject,
   @NotBlank String companionBigObject,
   @NotBlank String companionSmallObject,
   String localBigObject,
   String localSmallObject
) {}
