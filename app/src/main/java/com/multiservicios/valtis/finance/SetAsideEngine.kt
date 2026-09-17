package com.multiservicios.valtis.finance

import java.time.LocalDate
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

enum class ObligationType {
    COMPROMISO,
    DEUDA
}

data class SetAsideObligation(
    val id: Long,
    val name: String,
    val amount: Double,
    val dueDate: LocalDate,
    val alreadySetAside: Double = 0.0,
    val previousShortfall: Double = 0.0,
    val active: Boolean = true,
    val type: ObligationType = ObligationType.COMPROMISO
)

data class SetAsideResult(
    val obligationId: Long,
    val name: String,
    val type: ObligationType,
    val totalAmount: Double,
    val alreadySetAside: Double,
    val previousShortfall: Double,
    val remainingAmount: Double,
    val depositsAvailable: Int,
    val recommendedSetAside: Double,
    val projectedShortfall: Double,
    val dueDate: LocalDate
)

data class UnifiedFinancialCalculation(
    val depositAmount: Double,
    val totalRecommendedSetAside: Double,
    val availableToSpend: Double,
    val totalProjectedShortfall: Double,
    val hasInsufficientFunds: Boolean,
    val obligations: List<SetAsideResult>
)

object SetAsideEngine {

    fun calculate(
        deposit: PayrollDeposit,
        obligations: List<SetAsideObligation>,
        frequency: PayrollFrequency
    ): UnifiedFinancialCalculation {

        /*
         * Las obligaciones se mantienen en el cálculo aunque
         * estén vencidas, siempre que todavía tengan dinero pendiente.
         *
         * Prioridad:
         * 1. Vencidas
         * 2. Vencen antes
         * 3. Tipo
         * 4. ID
         */
        val activeObligations =
            obligations
                .filter { it.active }
                .filter { it.amount > 0.0 }
                .filter {
                    it.amount > it.alreadySetAside
                }
                .sortedWith(
                    compareBy<SetAsideObligation> {
                        it.dueDate.isBefore(deposit.date)
                    }.reversed()
                        .thenBy { it.dueDate }
                        .thenBy { it.type }
                        .thenBy { it.id }
                )

        var available = deposit.amount
        val results = mutableListOf<SetAsideResult>()

        for (obligation in activeObligations) {

            val remainingAmount =
                max(
                    0.0,
                    obligation.amount -
                        obligation.alreadySetAside
                )

            if (remainingAmount <= 0.0) {
                continue
            }

            val vencida =
                obligation.dueDate.isBefore(
                    deposit.date
                )

            val depositsAvailable =
                if (vencida) {
                    /*
                     * Si ya venció, no repartimos el faltante
                     * entre futuras nóminas.
                     *
                     * El objetivo inmediato es cubrir lo
                     * pendiente cuanto antes.
                     */
                    1
                } else {
                    countDepositsAvailable(
                        fromDate = deposit.date,
                        dueDate = obligation.dueDate,
                        frequency = frequency
                    )
                }

            val deposits =
                max(
                    1,
                    depositsAvailable
                )

            val idealSetAside =
                if (vencida) {
                    /*
                     * Obligación vencida:
                     * intentar cubrir todo el pendiente.
                     */
                    remainingAmount
                } else {
                    ceil(
                        (
                            remainingAmount /
                                deposits
                        ) * 100.0
                    ) / 100.0
                }

            val actualSetAside =
                min(
                    available,
                    min(
                        remainingAmount,
                        idealSetAside
                    )
                )

            available -= actualSetAside

            val projectedShortfall =
                max(
                    0.0,
                    idealSetAside -
                        actualSetAside
                )

            results += SetAsideResult(
                obligationId =
                    obligation.id,
                name =
                    obligation.name,
                type =
                    obligation.type,
                totalAmount =
                    obligation.amount,
                alreadySetAside =
                    obligation.alreadySetAside,
                previousShortfall =
                    0.0,
                remainingAmount =
                    remainingAmount,
                depositsAvailable =
                    deposits,
                recommendedSetAside =
                    actualSetAside,
                projectedShortfall =
                    projectedShortfall,
                dueDate =
                    obligation.dueDate
            )
        }

        val totalRecommendedSetAside =
            results.sumOf {
                it.recommendedSetAside
            }

        val totalProjectedShortfall =
            results.sumOf {
                it.projectedShortfall
            }

        return UnifiedFinancialCalculation(
            depositAmount =
                deposit.amount,
            totalRecommendedSetAside =
                totalRecommendedSetAside,
            availableToSpend =
                max(
                    0.0,
                    deposit.amount -
                        totalRecommendedSetAside
                ),
            totalProjectedShortfall =
                totalProjectedShortfall,
            hasInsufficientFunds =
                totalProjectedShortfall > 0.009,
            obligations =
                results
        )
    }

    /*
     * Compatibilidad con la API anterior.
     */
    fun calculate(
        deposit: PayrollDeposit,
        commitments: List<FinancialCommitment>,
        frequency: PayrollFrequency
    ): FinancialCalculation {

        val obligations =
            commitments.map {
                SetAsideObligation(
                    id =
                        it.id,
                    name =
                        it.name,
                    amount =
                        it.amount,
                    dueDate =
                        it.dueDate,
                    alreadySetAside =
                        it.alreadySetAside,
                    previousShortfall =
                        0.0,
                    active =
                        it.active,
                    type =
                        ObligationType.COMPROMISO
                )
            }

        val result =
            calculate(
                deposit =
                    deposit,
                obligations =
                    obligations,
                frequency =
                    frequency
            )

        return FinancialCalculation(
            depositAmount =
                result.depositAmount,
            totalRecommendedSetAside =
                result.totalRecommendedSetAside,
            availableToSpend =
                result.availableToSpend,
            totalProjectedShortfall =
                result.totalProjectedShortfall,
            hasInsufficientFunds =
                result.hasInsufficientFunds,
            commitments =
                result.obligations.map {
                    CommitmentCalculation(
                        commitmentId =
                            it.obligationId,
                        name =
                            it.name,
                        totalAmount =
                            it.totalAmount,
                        alreadySetAside =
                            it.alreadySetAside,
                        previousShortfall =
                            0.0,
                        remainingAmount =
                            it.remainingAmount,
                        depositsAvailable =
                            it.depositsAvailable,
                        recommendedSetAside =
                            it.recommendedSetAside,
                        projectedShortfall =
                            it.projectedShortfall,
                        dueDate =
                            it.dueDate
                    )
                }
        )
    }

    private fun countDepositsAvailable(
        fromDate: LocalDate,
        dueDate: LocalDate,
        frequency: PayrollFrequency
    ): Int {

        if (fromDate.isAfter(dueDate)) {
            return 1
        }

        var count = 1

        var nextDeposit =
            nextDepositDate(
                fromDate =
                    fromDate,
                frequency =
                    frequency
            )

        while (!nextDeposit.isAfter(dueDate)) {

            count++

            if (count > 520) {
                break
            }

            nextDeposit =
                nextDepositDate(
                    fromDate =
                        nextDeposit,
                    frequency =
                        frequency
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
