package love.sola.nfc.kts

import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 * @author Sola
 */
object Dojo {

    init {
        setIdeaIoUseFallback()
    }

    val engine: ScriptEngine = ScriptEngineManager().getEngineByExtension("kts")

    fun script(script: String): Any? = engine.eval(script)

}