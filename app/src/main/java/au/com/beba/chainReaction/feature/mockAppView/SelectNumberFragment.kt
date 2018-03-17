package au.com.beba.chainReaction.feature.mockAppView

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import au.com.beba.chainReaction.R
import org.jetbrains.anko.support.v4.find

class SelectNumberFragment : Fragment() {

    private lateinit var chainTag: String
    private lateinit var chainRequest: String

    companion object {
        private const val ARG_CHAIN_TAG = "ARG_CHAIN_TAG"
        private const val ARG_CHAIN_REQUEST = "ARG_CHAIN_REQUEST"

        fun newInstance(chainTag: String, chainRequest: String): SelectNumberFragment {
            val fragment = SelectNumberFragment()
            val args = Bundle()
            args.putString(ARG_CHAIN_TAG, chainTag)
            args.putString(ARG_CHAIN_REQUEST, chainRequest)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var requestValueListener: RequestValueListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            chainTag = arguments!!.getString(ARG_CHAIN_TAG)
            chainRequest = arguments!!.getString(ARG_CHAIN_REQUEST)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout
        return inflater.inflate(R.layout.fragment_select_number, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        find<Button>(R.id.button1).setOnClickListener { selectValue(1) }
        find<Button>(R.id.button2).setOnClickListener { selectValue(2) }
        find<Button>(R.id.button3).setOnClickListener { selectValue(3) }
        find<Button>(R.id.button4).setOnClickListener { selectValue(5) }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is RequestValueListener) {
            this.requestValueListener = context
        }
    }

    private fun selectValue(value: Int) {
        // RETURN SELECTED VALUE TO THE CHAIN (VIA PARENT CALLBACK)
        requestValueListener.onValueSelected(chainTag, chainRequest, value)
    }
}
