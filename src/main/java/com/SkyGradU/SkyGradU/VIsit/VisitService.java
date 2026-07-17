package com.SkyGradU.SkyGradU.VIsit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;

    @Transactional
    public void recordVisitOncePerSession(HttpServletRequest request, HttpSession session) {
        LocalDate today = LocalDate.now();
        String visitedKey = "visited_" + today;

        if (session.getAttribute(visitedKey) == null) {
            Visit visit = new Visit();
            visit.setVisitDate(today);
            visit.setIpAddress(request.getRemoteAddr());
            visit.setUserAgent(request.getHeader("User-Agent"));
            visitRepository.save(visit);

            session.setAttribute(visitedKey, true); // 오늘 날짜로 방문 체크
        }
    }

    public long getTodayVisitCount() {
        return visitRepository.countByVisitDate(LocalDate.now());
    }

    public long getTotalVisitCount() {
        return visitRepository.count();
    }
}
