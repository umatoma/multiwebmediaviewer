package io.github.umatoma.multiwebmediaviewer.common.hatena.entity

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "rdf:RDF", strict = false)
class HatenaRssRoot {
    @get:ElementList(inline = true)
    @set:ElementList(inline = true)
    var itemList: List<HatenaRssItem>? = null
}