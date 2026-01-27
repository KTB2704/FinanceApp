package com.example.projectthuctap.base
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.projectthuctap.R
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding
        ?: error("Binding is only valid between onCreateView and onDestroyView")

    abstract fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    protected fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    protected fun formatMoney(value: Double): String {
        return NumberFormat.getCurrencyInstance(
            Locale("vi", "VN")
        ).format(value)
    }

    protected fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()

    protected fun navigate(
        fragment: Fragment,
        @IdRes containerId: Int = R.id.fragment_container,
        addToBackStack: Boolean = true,
        withAnimation: Boolean = true
    ) {

        val transaction = parentFragmentManager.beginTransaction()

        if (withAnimation) {
            transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        }

        transaction
            .replace(containerId, fragment)
            .apply { if (addToBackStack) addToBackStack(null) }
            .commit()
    }

    protected fun showDateTimePicker(
        initialTime: Long = System.currentTimeMillis(),
        onDateTimeSelected: (Long) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = initialTime

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->

                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->

                        calendar.set(year, month, day, hour, minute)
                        onDateTimeSelected(calendar.timeInMillis)

                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    protected fun popBack() {
        parentFragmentManager.popBackStack()
    }
}
