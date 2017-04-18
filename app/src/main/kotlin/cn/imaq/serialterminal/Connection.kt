package cn.imaq.serialterminal

/**
 * Created by adn55 on 2017/4/16.
 */
abstract class Connection {

    protected var receiver: ((ByteArray, Int) -> Unit)? = null

    abstract val readRunnable: Runnable

    private var readThread: Thread? = null

    fun startReceive(receiver: (ByteArray, Int) -> Unit) {
        this.receiver = receiver
        if (readThread == null || !readThread!!.isAlive) {
            readThread = Thread(readRunnable)
            readThread!!.start()
        }
    }

    abstract fun send(bytes: ByteArray)

    abstract fun close()

}