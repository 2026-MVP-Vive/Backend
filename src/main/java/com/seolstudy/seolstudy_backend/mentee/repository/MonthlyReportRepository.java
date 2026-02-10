package com.seolstudy.seolstudy_backend.mentee.repository;

import com.seolstudy.seolstudy_backend.mentee.domain.MonthlyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyReportRepository extends JpaRepository<MonthlyReport, Long> {
    List<MonthlyReport> findByMenteeIdOrderByReportYearDescReportMonthDesc(Long menteeId);

    Optional<MonthlyReport> findByMenteeIdAndReportYearAndReportMonth(Long menteeId, Integer reportYear,
                                                                      Integer reportMonth);
}