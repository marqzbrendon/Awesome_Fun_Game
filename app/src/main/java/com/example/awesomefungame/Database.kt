package com.example.awesomefungame//
//import android.widget.ArrayAdapter
//import android.widget.ListView
//import android.widget.TextView
//import com.example.awesomefungame.GameActivity
//import com.google.firebase.firestore.EventListener
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.FirebaseFirestoreException
//import com.google.firebase.firestore.QuerySnapshot
//
//
//open class AppDatabase(private val gameActivity: GameActivity) {
//    fun retrieveAllDocuments(
//        db: FirebaseFirestore?,
//        lobby: Long,
//        lobbyData: LobbyData,
//        textViewResult: TextView,
//        adapter: ArrayAdapter<Any>,
//    ) {
//
//        db?.collection("$lobby")?.addSnapshotListener(
//                object : EventListener<QuerySnapshot?> {
//                    override fun onEvent(
//                        snapshots: QuerySnapshot?,
//                        e: FirebaseFirestoreException?
//                    ) {
//                        // Clears any previous data to get the most recent ones
//                        lobbyData.clear()
//                        if (e != null) {
//                            println("Listen failed:$e")
//                            return
//                        }
//                        if (snapshots != null) {
//                            for (doc in snapshots) {
//                                doc.getString("player")?.let {
//                                    doc.getDouble("score")?.let {
//                                            it1 -> LobbyDataSample(it, it1) }
//                                }?.let { lobbyData.add(it) }
//                            }
//                            gameActivity.setUpScoreData(
//                                lobbyData,
//                                textViewResult,
//                                adapter)
//                        }
//                    }
//                })
//    }
//}
