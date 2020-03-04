package com.example.navigationwithtoolbar

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONArray

class HomeActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {


    private lateinit var navControler :NavController
    private lateinit var constants: Constants
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        navControler = Navigation.findNavController(this,R.id.fragment)
        constants = Constants(this)
        bottomNav.setupWithNavController(navControler)
        drawer_navigation_view.setNavigationItemSelectedListener(this)

        //checking for events
// ...
// Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "http://"+Constants.ip+"/pgr/getevents.php?email="+constants.email

// Request a string response from the provided URL.

        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    if ((!response.contains("noresfound"))&& (!response.contains("noupcomingevents"))){
                        val jsonArray = JSONArray(response)
                        val jsonObject = jsonArray.getJSONObject(0)
                        val title = jsonObject.getString("title")
                        val description = jsonObject.getString("description")
                        val date = jsonObject.getString("date")
                        val time = jsonObject.getString("time")
                        val venue = jsonObject.getString("venue")

                        //creating alert dialogue
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle(title.toUpperCase())
                        val message =
                                description+"\n" +
                                "Date: "+date+"\n" +
                                "Time: "+time+"\n" +
                                "Venue: "+venue+"\n\n" +
                                        "Please visit our site to register."
                        builder.setMessage(message)
                        builder.setPositiveButton("Ok, got it!"){dialog, which ->
                            //on click on interested.
                            Log.i("clicked","yes")
                        }
                        //builder.setNegativeButton("Cancel"){dialog, which -> Log.i("clicked","yes")}
                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.show()
                    }
                },
                Response.ErrorListener { Log.i("didnt work","true") })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)

    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.profile ->{
//                toast("profile!")
                intent = Intent(applicationContext,ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.query ->{
                // toast("feedback!")
                intent = Intent(applicationContext,SendQuestionActivity::class.java)
                startActivity(intent)
            }
            
            R.id.feedback ->{
               // toast("feedback!")
                intent = Intent(applicationContext,FeedbackActivity::class.java)
                startActivity(intent)
            }
            R.id.share ->{
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type="text/plain"
                //TODO:add share link content here.
                shareIntent.putExtra(Intent.EXTRA_TEXT, "")
                startActivity(Intent.createChooser(shareIntent,""))
                //toast("share!")
            }
            R.id.about ->{
                //toast("about!")
                val aboutIntent = Intent(applicationContext,AboutActivity::class.java)
                startActivity(aboutIntent)
            }
            R.id.pim ->{
                //toast("about!")
                val pimIntent = Intent(applicationContext,pimActivity::class.java)
                startActivity(pimIntent)
            }
            R.id.exit ->{
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Exit")
                builder.setMessage("Are you sure you want to Exit?")
                builder.setPositiveButton("YES"){dialog, which ->
                    // Do something when user press the positive button
                    finish()
                }
                builder.setNeutralButton("NO"){dialog, which ->
                    dialog.dismiss()
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
            R.id.report_bug->{
//                val emailIntent = Intent(Intent.ACTION_SEND)
//                emailIntent.setType("text/plain")
//                startActivity(emailIntent)
                try {
                    val i = Intent(Intent.ACTION_VIEW,Uri.parse("mailto:" + "contact@shresthdubey.me"))
                    i.putExtra(Intent.EXTRA_SUBJECT, "Bug Report")
                    i.putExtra(Intent.EXTRA_TEXT,"Please write your bug below:")
                    startActivity(i)
                }catch (e: Exception){

                }
            }
            R.id.logout->{
//                constants.removeUser()
//                intent = Intent(applicationContext,MainActivity::class.java)
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                startActivity(intent)
                val builder = AlertDialog.Builder(this);
                builder.setTitle("Logout")
                builder.setMessage("Are you sure you want to logout?")
                builder.setPositiveButton("YES"){dialog, which ->
                    // Do something when user press the positive button
                    constants.removeUser()
                    intent = Intent(applicationContext,MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
                builder.setNeutralButton("NO"){dialog, which ->
                    dialog.dismiss()
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
        drawerlayout.closeDrawer(GravityCompat.START)
        return true
    }
}
