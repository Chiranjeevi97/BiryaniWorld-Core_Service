package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.Campaign;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.CampaignRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.CampaignRepository;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.notification.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CampaignService {
    private static final Logger logger = LoggerFactory.getLogger(CampaignService.class);

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Campaign createCampaign(CampaignRequest request) {
        logger.debug("Creating new campaign: {}", request.getTitle());
        Campaign campaign = new Campaign();
        campaign.setTitle(request.getTitle());
        campaign.setContent(request.getContent());
        campaign.setScheduledTime(request.getScheduledTime());
        campaign.setTargetAudience(request.getTargetAudience());
        return campaignRepository.save(campaign);
    }

    @Transactional(readOnly = true)
    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Campaign getCampaignById(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));
    }

    @Transactional
    public Campaign updateCampaign(Long id, CampaignRequest request) {
        Campaign campaign = getCampaignById(id);
        campaign.setTitle(request.getTitle());
        campaign.setContent(request.getContent());
        campaign.setScheduledTime(request.getScheduledTime());
        campaign.setTargetAudience(request.getTargetAudience());
        return campaignRepository.save(campaign);
    }

    @Transactional
    public void deleteCampaign(Long id) {
        campaignRepository.deleteById(id);
    }

    @Scheduled(fixedRate = 300000) // Check every 5 minutes
    @Transactional
    public void processScheduledCampaigns() {
        logger.info("Checking for scheduled campaigns");
        List<Campaign> campaigns = campaignRepository.findByActiveTrueAndScheduledTimeBefore(LocalDateTime.now());
        
        for (Campaign campaign : campaigns) {
            try {
                emailService.sendCampaign(campaign);
                campaign.setSentAt(LocalDateTime.now());
                campaign.setActive(false);
                campaignRepository.save(campaign);
                logger.info("Campaign sent successfully: {}", campaign.getTitle());
            } catch (Exception e) {
                logger.error("Failed to send campaign: {}", campaign.getTitle(), e);
            }
        }
    }

    @Transactional
    public void updateCampaignStats(Long campaignId, int opened, int clicked, int bounced) {
        Campaign campaign = getCampaignById(campaignId);
        campaign.setOpenedCount(opened);
        campaign.setClickedCount(clicked);
        campaign.setBouncedCount(bounced);
        campaignRepository.save(campaign);
    }
} 