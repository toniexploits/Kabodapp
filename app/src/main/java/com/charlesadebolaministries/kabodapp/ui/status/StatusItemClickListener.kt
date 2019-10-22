package com.charlesadebolaministries.kabodapp.ui.status

import com.charlesadebolaministries.kabodapp.util.StatusListElement

interface StatusItemClickListener {
    fun onItemClicked(statusElement: StatusListElement)
}