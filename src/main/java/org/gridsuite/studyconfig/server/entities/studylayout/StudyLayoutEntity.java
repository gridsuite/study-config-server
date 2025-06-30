package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gridsuite.studyconfig.server.dto.studylayout.StudyLayout;

import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
public class StudyLayoutEntity {
    @Id
    UUID uuid;

    @OneToMany
    List<AbstractDiagramLayoutEntity> diagramGridLayoutEntityList;

    public StudyLayout toDto() {
        return StudyLayout.builder()
            .diagramLayoutParams(diagramGridLayoutEntityList.stream().map(AbstractDiagramLayoutEntity::toDto).toList())
            .build();
    }
}
