package io.hhplus.tdd.database

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class UserPointTableTest {

    @Nested
    @DisplayName("유저의 모든 내역 조회")
    inner class SelectUserPointTest {

        @Test
        fun `전달 받은 id로 유저의 포인트를 조회해 반환한다`() {
            val table = UserPointTable()
            val id = (1L..10L).random()
            val point = (1L..10L).random()
            table.insertOrUpdate(id, point)

            val result = table.selectById(id)

            assertAll(
                { assert(result.id == id) },
                { assert(result.point == point) }
            )
        }

        @Test
        fun `전달 받은 id를 가진 유저의 포인트가 저장되어있지 않은 경우, 포인트를 0으로 반환한다`() {
            val id = 1L
            val table = UserPointTable()

            val result = table.selectById(id)

            assertAll(
                { assertThat(result.id).isEqualTo(id) },
                { assertThat(result.point).isEqualTo(0) }
            )
        }
    }

    @Nested
    @DisplayName("유저 포인트 생성 혹은 업데이트")
    inner class InsertOrUpdatePointTest {
        @Test
        fun `전달 받은 id의 유저 포인트가 저장되어 있는 경우, 해당 정보로 변경된다`() {
            val table = UserPointTable()
            val id = (1L..10L).random()
            val point = (1L..10L).random()
            table.insertOrUpdate(id, point)

            val updatedPoint = point + 10
            table.insertOrUpdate(id, updatedPoint)

            val result = table.selectById(id)
            assertAll(
                { assertThat(result.id).isEqualTo(id) },
                { assertThat(result.point).isEqualTo(updatedPoint) }
            )
        }

        @Test
        fun `전달 받은 id의 유저 포인트가 저장되어 있지 않은 경우, 해당 정보로 포인트를 생성한다`() {
            val table = UserPointTable()
            val id = (1L..10L).random()
            val point = (1L..10L).random()

            table.insertOrUpdate(id, point)

            val result = table.selectById(id)
            assertAll(
                { assertThat(result.id).isEqualTo(id) },
                { assertThat(result.point).isEqualTo(point) }
            )
        }
    }
}
