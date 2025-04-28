package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByActiveTrueAndScheduledTimeBefore(LocalDateTime time);
    List<Campaign> findByTargetAudience(String targetAudience);
} 