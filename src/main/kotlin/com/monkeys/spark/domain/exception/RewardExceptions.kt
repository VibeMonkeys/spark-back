package com.monkeys.spark.domain.exception

class RewardNotFoundException(rewardId: String) : EntityNotFoundException("Reward", rewardId, "REWARD_NOT_FOUND")

class InsufficientPointsException(
    required: Int,
    available: Int
) : DomainException(
    "Insufficient points. Required: $required, Available: $available",
    "INSUFFICIENT_POINTS"
)

class RewardNotActiveException(rewardId: String) : DomainException(
    "Reward is not active: $rewardId",
    "REWARD_NOT_ACTIVE"
)

class UserRewardNotFoundException(userRewardId: String) : EntityNotFoundException(
    "UserReward",
    userRewardId,
    "USER_REWARD_NOT_FOUND"
)

class UnauthorizedRewardAccessException(userId: String) : DomainException(
    "User $userId is not authorized to access this reward",
    "UNAUTHORIZED_REWARD_ACCESS"
)