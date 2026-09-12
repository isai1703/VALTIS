package com.multiservicios.valtis

data class ValtisDeposit(
    val amount: Double
)

data class ValtisCommitment(
    val name: String,
    val amount: Double,
    val depositsUntilDue: Int,
    val alreadySetAside: Double = 0.0
)

data class ValtisCommitmentResult(
    val name: String,
    val requiredAmount: Double,
    val recommendedSetAside: Double,
    val remainingAfterDeposit: Double
)

data class ValtisFinancialResult(
    val deposit: Double,
    val totalSetAside: Double,
    val available: Double,
    val commitments: List<ValtisCommitmentResult>
)

object FinancialEngine {

    fun calculate(
        deposit: ValtisDeposit,
        commitments: List<ValtisCommitment>
    ): ValtisFinancialResult {

        var availableMoney = deposit.amount
        val results = mutableListOf<ValtisCommitmentResult>()

        commitments
            .filter { it.amount > it.alreadySetAside }
            .sortedBy { it.depositsUntilDue }
            .forEach { commitment ->

                val pendingAmount =
                    (commitment.amount - commitment.alreadySetAside)
                        .coerceAtLeast(0.0)

                val deposits =
                    commitment.depositsUntilDue.coerceAtLeast(1)

                val recommended =
                    pendingAmount / deposits

                val actualSetAside =
                    minOf(availableMoney, recommended)

                availableMoney -= actualSetAside

                results += ValtisCommitmentResult(
                    name = commitment.name,
                    requiredAmount = pendingAmount,
                    recommendedSetAside = actualSetAside,
                    remainingAfterDeposit =
                        pendingAmount - actualSetAside
                )
            }

        return ValtisFinancialResult(
            deposit = deposit.amount,
            totalSetAside = deposit.amount - availableMoney,
            available = availableMoney,
            commitments = results
        )
    }
}
