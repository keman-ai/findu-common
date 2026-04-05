package com.findu.common.moderation.impl;

import com.findu.common.moderation.ContentModerationClient;
import com.findu.common.moderation.ModerationProperties;
import com.findu.common.moderation.ModerationResponse;
import com.findu.common.moderation.dto.ModerationApiResponse;
import com.findu.common.moderation.dto.ModerationSubmitRequest;
import com.findu.common.moderation.dto.ModerationSubmitResponse;
import com.findu.common.moderation.dto.ModerationSubmitMixedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容审核HTTP客户端。
 */
public class MisContentModerationClient implements ContentModerationClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MisContentModerationClient.class);

    private static final String SUBMIT_PATH = "/api/v1/inner/moderation/submit";
    private static final String SUBMIT_MIXED_PATH = "/api/v1/inner/moderation/submit_mixed";
    private static final String QUERY_REVIEW_PATH = "/api/v1/inner/moderation/result";

    private final RestTemplate restTemplate;

    private final ModerationProperties moderationProperties;

    public MisContentModerationClient(RestTemplate restTemplate,
                                      ModerationProperties moderationProperties) {
        this.restTemplate = restTemplate;
        this.moderationProperties = moderationProperties;
    }

    @Override
    public ModerationResponse submitText(String materialId, String userId, String text, String callbackUrl) {
        ModerationSubmitRequest request = new ModerationSubmitRequest();
        request.setMaterialId(materialId);
        request.setUserId(userId);
        request.setContentType("text");
        request.setTextContent(text);
        request.setCallbackUrl(callbackUrl);
        return submit(request);
    }

    @Override
    public ModerationResponse submitImage(String materialId, String userId, String url, String callbackUrl) {
        ModerationSubmitRequest request = new ModerationSubmitRequest();
        request.setMaterialId(materialId);
        request.setUserId(userId);
        request.setContentType("image");
        request.setMaterialUrl(url);
        request.setCallbackUrl(callbackUrl);
        return submit(request);
    }

    private ModerationResponse submit(ModerationSubmitRequest request) {
        String baseUrl = moderationProperties.getBaseUrl();
        if (!StringUtils.hasText(baseUrl)) {
            LOGGER.warn("Moderation base url not configured, fallback to pending status");
            return ModerationResponse.pendingFallback();
        }
        String endpoint = baseUrl + SUBMIT_PATH;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ModerationSubmitRequest> entity = new HttpEntity<>(request, headers);
        try {
            ResponseEntity<ModerationApiResponse<ModerationSubmitResponse>> response =
                    restTemplate.exchange(endpoint,
                            org.springframework.http.HttpMethod.POST,
                            entity,
                            new ParameterizedTypeReference<ModerationApiResponse<ModerationSubmitResponse>>() {
                            });
            ModerationApiResponse<ModerationSubmitResponse> body = response.getBody();
            if (body == null || body.getData() == null) {
                LOGGER.warn("Moderation service returned empty body");
                return ModerationResponse.pendingFallback();
            }
            ModerationSubmitResponse data = body.getData();
            return new ModerationResponse(
                    data.getReviewId(),
                    data.getReviewStatus(),
                    data.getRiskLevel(),
                    data.getRejectReason()
            );
        } catch (RestClientException ex) {
            LOGGER.error("Call moderation service failed", ex);
            return ModerationResponse.pendingFallback();
        }
    }

    @Override
    public ModerationResponse submitMixed(String materialId, String userId, List<ContentModerationClient.ModerationMaterial> materials, String callbackUrl) {
        ModerationSubmitMixedRequest request = new ModerationSubmitMixedRequest();
        request.setUserId(userId);
        
        List<ModerationSubmitMixedRequest.MaterialItem> materialItems = new ArrayList<>();
        for (ContentModerationClient.ModerationMaterial material : materials) {
            ModerationSubmitMixedRequest.MaterialItem item = new ModerationSubmitMixedRequest.MaterialItem();
            item.setMaterialId(material.getMaterialId());
            item.setContentType(material.getContentType());
            if (material.getTextContent() != null) {
                item.setTextContent(material.getTextContent());
            }
            if (material.getMaterialUrl() != null) {
                item.setMaterialUrl(material.getMaterialUrl());
            }
            materialItems.add(item);
        }
        
        request.setMaterials(materialItems);
        request.setCallbackUrl(callbackUrl);
        return submitMixed(request);
    }

    private ModerationResponse submitMixed(ModerationSubmitMixedRequest request) {
        String baseUrl = moderationProperties.getBaseUrl();
        if (!StringUtils.hasText(baseUrl)) {
            LOGGER.warn("Moderation base url not configured, fallback to pending status");
            return ModerationResponse.pendingFallback();
        }
        String endpoint = baseUrl + SUBMIT_MIXED_PATH;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ModerationSubmitMixedRequest> entity = new HttpEntity<>(request, headers);
        try {
            ResponseEntity<ModerationApiResponse<ModerationSubmitResponse>> response =
                    restTemplate.exchange(endpoint,
                            org.springframework.http.HttpMethod.POST,
                            entity,
                            new ParameterizedTypeReference<ModerationApiResponse<ModerationSubmitResponse>>() {
                            });
            ModerationApiResponse<ModerationSubmitResponse> body = response.getBody();
            if (body == null || body.getData() == null) {
                LOGGER.warn("Moderation service returned empty body");
                return ModerationResponse.pendingFallback();
            }
            ModerationSubmitResponse data = body.getData();
            return new ModerationResponse(
                    data.getReviewId(),
                    data.getReviewStatus(),
                    data.getRiskLevel(),
                    data.getRejectReason()
            );
        } catch (RestClientException ex) {
            LOGGER.error("Call moderation service failed", ex);
            return ModerationResponse.pendingFallback();
        }
    }

    @Override
    public ModerationResponse queryReviewStatus(String reviewId, String userId) {
        String baseUrl = moderationProperties.getBaseUrl();
        if (!StringUtils.hasText(baseUrl)) {
            LOGGER.warn("Moderation base url not configured, fallback to pending status");
            return ModerationResponse.pendingFallback();
        }
        String endpoint = baseUrl + QUERY_REVIEW_PATH + "/" + reviewId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<ModerationApiResponse<ModerationSubmitResponse>> response =
                    restTemplate.exchange(endpoint,
                            org.springframework.http.HttpMethod.GET,
                            entity,
                            new ParameterizedTypeReference<ModerationApiResponse<ModerationSubmitResponse>>() {
                            });
            ModerationApiResponse<ModerationSubmitResponse> body = response.getBody();
            if (body == null || body.getData() == null) {
                LOGGER.warn("Moderation service returned empty body for reviewId={}", reviewId);
                return ModerationResponse.pendingFallback();
            }
            ModerationSubmitResponse data = body.getData();
            return new ModerationResponse(
                    data.getReviewId(),
                    data.getReviewStatus(),
                    data.getRiskLevel(),
                    data.getRejectReason()
            );
        } catch (RestClientException ex) {
            LOGGER.error("Call moderation service query failed, reviewId={}", reviewId, ex);
            return ModerationResponse.pendingFallback();
        }
    }

}

