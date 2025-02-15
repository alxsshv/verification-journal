package com.alxsshv.journal.repository;

import com.alxsshv.journal.model.Journal;
import com.alxsshv.journal.model.Protocol;
import com.alxsshv.security.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProtocolRepository extends JpaRepository<Protocol, Long> {

    Page<Protocol> findByJournal(Journal journal, Pageable pageable);

    List<Protocol> findByJournal(Journal journal);

    Page<Protocol> findByJournalAndNumberIgnoreCaseContainingOrDescriptionIgnoreCaseContaining(Journal journal, String number, String description, Pageable pageable);

    int countBySignedAndVerificationEmployee(boolean signed, User verificationEmployee);

    List<Protocol> findBySignedAndVerificationEmployee(boolean signed, User verificationEmployee);

    List<Protocol> findBySignedAndVerificationEmployeeAndDescriptionIgnoreCaseContaining(boolean signed, User verificationEmployee,String description);
}
