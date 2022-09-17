package kr.zentry.devdolittle_graph_hr_file_3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class MeasureBaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        val manager: FragmentManager? = activity?.supportFragmentManager
        if (manager?.findFragmentByTag("dolittle") != null) {
            val MeasureDolittle: MeasureDolittleFragment =
                activity?.supportFragmentManager?.findFragmentByTag("dolittle") as MeasureDolittleFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.fragment_measure_base, container, false)
        val radio_measure = v.findViewById<RadioGroup>(R.id.radio_measure)

        radio_measure.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_measure_tap -> {
                    val fragmentTransaction1: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    fragmentTransaction1.replace(
                        R.id.container_measure,
                        MeasureTapFragment(),
                        "tap"
                    )
                    fragmentTransaction1.commit()
                }
                R.id.radio_measure_dolittle -> {
                    val fragmentTransaction2: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    fragmentTransaction2.replace(
                        R.id.container_measure,
                        MeasureDolittleFragment(),
                        "dolittle"
                    )
                    fragmentTransaction2.commit()
                }
            }
        })

        val fragmentTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container_measure, MeasureDolittleFragment(), "dolittle")
        fragmentTransaction.commit()



        return v;
    }
}