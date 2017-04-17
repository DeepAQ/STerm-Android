package cn.imaq.serialterminal

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import kotlinx.android.synthetic.main.activity_cloud.*
import org.json.JSONArray
import java.io.File

class CloudActivity : AppCompatActivity() {

    private val cloudBase = "https://wx.imaq.cn/sterm/"
    private var scriptList = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cloud)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = object : RecyclerView.Adapter<ViewHolder>() {
                override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                    val obj = scriptList.getJSONObject(position)
                    with(holder) {
                        val name = obj.getString("name")
                        title.text = name
                        subtitle.text = obj.getString("desc")
                        button.setOnClickListener {
                            downloadScript(name)
                        }
                    }
                }

                override fun getItemCount(): Int = scriptList.length()

                override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
                    layoutInflater.inflate(R.layout.button_list_item, parent, false).let {
                        return ViewHolder(it)
                    }
                }
            }
        }

        updateList()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateList() {
        val p = ProgressDialog.show(this, "Please wait", "Fetching script list from cloud ...")
        Fuel.get("$cloudBase/script_list.aq").responseJson { _, _, result ->
            scriptList = result.get().array()
            recyclerView.adapter.notifyDataSetChanged()
            p.dismiss()
        }
    }

    private fun downloadScript(name: String) {
        val file = File(getDir("scripts", MODE_PRIVATE), "$name.sh")
        if (file.exists()) {
            AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("$name.sh exists, overwrite?")
                    .setPositiveButton("Yes", { _, _ ->
                        file.delete()
                        downloadScript(name)
                    })
                    .setNegativeButton("No", null)
                    .create()
                    .show()
        } else {
            val p = ProgressDialog.show(this, "Please wait", "Downloading $name.sh from cloud ...")
            Fuel.get("$cloudBase/get_script.aq", listOf("name" to name)).response { _, _, result ->
                val bytes = result.get()
                if (bytes.isNotEmpty()) {
                    with(file.outputStream()) {
                        write(bytes)
                        flush()
                        close()
                    }
                    Snackbar.make(recyclerView, "$name.sh successfully downloaded", Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(recyclerView, "Download failed: $name.sh is empty", Snackbar.LENGTH_LONG).show()
                }
                p.dismiss()
            }
        }
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
