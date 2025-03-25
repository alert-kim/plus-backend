package io.hhplus.tdd.database

import io.hhplus.tdd.point.TransactionType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIterable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class PointHistoryTableTest {

    @Nested
    @DisplayName("포인트 내역 조회")
    inner class SelectAllByUserIdTest {
        @Test
        fun `해당 유저의 내역이 있는 경우 모든 내역을 조회해 반환한다`() {
            val table = PointHistoryTable()
            val userId = 1L
            val historyData = listOf(
                Triple(1L, TransactionType.USE, System.currentTimeMillis()),
                Triple(2L, TransactionType.CHARGE, System.currentTimeMillis()),
                Triple(3L, TransactionType.USE, System.currentTimeMillis()),
            )
            historyData.forEach { (amount, type, time) ->
                table.insert(userId, amount, type, time)
            }
            // other user
            table.insert(2L, 100, TransactionType.CHARGE, System.currentTimeMillis())

            val result = table.selectAllByUserId(userId)

            assertThat(result).allSatisfy {
                val data = Triple(it.amount, it.type, it.timeMillis)
                assertThat(historyData).contains(data)
            }
        }

        @Test
        fun `해당 유저의 내역이 있는 경우 내역은 저장된 순서대로 반환된다`() {
            val table = PointHistoryTable()
            val userId = 1L
            val historyIdsInOrder =
            List(3) {
                val historyId = table.insert(userId, 100, TransactionType.CHARGE, System.currentTimeMillis()).id
                historyId
            }

            val result = table.selectAllByUserId(userId)

            assertThatIterable(result).extracting("id").isEqualTo(historyIdsInOrder)
        }

        @Test
        fun `전달 받은 id의 유저의 내역이 저장되어있지 않은 경우, 빈 리스트를 반환한다`() {
            val table = PointHistoryTable()

            val result = table.selectAllByUserId(1L)

            assertThat(result).isEmpty()
        }
    }

    @Nested
    @DisplayName("포인트 내역 생성")
    inner class InsertTest {
        @Test
        fun `포인트 내역을 생성한다`() {
            val table = PointHistoryTable()
            val userId = 3L
            val amount = 1000L
            val transactionType = TransactionType.CHARGE
            val updateMillis = System.currentTimeMillis()

            val result = table.insert(
                userId = userId,
                amount = amount,
                transactionType = transactionType,
                updateMillis = updateMillis
            )

            assertAll(
                { assertThat(result.userId).isEqualTo(userId) },
                { assertThat(result.amount).isEqualTo(amount) },
                { assertThat(result.type).isEqualTo(transactionType) },
                { assertThat(result.timeMillis).isEqualTo(updateMillis) }
            )
            val allSaved = table.selectAllByUserId(userId)
            assertThat(allSaved.size).isEqualTo(1)
            val saved = allSaved.first()
            assertAll(
                { assertThat(saved.userId).isEqualTo(userId) },
                { assertThat(saved.amount).isEqualTo(amount) },
                { assertThat(saved.type).isEqualTo(transactionType) },
                { assertThat(saved.timeMillis).isEqualTo(updateMillis) }
            )
        }
    }
}
