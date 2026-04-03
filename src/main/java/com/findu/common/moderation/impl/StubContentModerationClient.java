package com.findu.common.moderation.impl;

import com.findu.common.moderation.ContentModerationClient;
import com.findu.common.moderation.ModerationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * 开发/测试环境使用的审核客户端实现，直接返回待审核状态。
 */
@Profile({"dev", "test"})
@Component
public class StubContentModerationClient implements ContentModerationClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StubContentModerationClient.class);

    @Override
    public ModerationResponse submitText(String materialId, String userId, String text, String callbackUrl) {
        LOGGER.info("submitText: materialId={}, userId={}, text={}, callbackUrl={}",
                materialId, userId, text, callbackUrl);
        return new ModerationResponse(generateReviewId(), "pending", "medium", null);
    }

    @Override
    public ModerationResponse submitImage(String materialId, String userId, String url, String callbackUrl) {
        LOGGER.info("submitImage: materialId={}, userId={}, url={}, callbackUrl={}",
                materialId, userId, url, callbackUrl);
        return new ModerationResponse(generateReviewId(), "pending", "medium", null);
    }

    @Override
    public ModerationResponse submitMixed(String materialId, String userId, List<ModerationMaterial> materials, String callbackUrl) {
        LOGGER.info("submitMixed: materialId={}, userId={}, materialsCount={}, callbackUrl={}",
                materialId, userId, materials != null ? materials.size() : 0, callbackUrl);
        return new ModerationResponse(generateReviewId(), "pending", "medium", null);
    }

    @Override
    public ModerationResponse queryReviewStatus(String reviewId, String userId) {
        LOGGER.info("queryReviewStatus: reviewId={}, userId={}", reviewId, userId);
        // 测试环境默认返回pending状态
        return new ModerationResponse(reviewId, "pending", "medium", null);
    }

    private String generateReviewId() {
        return "stub-" + UUID.randomUUID();
    }
}

