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
            try {
                val cs = ssocket!!.accept()
                csocket?.close() // close old connection
                csocket = cs
                Thread {
                    val ins = cs.getInputStream()
                    while (!csocket!!.isClosed) {
                        try {
                            val buf = ByteArray(1024)
                            val len = ins.read(buf)
                            if (len > 0) {
                                receiver?.invoke(buf, len)
                            } else {
                                break
                            }
                        } catch (e: Exception) {
                        }
                    }
                    ins.close()
                    cs.close()
                }.start()
            } catch (e: Exception) {
            }
        }
    }

    override fun send(bytes: ByteArray, length: Int) {
        with(csocket!!.getOutputStream()) {
            write(bytes, 0, length)
            flush()
        }
    }

    override fun close() {
        csocket?.close()
        ssocket?.close()
    }
}