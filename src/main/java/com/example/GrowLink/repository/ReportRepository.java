package com.example.GrowLink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.GrowLink.entity.Report;
import com.example.GrowLink.entity.User;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByReportedUser(User reportedUser);

    long countByReportedUser(User reportedUser);
}