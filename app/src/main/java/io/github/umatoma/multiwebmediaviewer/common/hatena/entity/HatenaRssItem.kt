package io.github.umatoma.multiwebmediaviewer.common.hatena.entity

import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root

@Root(name = "item", strict = false)
class HatenaRssItem {
    @set:Element(name = "title")
    @get:Element(name = "title")
    var title: String? = null

    @set:Element(name = "link")
    @get:Element(name = "link")
    var link: String? = null

    @set:Element(name = "description", required = false)
    @get:Element(name = "description", required = false)
    var description: String? = null

    @Namespace(prefix = "content")
    @set:Element(name = "encoded")
    @get:Element(name = "encoded")
    var content: String? = null

    @Namespace(prefix = "hatena")
    @set:Element(name = "imageurl", required = false)
    @get:Element(name = "imageurl", required = false)
    var imageurl: String? = null

    @Namespace(prefix = "hatena")
    @set:Element(name = "bookmarkcount")
    @get:Element(name = "bookmarkcount")
    var bookmarkcount: String? = null

    @Namespace(prefix = "hatena")
    @set:Element(name = "bookmarkSiteEntriesListUrl")
    @get:Element(name = "bookmarkSiteEntriesListUrl")
    var bookmarkSiteEntriesListUrl: String? = null

    @Namespace(prefix = "hatena")
    @set:Element(name = "bookmarkCommentListPageUrl")
    @get:Element(name = "bookmarkCommentListPageUrl")
    var bookmarkCommentListPageUrl: String? = null
}