package com.multiservicios.valtis

data class ValtisDeposit(
    val amount: Double
)

data class ValtisCommitment(
    val id: Long,
    val name: String,
    val amount: Double,
    val depositsUntilDue: Int,
    val alreadySetAside: Double = 0.0
)

data class ValtisCommitmentResult(
    val id: Long,
    val name: String,
    val pendingAmount: Double,
    val recommendedSetAside: Double,
    val remainingAmount: Double
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

        var available = deposit.amount
        val results = mutableListOf<ValtisCommitmentResult>()

        commitments
            .filter { it.amount > it.alreadySetAside }
            .sortedBy { it.depositsUntilDue }
            .forEach { commitment ->

                val pending =
                    (commitment.amount - commitment.alreadySetAside)
                        .coerceAtLeast(0.0)

                val deposits =
                    commitment.depositsUntilDue
                        .coerceAtLeast(1)

                val recommended =
                    pending / deposits

                val actualSetAside =
                    minOf(available, recommended)

                available -= actualSetAside

                results += ValtisCommitmentResult(
                    id = commitment.id,
                    name = commitment.name,
                    pendingAmount = pending,
                    recommendedSetAside = actualSetAside,
                    remainingAmount =
                        pending - actualSetAside
                )
            }

        return ValtisFinancialResult(
            deposit = deposit.amount,
            totalSetAside = deposit.amount - available,
            available = available,
            commitments = results
        )
    }
}
