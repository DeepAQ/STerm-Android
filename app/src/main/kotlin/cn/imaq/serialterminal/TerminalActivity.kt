package cn.imaq.serialterminal

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_terminal.*
import kotlinx.android.synthetic.main.content_terminal.*

class TerminalActivity : AppCompatActivity() {

    companion object {
        var connection: Connection? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminal)
        setSupportActionBar(toolbar)
        editTerm.keyListener = null

        if (connection != null) {
            connection!!.startReceive { bytes, len ->
                runOnUiThread {
                    val textHeight = editTerm.lineHeight * editTerm.lineCount
                    editTerm.append(String(bytes, 0, len))
                    if (editTerm.scrollY >= textHeight - editTerm.height - editTerm.lineHeight * 3) {
                        editTerm.scrollTo(0, textHeight)
                    }
                }
            }
        } else {
            finish()
        }
    }

}
