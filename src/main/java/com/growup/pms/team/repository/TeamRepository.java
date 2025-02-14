package com.growup.pms.team.repository;

import com.growup.pms.common.exception.code.ErrorCode;
import com.growup.pms.common.exception.exceptions.BusinessException;
import com.growup.pms.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("""
        SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
        FROM Team t
        WHERE t.id = :teamId AND t.creator.id = :userId""")
    boolean isUserTeamLeader(Long teamId, Long userId);

    boolean existsByName(String name);

    default Team findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));
    }
}
