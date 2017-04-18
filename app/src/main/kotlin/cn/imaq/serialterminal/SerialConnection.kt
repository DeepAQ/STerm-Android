package cn.imaq.serialterminal

import com.felhr.usbserial.CH34xSerialDevice

/**
 * Created by adn55 on 2017/4/16.
 */
class SerialConnection : Connection() {

    companion object {
        var device: CH34xSerialDevice? = null
    }

    override val readRunnable = Runnable {
        device?.read { buf ->
            receiver?.invoke(buf, buf.size)
        }
    }

    override fun send(bytes: ByteArray) {
        device!!.write(bytes)
    }

    override fun close() {
        device?.close()
    }

}