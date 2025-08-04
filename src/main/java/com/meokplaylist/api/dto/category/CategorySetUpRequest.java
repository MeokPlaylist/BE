package com.meokplaylist.api.dto.category;

import com.meokplaylist.infra.Category.LocalCategory;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CategorySetUpRequest(
            @NotBlank   List<String> categoryFoodNames,
                        List<String> categoryLocalNames
) { }
