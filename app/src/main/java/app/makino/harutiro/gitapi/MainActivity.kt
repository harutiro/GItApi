package app.makino.harutiro.gitapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import coil.api.load
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gson: Gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

        val retrofit: Retrofit =  Retrofit.Builder()
            .baseUrl("https:api.github.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val userService: UserService = retrofit.create(UserService::class.java)

        findViewById<Button>(R.id.requestButton).setOnClickListener{

            //情報取得を別のスレッドでおこなうようにする
            runBlocking(Dispatchers.IO){
                runCatching {
                    //userServiseで定義したメソッドを使ってユーザー情報を取得する
                    userService.getUser("harutiro")
                }
            }.onSuccess{
                //読み込んだデータをはめ込む
                findViewById<ImageView>(R.id.avatarImageView).load(it.avatarUrl)
                findViewById<TextView>(R.id.nameTextView).text = it.name
                findViewById<TextView>(R.id.userIdTextView).text = it.userId
                findViewById<TextView>(R.id.followingTextView).text = it.following.toString()
                findViewById<TextView>(R.id.followersTextView).text = it.followers.toString()
                
            }.onFailure {
                //失敗した時のところ。
                Toast.makeText(this,"失敗",Toast.LENGTH_LONG).show()
            }

        }
    }
}