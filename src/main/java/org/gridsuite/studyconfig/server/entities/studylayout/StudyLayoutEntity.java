package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.*;
import lombok.*;
import org.gridsuite.studyconfig.server.dto.studylayout.StudyLayout;

import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
public class StudyLayoutEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID uuid;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<AbstractDiagramLayoutEntity> diagramGridLayoutEntityList;

    public StudyLayout toDto() {
        return StudyLayout.builder()
            .diagramLayoutParams(diagramGridLayoutEntityList.stream().map(AbstractDiagramLayoutEntity::toDto).toList())
            .build();
    }

    public void replaceAllDiagramLayouts(List<AbstractDiagramLayoutEntity> diagramGridLayoutEntityList) {
        this.diagramGridLayoutEntityList.clear();
        this.diagramGridLayoutEntityList.addAll(diagramGridLayoutEntityList);
    }
}
