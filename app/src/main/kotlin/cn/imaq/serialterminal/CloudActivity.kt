package cn.imaq.serialterminal

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_cloud.*

class CloudActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cloud)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val list = listOf("test1", "test2", "test3", "test4")

        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = object : RecyclerView.Adapter<ViewHolder>() {
                override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                    holder.title.text = list[position]
                    holder.subtitle.text = "[TODO]"
                }

                override fun getItemCount(): Int = list.size

                override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
                    layoutInflater.inflate(R.layout.button_list_item, parent, false).let {
                        return ViewHolder(it)
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val button = v.findViewById(R.id.imageButton) as ImageButton
        val title = v.findViewById(android.R.id.text1) as TextView
        val subtitle = v.findViewById(android.R.id.text2) as TextView

        init {
            button.setImageResource(android.R.drawable.stat_sys_download)
        }
    }

}
