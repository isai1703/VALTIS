package com.multiservicios.valtis.finance

import java.time.LocalDate
import kotlin.math.ceil
import kotlin.math.max

object SetAsideEngine {

    fun calculate(
        deposit: PayrollDeposit,
        commitments: List<FinancialCommitment>,
        frequency: PayrollFrequency
    ): FinancialCalculation {

        val activeCommitments = commitments
            .filter { it.active }
            .filter { it.amount > 0.0 }
            .filter { it.dueDate >= deposit.date }

        val calculations = activeCommitments.map { commitment ->
            calculateCommitment(
                deposit = deposit,
                commitment = commitment,
                frequency = frequency
            )
        }

        val totalRecommendedSetAside = calculations.sumOf {
            it.recommendedSetAside
        }

        val totalProjectedShortfall = calculations.sumOf {
            it.projectedShortfall
        }

        val availableToSpend = max(
            0.0,
            deposit.amount - totalRecommendedSetAside
        )

        return FinancialCalculation(
            depositAmount = deposit.amount,
            totalRecommendedSetAside = totalRecommendedSetAside,
            availableToSpend = availableToSpend,
            totalProjectedShortfall = totalProjectedShortfall,
            hasInsufficientFunds = totalProjectedShortfall > 0.009,
            commitments = calculations
        )
    }

    private fun calculateCommitment(
        deposit: PayrollDeposit,
        commitment: FinancialCommitment,
        frequency: PayrollFrequency
    ): CommitmentCalculation {

        val remainingAmount = max(
            0.0,
            commitment.amount - commitment.alreadySetAside
        )

        if (remainingAmount <= 0.0) {
            return CommitmentCalculation(
                commitmentId = commitment.id,
                name = commitment.name,
                totalAmount = commitment.amount,
                alreadySetAside = commitment.alreadySetAside,
                previousShortfall = commitment.previousShortfall,
                remainingAmount = 0.0,
                depositsAvailable = 0,
                recommendedSetAside = 0.0,
                projectedShortfall = 0.0,
                dueDate = commitment.dueDate
            )
        }

        val depositsAvailable = countDepositsAvailable(
            fromDate = deposit.date,
            dueDate = commitment.dueDate,
            frequency = frequency
        )

        val deposits = max(1, depositsAvailable)

        val normalSetAside = ceil(
            (remainingAmount / deposits) * 100.0
        ) / 100.0

        val recommendedBeforeCap =
            normalSetAside + commitment.previousShortfall

        val recommendedSetAside = minOf(
            remainingAmount,
            recommendedBeforeCap
        )

        val projectedShortfall = max(
            0.0,
            remainingAmount - (recommendedSetAside * deposits)
        )

        return CommitmentCalculation(
            commitmentId = commitment.id,
            name = commitment.name,
            totalAmount = commitment.amount,
            alreadySetAside = commitment.alreadySetAside,
            previousShortfall = commitment.previousShortfall,
            remainingAmount = remainingAmount,
            depositsAvailable = deposits,
            recommendedSetAside = recommendedSetAside,
            projectedShortfall = projectedShortfall,
            dueDate = commitment.dueDate
        )
    }

    private fun countDepositsAvailable(
        fromDate: LocalDate,
        dueDate: LocalDate,
        frequency: PayrollFrequency
    ): Int {

        if (fromDate.isAfter(dueDate)) {
            return 0
        }

        var count = 1

        var nextDeposit = nextDepositDate(
            fromDate = fromDate,
            frequency = frequency
        )

        while (!nextDeposit.isAfter(dueDate)) {
            count++

            if (count > 520) {
                break
            }

            nextDeposit = nextDepositDate(
                fromDate = nextDeposit,
                frequency = frequency
            )
        }

        return count
    }

    private fun nextDepositDate(
        fromDate: LocalDate,
        frequency: PayrollFrequency
    ): LocalDate {

        return when (frequency) {
            PayrollFrequency.WEEKLY ->
                fromDate.plusWeeks(1)

            PayrollFrequency.BIWEEKLY ->
                fromDate.plusWeeks(2)

            PayrollFrequency.MONTHLY ->
                fromDate.plusMonths(1)
        }
    }
}
