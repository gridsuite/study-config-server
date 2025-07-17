package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "study_layout_id", foreignKey = @ForeignKey(name = "fk_diagram_layout_study_layout"))
    List<AbstractDiagramLayoutEntity> diagramGridLayoutEntityList;

    public void replaceAllDiagramLayouts(List<AbstractDiagramLayoutEntity> diagramGridLayoutEntityList) {
        this.diagramGridLayoutEntityList.clear();
        this.diagramGridLayoutEntityList.addAll(diagramGridLayoutEntityList);
    }
}
