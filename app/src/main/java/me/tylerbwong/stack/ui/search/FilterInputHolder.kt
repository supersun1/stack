package me.tylerbwong.stack.ui.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.filter_input_holder.*
import me.tylerbwong.stack.R
import me.tylerbwong.stack.data.model.SearchPayload
import me.tylerbwong.stack.ui.home.FilterInputItem
import me.tylerbwong.stack.ui.utils.inflate

class FilterInputHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    @Suppress("ComplexMethod")
    fun bind(item: FilterInputItem) {
        val payload = item.searchPayload as? SearchPayload.Standard ?: return
        val (_, isAccepted, minNumAnswers, bodyContains, isClosed, tags, titleContains) = payload

        advancedOptions.removeAllViews()
        val addedFilters = listOf(
            isAccepted?.let { Filter.Accepted(it) } ?: Filter.None,
            minNumAnswers?.let { Filter.MinAnswers(it) } ?: Filter.None,
            bodyContains?.let { Filter.BodyContains(it) } ?: Filter.None,
            isClosed?.let { Filter.Closed(it) } ?: Filter.None,
            tags?.let { Filter.Tags(it) } ?: Filter.None,
            titleContains?.let { Filter.TitleContains(it) } ?: Filter.None
        ).filter { it != Filter.None }

        addedFilters
            .map { it.getLabel(advancedOptions.context) }
            .forEach { label ->
                advancedOptions.addView(
                    advancedOptions.inflate<Chip>(R.layout.advanced_filter_chip).apply {
                        text = label
                        setOnCloseIconClickListener {
                            val removedFilter = addedFilters.firstOrNull { filter ->
                                label in filter.getLabel(it.context)
                            } ?: Filter.None
                            val newPayload = when (removedFilter) {
                                is Filter.Accepted -> payload.copy(isAccepted = null)
                                is Filter.MinAnswers -> payload.copy(minNumAnswers = null)
                                is Filter.BodyContains -> payload.copy(bodyContains = null)
                                is Filter.Closed -> payload.copy(isClosed = null)
                                is Filter.Tags -> payload.copy(tags = null)
                                is Filter.TitleContains -> payload.copy(
                                    titleContains = null
                                )
                                is Filter.None -> payload.copy()
                            }
                            item.onPayloadReceived(newPayload)
                        }
                    }
                )
            }
    }
}