package edu.nextstep.camp.calculator

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import edu.nextstep.camp.calculator.CalculatorEvent.*
import edu.nextstep.camp.calculator.domain.*
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CalculatorViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    lateinit var viewModel: CalculatorViewModel
    val mainThreadSurrogate = newSingleThreadContext(CalculatorViewModel::class.java.simpleName)

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @Test
    fun `빈 수식에 피연산자 1을 추가 하면 수식에 해당 피연산자 1이 추가된다`() {
        // given 수식이 빈상태에서
        viewModel =
            CalculatorViewModel(
                lastExpression = Expression.EMPTY,
                calculationResultRepository = mockk()
            )

        // when 피연산자 1이 추가 되면
        viewModel.addOperandToExpression(1)

        // then 수식에 해당 피연산자가 추가된다.
        val expected = Expression(listOf(1))
        val actual = viewModel.expression.getOrAwaitValue()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `피연산자 1이 마지막으로 입력된 수식에 피연산자 2을 추가 하면 수식의 마지막 피연산자는 12로 변경된다`() {
        // given 수식에 1이 입력된 상태에서
        val inputtedExpression = Expression(listOf(1))
        viewModel =
            CalculatorViewModel(
                lastExpression = inputtedExpression,
                calculationResultRepository = mockk()
            )

        // when 피연산자 2가 추가 되면
        viewModel.addOperandToExpression(2)

        // then 수식의 마지막 피연산자는 12가 변경된다.
        val expected = Expression(listOf(12))
        val actual = viewModel.expression.getOrAwaitValue()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `피연산자 1이 마지막으로 입력된 수식에 연산자 +를 추가 하면 수식은 1 + 로 표현된다`() {
        // given 수식에 피연산자 1이 추가된 상태
        val inputtedExpression = Expression(listOf(1))
        viewModel =
            CalculatorViewModel(
                lastExpression = inputtedExpression,
                calculationResultRepository = mockk()
            )

        // when 연산자 + 가 추가되면
        viewModel.addOperatorToExpression(Operator.Plus)

        // then 수식은 1 + 로 표현된다.
        val expected = Expression(listOf(1, Operator.Plus))
        val actual = viewModel.expression.getOrAwaitValue()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `수식의 마지막 연산자 +가 일때 수식에 연산자 - 를 추가 하면 수식의 마지막 연산자가 - 로 변경된다`() {
        // given 수식이 1 + 일 때
        val inputtedExpression = Expression(listOf(1, Operator.Plus))
        viewModel =
            CalculatorViewModel(
                lastExpression = inputtedExpression,
                calculationResultRepository = mockk()
            )

        // when 연산자 - 가 추가되면
        viewModel.addOperatorToExpression(Operator.Minus)

        // then 수식은 1 - 로 표현된다.
        val expected = Expression(listOf(1, Operator.Minus))
        val actual = viewModel.expression.getOrAwaitValue()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `빈 수식에 연산자를 추가 하면 빈 수식을 유지 한다`() {
        // given 빈 수식인 경우
        val inputtedExpression = Expression.EMPTY
        viewModel =
            CalculatorViewModel(
                lastExpression = inputtedExpression,
                calculationResultRepository = mockk()
            )

        // when 연산자 - 가 추가되면
        viewModel.addOperatorToExpression(Operator.Minus)

        // then 수식은 빈 수식을 유지 한다.
        val expected = Expression.EMPTY
        val actual = viewModel.expression.getOrAwaitValue()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `12 + 34 인 수식에 삭제 요청시 수식은 마지막에 위치한 피연산자 4가 삭제 된 12 + 3의 형태를 가진다`() {
        // given 수식이 12 + 34 인 경우
        val inputtedExpression = Expression(listOf(12, Operator.Plus, 34))
        viewModel =
            CalculatorViewModel(
                lastExpression = inputtedExpression,
                calculationResultRepository = mockk()
            )

        // when 삭제를 요청 할 경우
        viewModel.removeLastFromExpression()

        // then 수식은 12 + 3의 형태를 가진다
        val expected = Expression(listOf(12, Operator.Plus, 3))
        val actual = viewModel.expression.getOrAwaitValue()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `12 +인 수식에 삭제 요청시 수식은 마지막에 위치한 연산자 + 가 삭제 된 12의 형태를 가진다`() {
        // given 수식이 12 + 인 경우
        val inputtedExpression = Expression(listOf(12, Operator.Plus))
        viewModel =
            CalculatorViewModel(
                lastExpression = inputtedExpression,
                calculationResultRepository = mockk()
            )

        // when 삭제를 요청 할 경우
        viewModel.removeLastFromExpression()

        // then 수식은 12의 형태를 가진다
        val expected = Expression(listOf(12))
        val actual = viewModel.expression.getOrAwaitValue()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `빈 수식일 때 삭제 요청시 빈 수식을 유지 한다`() {
        // given 빈 수식인 경우
        val inputtedExpression = Expression.EMPTY
        viewModel =
            CalculatorViewModel(
                lastExpression = inputtedExpression,
                calculationResultRepository = mockk()
            )

        // when 삭제를 요청 할 경우
        viewModel.removeLastFromExpression()

        // then 수식은 빈 수식을 유지 한다.
        val expected = Expression.EMPTY
        val actual = viewModel.expression.getOrAwaitValue()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `완전한 수식일 때 계산 요청시 계산 결과를 전달한다`() {
        // given 완전한 수식인 경우
        val inputtedExpression = Expression(listOf(12, Operator.Plus, 34))
        viewModel =
            CalculatorViewModel(
                lastExpression = inputtedExpression,
                calculationResultRepository = mockk()
            )
        // when 계산요청시
        viewModel.requestCalculate()

        // then 수식의 결과 값을 전달 한다.
        val expected = Expression(listOf(46))
        val actual = viewModel.expression.getOrAwaitValue()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `연산자로 끝나는 불완전한 수식일 때 계산 요청시 '완성되지 않은 수식'에러 이벤트를 발생시킨다`() {
        // given 연산자로 끝나는 불완전한 수식인 경우
        val inputtedExpression = Expression(listOf(12, Operator.Plus))
        viewModel =
            CalculatorViewModel(
                lastExpression = inputtedExpression,
                calculationResultRepository = mockk()
            )

        // when 계산요청시
        viewModel.requestCalculate()

        // then '완성되지 않은 수식' 이벤트를 발생시킨다.
        val expected = ERROR_INCOMPLETE_EXPRESSION
        val actual = viewModel.event.getOrAwaitValue().consume()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `피연산자 하나만 있는 불완전한 수식일 때 계산 요청시 '완성되지 않은 수식'에러 이벤트를 발생시킨다`() {
        // given 연산자로 끝나는 불완전한 수식인 경우
        val inputtedExpression = Expression(listOf(12))
        viewModel =
            CalculatorViewModel(
                lastExpression = inputtedExpression,
                calculationResultRepository = mockk()
            )

        // when 계산요청시
        viewModel.requestCalculate()

        // then '완성되지 않은 수식' 이벤트를 발생시킨다.
        val expected = ERROR_INCOMPLETE_EXPRESSION
        val actual = viewModel.event.getOrAwaitValue().consume()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `계산 결과 목록의 visible 상태 값이 true일 때 상태 값 변경 요청을 하면 false를 전달 한다`() {
        // given 계산 결과 목록의 visible 상태 값이 true일 때
        viewModel = CalculatorViewModel(
            lastCalculationHistoryVisibility = true,
            calculationResultRepository = mockk()
        )

        // when 상태 값 변경 요청을 하면
        viewModel.toggleCalculationHistoryVisibility()

        // then 상태 값은 false 가 전달 된다
        val actual = viewModel.isCalculationHistoryVisible.getOrAwaitValue()
        assertThat(actual).isFalse()
    }

    @Test
    fun `계산 결과 목록의 visible 상태 값이 false일 때 상태 값 변경 요청을 하면 true를 전달 한다`() {
        // given 계산 결과 목록의 visible 상태 값이 false일 때
        viewModel = CalculatorViewModel(
            lastCalculationHistoryVisibility = false,
            calculationResultRepository = mockk()
        )

        // when 상태 값 변경 요청을 하면
        viewModel.toggleCalculationHistoryVisibility()

        // then 상태 값은 true 가 전달 된다
        val actual = viewModel.isCalculationHistoryVisible.getOrAwaitValue()
        assertThat(actual).isTrue()
    }

    @Test
    fun `결과 목록의 Visibility 상태 값이 false에서 true로 변경 될때 계산결과 를 전달 한다`() {
        // given 이미 저장된 결과가 있고 Visibility 가 false 일때
        val expectedList =
            mutableListOf(
                CalculationResult(Expression(listOf("1", Operator.Plus, "1")), 2),
                CalculationResult(Expression(listOf("3", Operator.Plus, "2")), 5)
            )
        viewModel =
            CalculatorViewModel(
                calculationResultStorage = CalculationResultStorage(expectedList),
                lastCalculationHistoryVisibility = false,
                calculationResultRepository = mockk()
            )

        // when 계산 결과 Visibility 변경을 요청하면
        viewModel.toggleCalculationHistoryVisibility()

        // then 저장된 계산 결과가 전달 된다
        val actual = viewModel.calculationResults.getOrAwaitValue()
        assertThat(actual).isEqualTo(expectedList)
    }
}