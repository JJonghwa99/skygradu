package com.SkyGradU.SkyGradU.QnA;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QnARepository extends JpaRepository<QnA, Long> {
}
