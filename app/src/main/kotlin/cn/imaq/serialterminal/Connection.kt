package cn.imaq.serialterminal

/**
 * Created by adn55 on 2017/4/16.
 */
abstract class Connection {

    var receiver: ((ByteArray, Int) -> Unit)? = null

    abstract val readThread: Thread

    fun startReceive(receiver: (ByteArray, Int) -> Unit) {
        this.receiver = receiver
        if (!readThread.isAlive) {
            readThread.start()
        }
    }

    abstract fun send(bytes: ByteArray, length: Int)

    abstract fun close()

}