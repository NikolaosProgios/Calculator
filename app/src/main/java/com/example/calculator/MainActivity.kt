package com.example.calculator

import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.example.calculator.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var button: MaterialButton
    private lateinit var buttonText: String
    private lateinit var dataToCalculate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onClick(v: View) {
        binding.solutionTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42F)
        binding.resultTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42F)

        button = v as MaterialButton
        buttonText = button.text.toString()
        dataToCalculate = binding.solutionTv.text.toString()

        if (isBntEquals()) return
        if (isBntClear()) return
        if (isBntClearAll()) return

        dataToCalculate += buttonText
        binding.solutionTv.text = /*dataToCalculate*/addData(dataToCalculate)
        val finalResult: String = getResult(dataToCalculate)
        if (finalResult.contains("Undefined")) binding.resultTv.text = ""
        else if (finalResult != "Err") binding.resultTv.text = getString(R.string.result, finalResult)
    }

    private fun getResult(data: String?): String {
        return try {
            val context = Context.enter()
            context.optimizationLevel = -1
            val scriptable: Scriptable = context.initStandardObjects()
            var finalResult: String =
                context.evaluateString(scriptable, data, "Javascript", 1, null).toString()
            if (finalResult.endsWith(".0")) {
                finalResult = finalResult.replace(".0", "")
            }
            finalResult
        } catch (e: Exception) {
            "Err"
        }
    }

    private fun isBntEquals(): Boolean {
        if (buttonText == "=") {
            if (binding.solutionTv.text.isEmpty()) return true
            binding.solutionTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24F)
            binding.resultTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60F)
            return true
        }
        return false
    }

    private fun isBntClear(): Boolean {
        if (buttonText == "C") {
            with(dataToCalculate) {
                if (isEmpty()) return true
                if (length == 1) {
                    buttonText = "AC"
                    isBntClearAll()
                    return true
                }
                if (isNotEmpty()) {
                    buttonText = ""
                    dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length - 1)
                    return false
                }
            }
        }
        return false
    }

    private fun isBntClearAll(): Boolean {
        if (buttonText == "AC") {
            binding.solutionTv.text = ""
            binding.resultTv.text = "0"
            return true
        }
        return false
    }

    private fun addData(data: String): CharSequence? {
        var textToBeColored2 = ""
        data.forEach {
            if (!it.isDigit()) {
                val char = it.toString()
                val color = getColor(R.color.tv_symbols)
                val htmlText: String = char.replace(
                    char, "<font color='$color'>$char</font>"
                )
                textToBeColored2 += htmlText
            } else {
                textToBeColored2 += it
            }
        }
        return Html.fromHtml(textToBeColored2)
    }
}