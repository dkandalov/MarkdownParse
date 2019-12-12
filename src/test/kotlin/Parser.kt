import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.*
import org.junit.jupiter.api.*

class MarkdownParserTests {
    @Test fun `Empty markdown produces empty html`() {
        val html = parseMarkdownToHtml("")
        assertThat(html, equalTo(""))
    }

    @Test fun `Plain text produces the same html`() {
        val texts = listOf("lorem ipsum", "", "fghgf tytrty tytrtyu", "line 1\nline 2")
        for (text in texts) {
            val html = parseMarkdownToHtml(text)
            assertThat(html, equalTo(text))
        }
    }

    @Test fun `Header in markdown produces h1 in html`() {
        val html = parseMarkdownToHtml("# Header 1")
        assertThat(html, equalTo("<h1>Header 1</h1>"))
    }

    @Test fun `Header in markdown produces h2 in html`() {
        val html = parseMarkdownToHtml("## Header 2")
        assertThat(html, equalTo("<h2>Header 2</h2>"))
    }

    @Test fun `Header in markdown converted to header object`() {
        assertThat(
            parseMarkdown("# Header 1"),
            equalTo(listOf<Token>(Header(level = 1, text = "Header 1")))
        )
    }

    @Test fun `Plain text in markdown converted to text object`() {
        assertThat(
            parseMarkdown("Text 1"),
            equalTo(listOf<Token>(Text(text = "Text 1")))
        )
    }

    @Test fun `Plain text and headers markdown converted to text and header objects`() {
        assertThat(
            parseMarkdown("""
                |# Header 1
                |Text 1
            """.trimMargin()),
            equalTo(listOf(
                Header(level = 1, text = "Header 1"),
                Text(text = "Text 1")
            ))
        )
    }
}

sealed class Token
data class Header(val level: Int, val text: String): Token()
data class Text(val text: String): Token()

fun parseMarkdownToHtml(markdownText: String): String =
    parseMarkdown(markdownText)
        .joinToString("\n") { it.toHtml() }

private fun Token.toHtml(): String = when (this) {
    is Header -> "<h$level>$text</h$level>"
    is Text   -> text
}

private fun parseMarkdown(markdownText: String): List<Token> {
    return markdownText.split("\n").map { line ->
        when {
            line.startsWith("# ")  -> Header(level = 1, text = line.trimMargin("# "))
            line.startsWith("## ") -> Header(level = 2, text = line.trimMargin("## "))
            else                   -> Text(line)
        }
    }
}