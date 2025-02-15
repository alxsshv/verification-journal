package com.alxsshv.journal.repository;

import com.alxsshv.journal.model.Journal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
    Optional<Journal> findByNumber(String number);

    List<Journal> findByNumberContainingOrTitleContainingOrDescriptionContaining(String number,
                                                                                 String title,
                                                                                 String description);

    Page<Journal> findByNumberContainingOrTitleContainingOrDescriptionContaining(String number,
                                                                                 String title,
                                                                                 String description,
                                                                                 Pageable pageable);
}
