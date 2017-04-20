package cn.imaq.serialterminal

import com.felhr.usbserial.UsbSerialDevice

/**
 * Created by adn55 on 2017/4/16.
 */
class SerialConnection : Connection() {

    companion object {
        var device: UsbSerialDevice? = null
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