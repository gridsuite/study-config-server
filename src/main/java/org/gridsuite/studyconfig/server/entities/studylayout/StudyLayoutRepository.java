package org.gridsuite.studyconfig.server.entities.studylayout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudyLayoutRepository extends JpaRepository<StudyLayoutEntity, UUID> {
}
