package com.digitalcoin.holdings_service.service.impl;

import com.digitalcoin.holdings_service.entity.SuggestReport;
import com.digitalcoin.holdings_service.repository.SuggestReportRepository;
import com.digitalcoin.holdings_service.service.SuggestReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 投资建议报告服务实现类
 */
@Service
public class SuggestReportServiceImpl implements SuggestReportService {

    @Autowired
    private SuggestReportRepository suggestReportRepository;

    @Override
    public Optional<SuggestReport> findById(Long id) {
        return suggestReportRepository.findById(id);
    }

    @Override
    public Page<SuggestReport> findByUserId(Long userId, Pageable pageable) {
        return suggestReportRepository.findByUserIdOrderByReportDateDesc(userId, pageable);
    }

    @Override
    public List<SuggestReport> findByUserIdAndReportType(Long userId, String reportType) {
        return suggestReportRepository.findByUserIdAndReportTypeOrderByReportDateDesc(userId, reportType);
    }

    @Override
    public long countUnreadByUserId(Long userId) {
        Integer count = suggestReportRepository.countUnreadByUserId(userId);
        return count != null ? count : 0;
    }

    @Override
    @Transactional
    public SuggestReport save(SuggestReport report) {
        return suggestReportRepository.save(report);
    }

    @Override
    @Transactional
    public List<SuggestReport> saveAll(List<SuggestReport> reports) {
        return suggestReportRepository.saveAll(reports);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        suggestReportRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        suggestReportRepository.deleteAllById(ids);
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        SuggestReport report = suggestReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("报告不存在"));
        report.setIsRead(true);
        report.setReadAt(LocalDateTime.now());
        suggestReportRepository.save(report);
    }

    @Override
    @Transactional
    public void markAllAsRead(List<Long> ids) {
        List<SuggestReport> reports = suggestReportRepository.findAllById(ids);
        LocalDateTime now = LocalDateTime.now();
        for (SuggestReport report : reports) {
            report.setIsRead(true);
            report.setReadAt(now);
        }
        suggestReportRepository.saveAll(reports);
    }

    @Override
    public Optional<SuggestReport> findLatestByUserId(Long userId) {
        // 使用Pageable获取最新的一条记录
        Pageable pageable = PageRequest.of(0, 1);
        List<SuggestReport> reports = suggestReportRepository.findLatestByUserId(userId, pageable);
        return reports.isEmpty() ? Optional.empty() : Optional.ofNullable(reports.get(0));
    }

    @Override
    public List<SuggestReport> findByUserIdAndCreatedTimeBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return suggestReportRepository.findByUserIdAndReportDateBetween(userId, startTime, endTime);
    }

    @Override
    @Transactional
    public SuggestReport createReport(Long userId, String reportType, String title, String content) {
        SuggestReport report = new SuggestReport();
        report.setUserId(userId);
        report.setReportType(reportType);
        report.setTitle(title);
        report.setContent(content);
        report.setStatus("PENDING"); // 初始状态为待审核
        report.setIsRead(false);
        report.setReportDate(LocalDateTime.now());
        return suggestReportRepository.save(report);
    }

    @Override
    public List<SuggestReport> findByUserIdAndStatus(Long userId, String status) {
        // 可以通过自定义查询实现
        // 这里简单返回所有报告，实际应根据状态过滤
        return suggestReportRepository.findByUserIdOrderByReportDateDesc(userId, Pageable.unpaged()).getContent()
                .stream().filter(r -> r.getStatus().equals(status)).toList();
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        SuggestReport report = suggestReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("报告不存在"));
        report.setStatus(status);
        // 不需要设置ReviewTime，因为SuggestReport实体中没有这个字段
        suggestReportRepository.save(report);
    }
}