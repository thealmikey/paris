package com.airbnb.paris

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.AttrRes
import android.support.annotation.StyleRes
import android.util.AttributeSet
import com.airbnb.paris.Style.Config
import com.airbnb.paris.Style.DebugListener
import java.util.*

// TODO Can all the parameters be private?
data class SimpleStyle internal constructor(
        private val attributeMap: Map<Int, Int>?,
        val attributeSet: AttributeSet?,
        @StyleRes val styleRes: Int,
        val config: Config?) : Style {

    private constructor(builder: Builder) : this(builder.attrResToValueResMap, null, 0, null)
    @JvmOverloads constructor(attributeSet: AttributeSet, config: Config? = null) : this(null, attributeSet, 0, config)
    @JvmOverloads constructor(@StyleRes styleRes: Int, config: Config? = null) : this(null, null, styleRes, config)

    class Builder internal constructor() {

        internal val attrResToValueResMap = HashMap<Int, Int>()

        fun isEmpty(): Boolean = attrResToValueResMap.isEmpty()

        fun put(@AttrRes attrRes: Int, valueRes: Int): Builder {
            attrResToValueResMap.put(attrRes, valueRes)
            return this
        }

        fun build(): SimpleStyle = SimpleStyle(this)
    }

    companion object {
        internal val EMPTY = SimpleStyle(null, null, 0, null)

        fun builder(): Builder = Builder()
    }

    override val shouldApplyParent = attributeSet == null

    /**
     * Visible for debug
     */
    override var debugListener: DebugListener? = null

    override fun name(context: Context): String = when {
        styleRes != 0 -> context.resources.getResourceEntryName(styleRes)
        else -> "unknown name"
    }

    @SuppressLint("Recycle")
    override fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper = when {
        attributeMap != null -> MapTypedArrayWrapper(context.resources, attrs, attributeMap)
        attributeSet != null -> TypedArrayTypedArrayWrapper(context.obtainStyledAttributes(attributeSet, attrs, 0, styleRes))
        styleRes != 0 -> TypedArrayTypedArrayWrapper(context.obtainStyledAttributes(styleRes, attrs))
        else -> EmptyTypedArrayWrapper
    }

    override fun hasOption(option: Config.Option): Boolean {
        return config != null && config.contains(option)
    }
}