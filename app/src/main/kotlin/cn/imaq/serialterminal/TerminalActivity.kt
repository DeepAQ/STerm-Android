package cn.imaq.serialterminal

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_terminal.*
import kotlinx.android.synthetic.main.content_terminal.*

class TerminalActivity : AppCompatActivity() {

    companion object {
        var connection: Connection? = null
        var scriptTitle = ""
        var scriptContent = ""
    }

    var sFragment: ScriptFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminal)
        setSupportActionBar(toolbar)
        editTerm.keyListener = null
        editCommand.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                onSendClicked(v)
            }
            true
        }

        sFragment = scriptFragment as ScriptFragment
        sFragment?.setOnClose {
            scriptTitle = ""
            onResume()
        }
        sFragment?.setOnSend { s ->
            editCommand.setText(s.trim(), TextView.BufferType.EDITABLE)
            onSendClicked(editCommand)
        }
        sFragment?.setOnSendAll { lines ->
            for (s in lines) {
                editCommand.setText(s.trim(), TextView.BufferType.EDITABLE)
                onSendClicked(editCommand)
            }
        }

        if (connection != null) {
            connection!!.startReceive { bytes, len ->
                runOnUiThread {
                    val textHeight = editTerm.lineHeight * editTerm.lineCount
                    val termStr = StringBuilder(editTerm.text)
                    (0 until len)
                            .map { bytes[it].toChar() }
                            .forEach {
                                when (it) {
                                    '\b' -> {
                                        if (termStr.lastIndex >= 0) {
                                            termStr.deleteCharAt(termStr.lastIndex)
                                        }
                                    }
                                    else -> termStr.append(it)
                                }
                            }
                    if (termStr.length > 20480) {
                        termStr.delete(0, 20480 - termStr.length)
                    }
                    editTerm.text.clear()
                    editTerm.append(termStr)
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

    override fun onResume() {
        super.onResume()
        if (scriptTitle.isNotBlank()) {
            sFragment?.setScript(scriptTitle, scriptContent)
            supportFragmentManager.beginTransaction().show(sFragment).commit()
        } else {
            supportFragmentManager.beginTransaction().hide(sFragment).commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_terminal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_scripts -> startActivity(Intent(this, ScriptsActivity::class.java))
            R.id.action_ctrl_space -> send(" ")
            R.id.action_ctrl_c -> send("\u0003")
            R.id.action_ctrl_z -> send("\u001a")
            R.id.action_ctrl_escape -> send("\u001e")
        }
        return super.onOptionsItemSelected(item)
    }

    fun onSendClicked(v: View) {
        if (send("${editCommand.text}\r")) {
            editCommand.text.clear()
        }
    }

    private fun send(command: String): Boolean {
        try {
            connection!!.send(command.toByteArray())
            return true
        } catch (e: Exception) {
            Snackbar.make(editCommand, "Send command failed: ${e.javaClass.simpleName}", Snackbar.LENGTH_LONG).show()
            return false
        }
    }

}
