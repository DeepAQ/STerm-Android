package cn.imaq.serialterminal

import java.net.ServerSocket
import java.net.Socket

/**
 * Created by adn55 on 2017/4/16.
 */
class SocketConnection(port: Int) : Connection() {

    companion object {
        var ssocket: ServerSocket? = null
        var csocket: Socket? = null
    }

    init {
        ssocket?.close()
        ssocket = ServerSocket(port)
    }

    override val readThread = Thread {
        while (ssocket != null && !ssocket!!.isClosed) {
            while (true) {
                val cs = ssocket!!.accept()
                csocket?.close() // close old connection
                csocket = cs
                Thread {
                    val ins = cs.getInputStream()
                    val buf = ByteArray(1024)
                    while (!csocket!!.isClosed) {
                        val len = ins.read(buf, 0, buf.size)
                        if (len > 0) {
                            receiver?.invoke(buf, len)
                        } else {
                            break
                        }
                    }
                    ins.close()
                    cs.close()
                }.start()
            }
        }
    }

    override fun send(bytes: ByteArray, length: Int) {
        csocket?.getOutputStream()?.let {
            it.write(bytes, 0, length)
            it.flush()
        }
    }

}