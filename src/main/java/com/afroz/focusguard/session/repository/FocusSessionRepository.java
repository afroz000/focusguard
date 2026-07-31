package com.afroz.focusguard.session.repository;

import com.afroz.focusguard.session.entity.FocusSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {
}