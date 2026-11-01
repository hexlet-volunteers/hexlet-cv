package io.hexlet.cv.model.converter;

import io.hexlet.cv.model.enums.RoleType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleTypeConverter implements AttributeConverter<RoleType, String> {

    @Override
    public String convertToDatabaseColumn(RoleType roleType) {
        return roleType == null ? null : roleType.name().toLowerCase();
    }

    @Override
    public RoleType convertToEntityAttribute(String dbValue) {
        return dbValue == null ? null : RoleType.valueOf(dbValue.toUpperCase());
    }
}
