# findu-common Invariants

## Interface Stability

- **MQProducer interface signature must not change.** All services depend on `MQProducer.sendMessage(String topic, String tag, String key, Object payload)`. Any signature change is a breaking change requiring coordinated updates across all consumers.

## ErrorCode Governance

- **ErrorCode additions must go through findu-common, not individual services.** The `ErrorCode` enum is the single source of truth for business error codes. Services must not define their own error code enums that duplicate or conflict with `ErrorCode`.

## Dependency Direction

- **Services must depend on interfaces (CacheService, MQProducer, ContentModerationClient), never on implementations directly.** Inject `CacheService`, not `RedisCacheService`. Inject `MQProducer`, not `SNSProducer`. Inject `ContentModerationClient`, not `MisContentModerationClient`. This allows profile-based switching (dev/test vs stable/prod) and future implementation changes without modifying service code.

## Version Coordination

- **Version bumps require both findu-user and findu-content to update.** When findu-common is released with a new version, all consuming services (findu-user, findu-content, and any future services) must update their dependency version in the same release cycle to prevent API drift.
