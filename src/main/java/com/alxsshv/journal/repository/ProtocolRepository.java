package com.alxsshv.journal.repository;

import com.alxsshv.journal.model.Journal;
import com.alxsshv.journal.model.Protocol;
import com.alxsshv.security.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProtocolRepository extends JpaRepository<Protocol, Long> {

    Page<Protocol> findByJournal(Journal journal, Pageable pageable);

    List<Protocol> findByJournal(Journal journal);

    Page<Protocol> findByJournalAndNumberIgnoreCaseContainingOrDescriptionIgnoreCaseContaining(Journal journal, String number, String description, Pageable pageable);

    int countBySignedAndVerificationEmployee(boolean signed, User verificationEmployee);

    List<Protocol> findBySignedAndVerificationEmployee(boolean signed, User verificationEmployee);

    @NativeQuery("""
            select *
            from verification_protocols vp\s
            where signed = false and\s
            verification_employee_id  = :userId and\s
            (number ilike %:searchString% or description ilike %:searchString% or mi_serial_number ilike %:searchString% or mi_model ilike %:searchString%)""")
    List<Protocol> findNotSignedProtocolsByUserAndSearchSting(@Param("userId") long userId, @Param("searchString") String searhString);
}
