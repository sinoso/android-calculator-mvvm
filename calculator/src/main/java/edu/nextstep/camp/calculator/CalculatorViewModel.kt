package edu.nextstep.camp.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.nextstep.camp.calculator.CalculatorEvent.*
import edu.nextstep.camp.calculator.domain.*
import kotlinx.coroutines.launch

class CalculatorViewModel(
    private val calculator: Calculator = Calculator(),
    lastExpression: Expression = DEFAULT_EXPRESSION,
    private var calculationResultStorage: CalculationResultStorage = CalculationResultStorage(),
    lastCalculationHistoryVisibility: Boolean = DEFAULT_CALCULATION_RESULT_VISIBILITY,
    private val calculationResultRepository: CalculationResultRepository
) : ViewModel() {
    init {
        requestGetCalculationResultsFromRepository()
    }

    private val _isCalculationHistoryVisible: MutableLiveData<Boolean> =
        MutableLiveData(lastCalculationHistoryVisibility)
    val isCalculationHistoryVisible: LiveData<Boolean> = _isCalculationHistoryVisible

    private val _expression = MutableLiveData(lastExpression)
    val expression: LiveData<Expression>
        get() = _expression

    private val _event = MutableLiveData<Event<CalculatorEvent>>()
    val event: LiveData<Event<CalculatorEvent>>
        get() = _event

    private val _calculationResults = MutableLiveData(calculationResultStorage.getResultsAsList())
    val calculationResults: LiveData<List<CalculationResult>>
        get() = _calculationResults

    fun addOperandToExpression(operand: Int) {
        val newExpression = getExpressionOrDefault() + operand
        _expression.value = newExpression
    }

    fun addOperatorToExpression(operator: Operator) {
        val newExpression = getExpressionOrDefault() + operator
        _expression.value = newExpression
    }

    fun removeLastFromExpression() {
        val newExpression = getExpressionOrDefault().removeLast()
        _expression.value = newExpression
    }

    fun toggleCalculationHistoryVisibility() {
        if (isCalculationHistoryVisible.value ?: DEFAULT_CALCULATION_RESULT_VISIBILITY) {
            _isCalculationHistoryVisible.value = false
            return
        }
        _isCalculationHistoryVisible.value = true
        sendCalculationResultsToView()
    }

    fun requestCalculate() {
        val inputtedExpression = getExpressionOrDefault()
        if (!inputtedExpression.isCompletedExpression()) {
            _event.value = Event(ERROR_INCOMPLETE_EXPRESSION)
            return
        }
        val result = calculator.calculate(inputtedExpression.toString())
        if (result == null) {
            _event.value = Event(ERROR_INCOMPLETE_EXPRESSION)
            return
        }
        processCalculationResult(CalculationResult(inputtedExpression, result))
    }

    private fun requestGetCalculationResultsFromRepository() {
        viewModelScope.launch {
            calculationResultRepository
                .getAll()
                .run { calculationResultStorage += this }
        }
    }

    private fun processCalculationResult(calculationResult: CalculationResult) {
        addCalculationResultToStorage(calculationResult)
        putCalculationResultToRepository(calculationResult)
        val newExpression = Expression.EMPTY + calculationResult.result
        _expression.value = newExpression
    }

    private fun putCalculationResultToRepository(calculatedResult: CalculationResult) {
        viewModelScope.launch {
            calculationResultRepository.insert(calculatedResult)
        }
    }

    private fun getExpressionOrDefault(): Expression = expression.value ?: DEFAULT_EXPRESSION

    private fun addCalculationResultToStorage(calculationResult: CalculationResult) {
        calculationResultStorage += calculationResult
    }

    private fun sendCalculationResultsToView() {
        _calculationResults.value = calculationResultStorage.getResultsAsList()
    }

    companion object {
        private val DEFAULT_EXPRESSION = Expression.EMPTY
        private const val DEFAULT_CALCULATION_RESULT_VISIBILITY = false
    }
}