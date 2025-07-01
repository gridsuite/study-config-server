package org.gridsuite.studyconfig.server.service;

import jakarta.persistence.EntityNotFoundException;
import org.gridsuite.studyconfig.server.dto.studylayout.StudyLayout;
import org.gridsuite.studyconfig.server.entities.studylayout.StudyLayoutEntity;
import org.gridsuite.studyconfig.server.entities.studylayout.StudyLayoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class StudyLayoutService {
    private final StudyLayoutRepository studyLayoutRepository;

    public StudyLayoutService(StudyLayoutRepository studyLayoutRepository) {
        this.studyLayoutRepository = studyLayoutRepository;
    }

    @Transactional
    public StudyLayout getByStudyUuidAndUserId(UUID studyLayoutUuid) {
        return studyLayoutRepository
            .findById(studyLayoutUuid)
            .orElseThrow(() -> new EntityNotFoundException("Study layout not found with id: " + studyLayoutUuid))
            .toDto();
    }

    @Transactional
    public UUID saveStudyLayout(StudyLayout studyLayout) {
        StudyLayoutEntity studyLayoutEntity = studyLayoutRepository.save(studyLayout.toEntity());

        return studyLayoutEntity.getUuid();
    }
}
