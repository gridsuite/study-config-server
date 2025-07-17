package org.gridsuite.studyconfig.server.service;

import jakarta.persistence.EntityNotFoundException;
import org.gridsuite.studyconfig.server.dto.studylayout.StudyLayout;
import org.gridsuite.studyconfig.server.entities.studylayout.StudyLayoutEntity;
import org.gridsuite.studyconfig.server.entities.studylayout.StudyLayoutRepository;
import org.gridsuite.studyconfig.server.mapper.StudyLayoutMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class StudyLayoutService {
    private final StudyLayoutRepository studyLayoutRepository;

    public StudyLayoutService(StudyLayoutRepository studyLayoutRepository) {
        this.studyLayoutRepository = studyLayoutRepository;
    }

    @Transactional(readOnly = true)
    public StudyLayout getByStudyLayoutUuid(UUID studyLayoutUuid) {
        StudyLayoutEntity entity = studyLayoutRepository
                .findById(studyLayoutUuid)
                .orElseThrow(() -> new EntityNotFoundException("Study layout not found with id: " + studyLayoutUuid));

        return StudyLayoutMapper.toDto(entity);
    }

    @Transactional
    public void deleteStudyLayout(UUID studyLayoutUuid) {
        studyLayoutRepository.deleteById(studyLayoutUuid);
    }

    @Transactional
    public UUID saveStudyLayout(StudyLayout studyLayout) {
        StudyLayoutEntity studyLayoutEntity = studyLayoutRepository.save(StudyLayoutMapper.toEntity(studyLayout));

        return studyLayoutEntity.getUuid();
    }

    @Transactional
    public void updateStudyLayout(UUID studyLayoutUuid, StudyLayout studyLayout) {
        StudyLayoutEntity studyLayoutEntity = studyLayoutRepository.findById(studyLayoutUuid).orElseThrow(() -> new EntityNotFoundException("Study layout not found with id: " + studyLayoutUuid));

        studyLayoutEntity.replaceAllDiagramLayouts(studyLayout.getDiagramLayoutParams().stream()
                .map(StudyLayoutMapper::toDiagramLayoutEntity)
                .toList());

        studyLayoutRepository.save(studyLayoutEntity);
    }
}
