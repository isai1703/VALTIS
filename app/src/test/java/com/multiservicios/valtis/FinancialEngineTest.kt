package com.multiservicios.valtis

import org.junit.Assert.assertEquals
import org.junit.Test

class FinancialEngineTest {

    @Test
    fun deposito4500_compromiso1500_dosDepositos() {

        val result = FinancialEngine.calculate(
            deposit = ValtisDeposit(4500.0),
            commitments = listOf(
                ValtisCommitment(
                    name = "Moto",
                    amount = 1500.0,
                    depositsUntilDue = 2
                )
            )
        )

        assertEquals(750.0, result.totalSetAside, 0.01)
        assertEquals(3750.0, result.available, 0.01)
        assertEquals(
            750.0,
            result.commitments.first().remainingAfterDeposit,
            0.01
        )
    }

    @Test
    fun depositoInsuficiente_arrastraFaltante() {

        val result = FinancialEngine.calculate(
            deposit = ValtisDeposit(500.0),
            commitments = listOf(
                ValtisCommitment(
                    name = "Moto",
                    amount = 1500.0,
                    depositsUntilDue = 1
                )
            )
        )

        assertEquals(500.0, result.totalSetAside, 0.01)
        assertEquals(0.0, result.available, 0.01)
        assertEquals(
            1000.0,
            result.commitments.first().remainingAfterDeposit,
            0.01
        )
    }

    @Test
    fun consideraLoYaApartado() {

        val result = FinancialEngine.calculate(
            deposit = ValtisDeposit(4500.0),
            commitments = listOf(
                ValtisCommitment(
                    name = "Moto",
                    amount = 1500.0,
                    depositsUntilDue = 2,
                    alreadySetAside = 750.0
                )
            )
        )

        assertEquals(375.0, result.totalSetAside, 0.01)
        assertEquals(4125.0, result.available, 0.01)
    }
}
