package cn.imaq.serialterminal

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_script.*

class ScriptFragment : Fragment() {

    private var scriptLines: List<String> = emptyList()
    private var sendHandler: ((String) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_script, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = object : RecyclerView.Adapter<ViewHolder>() {
                override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                    holder.line.text = scriptLines[position]
                    holder.button.setOnClickListener {
                        sendHandler?.invoke(scriptLines[position])
                    }
                }

                override fun getItemCount(): Int = scriptLines.size

                override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
                    getLayoutInflater(savedInstanceState).inflate(R.layout.button_list_item_singleline, parent, false).let {
                        return ViewHolder(it)
                    }
                }
            }
        }
    }

    fun setScript(name: String, content: String) {
        scriptName.text = name
        scriptLines = content.split("\n")
        recyclerView.adapter.notifyDataSetChanged()
    }

    fun setOnClose(handler: () -> Unit) {
        buttonClose.setOnClickListener {
            handler.invoke()
        }
    }

    fun setOnSend(handler: (String) -> Unit) {
        sendHandler = handler
    }

    private class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val line = v.findViewById(android.R.id.text1) as TextView
        val button = v.findViewById(R.id.imageButton) as ImageButton

        init {
            button.setImageResource(android.R.drawable.ic_menu_send)
        }
    }

}
