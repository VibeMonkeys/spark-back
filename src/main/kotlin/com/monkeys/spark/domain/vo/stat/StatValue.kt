package com.monkeys.spark.domain.vo.stat

/**
 * 스탯 값 Value Object
 * 스탯의 현재 값과 관련 메서드를 제공
 */
data class StatValue(
    val current: Int,
    val allocated: Int = 0 // 사용자가 직접 할당한 포인트
) {
    companion object {
        const val MIN_STAT = 0
        const val MAX_STAT = 999
        const val INITIAL_STAT = 10 // 초기 기본 스탯
    }

    init {
        require(current >= MIN_STAT) { "스탯 값은 $MIN_STAT 이상이어야 합니다" }
        require(current <= MAX_STAT) { "스탯 값은 $MAX_STAT 이하여야 합니다" }
        require(allocated >= 0) { "할당된 포인트는 0 이상이어야 합니다" }
    }

    /**
     * 기본 스탯 (미션을 통해 자동으로 얻은 스탯)
     */
    val base: Int get() = current - allocated


    /**
     * 스탯 등급 반환
     */
    val grade: StatGrade get() = StatGrade.fromValue(current)

    /**
     * 포인트 할당
     */
    fun allocatePoints(points: Int): StatValue {
        require(points > 0) { "할당할 포인트는 0보다 커야 합니다" }
        
        val newAllocated = allocated + points
        val newCurrent = current + points
        
        require(newCurrent <= MAX_STAT) { "최대 스탯 값을 초과할 수 없습니다" }
        
        return copy(
            current = newCurrent,
            allocated = newAllocated
        )
    }

    /**
     * 미션 완료로 스탯 증가
     */
    fun increaseByStat(points: Int): StatValue {
        require(points > 0) { "증가할 포인트는 0보다 커야 합니다" }
        
        val newCurrent = current + points
        require(newCurrent <= MAX_STAT) { "최대 스탯 값을 초과할 수 없습니다" }
        
        return copy(current = newCurrent)
    }

}

/**
 * 스탯 등급 열거형
 */
enum class StatGrade(
    val displayName: String,
    val minValue: Int,
    val maxValue: Int,
    val color: String
) {
    NOVICE("초보", 0, 29, "#9CA3AF"),
    APPRENTICE("견습", 30, 59, "#10B981"),
    SKILLED("숙련", 60, 99, "#3B82F6"),
    EXPERT("전문가", 100, 199, "#8B5CF6"),
    MASTER("대가", 200, 399, "#F59E0B"),
    GRANDMASTER("거장", 400, 699, "#EF4444"),
    LEGEND("전설", 700, 999, "#DC2626");

    companion object {
        fun fromValue(value: Int): StatGrade = values().first { value in it.minValue..it.maxValue }
    }
}