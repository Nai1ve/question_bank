package com.onepass.practice.practice.persistence;

import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PracticeSessionMapper {

    PracticeSessionDO selectBySessionId(@Param("sessionId") String sessionId);

    PracticeSessionDO selectLatestActiveSession(
            @Param("studentId") Long studentId,
            @Param("entryType") String entryType,
            @Param("categoryId") String categoryId
    );

    int insert(PracticeSessionDO session);

    int updateProgressAndActivity(
            @Param("sessionId") String sessionId,
            @Param("currentIndex") Integer currentIndex,
            @Param("lastActiveAt") LocalDateTime lastActiveAt,
            @Param("version") Long version
    );

    int updateStatus(
            @Param("sessionId") String sessionId,
            @Param("status") String status,
            @Param("lastActiveAt") LocalDateTime lastActiveAt,
            @Param("completedAt") LocalDateTime completedAt,
            @Param("expiredAt") LocalDateTime expiredAt,
            @Param("abandonedAt") LocalDateTime abandonedAt,
            @Param("version") Long version
    );
}
