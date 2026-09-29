package com.example

import com.example.data.model.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `test meal rate calculation logic`() {
        val totalFoodBazar = 15000.0
        val totalMeals = 300.0
        val mealRate = totalFoodBazar / totalMeals
        assertEquals(50.0, mealRate, 0.001)

        val memberMeals = 58.0
        val memberMealCost = memberMeals * mealRate
        assertEquals(2900.0, memberMealCost, 0.001)
    }

    @Test
    fun `test member statement due and advance balance calculation`() {
        val testMember = Member(
            id = 2,
            name = "Rahim",
            phone = "01811223344",
            roomNumber = "101",
            bedNumber = "Bed-1"
        )

        // Scenario 1: Cost exceeds Deposit -> DUE
        val statementWithDue = MemberStatement(
            member = testMember,
            mealCount = 50.0,
            mealCost = 2500.0,
            rentShare = 2500.0,
            utilityShare = 500.0,
            otherShare = 200.0,
            totalCost = 5700.0,
            totalPaid = 5000.0,
            netBalance = 5000.0 - 5700.0 // -700
        )

        assertTrue(statementWithDue.isDue)
        assertEquals(700.0, statementWithDue.dueAmount, 0.001)
        assertEquals(0.0, statementWithDue.advanceAmount, 0.001)

        // Scenario 2: Deposit exceeds Cost -> ADVANCE
        val statementWithAdvance = MemberStatement(
            member = testMember,
            mealCount = 40.0,
            mealCost = 2000.0,
            rentShare = 2500.0,
            utilityShare = 500.0,
            otherShare = 200.0,
            totalCost = 5200.0,
            totalPaid = 6000.0,
            netBalance = 6000.0 - 5200.0 // +800
        )

        assertFalse(statementWithAdvance.isDue)
        assertEquals(0.0, statementWithAdvance.dueAmount, 0.001)
        assertEquals(800.0, statementWithAdvance.advanceAmount, 0.001)
    }

    @Test
    fun `test mess total physical cash balance formula`() {
        val totalDeposits = 55000.0
        val totalBazar = 18500.0
        val houseRent = 25000.0
        val totalUtilities = 4500.0
        val otherExpenses = 700.0

        val totalExpenses = totalBazar + houseRent + totalUtilities + otherExpenses
        val currentMessBalance = totalDeposits - totalExpenses

        assertEquals(48700.0, totalExpenses, 0.001)
        assertEquals(6300.0, currentMessBalance, 0.001)
    }
}
