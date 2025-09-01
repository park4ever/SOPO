package com.sopo.dto.item.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ItemCategoryRenameRequest(
        @NotBlank @Size(max = 50) String newName
) {}