# 좋아요 시스템 아키텍처 개선 제안

## 현재 문제점
1. **중복 필드**: likes_count와 like_count 혼재
2. **동시성 이슈**: 여러 사용자 동시 좋아요 시 카운트 불일치 가능
3. **확장성 제한**: 좋아요 관련 추가 기능 구현 어려움

## 제안하는 아키텍처

### Option 1: 완전 정규화 + 실시간 계산 (권장)

```kotlin
// StoryEntity에서 like_count 제거
@Entity
class StoryEntity {
    // like_count 필드 완전 제거
    // 좋아요 수는 StoryLikeEntity에서 실시간 계산
}

// 좋아요 수 조회 시
fun getLikeCount(storyId: String): Int {
    return storyLikeJpaRepository.countByStoryId(storyId).toInt()
}

// 좋아요 여부 및 수 한번에 조회
data class StoryLikeInfo(
    val likeCount: Int,
    val isLikedByUser: Boolean
)

fun getStoryLikeInfo(storyId: String, userId: String): StoryLikeInfo {
    val likeCount = storyLikeJpaRepository.countByStoryId(storyId)
    val isLiked = storyLikeJpaRepository.existsByStoryIdAndUserId(storyId, userId)
    return StoryLikeInfo(likeCount.toInt(), isLiked)
}
```

**장점**:
- 데이터 일관성 완벽 보장
- 동시성 문제 해결
- 확장성 우수 (좋아요 시간, 알림 등 추가 가능)

**단점**:
- 매번 COUNT 쿼리 (성능 약간 저하, 하지만 인덱스로 최적화 가능)

### Option 2: 이벤트 기반 캐시 시스템

```kotlin
// 좋아요 이벤트
data class StoryLikeEvent(
    val storyId: String,
    val userId: String,
    val action: LikeAction // LIKE, UNLIKE
)

@Service
class StoryLikeService {
    @Transactional
    fun likeStory(storyId: String, userId: String) {
        // 1. StoryLikeEntity 저장
        storyLikeRepository.save(StoryLikeEntity(storyId, userId))
        
        // 2. 이벤트 발행
        eventPublisher.publishEvent(StoryLikeEvent(storyId, userId, LIKE))
    }
    
    @EventListener
    @Async
    fun handleLikeEvent(event: StoryLikeEvent) {
        // 비동기로 캐시 업데이트
        updateLikeCountCache(event.storyId)
    }
}
```

**장점**:
- 읽기 성능 최적화
- 이벤트 기반으로 다른 기능 확장 가능 (알림, 통계 등)
- 동시성 문제 완화

**단점**:
- 복잡성 증가
- 최종 일관성 (약간의 지연 가능)

### Option 3: Redis 캐시 활용

```kotlin
@Service
class StoryLikeService {
    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>
    
    fun likeStory(storyId: String, userId: String) {
        // 1. DB에 좋아요 저장
        storyLikeRepository.save(StoryLikeEntity(storyId, userId))
        
        // 2. Redis 캐시 업데이트
        val key = "story:$storyId:likes"
        redisTemplate.opsForValue().increment(key)
        
        // 3. 사용자별 좋아요 상태 캐시
        val userLikeKey = "story:$storyId:user:$userId"
        redisTemplate.opsForValue().set(userLikeKey, "true", Duration.ofHours(24))
    }
    
    fun getLikeCount(storyId: String): Int {
        val key = "story:$storyId:likes"
        return redisTemplate.opsForValue().get(key)?.toInt() 
            ?: storyLikeRepository.countByStoryId(storyId).toInt()
    }
}
```

**장점**:
- 최고 성능
- 확장성 우수
- 실시간 업데이트

**단점**:
- Redis 인프라 필요
- 캐시 동기화 복잡성

## 즉시 적용 가능한 개선사항

### 1. 동시성 문제 해결
```kotlin
@Transactional
fun likeStory(storyId: StoryId, userId: UserId): Story? {
    return storyJpaRepository.findById(storyId.value).map { entity ->
        if (!isLikedByUser(storyId, userId)) {
            // 1. 좋아요 관계 먼저 저장
            val likeEntity = StoryLikeEntity().apply {
                this.storyId = storyId.value
                this.userId = userId.value
                this.createdAt = LocalDateTime.now()
            }
            storyLikeJpaRepository.save(likeEntity)
            
            // 2. 원자적 업데이트 (DB 락 사용)
            storyJpaRepository.incrementLikeCount(storyId.value)
            
            // 3. 업데이트된 엔티티 다시 조회
            storyJpaRepository.findById(storyId.value).get()
        } else entity
    }.map { storyMapper.toDomain(it) }.orElse(null)
}
```

### 2. StoryJpaRepository에 원자적 업데이트 추가
```kotlin
interface StoryJpaRepository : JpaRepository<StoryEntity, String> {
    @Modifying
    @Query("UPDATE StoryEntity s SET s.likeCount = s.likeCount + 1 WHERE s.id = :storyId")
    fun incrementLikeCount(@Param("storyId") storyId: String)
    
    @Modifying
    @Query("UPDATE StoryEntity s SET s.likeCount = GREATEST(0, s.likeCount - 1) WHERE s.id = :storyId")
    fun decrementLikeCount(@Param("storyId") storyId: String)
}
```

## 권장사항

**단기**: Option 1 (완전 정규화) 적용
- 현재 코드에서 like_count 캐시 제거
- 실시간 계산으로 변경
- 성능은 약간 저하되지만 데이터 일관성 보장

**장기**: Option 2 (이벤트 기반) 도입
- 확장성과 성능을 모두 만족
- 다른 기능들(알림, 통계)과 연동 가능
- MSA 환경에 적합

**대규모**: Option 3 (Redis 캐시) 추가
- 높은 트래픽 환경에서 필요
- 실시간 성능 중요한 경우