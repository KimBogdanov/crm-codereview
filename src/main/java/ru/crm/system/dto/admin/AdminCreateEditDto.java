package ru.crm.system.dto.admin;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

@Builder
@FieldNameConstants
public record AdminCreateEditDto(String firstName,
                                 String lastName,
                                 String phone,
                                 String email,
                                 String rawPassword,
                                 BigDecimal shiftRate) {
}