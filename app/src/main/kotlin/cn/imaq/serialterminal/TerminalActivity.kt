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
                    editTerm.append(String(bytes, 0, len))
                }
            }
        } else {
            finish()
        }
    }

}
