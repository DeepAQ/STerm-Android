package cn.imaq.serialterminal

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.EditorInfo
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
        editCommand.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                onSendClicked(v)
            }
            true
        }

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

    override fun onDestroy() {
        super.onDestroy()
        connection?.close()
    }

    fun onSendClicked(v: View) {
        val command = editCommand.text.trim()
        if (!command.isEmpty()) {
            try {
                val bytes = "$command\r\n".toByteArray()
                connection!!.send(bytes, bytes.size)
                editCommand.text.clear()
            } catch (e: Exception) {
                Snackbar.make(v, "Send command failed: ${e.javaClass.simpleName}", Snackbar.LENGTH_LONG).show()
            }
        }
    }

}
