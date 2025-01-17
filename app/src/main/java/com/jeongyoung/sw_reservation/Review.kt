package com.jeongyoung.sw_reservation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RatingBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jeongyoung.sw_reservation.ReviewDBkey.Companion.DB_REVIEWS
import com.jeongyoung.sw_reservation.databinding.ActivityMainBinding
import com.jeongyoung.sw_reservation.databinding.ActivityReservationDetailBinding
import com.jeongyoung.sw_reservation.databinding.ActivityReviewWriteBinding
import com.jeongyoung.sw_reservation.databinding.FragmentReservationBinding
import com.jeongyoung.sw_reservation.reservation.DBkey
import com.jeongyoung.sw_reservation.reservation.ReservationAdapter
import com.jeongyoung.sw_reservation.reservation.ReservationModel

class Review : AppCompatActivity(), RatingBar.OnRatingBarChangeListener  {
    private lateinit var articleDB: DatabaseReference
    private lateinit var reviewAdapter: ReviewAdapter

    private  lateinit var auth: FirebaseAuth
    private val ReviewModelList = mutableListOf<ReviewModel>()
    val user = Firebase.auth.currentUser
    var ratingscore = 0.0
    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val reviewModel = snapshot.getValue(ReviewModel::class.java)
            reviewModel ?: return
             ReviewModelList.add(reviewModel)
            reviewAdapter.submitList(ReviewModelList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityReviewWriteBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.ratingbar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            binding.ratingbarText.text = "${rating}점"
            ratingscore = rating.toDouble()

        }


        //데이터 중복 방지
        ReviewModelList.clear()
        //추가할 데이터 위치 설정
        articleDB = Firebase.database.reference.child(DB_REVIEWS)

        binding.reviewButton.setOnClickListener {
            articleDB.addChildEventListener(listener)
            val dotString : String = user.email.toString()
            val splitArray = dotString.split(".")
            val emailString : String = splitArray[0].toString()
            val splitNameArray = emailString.split("@")
            val comment = binding.comment.text.toString()
            val id = splitNameArray[0]
            val reviewModel = ReviewModel(id,comment,ratingscore)

            articleDB.push().setValue(reviewModel)
            finish()
        }
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {

    }
}
