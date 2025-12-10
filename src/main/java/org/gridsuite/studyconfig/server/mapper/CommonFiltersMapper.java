package org.gridsuite.studyconfig.server.mapper;

import org.gridsuite.studyconfig.server.dto.ColumnInfos;
import org.gridsuite.studyconfig.server.dto.GlobalFilterInfos;
import org.gridsuite.studyconfig.server.entities.ColumnEntity;
import org.gridsuite.studyconfig.server.entities.GlobalFilterEntity;

public final class CommonFiltersMapper {

    private CommonFiltersMapper() { }

    public static ColumnInfos toColumnDto(ColumnEntity entity) {
        return new ColumnInfos(
                entity.getUuid(),
                entity.getName(),
                entity.getType(),
                entity.getPrecision(),
                entity.getFormula(),
                entity.getDependencies(),
                entity.getId(),
                entity.getFilterDataType(),
                entity.getFilterType(),
                entity.getFilterValue(),
                entity.getFilterTolerance(),
                entity.isVisible()
        );
    }

    public static ColumnEntity toColumnEntity(ColumnInfos dto) {
        return ColumnEntity.builder()
                .name(dto.name())
                .type(dto.type())
                .precision(dto.precision())
                .formula(dto.formula())
                .dependencies(dto.dependencies())
                .id(dto.id())
                .filterDataType(dto.filterDataType())
                .filterType(dto.filterType())
                .filterValue(dto.filterValue())
                .filterTolerance(dto.filterTolerance())
                .visible(dto.visible())
                .build();
    }

    public static GlobalFilterInfos toGlobalFilterDto(GlobalFilterEntity entity) {
        return GlobalFilterInfos.builder()
                .uuid(entity.getUuid())
                .filterType(entity.getFilterType())
                .filterSubtype(entity.getFilterSubtype())
                .label(entity.getLabel())
                .recent(entity.isRecent())
                .equipmentType(entity.getEquipmentType())
                .path(entity.getPath())
                .build();

    }

    public static GlobalFilterEntity toGlobalFilterEntity(GlobalFilterInfos dto) {
        return GlobalFilterEntity.builder()
                .filterType(dto.filterType())
                .filterSubtype(dto.filterSubtype())
                .uuid(dto.uuid())
                .label(dto.label())
                .recent(dto.recent())
                .equipmentType(dto.equipmentType())
                .path(dto.path())
                .build();
    }
}
