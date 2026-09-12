package com.multiservicios.valtis.finance

import java.time.LocalDate

enum class PayrollFrequency {
    WEEKLY,
    BIWEEKLY,
    MONTHLY
}

data class PayrollDeposit(
    val id: Long,
    val amount: Double,
    val date: LocalDate
)

data class FinancialCommitment(
    val id: Long,
    val name: String,
    val amount: Double,
    val dueDate: LocalDate,
    val alreadySetAside: Double = 0.0,
    val previousShortfall: Double = 0.0,
    val active: Boolean = true
)

data class CommitmentCalculation(
    val commitmentId: Long,
    val name: String,
    val totalAmount: Double,
    val alreadySetAside: Double,
    val previousShortfall: Double,
    val remainingAmount: Double,
    val depositsAvailable: Int,
    val recommendedSetAside: Double,
    val projectedShortfall: Double,
    val dueDate: LocalDate
)

data class FinancialCalculation(
    val depositAmount: Double,
    val totalRecommendedSetAside: Double,
    val availableToSpend: Double,
    val totalProjectedShortfall: Double,
    val hasInsufficientFunds: Boolean,
    val commitments: List<CommitmentCalculation>
)
