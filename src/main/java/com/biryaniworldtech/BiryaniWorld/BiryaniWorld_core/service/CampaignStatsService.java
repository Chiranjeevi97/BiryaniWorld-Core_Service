package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.Campaign;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.CampaignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CampaignStatsService {
    private static final Logger logger = LoggerFactory.getLogger(CampaignStatsService.class);

    @Autowired
    private CampaignRepository campaignRepository;

    @Transactional
    public void updateCampaignStats(Long campaignId, int opened, int clicked, int bounced) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));
        
        campaign.setOpenedCount(opened);
        campaign.setClickedCount(clicked);
        campaign.setBouncedCount(bounced);
        campaignRepository.save(campaign);
        
        logger.info("Updated stats for campaign: {} - Opened: {}, Clicked: {}, Bounced: {}", 
            campaign.getTitle(), opened, clicked, bounced);
    }
} 