package au.com.beba.chainReaction.feature.mockAppView

interface RequestValueListener {
    fun onValueSelected(chainTag: String, request: String, value: Any?)
}