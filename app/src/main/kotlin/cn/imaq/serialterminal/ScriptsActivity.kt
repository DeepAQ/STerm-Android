package cn.imaq.serialterminal

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_scripts.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ScriptsActivity : AppCompatActivity() {

    private var selectedIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scripts)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { v ->
            TODO("add custom script")
        }

        val scriptDir = getDir("scripts", MODE_PRIVATE)
        with (FileOutputStream(File(scriptDir, "test.sh"))) {
            write("test".toByteArray())
            flush()
            close()
        }

        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = object : RecyclerView.Adapter<ViewHolder>() {
                private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                    scriptDir.listFiles()[position].let {
                        holder.title.text = it.name
                        holder.subTitle.text = dateFormat.format(Date(it.lastModified()))
                    }
                }

                override fun getItemCount(): Int = scriptDir.listFiles().size

                override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
                    layoutInflater.inflate(android.R.layout.simple_expandable_list_item_2, parent, false).let {
                        it.background = theme.obtainStyledAttributes(intArrayOf(android.R.attr.selectableItemBackground)).getDrawable(0)
                        it.setOnClickListener { v -> v.showContextMenu() }
                        it.setOnCreateContextMenuListener { menu, view, info ->
                            selectedIndex = getChildAdapterPosition(view)
                            //menuInflater.inflate(R.menu.menu_bookmark, menu)
                        }
                        return ViewHolder(it)
                    }
                }
            }
        }
    }

    private class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title = v.findViewById(android.R.id.text1) as TextView
        val subTitle = v.findViewById(android.R.id.text2) as TextView
    }

}
