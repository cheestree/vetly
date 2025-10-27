package com.cheestree.vetly.repository.guide

import com.cheestree.vetly.domain.guide.Guide
import com.cheestree.vetly.repository.BaseSpecs

object GuideSpecs {
    fun likeTitle(title: String?) = BaseSpecs.likeString<Guide>(title, "title")
}