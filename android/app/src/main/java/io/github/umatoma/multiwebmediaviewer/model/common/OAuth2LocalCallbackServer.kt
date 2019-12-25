package io.github.umatoma.multiwebmediaviewer.model.common

import fi.iki.elonen.NanoHTTPD

class OAuth2LocalCallbackServer(port: Int) : NanoHTTPD(port) {

    override fun serve(session: IHTTPSession): Response {
        val queryParams = session.parms
        val html = buildString {
            append("<html>")
            append("  <meta charset=\"utf-8\">")
            append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
            append("  <style>")
            append("    body { text-align: center; padding-top: 64px; }")
            append("    pre { overflow: auto; white-space: pre-wrap; word-wrap: break-word; }")
            append("    pre { padding: 8px 16px; background: #ddd; }")
            append("  </style>")
            append("  <body>")
            append("    <h4>MultiWebMediaViewer</h4>")
            append("    <h2>アクセスを許可しました</h2>")
            append("    <p>以下のコードを<br/>アプリケーションに入力して下さい。</p>")
            append("    <pre>${queryParams["code"]}</pre>")
            append("  </body>")
            append("</html>")
        }
        return NanoHTTPD.newFixedLengthResponse(html)
    }
}