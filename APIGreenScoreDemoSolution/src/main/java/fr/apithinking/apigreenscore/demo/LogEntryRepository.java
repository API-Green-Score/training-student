package fr.apithinking.apigreenscore.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    // Pas besoin de méthode personnalisée pour l’instant
}
