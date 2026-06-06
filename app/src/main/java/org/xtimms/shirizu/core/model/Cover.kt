package org.xtimms.shirizu.core.model

import org.koitharu.kotatsu.parsers.model.MangaSource as ParsersMangaSource

data class Cover(
    val url: String?,
    val source: String,
) {
    val mangaSource: ParsersMangaSource by lazy { MangaSource(source) }
}