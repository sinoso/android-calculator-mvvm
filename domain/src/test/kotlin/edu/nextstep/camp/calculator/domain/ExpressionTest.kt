package edu.nextstep.camp.calculator.domain

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ExpressionTest {
    @Test
    fun `빈 수식일 때, 피연산자를 추가할 수 있어야한다`() {
        // given
        val expression = Expression.EMPTY

        // when
        val actual = expression + 1

        // then
        assertThat(actual.toString()).isEqualTo("1")
    }

    @Test
    fun `'8' 수식이 있을 때, 9를 입력하면 89로 바뀌어야 한다`() {
        // given
        val expression = Expression(listOf(8))

        // when
        val actual = expression + 9

        // then
        assertThat(actual.toString()).isEqualTo("89")
    }

    @Test
    fun `빈 수식일 때, + 연산자를 추가할 수 없어야 한다`() {
        // given
        val expression = Expression.EMPTY

        // when
        val actual = expression + Operator.Plus

        // then
        assertThat(actual.toString()).isEqualTo("")
    }

    @Test
    fun `'1' 수식이 있을 때, + 연산자를 추가할 수 있어야 한다`() {
        // given
        val expression = Expression(listOf(1))

        // when
        val actual = expression + Operator.Plus

        // then
        assertThat(actual.toString()).isEqualTo("1 +")
    }

    @Test
    fun `'8 +' 수식이 있을 때, + 연산자를 - 연산자로 변경할 수 있어야 한다`() {
        // given
        val expression = Expression(listOf(8, Operator.Plus))

        // when
        val actual = expression + Operator.Minus

        // then
        assertThat(actual.toString()).isEqualTo("8 -")
    }

    @Test
    fun `'32 + 1' 수식이 있을 때, 마지막 1을 제거할 수 있어야 한다`() {
        // given
        val expression = Expression(listOf(32, Operator.Plus, 1))

        // when
        val actual = expression.removeLast()

        // then
        assertThat(actual.toString()).isEqualTo("32 +")
    }

    @Test
    fun `'32 +' 수식이 있을 때, 마지막 +를 제거할 수 있어야 한다`() {
        // given
        val expression = Expression(listOf(32, Operator.Plus))

        // when
        val actual = expression.removeLast()

        // then
        assertThat(actual.toString()).isEqualTo("32")
    }

    @Test
    fun `'32' 수식이 있을 때, 마지막 2를 제거할 수 있어야 한다`() {
        // given
        val expression = Expression(listOf(32))

        // when
        val actual = expression.removeLast()

        // then
        assertThat(actual.toString()).isEqualTo("3")
    }

    @Test
    fun `'3' 수식이 있을 때, 마지막 3을 제거할 수 있어야 한다`() {
        // given
        val expression = Expression(listOf(3))

        // when
        val actual = expression.removeLast()

        // then
        assertThat(actual.toString()).isEqualTo("")
    }

    @Test
    fun `빈 수식일 때, 마지막을 제거해도 빈 수식이어야 한다`() {
        // given
        val expression = Expression.EMPTY

        // when
        val actual = expression.removeLast()

        // then
        assertThat(actual.toString()).isEqualTo("")
    }

    @Test
    fun `빈 수식이 완성된 수식인지 확인시 false 가 반환된다`() {
        // given 빈 수식이 주어졌을때
        val expression = Expression.EMPTY

        // when 완성된 수식인지 확인시
        val actual = expression.isCompletedExpression()

        // then false를 반환 한다.
        assertThat(actual).isFalse()
    }

    @Test
    fun `수식 '14'이 완성된 수식인지 확인시 false 가 반환된다`() {
        // given 14 수식이 주어졌을때
        val expression = Expression(listOf(14))

        // when 완성된 수식인지 확인시
        val actual = expression.isCompletedExpression()

        // then false를 반환 한다.
        assertThat(actual).isFalse()
    }

    @Test
    fun `수식 '14 + '이 완성된 수식인지  확인시 false 가 반환된다`() {
        // given 14 + 수식이 주어졌을때
        val expression = Expression(listOf(14, Operator.Plus))

        // when 완성된 수식인지 확인시
        val actual = expression.isCompletedExpression()

        // then false를 반환 한다.
        assertThat(actual).isFalse()
    }

    @Test
    fun `수식 '14 + 18'이 완성된 수식인지  확인시 true 가 반환된다`() {
        // given 14 + 18 수식이 주어졌을때
        val expression = Expression(listOf(14, Operator.Plus, 18))

        // when 완성된 수식인지 확인시
        val actual = expression.isCompletedExpression()

        // then true 를 반환 한다.
        assertThat(actual).isTrue()
    }

    @Test
    fun `수식의 형태(1 + 2)를 가진 string으로 수식을 생성 할수 있다`() {
        // when "1 + 2" 수식의 형태의 문자열이 주어졌을때
        val inputString = "1 + 2"

        // then 수식을 만들 수 있다.
        val expression = Expression.from(inputString)
        assertThat(expression).isEqualTo(Expression(listOf(1, Operator.Plus, 2)))
    }

    @Test
    fun `연산자, 피연산자가 아닌 문자가 포함된 string(a1 + 2)으로 부터 수식을 생성하려하면 IllegalArgumentException 예외가 발생한다`() {
        // when "a1+2" 연산자, 피연산자가 아닌 문자가 포함된 문자열로
        val inputString = "a1 + 2"

        // then 수식을 만들려 하면 IllegalArgumentException이 발생한다
        assertThrows<IllegalArgumentException> { Expression.from(inputString) }
    }

}
