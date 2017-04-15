package cn.imaq.serialterminal

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_terminal.*
import kotlinx.android.synthetic.main.content_terminal.*
import java.net.ServerSocket

class TerminalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminal)
        setSupportActionBar(toolbar)
        editTerm.keyListener = null

        Thread {
            val ss = ServerSocket(12345)
            while (true) {
                val cs = ss.accept()
                Thread {
                    val ins = cs.getInputStream()
                    while (true) {
                        val buf = ByteArray(1024)
                        val len = ins.read(buf)
                        if (len < 0) {
                            break
                        } else {
                            runOnUiThread {
                                val s = String(buf, 0, len)
                                editTerm.append(s)
                                //editTerm.scrollTo(0, editTerm.bottom)
                            }
                        }
                    }
                    ins.close()
                    cs.close()
                }.start()
            }
        }.start()
    }

}
